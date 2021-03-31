package cz4013.shared.response;

import java.util.ArrayList;

/**
 * The response from the server to client for viewing information of a facility
 */

public class ViewFacilityDetailResponse {
    public String name;
    public String operatingHours;
    public String address;
    public ArrayList<String> reviews;
    public String errorMessage;
    
    public ViewFacilityDetailResponse(){

    }

    public ViewFacilityDetailResponse(String name, String operatingHours, String address, ArrayList<String> reviews, String errorMessage){
        this.name = name;
        this.operatingHours = operatingHours;
        this.address = address;
        this.reviews = reviews;
        this.errorMessage = errorMessage;
    }

    public static ViewFacilityDetailResponse failed(String errorMessage) {
        return new ViewFacilityDetailResponse("", "", "", null, errorMessage);
    }
}