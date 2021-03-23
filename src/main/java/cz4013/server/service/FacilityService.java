package cz4013.server.service;

import cz4013.server.db.Database;
import cz4013.server.entity.BookingDetail;
import cz4013.server.entity.FacilityDetail;
import cz4013.shared.request.*;
import cz4013.shared.response.*;
import cz4013.shared.rpc.Transport;

import java.net.SocketAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * This class provides various facility services and handle interactions with
 * DB.
 */
public class FacilityService {
    private Database db = new Database();
    private Transport transport;
    private Map<SocketAddress, Instant> listeners = new HashMap<>();
    private int nextAvailableBookingId = 1;

    /**
     * Constructs a {@code BankService}.
     *
     * @param transport transport layer to be used to send data for registered
     *                  monitoring clients
     */
    public FacilityService(Transport transport) {
        this.transport = transport;
    }

    /**
     * Processes a view facility detail request.
     *
     * @param req the request to be processed
     * @return the response after processing the given request
     */
    public ViewFacilityDetailResponse processViewFacilityDetail(ViewFacilityDetailRequest req) {
        FacilityDetail facDetail = db.getFacility(req.facName);
        if (facDetail == null) {
            return ViewFacilityDetailResponse.failed("There is no such facility.");
        } 
        else {
            return new ViewFacilityDetailResponse(facDetail.name, facDetail.operatingHours, facDetail.address, facDetail.reviews, "");
        }
    }

    /**
     * Processes a request to write a review about a facility.
     *
     * @param req the request to be processed
     * @return the response after processing the given request
     */
    public AddFacilityReviewResponse processAddFacilityReview(AddFacilityReviewRequest req) {
        boolean success = db.addReview(req.facName, req.review);
        if(success){
            return new AddFacilityReviewResponse(true, "");
        }
        else{
            return AddFacilityReviewResponse.failed("There is no such facility.");
        }
    }

    /**
     * Processes a view facility availability request.
     *
     * @param req the request to be processed
     * @return the response after processing the given request
     */
    public ViewFacilityAvailabilityResponse processViewFacilityAvailability(ViewFacilityAvailabilityRequest req) {
        ArrayList<ArrayList<BookingDetail>> bookingList = db.getBookings(req.facName, req.days);
        if (bookingList == null) {
            return ViewFacilityAvailabilityResponse.failed("There is no such facility/ no bookings for this facility.");
        } else {
            ArrayList<ArrayList<ArrayList<String>>> ar = new ArrayList<ArrayList<ArrayList<String>>>();
            int startHour, startMin;
            for (int i = 0; i < bookingList.size(); i++) {
                ar.add(new ArrayList<ArrayList<String>>());

                for (int j = 0; j < bookingList.get(i).size(); j++) {
                    // Content of inner most array: facName, bookID, day, startHour, startMin, endHour, endMin, duration
                    ArrayList<String> bookingDetail = new ArrayList<String>();

                    startHour = bookingList.get(i).get(j).startHour;
                    startMin = bookingList.get(i).get(j).startMin;
                    bookingDetail.add(Integer.toString(startHour));
                    bookingDetail.add(Integer.toString(startMin));

                    double duration = bookingList.get(i).get(j).duration;
                    ArrayList<String> endTime = convertDurationToEndTime(startHour, startMin, duration);

                    bookingDetail.add(endTime.get(0));
                    bookingDetail.add(endTime.get(1));
                    bookingDetail.add(Double.toString(duration));
                    ar.get(i).add(bookingDetail);
                }
            }
            // broadcast(String.format("Someone queried facility availability for %s ", req.facName));
            return new ViewFacilityAvailabilityResponse(ar, "");
        }
    }

    /**
     * Processes a request to view all bookings made by the client.
     *
     * @param req the request to be processed
     * @return the response after processing the given request
     */
    public ViewPersonalBookingsResponse processViewPersonalBookings(ViewPersonalBookingsRequest req){
        ArrayList<ArrayList<String>> resp = new ArrayList<ArrayList<String>>();
        for(int i = 0; i < req.length; i++){
            String facName = req.facNameList.get(i);
            int day = req.dayList.get(i);
            int id = req.idList.get(i);
            BookingDetail bd = db.getOneBooking(facName, day, id);
            if(bd == null){
                continue;
            }
            ArrayList<String> bdStr = new ArrayList<String>();
            bdStr.add(facName);
            bdStr.add(Integer.toString(id));
            bdStr.add(Integer.toString(day));
            bdStr.add(Integer.toString(bd.startHour));
            bdStr.add(Integer.toString(bd.startMin));
            ArrayList<String> endTime = convertDurationToEndTime(bd.startHour, bd.startMin, bd.duration);
            bdStr.add(endTime.get(0));
            bdStr.add(endTime.get(1));
            resp.add(bdStr);
        }
        return new ViewPersonalBookingsResponse(resp, "");
    }
    
