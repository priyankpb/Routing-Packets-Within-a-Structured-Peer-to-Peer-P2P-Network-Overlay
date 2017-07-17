package cs455.overlay.wireformat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;

public class RegistrySendsNodeManifest implements Event{

    

        private byte type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
        private RoutingEntry[] re;
        private int nodeID;
        private int numberofNodes;
        private int[] nodes;

    public RoutingEntry[] getRe() {
        return re;
    }

    public void setRe(RoutingEntry[] re) {
        this.re = re;
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public int getNumberofNodes() {
        return numberofNodes;
    }

    public void setNumberofNodes(int numberofNodes) {
        this.numberofNodes = numberofNodes;
    }

    public int[] getNodes() {
        return nodes;
    }

    public void setNodes(int[] nodes) {
        this.nodes = nodes;
    }
        
        public RegistrySendsNodeManifest(byte[] data) throws IOException {
            ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
            this.type = din.readByte();
            int length = din.readByte();
            re = new RoutingEntry[length];
            for (int i = 0; i < length; i++) {
                re[i] = new RoutingEntry();
                re[i].setNodeID(din.readByte());
                int ipLength = din.readByte();
                byte[] ip = new byte[ipLength];
                din.readFully(ip, 0, ipLength);
                re[i].setIp(new String(ip));
                re[i].setPort(din.readInt()); 
            }
            
            numberofNodes = din.read();
            nodes = new int[numberofNodes];
            for (int i = 0; i < numberofNodes; i++) {
                nodes[i] = din.read();
            }
            baInputStream.close();
            din.close();
        }

        RegistrySendsNodeManifest() {
        
        }
         
    
	@Override
	public byte[] getByte() throws IOException {
            byte[] marshalledBytes = null;
            ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
            dout.write(getType());
            
            int length = re.length;
            dout.write(length);
            for (int i = 0; i < length; i++) {
                RoutingEntry currentEntry = re[i];
                dout.write(currentEntry.getNodeID());
                String ip = re[i].getIp();
                int ipLength = ip.length();
                dout.write(ipLength);
                dout.write(ip.getBytes());
                dout.writeInt(re[i].getPort());
            }
            
            dout.write(numberofNodes);
            for (int i = 0; i < numberofNodes; i++) {
                dout.write(nodes[i]);
            }
            
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