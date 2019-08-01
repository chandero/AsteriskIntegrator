#!/bin/sh

THISDIR=$(cd "$(dirname "$0")"; pwd)
cd $THISDIR

JAVACLASS=spasteriskconnector.SPAsteriskConnector
JAVACP=../source/classes:"../libs/*"

# Kill old process
JAVAPID=$(ps ax | grep $JAVACLASS | grep -v grep | head -1 | awk '{print $1}')
if [ "$JAVAPID" != "" ]
then
    kill -15 $JAVAPID
fi

if [ "$1" != "stop" ]
then
    java -cp $JAVACP $JAVACLASS
fi
