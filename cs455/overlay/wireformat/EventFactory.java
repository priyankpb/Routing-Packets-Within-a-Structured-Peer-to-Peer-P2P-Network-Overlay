package cs455.overlay.wireformat;

import java.io.IOException;

public class EventFactory {

	private static EventFactory eventFactory = new EventFactory();
	
	private EventFactory(){
		
	}
	
	public static EventFactory getInstance(){
		return eventFactory;
	}
        
	
	public Event createEvent(byte eventType) throws IOException{
            switch(eventType){
                case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                    return new OverlayNodeSendsRegistration();
                    
                case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                    return new RegistryReportsRegistrationStatus();
                    
                case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                    return new RegistryReportsDeregistrationStatus();
                 
                case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                    return new RegistrySendsNodeManifest();
                    
                case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                    return new OverlayNodeSendsDeregistration();
                    
                case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                    return new NodeReportsOverlaySetupStatus();
                    
                case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                    return new RegistryRequestsTaskInitiate();
                    
                case Protocol.OVERLAY_NODE_SENDS_DATA:
                    return new OverlayNodeSendsData();
                    
                case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                    return new OverlayNodeReportsTaskFinished();
                
                case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                    return new RegistryRequestsTrafficSummary();
                    
                case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                    return new OverlayNodeReportsTrafficSummary();
                    
                default:
                    return null;
            }
                
	}
	
	
}
