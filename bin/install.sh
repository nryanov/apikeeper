#!/bin/sh

echo "Build frontend"
cd ./../frontend || exit
echo "Run npm ci"
#npm ci
echo "Run npm run build"
#npm run build
cd ./.. || exit
echo "Build backend"
sbt clean assembly