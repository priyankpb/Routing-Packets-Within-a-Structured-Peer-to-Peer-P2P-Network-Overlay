/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs455.overlay.wireformat;

/**
 *
 * @author YANK
 */
public class Protocol {
    public static final byte OVERLAY_NODE_SENDS_REGISTRATION = 2;
    public static final byte REGISTRY_REPORTS_REGISTRATION_STATUS = 3;
    public static final byte OVERLAY_NODE_SENDS_DEREGISTRATION = 4;
    public static final byte REGISTRY_REPORTS_DEREGISTRATION_STATUS = 5;
    public static final byte REGISTRY_SENDS_NODE_MANIFEST = 6;
    public static final byte NODE_REPORTS_OVERLAY_SETUP_STATUS = 7;
    public static final byte REGISTRY_REQUESTS_TASK_INITIATE = 8;
    public static final byte OVERLAY_NODE_SENDS_DATA = 9;
    public static final byte OVERLAY_NODE_REPORTS_TASK_FINISHED = 10;
    public static final byte REGISTRY_REQUESTS_TRAFFIC_SUMMARY = 11;
    public static final byte OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY = 12;
}


