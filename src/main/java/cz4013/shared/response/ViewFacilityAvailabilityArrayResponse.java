package cz4013.shared.response;

import java.util.ArrayList;

/**
 * The response from the server to client for the facility's availability in array data format
 */

public class ViewFacilityAvailabilityArrayResponse {
    public ArrayList<ArrayList<Integer>> availArray;
    public String errorMessage;

    public ViewFacilityAvailabilityArrayResponse (){

    }

    public ViewFacilityAvailabilityArrayResponse (ArrayList<ArrayList<Integer>> availArray, String errorMessage){
        this.availArray = availArray;
        this.errorMessage = errorMessage;
    }

    public static ViewFacilityAvailabilityArrayResponse failed(String errorMessage){
        return new ViewFacilityAvailabilityArrayResponse(null, errorMessage);
    }
}