    /**
     * Processes a request to make a booking for a facility.
     *
     * @param req the request to be processed
     * @return the response after processing the given request
     */
    public AddFacilityBookingResponse processAddFacilityBooking(AddFacilityBookingRequest req) {
        int bookingId = nextAvailableBookingId++;
        
        double duration = req.endHour - req.startHour;
        if(req.endMin - req.startMin == 30){
            duration += 0.5;
        }
        else if(req.startMin - req.endMin == 30){
            duration -= 0.5;
        }

        BookingDetail bookingDetail =  new BookingDetail(req.facName, bookingId, req.day, req.startHour, req.startMin, duration/*req.endHour, req.endMin*/);
        boolean success = db.addBooking(req.facName, bookingDetail);
        if(success){
            return AddFacilityBookingResponse.failed("Booking failed.");
        }
        else{
            return new AddFacilityBookingResponse(bookingId, true, "");
        }
    }

    /**
     * Processes a request to modify a booking for a facility.
     *
     * @param req the request to be processed
     * @return the response after processing the given request
     */
    public ModifyFacilityBookingResponse processModifyFacilityBooking(ModifyFacilityBookingRequest req) {
        // if offset is positive, means client is requesting to push the booking timing to a later time
        BookingDetail oldBookingDetail = db.getOneBooking(req.facName, req.day, req.bookingId);
        if(oldBookingDetail == null){
            return ModifyFacilityBookingResponse.failed("Booking doesn't exist");
        }
        boolean deleteSuccess = db.deleteBooking(req.facName, oldBookingDetail);
        if(!deleteSuccess){
            return ModifyFacilityBookingResponse.failed("Booking modification failed.");
        }
        BookingDetail newBookingDetail = oldBookingDetail;
        if(req.offset > 0){
            if(req.offset % 2 == 1){
                if(oldBookingDetail.startMin == 30){
                    newBookingDetail.startMin = 0;
                    req.offset += 1;
                }
                else{
                    newBookingDetail.startMin = 30;
                }
            }
            newBookingDetail.startHour += (req.offset / 2);
        }
        else{
            if(req.offset % 2 == 1){
                if(oldBookingDetail.startMin == 30){
                    newBookingDetail.startMin = 0;
                    req.offset -= 1;
                }
                else{
                    newBookingDetail.startMin = 30;
                }
            }
            newBookingDetail.startHour -= (req.offset / 2);
        }
        boolean addSuccess = db.addBooking(req.facName, newBookingDetail);
        if(addSuccess){
            return new ModifyFacilityBookingResponse(newBookingDetail.bookingId, addSuccess, "");
        }
        else{
            return ModifyFacilityBookingResponse.failed("Booking modification failed.");
        }
    }

    private ArrayList<String> convertDurationToEndTime(int startHour, int startMin, Double duration){
        ArrayList<String> endTime = new ArrayList<String>();
        int endHour, endMin;
        String durationStr = String.valueOf(duration);
        int indexOfDecimal = durationStr.indexOf(".");
        String minStr = durationStr.substring(indexOfDecimal);
        if(minStr == "30"){
            if(startMin == 30){
                endHour = startHour + (int)Math.floor(duration) + 1;
                endMin = 0;
            }
            else{
                endHour = startHour;
                endMin = 30;
            }
        }
        else{
            endHour = startHour + (int)Math.floor(duration);
            endMin = startMin;
        }
        endTime.add(Integer.toString(endHour));
        endTime.add(Integer.toString(endMin));
        return endTime;
    }

    // private void broadcast(String info) {
    //     purgeListeners();
    //     System.out.println(info);
    //     Response<MonitorUpdateResponse> resp = new Response<>(new ResponseHeader(UUID.randomUUID(), Status.OK),
    //             Optional.of(new MonitorUpdateResponse(info)));
    //     listeners.forEach((socketAddress, x) -> {
    //         transport.send(socketAddress, resp);
    //     });
    // }

    // private void purgeListeners() {
    //     listeners.entrySet().removeIf(x -> x.getValue().isBefore(Instant.now()));
    // }
}
