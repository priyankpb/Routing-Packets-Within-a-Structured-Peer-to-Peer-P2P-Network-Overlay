package cs455.overlay.transport;

import java.net.Socket;
import cs455.overlay.node.Node;


/**
 *
 * @author YANK
 */
public class TCPConnection {

    private Socket socket;
    private TCPSender sender;
    private TCPReceiver receiver;

    public TCPConnection(Socket socket, Node node) {
        this.socket = socket;
        this.sender = new TCPSender(socket);
        this.receiver = new TCPReceiver(socket, node);
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public TCPSender getSender() {
        return sender;
    }

    public void setSender(TCPSender sender) {
        this.sender = sender;
    }

    public TCPReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(TCPReceiver receiver) {
        this.receiver = receiver;
    }
    
    
}
