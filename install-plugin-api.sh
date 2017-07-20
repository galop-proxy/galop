#!/bin/bash

cd $(mktemp -d)

git clone https://github.com/galop-proxy/plugin-api.git
cd plugin-api

mvn clean install
