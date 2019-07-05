resource "azurerm_network_security_group" "firewall-closed" {
  name     = "${var.prefix}-firewall-closed"
  location = azurerm_resource_group.test.location
  resource_group_name = azurerm_resource_group.test.name
}

resource "azurerm_network_security_group" "firewall-open" {
  name     = "${var.prefix}-firewall-open"
  location = azurerm_resource_group.test.location
  resource_group_name = azurerm_resource_group.test.name
}

resource "azurerm_network_security_rule" "firewall-open" {
  name                        = "${var.prefix}-firewall-open-ssh"
  priority                    = 100
  direction                   = "Inbound"
  access                      = "Allow"
  protocol                    = "Tcp"
  source_port_range           = "*"
  destination_port_range      = "22"
  source_address_prefix       = "*"
  destination_address_prefix  = "*"
  resource_group_name         = azurerm_resource_group.test.name
  network_security_group_name = azurerm_network_security_group.firewall-open.name
}

