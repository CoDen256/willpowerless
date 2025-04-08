#!/usr/bin/env sh

# set long current password and hide it

pass="abc"
echo -e "$pass\n$pass" | passwd "root"
echo "$pass" | sha256sum | tee hash
