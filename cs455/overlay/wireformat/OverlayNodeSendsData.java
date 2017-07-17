package cs455.overlay.wireformat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeSendsData implements Event{
        
        private byte type = Protocol.OVERLAY_NODE_SENDS_DATA;
        public int destinationID;
        public int sourceID;
        public int payload;
        public int route;

    public int getRoute() {
        return route;
    }

    public void setRoute(int route) {
        this.route = route;
    }

        public int getDestinationID() {
            return destinationID;
        }

        public void setDestinationID(int destinationID) {
            this.destinationID = destinationID;
        }

        public int getSourceID() {
            return sourceID;
        }

        public void setSourceID(int sourceID) {
            this.sourceID = sourceID;
        }

        public int getPayload() {
            return payload;
        }

        public void setPayload(int payload) {
            this.payload = payload;
        }

    public OverlayNodeSendsData() {

    }
        
    public OverlayNodeSendsData(byte[] data) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
	DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
	this.type = din.readByte();
	this.destinationID = din.readInt();
	this.sourceID = din.readInt();
	this.payload = din.readInt();
        
	baInputStream.close();
	din.close();        
    }   
        
	@Override
	public byte[] getByte() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		dout.write(getType());
		dout.writeInt(this.destinationID);
                dout.writeInt(this.sourceID);
                dout.writeInt(this.payload);
                
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