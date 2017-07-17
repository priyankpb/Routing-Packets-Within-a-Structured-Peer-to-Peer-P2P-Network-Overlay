/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformat.EventFactory;
import cs455.overlay.wireformat.NodeReportsOverlaySetupStatus;
import cs455.overlay.wireformat.OverlayNodeReportsTaskFinished;
import cs455.overlay.wireformat.OverlayNodeReportsTrafficSummary;
import cs455.overlay.wireformat.OverlayNodeSendsData;
import cs455.overlay.wireformat.OverlayNodeSendsDeregistration;
import cs455.overlay.wireformat.OverlayNodeSendsRegistration;
import cs455.overlay.wireformat.Protocol;
import cs455.overlay.wireformat.RegistryReportsDeregistrationStatus;
import cs455.overlay.wireformat.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformat.RegistryRequestsTaskInitiate;
import cs455.overlay.wireformat.RegistryRequestsTrafficSummary;
import cs455.overlay.wireformat.RegistrySendsNodeManifest;

/**
 *
 * @author YANK
 */
public class MessagingNode extends Thread implements Node {

    public Socket s;
    public ServerSocket mnServerSocket;
    private TCPConnection conn;
    private int nodeID;
    private RoutingTable myRT;
    private int[] nodeArray;
    public Map<Integer, TCPConnection> connectionCache = new TreeMap<Integer, TCPConnection>();
    public int packetsSent;
    public int packetsReceived;
    public int packetsRelayed;
    public long sumValuesSent;
    public long sumValuesReceived;
    private Queue<OverlayNodeSendsData> relayQueue = new LinkedList<OverlayNodeSendsData>();

    public int getID() {
        return this.nodeID;
    }

    public void setID(int nodeID) {
        this.nodeID = nodeID;
    }

    private MessagingNode(String ip, int port) throws Exception {
    	
        s = new Socket(ip, port);
        conn = new TCPConnection(s, this);
        mnServerSocket = new ServerSocket(0);
        InteractiveCommandParser interactiveCommandParser = new InteractiveCommandParser(this);
        Thread commandThread = new Thread(interactiveCommandParser);
        commandThread.start();
        Thread mnThread = new Thread(this);
        mnThread.start();
        EventFactory eventFactory = EventFactory.getInstance();
        OverlayNodeSendsRegistration onsr = (OverlayNodeSendsRegistration) eventFactory.createEvent(Protocol.OVERLAY_NODE_SENDS_REGISTRATION);
        onsr.setIp(InetAddress.getLocalHost().getHostAddress().getBytes());
        onsr.setPort(mnServerSocket.getLocalPort());
        conn.getSender().sendData(onsr.getByte());

        while (true) {
            Socket mnClient = mnServerSocket.accept();
            TCPConnection ClientConn = new TCPConnection(mnClient, this);

        }

    }

    public static void main(String[] args) throws Exception {
        if(args.length!=2){
            System.out.println("Error in arguements");
            return;
        }
        String hostName = args[0];
//        InetAddress ip = InetAddress.getByName(hostName);
        int port = Integer.parseInt(args[1]);
//        InetAddress ip = InetAddress.getLocalHost();
//        int port = 5000;
        MessagingNode mn = new MessagingNode(hostName, port);
    }

