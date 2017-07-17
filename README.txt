=====INDEX=====
1.Included files
2.How to run
3.Console Commands
4.Notes
5.Class Description

-----------------------------------------------------------------------------------------------------------

1.INCLUDED FILES:

All source files are within the following packages.

cs455/overlay/node/node
cs455/overlay/node/routing
cs455/overlay/node/transport
cs455/overlay/node/util
cs455/overlay/node/wireformats
READ_ME.txt
Makefile

-----------------------------------------------------------------------------------------------------------

2.HOW TO RUN:

      Registry : java cs455.overlay.node.Registry <listenig_portnum>
Messaging node : java cs455.overlay.node.MessagingNode registry-host> <registry-port>

-----------------------------------------------------------------------------------------------------------

3.CONSOLE COMMANDS

list-messaging-nodes

setup-overlay <number-of-routing-table-entries>

list-routing-tables

start number-of-messages

print-counters-and-diagnostics

exit-overlay

-----------------------------------------------------------------------------------------------------------

4.NOTES:

-Registry will handle the case when messaging node goes down suddenly after requesting for registration.
-On providing invalid port number, it will display error message and prompt for port number.
-On entering improper commands, it will display error message.
-'setup-overlay <routing_table_size>' command will determine, whether the <routing_table_size> is applicable to connected nodes. If not, then it will show error message.
  *criterias for 'setup-overlay'
  -<routing_table_size> should not be greater than (log2(messaging-node-count) + 1).
  -Code will calculate <routing_table_size> if not provided.
-Command parser will not allow MessagingNode's commands to be fired from Registry's console and vice-versa.
-Upon executing the 'exit-overlay' command, messaging node requests fo de-registration. 
 Registry will check for IP conflicts and sends appropriate message. On successful request, registry will remove that messaging node and close the socket, receiver's thread.
-Messaging node will notify registry about establishing connection with nodes resides in its routing table.
-Last node completing message sending will notify registry about its task completion, which is indication that all nodes have completed message passing.
-After getting notification about task completion, registry will wait for 20s to request for traffic summary.
-On request of traffic summary, registry will print its summary and reset the counters.
-After printing traffic summary registry will reset its counter so that it can provide meaningful data for future tasks.

-----------------------------------------------------------------------------------------------------------

5.CLASS DESCRIPTION:

***cs455.overlay.node.Registry.java***

-Arguements: 
 <portnum> = defines the port on which the registry accepts incoming connections.
 
-Responsible for managing(registering/deregestering) 'Messaging nodes'.
-It will store and display messaging node details as well as routing tables for same.
-Setting-up an overlay and routing-table for each node.
-Issues command to start the message passing.
-It will not takepart in message passing.
-After completing tasks, messaging nodes inform completion and send their traffic summary and finally registry displays it.

***cs455.overlay.node.MessagingNode.java***
-Arguements:
 <registry-host> = IP address of the machine on which registry is running.
 <registry-port> = defines the port on which the registry accepts incoming connections.

-Connects with registry as well as listen for incoming connections from other nodes.
-Stores routing manifest sent by registry.
-Connects with other nodes which resides in its routing table.
-On instruction from registry, it starts the message passing by randomly choosing destination as well as payload.
-Keeps track of traffic.
-On request of registry, it displays and sends its traffic summary and resets the counters.

***cs455.overlay.util.InteractiveCommandParser.java***

-This is a thread to get input commands from console.
-According to the input console, it will call the appropriate method related to command.
     
***cs455.overlay.wireformats***

-This package contains the wireformats for communication between Registry and MessagingNode and between MessagingNodes also. All of these wireformats implement the 'Event'.
-This package contains a singleton class, 'EventFactory.java' which is used to get 'Events'.
-----------------------------------------------------------------------------------------------------------
