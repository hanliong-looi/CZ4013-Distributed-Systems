package cz4013.shared.response;

public class MonitorStatusResponse {
    public boolean success;

    public MonitorStatusResponse() {

    }

    public MonitorStatusResponse(String name, boolean success){
        this.success = success;
    }

    public String toString(){
        return "MonitorStatusResponse(" + success + ")";
    }
}
