package com.opsbears.cscanner;

import com.opsbears.cscanner.aws.AWSConfiguration;
import com.opsbears.cscanner.azure.AzureConfiguration;
import com.opsbears.cscanner.digitalocean.DigitalOceanConfiguration;
import com.opsbears.cscanner.exoscale.ExoscaleConfiguration;
import com.opsbears.cscanner.terraform.TerraformRunner;
import org.testng.annotations.BeforeSuite;

import java.nio.file.Paths;

public abstract class IntegrationTest {
    private TerraformRunner createTerraformRunner() {
        return new TerraformRunner(
                Paths.get(this.getClass().getResource("variables.tf").getPath()).getParent().toString(),
                "cscanner-" + IntegrationTest.class.getPackage().getImplementationVersion(),
                System.getenv("AWS_ACCESS_KEY_ID"),
                System.getenv("AWS_SECRET_ACCESS_KEY"),
                System.getenv("ARM_CLIENT_ID"),
                System.getenv("ARM_SUBSCRIPTION_ID"),
                System.getenv("ARM_TENANT_ID"),
                System.getenv("ARM_CLIENT_SECRET"),
                System.getenv("DIGITALOCEAN_TOKEN"),
                System.getenv("DIGITALOCEAN_SPACES_KEY"),
                System.getenv("DIGITALOCEAN_SPACES_SECRET"),
                System.getenv("EXOSCALE_KEY"),
                System.getenv("EXOSCALE_SECRET")
        );
    }

    protected ExoscaleConfiguration getExoscaleConfiguration() {
        return new ExoscaleConfiguration(
                System.getenv("EXOSCALE_KEY"),
                System.getenv("EXOSCALE_SECRET"),
                null,
                null
        );
    }

    protected AWSConfiguration getAWSConfiguration() {
        return new AWSConfiguration(
                System.getenv("AWS_ACCESS_KEY_ID"),
                System.getenv("AWS_SECRET_ACCESS_KEY"),
                null,
                null
        );
    }

    protected AzureConfiguration getAzureConfiguration() {
        return new AzureConfiguration(
                System.getenv("ARM_SUBSCRIPTION_ID"),
                System.getenv("ARM_TENANT_ID"),
                System.getenv("ARM_CLIENT_ID"),
                System.getenv("ARM_CLIENT_SECRET"),
                null,
                null
        );
    }

    private DigitalOceanConfiguration getDigitalOceanConfiguration() {
        return new DigitalOceanConfiguration(
                System.getenv("DIGITALOCEAN_TOKEN"),
                System.getenv("DIGITALOCEAN_SPACES_KEY"),
                System.getenv("DIGITALOCEAN_SPACES_SECRET")
        );
    }

    @BeforeSuite
    public void up() {
        TerraformRunner terraform = createTerraformRunner();
        terraform.init();
        terraform.apply();
    }

    @BeforeSuite
    public void down() {
        TerraformRunner terraform = createTerraformRunner();
        terraform.destroy();
    }
}
