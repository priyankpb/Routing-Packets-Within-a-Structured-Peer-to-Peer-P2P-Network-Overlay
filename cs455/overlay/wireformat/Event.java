package cs455.overlay.wireformat;

import cs455.overlay.node.Node;

public interface Event {
	byte[] getByte() throws Exception;
	byte getType();
}
