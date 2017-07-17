package cs455.overlay.wireformat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeReportsTrafficSummary implements Event {

    private byte type = Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
    private int nodeID;
    private int packetsSent;
    private int packetsRelayed;
    private long payloadSent;
    private int packetsReceived;
    private long payloadReceived;

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public int getPacketsSent() {
        return packetsSent;
    }

    public void setPacketsSent(int packetsSent) {
        this.packetsSent = packetsSent;
    }

    public int getPacketsRelayed() {
        return packetsRelayed;
    }

    public void setPacketsRelayed(int packetsRelayed) {
        this.packetsRelayed = packetsRelayed;
    }

    public long getPayloadSent() {
        return payloadSent;
    }

    public void setPayloadSent(long payloadSent) {
        this.payloadSent = payloadSent;
    }

    public int getPacketsReceived() {
        return packetsReceived;
    }

    public void setPacketsReceived(int packetsReceived) {
        this.packetsReceived = packetsReceived;
    }

    public long getPayloadReceived() {
        return payloadReceived;
    }

    public void setPayloadReceived(long payloadReceived) {
        this.payloadReceived = payloadReceived;
    }
    
    public OverlayNodeReportsTrafficSummary(){
        
    }
    
    public OverlayNodeReportsTrafficSummary(byte[] data) throws IOException{
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        this.type = din.readByte();
        this.nodeID = din.readInt();
        this.packetsSent = din.readInt();
        this.packetsRelayed = din.readInt();
        this.payloadSent = din.readLong();
        this.packetsReceived = din.readInt();
        this.payloadReceived = din.readLong();
        baInputStream.close();
        din.close();
    }
    
    @Override
    public byte[] getByte() throws IOException {
        // TODO Auto-generated method stub
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.write(getType());
        dout.writeInt(this.nodeID);
        dout.writeInt(this.packetsSent);
        dout.writeInt(this.packetsRelayed);
        dout.writeLong(this.payloadSent);
        dout.writeInt(this.packetsReceived);
        dout.writeLong(this.payloadReceived);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }

    @Override
    public byte getType() {
        // TODO Auto-generated method stub
        return type;
    }

}
