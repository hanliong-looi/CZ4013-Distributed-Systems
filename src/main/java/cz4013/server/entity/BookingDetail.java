package cz4013.server.entity;

/**
 * Details about a booking stored in DB.
 */
public class BookingDetail {
    public String facName;
    public String bookingId;
    public int day;
    public int startHour;
    public int startMin;
    public int endHour;
    public int endMin;

    /**
     * Constructs an entry for a booking in DB.
     *
     * @param name    name of the facility
     * @param client  detail of client that made the booking
     * @param day     day of the booking (e.g 1 = Mon, 2 = Tue, ..., 7 = Sun)
     * @param time    time of the booking (e.g "0930 - 1030")
     */
    public BookingDetail(String facName, String bookingId, int day, int startHour, int startMin, int endHour, int endMin) {
        this.facName = facName;
        this.bookingId = bookingId;
        this.day = day;
        this.startHour = startHour;
        this.startMin = startMin;
        this.endHour = endHour;
        this.endMin = endMin;
    }

    @Override
    public String toString() {
        String str = "BookingDetail(Facility Name: " + facName + ", BookingID: " + bookingId + ", Day: " + Integer.toString(day) + 
        ", Start Time: " + Integer.toString(startHour) + ": " + Integer.toString(startMin) +
        ", End Time: " + Integer.toString(endHour) + ": " + Integer.toString(endMin);
        return str;
    }
}