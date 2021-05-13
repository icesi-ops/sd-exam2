#!/bin/bash
echo "start"
/cockroach/cockroach.sh start --insecure --join=db_1,db_2,db_3 &
echo "sleep 30 seconds"
sleep 30
echo "init"
/cockroach/cockroach.sh init --insecure
sleep 10
SQL="/cockroach/cockroach.sh sql --insecure"
$SQL -e "CREATE DATABASE movies;"
while true
do
  sleep 1d
done