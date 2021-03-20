package cz4013.shared.response;

public class AddFacilityReviewResponse {
    public boolean success;
    public String errorMessage;
    
    public AddFacilityReviewResponse(){

    }

    public AddFacilityReviewResponse(boolean success, String errorMessage){
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static AddFacilityReviewResponse failed(String errorMessage) {
        return new AddFacilityReviewResponse(false, errorMessage);
    }
}