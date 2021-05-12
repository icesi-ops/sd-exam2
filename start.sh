#!/bin/sh
echo "wait for db startup"
i=0
while [ $i -ne 60 ]; do
    i=$(($i + 5))
    sleep 5
    echo "$i%"
done
echo "Running..."
/app/bin/sd-exam-2
