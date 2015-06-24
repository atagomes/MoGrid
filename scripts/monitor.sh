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
echo "STARTing MoCA Monitor Service"
echo

# STARTing MoCA Monitor Service
# Args: monitoring (scanning) interval in seconds, MoGrid listener IP address, MoGrid listener port
exec $MONITOR_HOME/monitor 10 127.0.0.1 50000
# OR
#cd $MONITOR_HOME
#./monitor 10 146.134.201.202 50000

# For old DOS remove the set variables from ENV - we assume they were not set
# before we started - at least we don't leave any baggage around
export MONITOR_HOME=


