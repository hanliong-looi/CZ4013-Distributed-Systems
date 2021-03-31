package cz4013.shared.response;

/**
 * The response from the server to client after adding a booking for facility
 */

public class AddFacilityBookingResponse {
    public int bookingId;
    public boolean success;
    public String errorMessage;
    
    public AddFacilityBookingResponse(){

    }

    public AddFacilityBookingResponse(int bookingId, boolean success, String errorMessage){
        this.bookingId = bookingId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static AddFacilityBookingResponse failed(String errorMessage) {
        return new AddFacilityBookingResponse(-1, false, errorMessage);
    }
}