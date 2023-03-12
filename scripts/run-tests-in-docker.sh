#!/bin/bash
mkdir -m 777 coverage
mkdir -m 777 coverage/client
mkdir -m 777 coverage/server
docker run --rm -v `pwd`/coverage:coverage-out  citest scripts/test.sh
