package cz4013.server.entity;

/**
 * Details about a booking stored in DB.
 */
public class BookingDetail {
    public String facName;
    public String client;
    public int day;
    public String time;

    /**
     * Constructs an entry for a booking in DB.
     *
     * @param name    name of the facility
     * @param client  detail of client that made the booking
     * @param day     day of the booking (e.g 1 = Mon, 2 = Tue, etc.)
     * @param time    time of the booking (e.g "0930 - 1030")
     */
    public BookingDetail(String facName, String client, int day, String time) {
        this.facName = facName;
        this.client = client;
        this.day = day;
        this.time = time;
    }

    @Override
    public String toString() {
        String str = "BookingDetail(Facility Name: " + facName + ", Client: " + client + ", Day: " + day + ", Time: " + time + ")";
        return str;
    }
}
