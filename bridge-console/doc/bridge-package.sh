#!/bin/bash

# 注意不要挪动这个打包脚本的位置!!!

cd ../src/main/resources
rm -rf static
cd ../webapp/bridge
rm -rf dist
npm install
npm run build
cp -r dist ../../resources/static
cd ../../../../../
mvn clean package





