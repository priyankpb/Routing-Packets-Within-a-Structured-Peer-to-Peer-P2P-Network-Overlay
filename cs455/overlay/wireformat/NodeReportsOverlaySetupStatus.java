package cs455.overlay.wireformat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NodeReportsOverlaySetupStatus implements Event{
    
    private byte type = Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;    
    public int status;
    public String info;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    
    public NodeReportsOverlaySetupStatus(byte[] data) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		this.type = din.readByte();
                this.status = din.read();
		int infoLength = din.read();
		byte[] tempInfo = new byte[infoLength];
		din.readFully(tempInfo, 0, infoLength);
                this.info = new String(tempInfo);
		
		baInputStream.close();
		din.close();
	}
	
        public  NodeReportsOverlaySetupStatus(){
        }
    
	@Override
	public byte[] getByte() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		dout.write(getType());
                int infoLength = info.length();
                dout.write(this.status);
		dout.write(infoLength);
		dout.write(this.info.getBytes());
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
