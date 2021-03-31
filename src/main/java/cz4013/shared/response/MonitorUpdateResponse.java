package cz4013.shared.response;

/**
 * The response from the server to client when the client's monitoring facility has updates
 */

public class MonitorUpdateResponse {
    public String info;

    public MonitorUpdateResponse(){

    }

    public MonitorUpdateResponse(String info){
        this.info = info;
    }

    public String toString(){
        return "MonitorUpdateResponse(" + info + ")";
    }
}
