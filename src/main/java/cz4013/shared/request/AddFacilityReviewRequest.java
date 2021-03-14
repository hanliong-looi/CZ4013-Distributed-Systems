package cz4013.shared.request;

public class AddFacilityReviewRequest {
    public String facName;
    public String review;

    public AddFacilityReviewRequest(){

    }
    
    public AddFacilityReviewRequest(String facName, String review){
        this.facName = facName;
        this.review = review;
    }

    @Override
    public String toString() {
        String str = "AddFacilityReviewRequest(" + facName + ", " + review + ")";
        return str;
    }
}
