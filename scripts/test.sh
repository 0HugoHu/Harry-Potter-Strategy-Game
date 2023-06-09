#!/bin/bash
RUN chmod +x gradlew
./gradlew :server:build || exit 1
./gradlew :server:cloverGenerateReport || exit 1
./gradlew :shared:build || exit 1
./gradlew :shared:cloverGenerateReport || exit 1
./gradlew :client:build || exit 1
./gradlew :client:cloverGenerateReport || exit 1
scripts/coverage_summary.sh
mkdir -m 777 /coverage-out
mkdir -m 777 /coverage-out/client
mkdir -m 777 /coverage-out/server
mkdir -m 777 /coverage-out/shared
chmod +x /coverage-out/client
chmod +x /coverage-out/server
chmod +x /coverage-out/shared
chmod +x server/build/reports/clover/html/*
chmod +x client/build/reports/clover/html/*
chmod +x shared/build/reports/clover/html/*
cp -r server/build/reports/clover/html/* /coverage-out/server/ || exit 1
cp -r client/build/reports/clover/html/* /coverage-out/client/ || exit 1
cp -r shared/build/reports/clover/html/* /coverage-out/shared/ || exit 1
cp index.html /coverage-out/index.html || exit 1
