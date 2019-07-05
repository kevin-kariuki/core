resource "aws_s3_bucket" "objectstorage-secure" {
  bucket = "${var.prefix}-secure"
}

resource "aws_s3_bucket" "objectstorage-open" {
  bucket = "${var.prefix}-open"
  acl = "public-read"
}

resource "aws_s3_bucket" "objectstorage-file" {
  bucket = "${var.prefix}-open"
}

resource "aws_s3_bucket_object" "objectstorage-file" {
  bucket = "${aws_s3_bucket_object.objectstorage-file.bucket}"
  key = "test.txt"
  acl = "public-read"
  content = "Hello world!"
}
