package cz4013.shared.request;

/**
 * The request to modify facility booking.
 */
public class ModifyFacilityBookingRequest {
    public String facName;
    public int day;
    public int bookingId;
    public int offset;

    public ModifyFacilityBookingRequest(){

    }

    public ModifyFacilityBookingRequest(String facName, int day, int bookingId, int offset){
        //offset = x number of 30mins (e.g offset=1 means postpone by 30 mins, offset=-2 means bring forward by 1 hr)
        this.facName = facName;
        this.day = day;
        this.bookingId = bookingId;
        this.offset = offset;
    }

    @Override
    public String toString(){
        String str = "ModifyFacilityBookingRequest(" + facName + "," + Integer.toString(day) + "," +  Integer.toString(bookingId) + "," +  Integer.toString(offset) + ")"; 
        return str;
    }
}
