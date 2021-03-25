package cz4013.shared.response;

import java.util.ArrayList;

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
