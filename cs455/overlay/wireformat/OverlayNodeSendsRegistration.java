package cs455.overlay.wireformat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;

public class OverlayNodeSendsRegistration implements Event{
	
	private byte type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
	private byte[] ip;
	private int port;
        private byte[] data;
	
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
        
        public void setData(byte[] incomingData){
            this.data = incomingData;
        }
        
	public OverlayNodeSendsRegistration(byte[] data) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		this.type = din.readByte();
		int iplength = din.readByte();
		this.ip = new byte[iplength];
		din.readFully(this.ip, 0, iplength);
		this.port = din.readInt();
		baInputStream.close();
		din.close();
	}
	
        public  OverlayNodeSendsRegistration(){
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