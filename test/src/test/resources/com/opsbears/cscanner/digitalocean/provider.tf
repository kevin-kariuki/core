provider "digitalocean" {
  token = "${var.digitalocean_token}"
}

provider "aws" {
  region = "ams3"
  access_key = "${var.digitalocean_spaces_key}"
  secret_key = "${var.digitalocean_spaces_secret}"
  skip_credentials_validation = true
  skip_region_validation = true
  skip_get_ec2_platforms = true
  skip_metadata_api_check = true
  skip_requesting_account_id = true
  endpoints {
    s3 = "https://ams3.digitaloceanspaces.com"
    s3control = "https://ams3.digitaloceanspaces.com"
  }
}