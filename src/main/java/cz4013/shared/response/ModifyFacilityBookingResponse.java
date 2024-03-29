package cz4013.shared.response;

/**
 * The response from the server to client after a facility booking is modified
 */

public class ModifyFacilityBookingResponse {
    public int bookingId;
    public boolean success;
    public String errorMessage;

    public ModifyFacilityBookingResponse(){

    }

    public ModifyFacilityBookingResponse(int bookingId, boolean success, String errorMessage){
        this.bookingId = bookingId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static ModifyFacilityBookingResponse failed(String errorMessage){
        return new ModifyFacilityBookingResponse(-1, false, errorMessage);
    }
}
