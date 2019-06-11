package com.opsbears.cscanner.cli;

import com.opsbears.cscanner.aws.AWSPlugin;
import com.opsbears.cscanner.azure.AzurePlugin;
import com.opsbears.cscanner.core.RuleResult;
import com.opsbears.cscanner.core.ScannerCore;
import com.opsbears.cscanner.exoscale.ExoscalePlugin;
import com.opsbears.cscanner.firewall.FirewallPlugin;
import com.opsbears.cscanner.objectstorage.ObjectStoragePlugin;
import com.opsbears.cscanner.yaml.YamlPlugin;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class CLIApplication {
    private final List<OutputFormatter> outputFormatters;

    public CLIApplication(List<OutputFormatter> outputFormatters) {
        this.outputFormatters = outputFormatters;
    }

    public void run(String[] argv) {
        ArgumentParser parser = ArgumentParsers.newFor("cscanner.jar").build()
            .defaultHelp(true);

        parser
            .addArgument("--format")
            .choices(outputFormatters.stream().map(OutputFormatter::getType).toArray())
            .setDefault(outputFormatters.get(0).getType())
            .help("Set the output format.");

        parser
            .addArgument("--compliant")
            .type(Boolean.class)
            .setDefault(false)
            .action(Arguments.storeTrue())
            .help("Only output compliant rules. Conflicts with noncompliant.");

        parser
            .addArgument("--noncompliant")
            .type(Boolean.class)
            .setDefault(false)
            .action(Arguments.storeTrue())
            .help("Only output noncompliant rules. Conflicts with compliant.");

        parser
            .addArgument("-v")
            .type(Boolean.class)
            .setDefault(false)
            .action(Arguments.storeTrue())
            .help("Set log level to 'warning'.");

        parser
            .addArgument("-vv")
            .type(Boolean.class)
            .setDefault(false)
            .action(Arguments.storeTrue())
            .help("Set log level to 'info'.");
        parser
            .addArgument("-vvv")
            .type(Boolean.class)
            .setDefault(false)
            .action(Arguments.storeTrue())
            .help("Set log level to 'debug'.");

        parser
            .addArgument("--legacy-s3-output")
            .type(Boolean.class)
            .setDefault(false)
            .action(Arguments.storeTrue())
            .help("Output object storage results as 's3' instead of 'objectstorage'.");

        parser
            .addArgument("--list-ips")
            .type(Boolean.class)
            .setDefault(false)
            .action(Arguments.storeTrue())
            .help("Lists all IP addresses in all cloud accounts instead of scanning for rule violations.");

        parser
            .addArgument("FILE")
            .type(String.class)
            .help("Configuration file to read.");

        Namespace ns;
        try {
            ns = parser.parseArgs(argv);

            if (ns.getBoolean("compliant") && ns.getBoolean("noncompliant")) {
                throw new ArgumentParserException(
                    "Conflicting options: --compliant and --noncompliant",
                    parser
                );
            }
        } catch (ArgumentParserException e) {
            System.err.println("Error: " + e.getMessage());
            parser.printHelp();
            System.exit(1);
            return;
        }

        String file = ns.getString("FILE");

        if (ns.getBoolean("vvv")) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        } else if (ns.getBoolean("vv")) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        } else if (ns.getBoolean("vvv")) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        } else {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");
        }

        ScannerCore scannerCore = new ScannerCore(Arrays.asList(
            new YamlPlugin(file),
            new ObjectStoragePlugin(),
            new FirewallPlugin(),
            new AWSPlugin(),
            new AzurePlugin(),
            new ExoscalePlugin()
        ));

        //todo refactor this
        if (ns.getBoolean("list_ips")) {
            scannerCore
                .listIps()
                .forEach(
                    ip ->
                        System.out.println(
                            ip.connectionName + "\t" +
                                ip.resourceRegion + "\t" +
                                ip.ipAddress.toString() + "\t" +
                                String.join(",", ip.instanceIds)));
        } else {
            List<RuleResult> results = scannerCore.scan();

            if (ns.getBoolean("legacy_s3_output")) {
                results = results
                    .stream()
                    .map(result -> !result.resourceType.equalsIgnoreCase("objectstorage") ? result : new RuleResult(
                        result.connectionName,
                        "s3",
                        result.resourceRegion,
                        result.resourceName,
                        result.compliancy,
                        result.violations
                    ))
                    .collect(Collectors.toList());
            }

            List<RuleResult> finalResults;
            if (ns.getBoolean("compliant")) {
                finalResults = results
                    .stream()
                    .filter(result -> result.compliancy == RuleResult.Compliancy.COMPLIANT)
                    .collect(Collectors.toList());
            } else if (ns.getBoolean("noncompliant")) {
                finalResults = results
                    .stream()
                    .filter(result -> result.compliancy == RuleResult.Compliancy.NONCOMPLIANT)
                    .collect(Collectors.toList());
            } else {
                finalResults = results;
            }

            //noinspection OptionalGetWithoutIsPresent
            System.out.println(outputFormatters
                .stream()
                .filter(of -> of.getType().equalsIgnoreCase(ns.getString("format")))
                .findFirst()
                .get()
                .format(finalResults));
        }
    }

    public static void main(String[] argv) {
        CLIApplication app = new CLIApplication(
                Collections.singletonList(
                        new TextOutputFormatter()
                )
        );

        app.run(argv);
    }
}
