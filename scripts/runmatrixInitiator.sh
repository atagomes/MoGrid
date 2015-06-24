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
echo "STARTing RunMatrix Initiator Application"
echo


# Clean Initiator work dirs
rm -Rf $MOGRID_HOME/gridjob/*
rm -Rf $MOGRID_HOME/MatrixFiles/*.properties

# STARTing RunMatrix Initiator Application
# Script Usage:
# ./runmatrixInitiator.sh <numJobs> <request interval> <javaPath>
# ./runmatrixInitiator.sh 4 30000 /usr/lib/jvm/jdk1.5.0_06/jre/bin
#
# Where <numJobs> indicate the number of jobs to be executed
# Where <request interval> indicate the time in seconds for send requests (IReq messages)
# Where <javaPath> points to java.exe dir
# <javaPath> example: /usr/java/jre/bin
# <javaPath> is optional, it is used for APP run in Collaborator mode too.
$JAVA_EXE -Dlog4j.configuration=$MOGRID_CONF/MoGridLog4j.xml -Dmogrid.home=$MOGRID_HOME -classpath $MOGRID_BIN/mogrid.jar\:$MOGRID_LIB/log4j.jar\:$MOGRID_CONF\:$MOGRID_CONF/DiscoveryProtocol.properties martin.app.bagoftask.matrix.RunMatrixInitiator 8 60000


# For old DOS remove the set variables from ENV - we assume they were not set
# before we started - at least we don't leave any baggage around
export JAVA_HOME=
export JAVA_EXE=
export MOGRID_HOME=
export MOGRID_LIB=
export MOGRID_BIN=
export MOGRID_CONF=

