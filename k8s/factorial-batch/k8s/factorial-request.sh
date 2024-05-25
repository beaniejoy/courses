#!/bin/bash

n=2000
while true; do
    n=$((n+1))
    echo "$n!"
    curl "http://localhost/math/factorial/$n?key=abcd-1234-efgh"
    echo
done