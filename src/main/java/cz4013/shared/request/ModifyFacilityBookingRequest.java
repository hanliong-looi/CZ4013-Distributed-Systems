package cz4013.shared.request;

public class ModifyFacilityBookingRequest {
    public String facName;
    public int day;
    public int bookingId;
    public int offset;

    public ModifyFacilityBookingRequest(){

    }

    public ModifyFacilityBookingRequest(String facName, int day, int bookingId, int offset){
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
