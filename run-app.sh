#!/bin/bash

# Ensure the jar file exists
if [ ! -f target/Millesime-0.0.1-SNAPSHOT.jar ]; then
    echo "JAR file not found!"
    exit 1
fi

# Run the application with prod profile
exec java -Dspring.profiles.active=prod \
          -Dserver.port=${PORT:-8081} \
          -jar target/Millesime-0.0.1-SNAPSHOT.jar
