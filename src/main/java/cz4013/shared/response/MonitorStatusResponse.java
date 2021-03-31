package cz4013.shared.response;

/**
 * The response from the server to client when the client requests to monitor a facility
 */

public class MonitorStatusResponse {
    public boolean success;

    public MonitorStatusResponse() {

    }

    public MonitorStatusResponse(boolean success){
        this.success = success;
    }

    public String toString(){
        return "MonitorStatusResponse(" + success + ")";
    }
}
