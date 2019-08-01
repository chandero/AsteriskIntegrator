#!/bin/bash
THISDIR=$(cd "$(dirname "$0")"; pwd)
cd $THISDIR

date="$(date +'%Y%m%d-%H:%M')"

nohup "${THISDIR}/webapp.sh" > ../logs/nohup.${date}.out 2>&1 &

