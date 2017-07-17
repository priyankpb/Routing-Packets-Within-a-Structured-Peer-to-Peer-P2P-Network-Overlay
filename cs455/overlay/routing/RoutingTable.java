package cs455.overlay.routing;

import java.util.List;

public class RoutingTable {

    public int nodeID;
    public List<RoutingEntry> entries; 
    
    public RoutingTable() {
        
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public List<RoutingEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<RoutingEntry> entries) {
        this.entries = entries;
    }

    public void printRoutingTable(){
        System.out.println("----------------------------------");
        System.out.println("Routing table : "+nodeID);
        System.out.println("----------------------------------");
        System.out.format("%6s%15s%7s%5s","NodeId","IP       ","PortNo","Hops");
        System.out.println("");
        System.out.println("----------------------------------");
        for (RoutingEntry entry : entries) {
        	System.out.println("");
            System.out.format("%6s%15s%7s%5s",entry.nodeID,entry.ip,entry.port,entry.hopsAway);
        }
        System.out.println("");
        System.out.println("----------------------------------");
    }
    
}
