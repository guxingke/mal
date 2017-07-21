#!/bin/bash

set -x
cp -R $1 $2;
pushd $2
mv $1.java $2.java;
gsed -i "s/$1/$2/g" `ls ` 
