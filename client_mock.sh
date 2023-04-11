./gradlew :client:build -x test
./gradlew :client:run --args="127.0.0.1 mock" &
./gradlew :client:run --args="127.0.0.1 mock" &