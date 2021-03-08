package cz4013.shared.response;

public class AddFacilityReviewResponse {
    public String facName;
    public String review;
    public String errorMessage;
    
    public AddFacilityReviewResponse(){

    }

    public AddFacilityReviewResponse(String facName, String review, String errorMessage){
        this.facName = facName;
        this.review = review;
        this.errorMessage = errorMessage;
    }

    public static AddFacilityReviewResponse failed(String errorMessage) {
        return new AddFacilityReviewResponse("", "", errorMessage);
    }
}