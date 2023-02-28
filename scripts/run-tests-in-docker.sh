#!/bin/bash
mkdir -m 777 coverage
docker run --rm -v `pwd`/coverage:/coverage-out  citest scripts/test.sh
