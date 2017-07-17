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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.util.TrafficSummary;
import cs455.overlay.wireformat.Event;
import cs455.overlay.wireformat.EventFactory;
import cs455.overlay.wireformat.NodeReportsOverlaySetupStatus;
import cs455.overlay.wireformat.OverlayNodeReportsTaskFinished;
import cs455.overlay.wireformat.OverlayNodeReportsTrafficSummary;
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
public class Registry implements Node {

	public Map<Integer, String> depository = new TreeMap<Integer, String>();
	public Map<Socket, TCPConnection> tempCache = new HashMap<Socket, TCPConnection>();
	public Map<Integer, TCPConnection> connectionCache = new HashMap<Integer, TCPConnection>();
	public Map<Integer, TCPConnection> tempConnectionCache = new HashMap<Integer, TCPConnection>();
	public Map<Integer, RoutingTable> routingTables = new HashMap<Integer, RoutingTable>();
	public Map<Integer, TrafficSummary> trafficSummarycache = new HashMap<Integer, TrafficSummary>();
	private int taskFinishedCounter = 0;
	private int summaryCounter = 0;

	// private final Registry registrySingleton = new Registry();
	private Registry(int port) {
		ServerSocket registrySocket;
		boolean validSocket = false;
		while(!validSocket){
		try {
			registrySocket = new ServerSocket(port);
			validSocket = true;
			InteractiveCommandParser interactiveCommandParser = new InteractiveCommandParser(
					this);
			Thread commandThread = new Thread(interactiveCommandParser);
			commandThread.start();
			System.out.println("Registry started...");
			while (true) {
				Socket s;
				try {
					s = registrySocket.accept();
					TCPConnection conn = new TCPConnection(s, this);
					addtoTempCache(conn);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		} catch (IOException e) {
			System.out.println("invalid portnum");
			Scanner sc = new Scanner(System.in);
			port = sc.nextInt();
		}
		}
		
	}

	private void addtoTempCache(TCPConnection connection) {
		int idbyPort = connection.getSocket().getLocalPort();
		String ip = connection.getSocket().getInetAddress().getHostName();
		Socket s = connection.getSocket();
		synchronized (this) {
			tempCache.put(s, connection);
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("Error in port num");
			return;
		}
		int port = Integer.parseInt(args[0]);
		// int port = 5000;
		Registry registry = new Registry(port);

	}

	public void createServer() {

	}

	@Override
	public void onEvent(byte[] data, Socket s) throws IOException {
		EventFactory eventFactory = EventFactory.getInstance();
		switch (data[0]) {
		case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
			
			OverlayNodeSendsRegistration registration = new OverlayNodeSendsRegistration(
					data);
			// OverlayNodeSendsRegistration registration = new
			// OverlayNodeSendsRegistration(data);
			int nodeID = addToDepository(registration.getIp(),
					registration.getPort(), s);
			RegistryReportsRegistrationStatus sendRegStatus = (RegistryReportsRegistrationStatus) eventFactory
					.createEvent(Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS);
			sendRegStatus.setNodeID(nodeID);
			System.out.println("messaging node with id: " + nodeID
					+ " connected...");
			if (nodeID >= 0) {
				addConnectiontoCache(nodeID, s);
				int numberofNodes = depository.keySet().size();
				sendRegStatus
						.setInfo("Registration request successful. The number of messaging nodes currently constituting the overlay is "
								+ numberofNodes);
			} else if (nodeID == -1) {
				sendRegStatus.setInfo("Registration unsuccessfull: IP missmatch");
			}
			
			TCPConnection conn = connectionCache.get(nodeID);
			try {
			//System.out.println("cache:" + connectionCache);
			// System.out.println("TCP:"+conn);
			    
				conn.getSender().sendData(sendRegStatus.getByte());
			} catch (Exception e) {
				//TCPConnection conn = tempConnectionCache.get(nodeID);
				//conn.getReceiver().notify();
				if (depository.containsKey(nodeID)) {
					//String incomingIP = new String(deregistration.getIp());
					//int incomigPort = deregistration.getPort();

					//String stored = depository.get(incomingNodeId);
					//String[] storedIPnPort = stored.split(",");
					//int storedPort = Integer.parseInt(storedIPnPort[1]);

					//if (incomingIP.equalsIgnoreCase(storedIPnPort[0])
					//		&& incomigPort == storedPort) {
					//	int result = -1;
				
						//tempConnectionCache.putIfAbsent(nodeID,conn);
						depository.remove(nodeID);
						connectionCache.remove(nodeID);

						
					} 
				

			}

			break;

		case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
			OverlayNodeSendsDeregistration deregistration = new OverlayNodeSendsDeregistration(
					data);
			deRegisterNode(deregistration);
			break;

		case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
			NodeReportsOverlaySetupStatus nross = new NodeReportsOverlaySetupStatus(
					data);
			int status = nross.getStatus();
			String info = nross.getInfo();
			if (status == -1) {
				System.out.println("Overlay Setup Failed");
			} else {
				System.out.println("Node: " + status
						+ " has established connections...");
			}
			break;

		case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
			OverlayNodeReportsTaskFinished taskFinished = new OverlayNodeReportsTaskFinished(
					data);
			synchronized (this) {
				taskFinishedCounter++;
			}
			if (taskFinishedCounter == depository.size()) {
				System.out.println(taskFinishedCounter
						+ " Nodes Finished the Task");
				try {
					Thread.sleep(20000);
				} catch (InterruptedException ex) {
					Logger.getLogger(Registry.class.getName()).log(
							Level.SEVERE, null, ex);
				}
				RegistryRequestsTrafficSummary rrts = (RegistryRequestsTrafficSummary) eventFactory
						.createEvent(Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY);
				for (Map.Entry<Integer, TCPConnection> entrySet : connectionCache
						.entrySet()) {
					Integer key = entrySet.getKey();
					TCPConnection value = entrySet.getValue();
					value.getSender().sendData(rrts.getByte());
				}

			}

			break;

		case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
			OverlayNodeReportsTrafficSummary trafficSummary = new OverlayNodeReportsTrafficSummary(
					data);
			TrafficSummary ts = new TrafficSummary();
			int node = trafficSummary.getNodeID();
			ts.packetSent = trafficSummary.getPacketsSent();
			ts.packetRelayed = trafficSummary.getPacketsRelayed();
			ts.payloadSent = trafficSummary.getPayloadSent();
			ts.packetReceived = trafficSummary.getPacketsReceived();
			ts.payloadReceived = trafficSummary.getPayloadReceived();

			synchronized (this) {
				trafficSummarycache.put(node, ts);
				summaryCounter++;
			}
			if (summaryCounter == depository.size()) {
				StatisticsCollectorAndDisplay scad = new StatisticsCollectorAndDisplay(
						trafficSummarycache);
				resetCounters();
			}
			break;

		}

	}

	private int addToDepository(byte[] ipbytes, int port, Socket connectedSocket)
			throws UnknownHostException {
		String recievedIP = new String(ipbytes);
		String value = recievedIP + "," + port;
		String actualIP = connectedSocket.getInetAddress().getHostAddress();
		System.out.println("recv ip :" + recievedIP);
		System.out.println("actualip: " + actualIP);
		if (!recievedIP.equalsIgnoreCase(actualIP)) {
			return -1;
		}
		int nodeID;
		while (true) {
			nodeID = randomNumberGenerator();
			if (!depository.containsKey(nodeID)) {
				break;
			}
		}
		depository.put(nodeID, value);
		return nodeID;
	}

	private int randomNumberGenerator() {
		Random r = new Random();
		int number = r.nextInt(127);

		return number;
	}

	private void addConnectiontoCache(int id, Socket s) {
		TCPConnection tempObj = tempCache.get(s);
		if (tempObj != null) {
			connectionCache.put(id, tempObj);
			tempConnectionCache.put(id, tempObj);
		}
	}

	public void setupRoutingTable(int numofTimes) {

		for (Map.Entry<Integer, String> entry : depository.entrySet()) {
			int nodeId = entry.getKey();
			RoutingTable rt = new RoutingTable();
			rt.setNodeID(nodeId);
			List<RoutingEntry> tempList = new ArrayList<RoutingEntry>();

			List<Integer> keyList = new ArrayList<Integer>();
			keyList.addAll(depository.keySet());
			int index = keyList.indexOf(nodeId);

			for (int i = 0; i < numofTimes; i++) {
				RoutingEntry re = new RoutingEntry();
				int nodeatIndex = keyList
						.get(((index + ((int) Math.pow(2, i))) % (keyList
								.size())));
				re.setNodeID(nodeatIndex);
				String[] ipNport = depository.get(nodeatIndex).split(",");
				re.setIp(ipNport[0]);
				re.setPort(Integer.parseInt(ipNport[1]));
				re.setHopsAway((int) Math.pow(2, i));
				tempList.add(re);
			}
			rt.setEntries(tempList);
			// rt.printRoutingTable();
			routingTables.put(nodeId, rt);

		}
	}

	public void sendManifest(int n) throws IOException {
		// Event event =
		// EventFactory.getInstance().createEvent(Protocol.REGISTRY_SENDS_NODE_MANIFEST);
		int[] nodeArray = new int[connectionCache.size()];
		int nodeIndex = 0;
		for (Map.Entry<Integer, TCPConnection> entry : connectionCache
				.entrySet()) {
			nodeArray[nodeIndex] = entry.getKey();
			nodeIndex++;
		}

		for (Map.Entry<Integer, TCPConnection> entry : connectionCache
				.entrySet()) {
			int nodeId = entry.getKey();
			RoutingTable rtforNodeID = routingTables.get(nodeId);
			RegistrySendsNodeManifest rsnm = (RegistrySendsNodeManifest) EventFactory
					.getInstance().createEvent(
							Protocol.REGISTRY_SENDS_NODE_MANIFEST);
			rsnm.setNodeID(nodeId);
			List<RoutingEntry> routingEntries = rtforNodeID.getEntries();
			RoutingEntry[] reArray = new RoutingEntry[routingEntries.size()];
			// int[] nodeArray = new int[entry.];

			for (int i = 0; i < n; i++) {
				reArray[i] = routingEntries.get(i);
				// nodeArray[i] = routingEntries.get(i).getNodeID();
			}
			// rsnm.setRe((RoutingEntry[]) routingEntries.toArray());
			rsnm.setRe(reArray);
			rsnm.setNodes(nodeArray);
			rsnm.setNumberofNodes(nodeIndex);

			TCPConnection conn = entry.getValue();
			conn.getSender().sendData(rsnm.getByte());
		}
	}

	private void deRegisterNode(OverlayNodeSendsDeregistration deregistration)
			throws IOException {
		RegistryReportsDeregistrationStatus sendDeregStatus = (RegistryReportsDeregistrationStatus) EventFactory
				.getInstance().createEvent(
						Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS);
		int incomingNodeId = deregistration.getNodeID();
		TCPConnection conn = tempConnectionCache.get(incomingNodeId);

		if (depository.containsKey(incomingNodeId)) {
			String incomingIP = new String(deregistration.getIp());
			int incomigPort = deregistration.getPort();

			String stored = depository.get(incomingNodeId);
			String[] storedIPnPort = stored.split(",");
			int storedPort = Integer.parseInt(storedIPnPort[1]);

			if (incomingIP.equalsIgnoreCase(storedIPnPort[0])
					&& incomigPort == storedPort) {
				int result = -1;

				// tempConnectionCache.putIfAbsent(incomingNodeId, conn);
				depository.remove(incomingNodeId);
				connectionCache.remove(incomingNodeId);

				result = incomingNodeId;
				sendDeregStatus.setNodeID(result);
				if (result != -1) {
					sendDeregStatus.setInfo("Node Deregistration Successfull");
				} else {
					sendDeregStatus
							.setInfo("Node Deregistration Unsuccessfull: Error in Registry...");
				}
			} else {
				sendDeregStatus.setNodeID(-1);
				sendDeregStatus
						.setInfo("Node Deregistration Unsuccessfull: IP and/or Port missmatch...");

			}
		} else {
			sendDeregStatus.setNodeID(-1);
			sendDeregStatus
					.setInfo("Node Deregistration Unsuccessfull: Already Deregistered...");
		}
		conn.getSender().sendData(sendDeregStatus.getByte());
	}

	public void initiateMessages(int numberOfMessages) throws IOException {
		RegistryRequestsTaskInitiate rrti = (RegistryRequestsTaskInitiate) EventFactory
				.getInstance().createEvent(
						Protocol.REGISTRY_REQUESTS_TASK_INITIATE);
		rrti.setNumberOfDataPackets(numberOfMessages);
		for (Map.Entry<Integer, TCPConnection> entry : connectionCache
				.entrySet()) {
			TCPConnection conn = entry.getValue();
			conn.getSender().sendData(rrti.getByte());
		}
	}

	public void resetCounters() {
		this.summaryCounter = 0;
		this.taskFinishedCounter = 0;
	}
}
