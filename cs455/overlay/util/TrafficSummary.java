/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs455.overlay.util;

/**
 *
 * @author YANK
 */
public class TrafficSummary {
    public int packetSent;
    public int packetRelayed;
    public long payloadSent;
    public int packetReceived;
    public long payloadReceived;

    public int getPacketSent() {
        return packetSent;
    }

    public void setPacketSent(int packetSent) {
        this.packetSent = packetSent;
    }

    public int getPacketRelayed() {
        return packetRelayed;
    }

    public void setPacketRelayed(int packetRelayed) {
        this.packetRelayed = packetRelayed;
    }

    public long getPayloadSent() {
        return payloadSent;
    }

    public void setPayloadSent(long payloadSent) {
        this.payloadSent = payloadSent;
    }

    public int getPacketReceived() {
        return packetReceived;
    }

    public void setPacketReceived(int packetReceived) {
        this.packetReceived = packetReceived;
    }

    public long getPayloadReceived() {
        return payloadReceived;
    }

    public void setPayloadReceived(long payloadReceived) {
        this.payloadReceived = payloadReceived;
    }
    
    
}
