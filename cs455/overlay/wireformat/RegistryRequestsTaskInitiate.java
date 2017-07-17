package cs455.overlay.wireformat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistryRequestsTaskInitiate implements Event{
    
    private byte type = Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
    private int numberOfDataPackets;

    public RegistryRequestsTaskInitiate() {
       
    }
    
    public RegistryRequestsTaskInitiate(byte[] data) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		this.type = din.readByte();
		this.numberOfDataPackets = din.readInt();
                baInputStream.close();
		din.close();
	}
   
    public int getNumberOfDataPackets() {
        return numberOfDataPackets;
    }
    
    public void setNumberOfDataPackets(int numberOfDataPackets) {
        this.numberOfDataPackets = numberOfDataPackets;
    }
    
	@Override
	public byte[] getByte() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		dout.write(getType());
		dout.writeInt(numberOfDataPackets);
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