    @Override
    public void onEvent(byte[] data, Socket s) throws IOException {
        EventFactory eventFactory = EventFactory.getInstance();
        switch (data[0]) {
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                RegistryReportsRegistrationStatus receivedRegStatus = new RegistryReportsRegistrationStatus(data);
                this.setID(receivedRegStatus.getNodeID());
                System.out.println("My id: " + this.getID());
                System.out.println(receivedRegStatus.getInfo());
                break;

            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                RegistryReportsDeregistrationStatus receivedDeregStatus = new RegistryReportsDeregistrationStatus(data);
                System.out.println(receivedDeregStatus.getInfo());
                break;

            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                RegistrySendsNodeManifest recieveManifest = new RegistrySendsNodeManifest(data);
                myRT = new RoutingTable();

                myRT.setNodeID(nodeID);
                int numberofNodes = recieveManifest.getNumberofNodes();
                RoutingEntry[] reArray = new RoutingEntry[numberofNodes];
                reArray = recieveManifest.getRe();
                int routingTableSize = reArray.length;
                for (int i = 0; i < routingTableSize; i++) {
                    reArray[i].setHopsAway((int) Math.pow(2, i));
                }
                List<RoutingEntry> tempList = Arrays.asList(reArray);
                myRT.setEntries(tempList);
                nodeArray = new int[numberofNodes - 1];
                int[] tempNodeArray = new int[numberofNodes];
                tempNodeArray = recieveManifest.getNodes();

                int i = 0,
                 j = 0;
                for (i = 0; i < numberofNodes; i++) {
                    if (tempNodeArray[i] != nodeID) {
                        nodeArray[j] = tempNodeArray[i];
                        j++;
                    }
                }

                System.out.println("Routing Table Recieved");
                myRT.printRoutingTable();
                setupConnection();

                break;

            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                RegistryRequestsTaskInitiate taskInitiate = new RegistryRequestsTaskInitiate(data);
                int numberOfMessages = taskInitiate.getNumberOfDataPackets();
                 {
                    try {
                        startMessages(numberOfMessages);
                    } catch (Exception ex) {
                        Logger.getLogger(MessagingNode.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;

            case Protocol.OVERLAY_NODE_SENDS_DATA:
                OverlayNodeSendsData receivedData = new OverlayNodeSendsData(data);
                int source = receivedData.getSourceID();
                int destination = receivedData.getDestinationID();
                int payload = receivedData.getPayload();

                if (destination == this.nodeID) {
                    //System.out.println("Here");
                    addToReceived(payload);
                } else {
                    routingAlgo(source, destination, payload);
                    addToRelay();
                }
                break;

            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                RegistryRequestsTrafficSummary summary = new RegistryRequestsTrafficSummary(data);
                OverlayNodeReportsTrafficSummary trafficSummary = (OverlayNodeReportsTrafficSummary) eventFactory.createEvent(Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY);
                trafficSummary.setNodeID(this.nodeID);
                trafficSummary.setPacketsSent(packetsSent);
                trafficSummary.setPacketsRelayed(packetsRelayed);
                trafficSummary.setPayloadSent(sumValuesSent);
                trafficSummary.setPacketsReceived(packetsReceived);
                trafficSummary.setPayloadReceived(sumValuesReceived);

                conn.getSender().sendData(trafficSummary.getByte());
                resetCounters();
                break;
        }
    }

    private void setupConnection() throws IOException {
        for (RoutingEntry routingEntry : myRT.getEntries()) {
            Socket s = new Socket(routingEntry.getIp(), routingEntry.getPort());
            System.out.println("Connection established with: " + routingEntry.getNodeID());
            int mnID = routingEntry.getNodeID();
            TCPConnection connWithMNode = new TCPConnection(s, this);
            connectionCache.put(mnID, connWithMNode);
        }
        NodeReportsOverlaySetupStatus nross = (NodeReportsOverlaySetupStatus) EventFactory.getInstance().createEvent(Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS);
        nross.setStatus(nodeID);
        nross.setInfo("Node Overlay Setup Successfull");
        conn.getSender().sendData(nross.getByte());
    }

    public void exitOverlay() throws IOException, Exception {
        OverlayNodeSendsDeregistration onsd = (OverlayNodeSendsDeregistration) EventFactory.getInstance().createEvent(Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION);
        onsd.setNodeID(nodeID);
        onsd.setIp(InetAddress.getLocalHost().getHostAddress().getBytes());
        onsd.setPort(mnServerSocket.getLocalPort());
        conn.getSender().sendData(onsd.getByte());
    }

    private void startMessages(int numberOfMessages) throws IOException, Exception {
        for (int i = 0; i < numberOfMessages; i++) {
            int payload = randomNumberGenerator();
            int sendToNode = randomNodeSelector();

            routingAlgo(this.nodeID, sendToNode, payload);
            addToSent(payload);

        }
        OverlayNodeReportsTaskFinished taskFinished = (OverlayNodeReportsTaskFinished) EventFactory.getInstance().createEvent(Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED);
        taskFinished.setNodeID(nodeID);
        taskFinished.setIp(InetAddress.getLocalHost().getHostAddress().getBytes());
        taskFinished.setPort(mnServerSocket.getLocalPort());
        conn.getSender().sendData(taskFinished.getByte());
    }

    private int randomNumberGenerator() {
        Random r = new Random();
        int number = r.nextInt();
        return number;
    }

    private int randomNodeSelector() {
        Random r = new Random();
        int node = -1;
        while (true) {
            int number = r.nextInt(nodeArray.length);
            node = nodeArray[number];
            if (node != -1 && node != nodeID) {
                return node;
            } else {
            }
        }

    }

    private void routingAlgo(int source, int destination, int payload) throws IOException {
        int[] nodeArray2 = new int[connectionCache.keySet().size()];
        int j = 0;
        int finalNode = -1;

        boolean found = false;
        for (Map.Entry<Integer, TCPConnection> entrySet : connectionCache.entrySet()) {
            Integer key = entrySet.getKey();
            if (key == destination) {
                finalNode = destination;
                found = true;
            }
        }

        if (!found) {
            for (Map.Entry<Integer, TCPConnection> entrySet : connectionCache.entrySet()) {
                Integer key = entrySet.getKey();
                if (key < destination) {
                    nodeArray2[j] = key;
                    j++;
                }
            }
            int result = 0;
            for (int k = 0; k < nodeArray2.length; k++) {
                if (nodeArray2[k] == 0) {
                    result--;
                }
            }
            int x = nodeArray2.length;
            if (result == -x) {
                j = 0;
                for (Map.Entry<Integer, TCPConnection> entrySet : connectionCache.entrySet()) {
                    Integer key = entrySet.getKey();
                    nodeArray2[j] = key;
                    j++;
                }
            }
            finalNode = nodeArray2[0];
            for (int l : nodeArray2) {
                if (finalNode < l) {
                    finalNode = l;
                }
            }
        }
        OverlayNodeSendsData onsd = (OverlayNodeSendsData) EventFactory.getInstance().createEvent(Protocol.OVERLAY_NODE_SENDS_DATA);
        onsd.setDestinationID(destination);
        onsd.setSourceID(source);
        onsd.setPayload(payload);
        if (finalNode == -1) {
            System.out.println("error in finalNode");
        }
        onsd.setRoute(finalNode);
        //TCPConnection conn = connectionCache.get(finalNode);
        //System.out.println("-final-" + finalNode);
        synchronized(relayQueue){
           relayQueue.add(onsd);
        }
        
//        synchronized (this) {
//            conn.getSender().sendData(onsd.getByte());
//        }
        //System.out.println("Source :" + source + ", Destination :" + destination + ", Routing : " + finalNode);

    }

    public void run() {
        while (true) {
            OverlayNodeSendsData relayMsg;
            TCPConnection conn;
            synchronized (relayQueue) {
                relayMsg = (OverlayNodeSendsData)relayQueue.poll();
            }
            if (relayMsg != null) {
                try {
                    conn = connectionCache.get(relayMsg.getRoute());
                    byte[] bytesToSend = relayMsg.getByte();
                    conn.getSender().sendData(bytesToSend);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    synchronized void addToSent(int payload) {
        this.packetsSent++;
        this.sumValuesSent = this.sumValuesSent + payload;
    }

    synchronized void addToReceived(int payload) {
        this.packetsReceived++;
        this.sumValuesReceived = this.sumValuesReceived + payload;
    }

    synchronized void addToRelay() {
        this.packetsRelayed++;
    }

    public void resetCounters() {
        this.packetsSent = 0;
        this.packetsRelayed = 0;
        this.packetsReceived = 0;
        this.sumValuesSent = 0;
        this.sumValuesReceived = 0;
    }

	public void printinfo() {
		System.out.println("---Diagnostics---");
		System.out.println("---------------------------------------------");
		System.out.println("Packets Sent: "+this.packetsSent);
		System.out.println("Packets Relayed: "+this.packetsRelayed);
		System.out.println("Packets Received: "+this.packetsReceived);
		System.out.println("Payload Sent: "+this.sumValuesSent);
		System.out.println("Packets Received: "+this.sumValuesReceived);
		System.out.println("---------------------------------------------");
		
	}
}
