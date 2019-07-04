resource "aws_s3_bucket" "compliant-bucket" {
  bucket = "${var.prefix}-compliant-bucket"
}

resource "aws_s3_bucket" "noncompliant-file" {
  bucket = "${var.prefix}-noncompliant-file"
}

resource "aws_s3_bucket_object" "noncompliant-file" {
  bucket = aws_s3_bucket_object.noncompliant-file.bucket
  key = "test.txt"
  acl = "public-read"
  content = "Hello world!"
}

resource "aws_s3_bucket" "noncompliant-bucket" {
  bucket = "${var.prefix}-noncompliant-bucket"
  acl = "public-read"
}
