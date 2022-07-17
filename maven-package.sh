#!/bin/bash
# The directory this project is in
project_dir=$1
container_name=maven-clean-package

# Stay attached to provide output for calling script
docker run \
--rm \
--name $container_name \
--volume maven-repo:/root/.m2 \
--volume /mnt/user/Kuri/Development/"$project_dir":/root/"$project_dir" \
--workdir /root/"$project_dir" \
maven:3.8.6-openjdk-11 \
mvn clean package -DoutputDirectory=/root/"$project_dir"/target
