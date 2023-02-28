#!/bin/bash
RUN chmod +x gradlew
./gradlew :server:build || exit 1
./gradlew :server:cloverGenerateReport || exit 1
scripts/coverage_summary.sh
mkdir -m 777 coverage-out
chmod +x coverage-out
chmod +x server/build/reports/clover/html/*
cp -r server/build/reports/clover/html/* /coverage-out/ || exit 1

