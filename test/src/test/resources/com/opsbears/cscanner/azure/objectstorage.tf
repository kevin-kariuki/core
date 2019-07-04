resource "azurerm_storage_account" "objectstorage" {
  name = "${var.prefix}-objectstorage"
  account_replication_type = "GRS"
  account_tier = "Standard"
  location = azurerm_resource_group.test.location
  resource_group_name = azurerm_resource_group.test.name
}

resource "azurerm_storage_container" "compliant-bucket" {
  name = "${var.prefix}-compliant-bucket"
  resource_group_name = azurerm_resource_group.test.name
  storage_account_name = azurerm_storage_account.objectstorage.name
}

