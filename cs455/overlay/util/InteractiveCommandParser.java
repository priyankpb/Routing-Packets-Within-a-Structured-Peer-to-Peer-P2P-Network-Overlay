package cs455.overlay.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;
import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.wireformat.Event;
import cs455.overlay.wireformat.EventFactory;
import cs455.overlay.wireformat.Protocol;

public class InteractiveCommandParser implements Runnable{

    private final Node node;
    public InteractiveCommandParser(Node node) {
        this.node=node;
        
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while(true){
            String command = sc.nextLine();
            try {
                executeCommand(command);
            } catch (UnknownHostException ex) {
                Logger.getLogger(InteractiveCommandParser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(InteractiveCommandParser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(InteractiveCommandParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void executeCommand(String command) throws UnknownHostException, IOException, Exception{
        if(command.contains("list-messaging-nodes")){
            Registry reg = (Registry) this.node;  
            printNodes(reg); 
        }
        else if (command.contains("setup-overlay")){
            String[] temp = command.split(" ");
            int numberOfRoutingTableEntries=0;
            if(temp.length==2)
            	numberOfRoutingTableEntries = Integer.parseInt(temp[1]);
            
            Registry reg = (Registry) this.node;
            int nodeCounter = reg.depository.size();
            int x = (int) ((Math.log10(nodeCounter)/Math.log10(2))+1);
            if(numberOfRoutingTableEntries==0 || numberOfRoutingTableEntries>x){
            	numberOfRoutingTableEntries = x;
            }
            
            reg.setupRoutingTable(numberOfRoutingTableEntries);
            reg.sendManifest(numberOfRoutingTableEntries);
        }
        else if (command.contains("list-routing-tables")){
            Registry reg = (Registry) this.node;
            for (Map.Entry<Integer, RoutingTable> entrySet : reg.routingTables.entrySet()) {
                int key = entrySet.getKey();
                RoutingTable value = entrySet.getValue();
                System.out.println("----------------------------------");
                System.out.println("Routing table : "+key);
                System.out.println("----------------------------------");
                System.out.format("%6s%15s%7s%5s","NodeId","IP       ","PortNo","Hops");
                System.out.println("");
                System.out.println("----------------------------------");
                for (RoutingEntry entry : value.entries) {
                	System.out.println("");
                    //System.out.println(entry.getNodeID()+"\t"+entry.getIp()+"\t"+entry.getPort()+"\t   "+entry.getHopsAway());
                	System.out.format("%6s%15s%7s%5s",entry.getNodeID(),entry.getIp(),entry.getPort(),entry.getHopsAway());
                }
                System.out.println("");
                System.out.println("----------------------------------------");

            }
                
                
            
        }
        else if (command.contains("start")){
                String temp[] = command.split(" ");
                int numberOfMessages = Integer.parseInt(temp[1]);
                Registry reg = (Registry) this.node;
                reg.initiateMessages(numberOfMessages);
        }
        else if (command.contains("print-counters-and-diagnostics")){
            MessagingNode mn = (MessagingNode) this.node;
            mn.printinfo();
        }
        else if (command.contains("exit-overlay")){
            MessagingNode mn = (MessagingNode) this.node;
            mn.exitOverlay();
        }
        else
        	System.out.println("COmmand not found");
            
    }
    
    private void printNodes(Registry reg) throws UnknownHostException{
        System.out.println("\n          List of Nodes          ");
        System.out.println("---------------------------------");
        System.out.format("%-15s%7s%5s", "Hostname","Port","Node Id");
        System.out.println("");
        System.out.println("---------------------------------");

        for (Map.Entry<Integer, String> entry: reg.depository.entrySet()){
            int nodeId = entry.getKey();
            String ipNport = entry.getValue();
            String[] temp = ipNport.split(",");
            String temp3 = InetAddress.getByName(temp[0]).getHostName();
            StringTokenizer st = new StringTokenizer(temp3, ".");
    		String hostName = (String) st.nextElement();
            String port = temp[1];
            
            System.out.format("%-15s%7s%5s", hostName,port,nodeId);
            System.out.println("");
            //System.out.println(hostName+"\t"+port+"\t"+nodeId);
        }
    }
    
   

}
