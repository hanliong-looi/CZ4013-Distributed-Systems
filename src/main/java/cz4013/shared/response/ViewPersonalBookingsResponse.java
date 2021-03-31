package cz4013.shared.response;

import java.util.ArrayList;

/**
 * The response from the server to client to view bookings made by the client
 */

public class ViewPersonalBookingsResponse {
    public ArrayList<ArrayList<String>> bookingsMade;
    public String errorMessage;
    
    public ViewPersonalBookingsResponse(){

    }
    public ViewPersonalBookingsResponse(ArrayList<ArrayList<String>> bookingsMade, String errorMessage){
        this.bookingsMade = bookingsMade;
        this.errorMessage = errorMessage;
    }
}
