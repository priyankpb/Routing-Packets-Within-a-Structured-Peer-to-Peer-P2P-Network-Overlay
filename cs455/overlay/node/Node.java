/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author YANK
 */
public interface Node {
    public void onEvent(byte[] data, Socket s) throws IOException ;
}
