package cz4013.shared.request;

public class AddFacilityBookingRequest {
    public String facName;
    public int day;
    public int startHour;
    public int startMin;
    public int endHour;
    public int endMin;

    public AddFacilityBookingRequest(){

    }

    public AddFacilityBookingRequest(String facName, int day, int startHour, int startMin, int endHour, int endMin){
        this.facName = facName;
        this.day = day;
        this.startHour = startHour;
        this.startMin = startMin;
        this.endHour = endHour;
        this.endMin = endMin;
    }

    @Override
    public String toString(){
        String str = "AddFacilityBookingRequest(" + facName + "," + Integer.toString(day) + "," + Integer.toString(startHour) + "," + Integer.toString(startMin)
         + "," + Integer.toString(endHour) + "," + Integer.toString(endMin) + ")";
        return str;
    }
}
