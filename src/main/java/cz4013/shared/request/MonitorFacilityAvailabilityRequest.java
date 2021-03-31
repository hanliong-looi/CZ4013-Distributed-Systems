package cz4013.shared.request;

/**
 * The request to monitor availability of a facility.
 */
public class MonitorFacilityAvailabilityRequest {
    public String name; 
    public int interval;

    public MonitorFacilityAvailabilityRequest(){

    }

    public MonitorFacilityAvailabilityRequest(String name, int interval){
        this.name = name;
        this.interval = interval;
    }

    public String toString(){
        return "MonitorFacilityAvailabilityRequest(" + interval + ")";
    }
}
