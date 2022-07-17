#!/bin/bash
# The directory this project is in
project_dir=$1
container_name=$2

docker run \
--rm \
--detach \
--name "$container_name" \
--publish 80:8080 \
--volume /mnt/user/Kuri/Development/"$project_dir"/target/Kurumi.war:/"$project_dir"/target/Kurumi.war \
payara/micro:5.2022.2-jdk11 \
--deploy /"$project_dir"/target/Kurumi.war \
--nocluster
# Just for convenience, exit with ctrl+c
docker logs --follow "$container_name"
