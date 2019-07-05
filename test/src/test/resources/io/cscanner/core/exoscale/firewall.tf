//region Compliant
resource "exoscale_security_group" "compliant" {
  name = "${var.prefix}-compliant"
}

resource "exoscale_security_group_rules" "compliant" {
  security_group = exoscale_security_group.compliant.name

  ingress {
    protocol = "icmpv6"
    icmp_type = 128
    icmp_code = 0
    cidr_list = ["::/0"]
  }
}
//endregion

//region Noncompliant
resource "exoscale_security_group" "noncompliant" {
  name = "${var.prefix}-noncompliant"
}
resource "exoscale_security_group_rules" "noncompliant" {
  security_group = exoscale_security_group.noncompliant.name

  ingress {
    protocol = "tcp"
    ports = "22"
    cidr_list = ["0.0.0.0/0"]
  }
}
//endregion

//region Protocol-all
resource "exoscale_security_group" "protocol-all" {
  name = "${var.prefix}-protocol-all"
}
resource "exoscale_security_group_rules" "protocol-all" {
  security_group = exoscale_security_group.protocol-all.name

  ingress {
    protocol = "all"
    ports = "22"
    cidr_list = ["0.0.0.0/0"]
  }
}
//endregion