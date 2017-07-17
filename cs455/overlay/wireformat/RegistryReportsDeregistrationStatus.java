package cs455.overlay.wireformat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistryReportsDeregistrationStatus implements Event{

	private byte type = Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
	private int nodeID;
	private String info;
	private byte[] data;
	

	public RegistryReportsDeregistrationStatus(byte[] data) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		this.type = din.readByte();
		this.nodeID = din.readByte();
                int infoLength = din.readByte();
                byte[] temp = new byte[infoLength];
		din.readFully(temp, 0, infoLength);                
		this.info = new String(temp);
		baInputStream.close();
		din.close();
	}

        public RegistryReportsDeregistrationStatus() {

        }

	@Override
	public byte[] getByte() throws IOException {
		// TODO Auto-generated method stub
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		dout.write(getType());
		dout.write(nodeID);
		dout.write(info.length());
		dout.write(info.getBytes());
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

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
		
	}	

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void setType(byte type) {
		this.type = type;
	}

}