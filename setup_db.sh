#!/bin/bash
/cockroach/cockroach.sh start --insecure --join=db_1,db_2,db_3
sleep 30
/cockroach/cockroach.sh init --insecure
sleep 10
SQL="/cockroach/cockroach.sh sql --insecure"
$SQL -e "CREATE DATABASE movies;"