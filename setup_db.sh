#!/bin/bash
echo Wait for servers to be up
sleep 30

HOSTPARAMS="--host db_1 --insecure"
SQL="/cockroach/cockroach.sh sql $HOSTPARAMS"

$SQL -e "CREATE DATABASE movies;"
$SQL -e "CREATE USER movie_user WITH PASSWORD password;"
$SQL -e "GRANT ALL ON DATABASE movies TO movie_user"