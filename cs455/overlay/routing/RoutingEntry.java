package cs455.overlay.routing;

public class RoutingEntry {
    int nodeID;
    String ip;
    int port;
    int hopsAway;

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getHopsAway() {
        return hopsAway;
    }

    public void setHopsAway(int hopsAway) {
        this.hopsAway = hopsAway;
    }
}
