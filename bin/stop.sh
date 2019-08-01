#!/bin/sh
THISDIR=$(cd "$(dirname "$0")"; pwd)
cd $THISDIR

sh ${THISDIR}/webapp.sh stop
