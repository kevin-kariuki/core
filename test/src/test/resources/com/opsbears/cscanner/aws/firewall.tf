resource "aws_vpc" "firewall" {
  cidr_block = "10.0.0.0/16"
  tags {
    Name = "${var.prefix}-aws-firewall"
  }
}

resource "aws_subnet" "firewall" {
  cidr_block = "10.0.0.0/24"
  vpc_id = "${aws_vpc.firewall.id}"
  tags {
    Name = "${var.prefix}-aws-firewall"
  }
}

resource "aws_security_group" "firewall" {
  name = "${var.prefix}-aws-firewall"
}
