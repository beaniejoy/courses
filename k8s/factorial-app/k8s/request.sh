#!/bin/bash

while true; do
	count=$((RANDOM % 40 + 10))

	for ((i=0; i<count; i++)); do
	curl "http://localhost/math/factorial?n=1" &
	done
	sleep 1
done
