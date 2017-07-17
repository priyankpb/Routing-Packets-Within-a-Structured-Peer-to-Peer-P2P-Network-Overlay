package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author YANK
 */
public class TCPSender {

    Socket socket;
    DataOutputStream dout;

    public TCPSender(Socket socket) {
        this.socket = socket;
        try {
            this.dout = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.out.println("--exception in output stream--");
        }
    }

    public void sendData(byte[] data) throws IOException {
        int dataLength = data.length;
        try {
            dout.writeInt(dataLength);
            dout.write(data, 0, dataLength);
            dout.flush();
        } catch (IOException e) {
            System.out.println("--Exception in sending data--");
        }
    }

}
