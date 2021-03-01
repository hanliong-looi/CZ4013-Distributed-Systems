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
 * This class provides various facility services and handle interactions with DB.
 */
public class FacilityService {
    private Database db = new Database();
    private Transport transport;
    private Map<SocketAddress, Instant> listeners = new HashMap<>();

    /**
     * Constructs a {@code BankService}.
     *
     * @param transport transport layer to be used to send data for registered monitoring clients
     */
    public FacilityService(Transport transport) {
        this.transport = transport;
    }

    /**
   * Processes a view facility availability request.
   *
   * @param req the request to be processed
   * @return the response after processing the given request
   */
    public ViewFacilityAvailabilityResponse processViewFacilityAvailability(ViewFacilityAvailabilityRequest req) {
        ArrayList<ArrayList<BookingDetail>> bookingList = db.getBookings(req.facName, req.days);
        if(bookingList == null){
            return ViewFacilityAvailabilityResponse.failed("There is no such facility/ no bookings for this facility.");
        }
        else{
            ArrayList<ArrayList<ArrayList<String>>> ar = new ArrayList<ArrayList<ArrayList<String>>>();
            for(int i = 0; i < bookingList.size(); i++){
                ar.add(new ArrayList<ArrayList<String>>());

                for(int j = 0; j < bookingList.get(i).size(); j++){
                    //BookingDetail: facName, bookID, day, startHour, startMin, endHour, endMin
                    ArrayList<String> bookingDetail = new ArrayList<String>();
                    bookingDetail.add(Integer.toString(bookingList.get(i).get(j).startHour));
                    bookingDetail.add(Integer.toString(bookingList.get(i).get(j).startMin));
                    bookingDetail.add(Integer.toString(bookingList.get(i).get(j).endHour));
                    bookingDetail.add(Integer.toString(bookingList.get(i).get(j).endMin));
                    ar.get(i).add(bookingDetail);
                }
            }
            return new ViewFacilityAvailabilityResponse(ar, "");
        }

        // AccountDetail accountDetail = db.query(req.accountNumber);
        // if (accountDetail == null) {
        // return QueryResponse.failed("This account number doesn't exist");
        // }
        // if (!req.password.equals(accountDetail.password)) {
        // return QueryResponse.failed("Wrong password");
        // }
        // broadcast(String.format("Someone queries account %d", req.accountNumber));
        // return new QueryResponse(accountDetail.name, accountDetail.currency, accountDetail.amount, true, "");
    }
}
