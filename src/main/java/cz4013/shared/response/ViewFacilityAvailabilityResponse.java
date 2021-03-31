package cz4013.shared.response;

import java.util.ArrayList;

/**
 * The response from the server to client for the facility availability
 */

public class ViewFacilityAvailabilityResponse {
    public ArrayList<ArrayList<ArrayList<String>>> bookingList;
    public String errorMessage;
    
    public ViewFacilityAvailabilityResponse(){

    }

    public ViewFacilityAvailabilityResponse(ArrayList<ArrayList<ArrayList<String>>> bookingList, String errorMessage){
        this.bookingList = bookingList;
        this.errorMessage = errorMessage;
    }

    public static ViewFacilityAvailabilityResponse failed(String errorMessage) {
        return new ViewFacilityAvailabilityResponse(null, errorMessage);
    }
}