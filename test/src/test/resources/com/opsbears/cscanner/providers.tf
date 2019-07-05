module "aws" {
  source = "./aws"
  aws_access_key_id = var.aws_access_key_id
  aws_secret_access_key = var.aws_secret_access_key
  prefix = var.prefix
}

module "azure" {
<<<<<<< HEAD
  source = "..\/..\/..\/io\/cscanner\/core\/provider\/azure"
  arm_client_id = "${var.arm_client_id}"
  arm_client_secret = "${var.arm_client_secret}"
  arm_subscription_id = "${var.arm_subscription_id}"
  arm_tenant_id = "${var.arm_tenant_id}"
  prefix = "${var.prefix}"
=======
  source = "./azure"
  arm_client_id = var.arm_client_id
  arm_client_secret = var.arm_client_secret
  arm_subscription_id = var.arm_subscription_id
  arm_tenant_id = var.arm_tenant_id
  prefix = var.prefix
>>>>>>> a57a28e509d66f16a62523fb1b21fabe8753ca00
}

module "digitalocean" {
 source = "./digitalocean"
  digitalocean_spaces_key = var.digitalocean_spaces_key
  digitalocean_spaces_secret = var.digitalocean_spaces_secret
  digitalocean_token = var.digitalocean_token
  prefix = var.prefix
}

module "exoscale" {
  source = "./exoscale"
  exoscale_key = var.exoscale_key
  exoscale_secret = var.exoscale_secret
  prefix = var.prefix
}
