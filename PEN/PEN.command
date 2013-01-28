#!/bin/sh

cd `dirname $0`
java -jar -Djava.library.path=./lib64 PEN.jar &
