package io.cscanner.core.test.terraform;

import org.testng.log4testng.Logger;

import java.io.*;

public class TerraformRunner {
    private final String terraformDirectory;
    private final String prefix;
    private final String awsAccessKeyId;
    private final String awsSecretAccessKey;
    private final String armClientId;
    private final String armSubscriptionId;
    private final String armTenantId;
    private final String armClientSecret;
    private final String digitaloceanToken;
    private final String digitaloceanSpacesKey;
    private final String digitaloceanSpacesSecret;
    private final String exoscaleKey;
    private final String exoscaleSecret;

    public TerraformRunner(String terraformDirectory, String prefix, String awsAccessKeyId, String awsSecretAccessKey, String armClientId, String armSubscriptionId, String armTenantId, String armClientSecret, String digitaloceanToken, String digitaloceanSpacesKey, String digitaloceanSpacesSecret, String exoscaleKey, String exoscaleSecret) {
        this.terraformDirectory = terraformDirectory;
        this.prefix = prefix;
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretAccessKey = awsSecretAccessKey;
        this.armClientId = armClientId;
        this.armSubscriptionId = armSubscriptionId;
        this.armTenantId = armTenantId;
        this.armClientSecret = armClientSecret;
        this.digitaloceanToken = digitaloceanToken;
        this.digitaloceanSpacesKey = digitaloceanSpacesKey;
        this.digitaloceanSpacesSecret = digitaloceanSpacesSecret;
        this.exoscaleKey = exoscaleKey;
        this.exoscaleSecret = exoscaleSecret;
    }

    private void execute(String[] argv) {
        ProcessBuilder processBuilder = new ProcessBuilder(argv);
        processBuilder.directory(new File(terraformDirectory));
        Process process = null;
        Logger logger = Logger.getLogger(this.getClass());
        try {
            logger.info("Executing external task: " + String.join(" ", processBuilder.command()));
            process = processBuilder.start();
            InputStream stdOut               = process.getInputStream();
            InputStream       stdErr               = process.getErrorStream();
            InputStreamReader stdOutReader         = new InputStreamReader(stdOut);
            InputStreamReader stdErrReader         = new InputStreamReader(stdErr);
            BufferedReader stdOutBufferedReader = new BufferedReader(stdOutReader);
            BufferedReader    stdErrBufferedReader = new BufferedReader(stdErrReader);
            String            line;
            StringBuilder     buffer               = new StringBuilder();
            do {
                if ((line = stdOutBufferedReader.readLine()) != null) {
                    buffer.append(line).append("\n");
                    logger.info(line);
                }
                if ((line = stdErrBufferedReader.readLine()) != null) {
                    buffer.append(line).append("\n");
                    logger.error(line);
                }
            } while (process.isAlive());
            boolean finishedStd = false;
            boolean finishedErr = false;
            while (!finishedStd || !finishedErr) {
                if ((line = stdOutBufferedReader.readLine()) != null) {
                    buffer.append(line).append("\n");
                    logger.info(line);
                } else {
                    finishedStd = true;
                }
                if ((line = stdErrBufferedReader.readLine()) != null) {
                    buffer.append(line).append("\n");
                    logger.error(line);
                } else {
                    finishedErr = true;
                }
            }
            if (process.exitValue() != 0) {
                throw new RuntimeException("External program execution failed, exit value is non-zero. Tried to run: " + processBuilder.command() + " Program output:\n\n" + buffer);
            }
        } catch (IOException e) {
            logger.error("Execution failed: " + e.getMessage());
            if (process != null && process.isAlive()) {
                process.destroy();
            }
            throw new RuntimeException(e);
        }
    }

    public void init() {
        String[] argv = new String[]{
            "terraform",
            "init",
            "-backend-config=\"access_key=" + awsAccessKeyId + "\"",
            "-backend-config=\"secret_key=" + awsSecretAccessKey + "\"",
        };
        execute(argv);
    }

    public void apply() {
        String[] argv = new String[]{
                "terraform",
                "apply",
                "-auto-approve",
                "-var",
                "prefix=" + prefix,
                "-var",
                "aws_access_key_id=" + awsAccessKeyId,
                "-var",
                "aws_secret_access_key=" + awsSecretAccessKey,
                "-var",
                "arm_client_id=" + armClientId,
                "-var",
                "arm_subscription_id=" + armSubscriptionId,
                "-var",
                "arm_tenant_id=" + armTenantId,
                "-var",
                "arm_client_secret=" + armClientSecret,
                "-var",
                "digitalocean_token=" + digitaloceanToken,
                "-var",
                "digitalocean_spaces_key=" + digitaloceanSpacesKey,
                "-var",
                "digitalocean_spaces_secret=" + digitaloceanSpacesSecret,
                "-var",
                "exoscale_key=" + exoscaleKey,
                "-var",
                "exoscale_secret=" + exoscaleSecret,
        };
        execute(argv);
    }

    public void destroy() {
        String[] argv = new String[]{
                "terraform",
                "destroy",
                "-auto-approve",
                "-var",
                "prefix=" + prefix,
                "-var",
                "aws_access_key_id=" + awsAccessKeyId,
                "-var",
                "aws_secret_access_key=" + awsSecretAccessKey,
                "-var",
                "arm_client_id=" + armClientId,
                "-var",
                "arm_subscription_id=" + armSubscriptionId,
                "-var",
                "arm_tenant_id=" + armTenantId,
                "-var",
                "arm_client_secret=" + armClientSecret,
                "-var",
                "digitalocean_token=" + digitaloceanToken,
                "-var",
                "digitalocean_spaces_key=" + digitaloceanSpacesKey,
                "-var",
                "digitalocean_spaces_secret=" + digitaloceanSpacesSecret,
                "-var",
                "exoscale_key=" + exoscaleKey,
                "-var",
                "exoscale_secret=" + exoscaleSecret,
        };
        execute(argv);
    }
}
