#!/bin/bash
mkdir coverage
pwd
docker run --rm -v `pwd`/coverage:/coverage-out  citest scripts/test.sh
