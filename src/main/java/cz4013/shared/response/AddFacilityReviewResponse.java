package cz4013.shared.response;

/**
 * The response from the server to client after adding a review for facility
 */

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