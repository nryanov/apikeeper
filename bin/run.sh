#!/bin/sh

project_root_dir=$(cd ./.. && pwd)
echo "Run apikeeper.jar"
java -jar "${project_root_dir}"/apikeeper.jar