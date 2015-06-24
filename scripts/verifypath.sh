#!/bin/bash

# ---------------------------------------------------------------------------------
# Set CLASSPATH for MoGrid
#
# Environment Variable Prequisites
#
#   MOGRID_HOME   Must point at your MoGrid directory installation.
#   JAVA_HOME     Must point at your Java Runtime Environment (JRE) installation.
#
# Id: verifypath.sh, v 3.0 2006/02/16 11:57
# ----------------------------------------------------------------------------------

clear
echo
echo Verifyng MoGrid environments variables
echo
echo

# *** Set MoGrid environment variables (change ONLY lines 21 and 22) ***
export JAVA_HOME=/usr/lib/jvm/jre
export MOGRID_HOME=/usr/MoGrid


# Make sure prerequisite environment variables are set

# Verifying JAVA_HOME
if [ -z "$JAVA_HOME" ]; then
   echo "The JAVA_HOME environment variable is not defined"
   echo "This environment variable is needed to run this program"
   echo
   exit 1
fi
if [ ! -r "$JAVA_HOME"/bin/java ]; then
   echo "The JAVA_HOME environment variable is not defined correctly"
   echo "This environment variable is needed to run this program"
   echo "NB: JAVA_HOME should point to a JRE not a JDK"
   echo
   exit 1
fi


echo "Using JAVA_HOME   :  $JAVA_HOME"
echo "Using MOGRID_HOME :  $MOGRID_HOME"
echo


# Verifying CSV_HOME
if [ -z "$MOGRID_HOME" ]; then
   echo "The MOGRID_HOME environment variable is not defined"
   echo "This environment variable is needed to run this program"
   echo
   exit 1
fi
if [ ! -r "$MOGRID_HOME"/bin/mogrid.jar -o ! -r "$MOGRID_HOME"/bin/monitor ]; then
   echo "The file $MOGRID_HOME/bin/mogrid.jar does not exist"
   echo "This file is needed to run this program"
   echo
   echo "The MOGRID_HOME environment variable can be not defined correctly"
   echo
   exit 1
fi


# Set BASEDIR
export BASEDIR="$MOGRID_HOME"

# Set MoGrid Monitor Service environment variable
export MONITOR_HOME="$MOGRID_HOME"/bin

# Set MoGrid standard environment variables
export MOGRID_LIB="$MOGRID_HOME"/lib
export MOGRID_BIN="$MOGRID_HOME"/bin
export MOGRID_CONF="$MOGRID_HOME"/conf

# Set MoGrid execution environment variable
export JAVA_EXE=$JAVA_HOME/bin/java


