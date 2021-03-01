package cz4013.shared.response;

import java.util.ArrayList;

import cz4013.server.entity.BookingDetail;

public class ViewFacilityAvailabilityResponse {
    public ArrayList<ArrayList<BookingDetail>> bookingList;
    public String errorMessage;
    
    public ViewFacilityAvailabilityResponse(){

    }

    public ViewFacilityAvailabilityResponse(ArrayList<ArrayList<BookingDetail>> bookingList, String errorMessage){
        this.bookingList = bookingList;
        this.errorMessage = errorMessage;
    }

    public static ViewFacilityAvailabilityResponse failed(String errorMessage) {
        return new ViewFacilityAvailabilityResponse(null, errorMessage);
    }
}