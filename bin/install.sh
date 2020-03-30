#!/bin/sh

project_root_dir=$(cd ./.. && pwd)
frontend_dir="${project_root_dir}"/frontend

echo "Build frontend"
cd "${frontend_dir}" || exit
echo "Run npm ci"
npm ci
echo "Run npm run build"
npm run build
echo "Move built files"
mv "${frontend_dir}"/build/* "${project_root_dir}"/src/main/resources/WEB/

echo "Build backend"
cd "${project_root_dir}" || exit
sbt clean assembly
mv "${project_root_dir}"/target/scala-2.12/apikeeper.jar "${project_root_dir}"/apikeeper.jar
echo "Success"
