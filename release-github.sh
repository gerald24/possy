#!/usr/bin/env bash

USER=$1
TOKEN=$2
TAG=$3

res=$(curl --user "$USER:$TOKEN" -X POST "https://api.github.com/repos/gerald24/possy/releases" \
-d "
{
  \"tag_name\": \"$TAG\",
  \"name\": \"Possy $TAG\",
  \"body\": \"Release of Possy $TAG\",
  \"draft\": false,
  \"prerelease\": false
}")

echo "Create release result: $res"
rel_id=$(echo "$res" | python -c 'import json,sys;print(json.load(sys.stdin)["id"])')

daemon_file_name="daemon-${TAG:1}.jar"
daemon_file="daemon/target/${daemon_file_name}"

curl \
  --user "$USER:$TOKEN" \
  -X POST "https://uploads.github.com/repos/gerald24/possy/releases/${rel_id}/assets?name=${daemon_file_name}" \
  --header "Content-Type: text/javascript" \
  --upload-file "$daemon_file"

service_file_name="service-${TAG:1}.jar"
service_file="service/target/${service_file_name}"

curl \
  --user "$USER:$TOKEN" \
  -X POST "https://uploads.github.com/repos/gerald24/possy/releases/${rel_id}/assets?name=${service_file_name}" \
  --header "Content-Type: text/javascript" \
  --upload-file "$service_file"
