#!/usr/bin/env bash
export IP_ADDRESS=`ifconfig eth0| sed -En 's/127.0.0.1//;s/.*inet (addr:)?(([0-9]*\.){3}[0-9]*).*/\2/p'`
export HOSTNAME=`hostname`

echo "===== Detecting IP & Hostname ====="
echo "IP Address: $IP_ADDRESS"
echo "Hostname: $HOSTNAME"

echo "------ JAVA_XMX_OPTION ------"
XMX_OPTION=-Xmx${JAVA_XMX_SIZE:-2048}m
echo "Java xms option: $XMX_OPTION"

echo "===== Starting Service ====="

#Seccyrity by Deterministic Random Bit Generator (DRBG)
java -jar $XMX_OPTION -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${APPLICATION_PROFILE:-default} /app/service.jar
