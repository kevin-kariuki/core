provider "azurerm" {
  client_id = "${var.arm_client_id}"
  subscription_id = "${var.arm_subscription_id}"
  tenant_id = "${var.arm_tenant_id}"
  client_secret = "${var.arm_client_secret}"
}

resource "azurerm_resource_group" "test" {
  name     = "${var.prefix}"
  location = "West US"
}