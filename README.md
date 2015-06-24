# MoGrid
A peer-to-peer resource discovery and submission/sharing service for mobile grids

MoGrid is a Mobile Grid Middleware implementation for Windows XP and Linux that is particularly tailored to ad-hoc networks. It implements the P2PDP (P2P Discovery Protocol), which is aimed at efficiently discovering available resources and services, as well as some proprietary protocols for job submission and file sharing. 

Technical details about MoGrid architecture and implementation can be found in:

LIMA, L.S.; GOMES, A.T.A.; ZIVIANI, A.; ENDLER, M.; SOARES, L.F.G.; SCHULZE, B. 
Peer-to-Peer Resource Discovery in Mobile Grids. 
In: International Workshop on Middleware for Grid Computing (MGC),
2005, Grenoble, França. 

*and*

GOMES, A. T. A. ; ZIVIANI, A. ; LIMA, L. S. ; ENDLER, M. ; CHELIUS, G.
Mitigating Reply Implosions in Query-based Service Discovery Protocols for Mobile Wireless Ad Hoc Networks. 
In: International Conference on AD-HOC Networks & Wireless (AdHoc-NOW), 
2008, Sophia Antipolis, França. 

*MoGrid Copyright (C) 2005-2006 LNCC - PUC-Rio.*

##Technical requirements

MoGrid requires Java SDK 1.3.x or greater (compliance level).

##Installation on Linux

Let $MOGRID_HOME refer to the directory where MoGrid has been downloaded (e.g.: /usr/MoGrid). Change the permissions of some files and directories in $MOGRID_HOME:
> chmod 744 $MOGRID_HOME/scripts/*

> chmod 744 $MOGRID_HOME/bin/monitor

##Customization of configuration files

Modify the following configuration files under $MOGRID_HOME/conf (see Appendix II for more information about these files):

###MoGridLog4j.xml: (If $MOGRID_HOME=/usr/MoGrid, no changes are needed.) 

Either create a symlink from $MOGRID_HOME to /usr/MoGrid,
> ln -s $MOGRID_HOME /usr

*or* 

change value of all occurrences of tag \<param name="File"\> in MoGridLog4j.xml to $MOGRID_HOME:
> \<param name="File" value="$MOGRID_HOME/logs/MobileGrid\<log level\>.log"/\>

###DiscoveryProtocol.properties

The P2PDP protocol can use either broadcast or multicast during the resource/service discovery process.  The default configuration is broadcast, using UDP port 50030.  Examples of broadcast and multicast configurations can be found in:

> $MOGRID_HOME/conf/DiscoveryProtocol.properties.broadcast

> $MOGRID_HOME/conf/DiscoveryProtocol.properties.multicast

##Usage

Some testing scripts are provided in the MoGrid.tar archive ($MOGRID_HOME/scripts/). These scripts start the main modules of MoGrid middleware and run a simple testing application that uses the P2PDP protocol as well as a proprietary job submission protocol for performing distributed matrix multiplication. 

Modify JAVA_HOME and MOGRID_HOME environment variables in file $MOGRID_HOME/scripts/verifypath.sh (lines 21 and 22)

To start test applications:

1. Run Monitor Service ($MOGRID_HOME/scripts/monitor.sh)
2. Run Initiator ($MOGRID_HOME/scripts/runmatrixInitiator.sh) and Collaborator ($MOGRID_HOME/scripts/runmatrixCollaborator.sh) stand-alone *OR* altogether ($MOGRID_HOME/scripts/runmatrixIni-Collab.sh)

If no arguments are given to these scripts, default values are used. See Appendix I for more information about these scripts and their arguments.

##Contact Us

> martinlab@lncc.br

##APPENDIX I. MoGrid script files

The files in $MOGRID_HOME/scripts provide testing scripts with some arguments that can be customized. If not provided, the arguments take default values, which are shown in the following examples.

### $MOGRID_HOME/scripts/monitor.sh

STARTing MoCA Monitor Service

Usage: 
> monitor.sh \<req interval\> \<listener IP Addr\> \<listener port\>

E.g.:
> monitor.sh  10             127.0.0.1          50000        

Where:
> \<request interval\>: time interval (in seconds) to send local context information
> \<listener IP Addr\>: MoGrid listener IP address to send local monitored context information
> \<listener port\>: MoGrid listener port to send local monitored context information

### $MOGRID_HOME/scripts/runmatrixInitiator.sh

STARTing RunMatrix Initiator Application

Usage: 
> runmatrixInitiator.sh \<numJobs\> \<request interval\>

E.g.: 
> runmatrixInitiator.sh  8         60000          

Where:
> \<numJobs\>: number of jobs to be executed
> \<req interval\>: time (in seconds) to send requests (IReq messages)

### $MOGRID_HOME/scripts/runmatrixIni-Collab.sh
STARTing RunMatrix Initiator-Collaborator Application

Usage: 
> runmatrixInitiator.sh \<numJobs\> \<req interval\> \<javaPath\>

E.g.: 
> runmatrixInitiator.sh  8         60000          $JAVA_HOME/bin

Where:
> \<numJobs\>: number of jobs to be executed

> \<req interval\>: time (in seconds) to send requests (IReq messages)

> \<javaPath\>: directory of java.exe (javaPath is optional, used for APP to run in Collaborator mode too.)

### $MOGRID_HOME/scripts/runmatrixCollaborator.sh

STARTing RunMatrix Collaborator Application

Usage: 
> runmatrixCollaborator.sh \<javaPath\>

E.g.: 
> runmatrixCollaborator.sh  $JAVA_HOME/bin

Where:
> \<javaPath\>: directory of java.exe 

##APPENDIX II. Explanation about files in $MOGRID_HOME/conf

### MoGridLog4j.xml

Log4j logging facility configuration file for MoGrid.

### DiscoveryProtocol.properties:

Configuration file for MoGrid P2P Discovery Protocol

Properties:
> \<coordination.address\>: broadcast or multicast address for P2PDP comunnication channel

> \<coordination.port\>:    P2PDP comunnication channel port

> \<coordination.scan\>:    scan interval in milliseconds for P2PDP comunnication channel listen for new messages

###ContextListener.properties:

Context Listener (CL) configuration file. The CL listen to the monitor service for information about free CPU and memory.

Properties:
> \<monitor.port\>: context listener port

> \<monitor.scan\>: scan interval (in milliseconds) to listener for new monitor service info

