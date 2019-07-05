//region Compliant
resource "aws_security_group" "compliant" {
  name = "${var.prefix}-compliant"
}

resource "aws_security_group_rule" "compliant-icmp" {
  security_group_id = aws_security_group.compliant.id
  protocol = "icmp"
  from_port = 128
  to_port = 0
  type = "ingress"
  cidr_blocks = [
    "::/0"
  ]
}
//endregion

//region Noncompliant
resource "aws_security_group" "noncompliant" {
  name = "${var.prefix}-noncompliant"
}

resource "aws_security_group_rule" "noncompliant-ssh" {
  security_group_id = aws_security_group.noncompliant.id
  protocol = "tcp"
  from_port = 22
  to_port = 22
  type = "ingress"
  cidr_blocks = [
    "0.0.0.0/0"
  ]
}
//endregion

//region Protocol-all
resource "aws_security_group" "protocol-all" {
  name = "${var.prefix}-protocol-all"
}

resource "aws_security_group_rule" "protocol-all" {
  security_group_id = aws_security_group.protocol-all.id
  protocol = "all"
  from_port = 22
  to_port = 22
  type = "ingress"
  cidr_blocks = [
    "0.0.0.0/0"
  ]
}
//endregion