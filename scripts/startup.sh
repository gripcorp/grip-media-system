#!/bin/sh

DEPLOY_DIR=/home/grip/deploy
timestamp=`date +%Y%m%d%H%M%S`

exec java -javaagent:/home/grip/datadog/dd-java-agent.jar \
    -jar \
    -XX:InitialRAMPercentage=50.0 \
    -XX:MaxRAMPercentage=70.0 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/home/grip/logs/$1.heapdump.$timestamp \
    $DEPLOY_DIR/$1
