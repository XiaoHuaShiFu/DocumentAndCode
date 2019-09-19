#!/bin/sh
# -----------------------------------------------------------------------------
# Script to run the Jasper "offline JSP compiler"
#
# $Id: jspc.sh,v 1.4 2003/02/21 18:23:14 kinman Exp $
# -----------------------------------------------------------------------------

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
 
PRGDIR=`dirname "$PRG"`
EXECUTABLE=jasper.sh

# Check that target executable exists
if [ ! -x "$PRGDIR"/"$EXECUTABLE" ]; then
  echo "Cannot find $PRGDIR/$EXECUTABLE"
  echo "This file is needed to run this program"
  exit 1
fi

if [ "$1" = "debug" ]; then
  shift
  exec "$PRGDIR"/"$EXECUTABLE" debug "$@"
else
  exec "$PRGDIR"/"$EXECUTABLE" jspc "$@"
fi
