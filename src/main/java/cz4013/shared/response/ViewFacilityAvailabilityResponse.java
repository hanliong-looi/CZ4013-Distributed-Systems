package cz4013.shared.response;

public class ViewFacilityAvailabilityResponse {
    public String facilityName;
    //need to include availiability of the facility: array?
    public String availability;
    public String errorMessage;
    
    public ViewFacilityAvailabilityResponse(){

    }

    public ViewFacilityAvailabilityResponse(String facilityName, String availability, String errorMessage){
        this.facilityName = facilityName;
        this.availability = availability;
        this.errorMessage = errorMessage;
    }

    public static ViewFacilityAvailabilityResponse failed(String errorMessage) {
        return new ViewFacilityAvailabilityResponse("test", "not available", errorMessage);
    }
    
    @Override
    public String toString() {
        return "ViewFacilityAvailabilityResponse(" + facilityName + ", " + availability + ", " + errorMessage + ")";
      }
}