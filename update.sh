#!/bin/bash
# The directory this project is in
project_dir="kurumi"
container_name="Kurumi"

git pull
# Build
sh maven-package.sh $project_dir
# Stop the currently running container and wait for it to return
docker stop $container_name
sleep 3s
# Run container with updated artifact
sh run.sh project_dir container_name
