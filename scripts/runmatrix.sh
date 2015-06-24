#!/bin/bash

# Verifying VERIFYPATH.SH
if [ ! -r verifypath.sh ]; then
   echo "The verifypath.sh archive does not exist, it is not possible verify environment variables"
   echo
   exit 1
fi

# Execute VERIFYPATH.SH
command source "verifypath.sh"

echo
echo "STARTing RunMatrix Application"
echo


# Clean Initiator work dirs
rm -Rf $MOGRID_HOME/gridjob/*
rm -Rf $MOGRID_HOME/MatrixFiles/*.properties

# STARTing RunMatrix Application
# Script Usage:
# ./runmatrix.sh 
#
$JAVA_EXE -Dlog4j.configuration=$MOGRID_CONF/MoGridLog4j.xml -Dmogrid.home=$MOGRID_HOME -classpath $MOGRID_BIN/mogrid.jar\:$MOGRID_LIB/log4j.jar\:$MOGRID_CONF\:$MOGRID_CONF/DiscoveryProtocol.properties martin.app.bagoftask.matrix.RunMatrix


# For old DOS remove the set variables from ENV - we assume they were not set
# before we started - at least we don't leave any baggage around
export JAVA_HOME=
export JAVA_EXE=
export MOGRID_HOME=
export MOGRID_LIB=
export MOGRID_BIN=
export MOGRID_CONF=

