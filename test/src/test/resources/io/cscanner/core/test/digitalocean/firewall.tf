//region Compliant
resource "digitalocean_firewall" "compliant" {
  name = "${var.prefix}-compliant"
  inbound_rule {
    protocol = "icmp"
    source_addresses = ["::/0"]
  }
}
//endregion

//region Noncompliant
resource "digitalocean_firewall" "noncompliant" {
  name = "${var.prefix}-noncompliant"

  inbound_rule {
    protocol = "tcp"
    port_range = "22"
    source_addresses = ["0.0.0.0/0"]
  }
}
//endregion

//region Protocol-all
resource "digitalocean_firewall" "protocol-all" {
  name = "${var.prefix}-protocol-all"
  inbound_rule {
    protocol = "icmp"
    source_addresses = ["0.0.0.0/0"]
  }
  inbound_rule {
    protocol = "tcp"
    port_range = "22"
    source_addresses = ["0.0.0.0/0"]
  }
  inbound_rule {
    protocol = "udp"
    port_range = "22"
    source_addresses = ["0.0.0.0/0"]
  }
}
//endregion