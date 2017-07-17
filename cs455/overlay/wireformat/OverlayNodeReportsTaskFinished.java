package cs455.overlay.wireformat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeReportsTaskFinished implements Event {

    private byte type = Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;

    private byte[] ip;
    private int port;
    private int nodeID;

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public byte[] getIp() {
        return ip;
    }

    public void setIp(byte[] ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public OverlayNodeReportsTaskFinished(byte[] data) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        this.type = din.readByte();
        int iplength = din.readByte();
        this.ip = new byte[iplength];
        din.readFully(this.ip, 0, iplength);
        this.port = din.readInt();
        this.nodeID = din.readInt();
        baInputStream.close();
        din.close();
    }

    public OverlayNodeReportsTaskFinished() {
    }

    @Override
    public byte[] getByte() throws Exception {
		// TODO Auto-generated method stub

        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        dout.write(getType());
        byte[] ipbytes = this.ip;
        dout.write(ipbytes.length);
        dout.write(ipbytes);
        int port = this.port;
        dout.writeInt(port);
        dout.writeInt(this.nodeID);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }

    @Override
    public byte getType() {
        // TODO Auto-generated method stub
        return this.type;
    }
}
