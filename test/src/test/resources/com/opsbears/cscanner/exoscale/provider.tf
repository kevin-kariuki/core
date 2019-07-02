provider "exoscale" {
  key = "${var.exoscale_key}"
  secret = "${var.exoscale_secret}"
}

provider "aws" {
  region = "at-vie-1"
  access_key = "${var.exoscale_key}"
  secret_key = "${var.exoscale_secret}"
  skip_credentials_validation = true
  skip_region_validation = true
  skip_get_ec2_platforms = true
  skip_metadata_api_check = true
  skip_requesting_account_id = true
  endpoints {
    s3 = "https://sos-at-vie-1.exo.io"
    s3control = "https://sos-at-vie-1.exo.io"
  }
}
