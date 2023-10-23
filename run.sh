#!/usr/bin/env bash

# Remove MongoDB container
docker rm mongodb
# Run MongoDB instance
docker run --name mongodb -d -p 27017:27017 mongodb/mongodb-community-server:6.0-ubi8
