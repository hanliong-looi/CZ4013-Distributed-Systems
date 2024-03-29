package cz4013.server;

import cz4013.server.rpc.Router;
import cz4013.server.service.FacilityService;
import cz4013.shared.container.BufferPool;
import cz4013.shared.container.LruCache;
import cz4013.shared.request.*;
import cz4013.shared.response.Response;
import cz4013.shared.rpc.RawMessage;
import cz4013.shared.rpc.Transport;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws SocketException{
        String host = "172.20.10.2";
        int port = 12740;

        //determines whether the server uses at-least-once or at-most-once semantics
        boolean atLeastOnce = true;
        
        //set probability of packet loss to simulate loss of message during transmission
        double packetLossProb = 0.3;

        BufferPool pool = new BufferPool(8192, 1024);
        DatagramSocket serverSocket = new DatagramSocket(new InetSocketAddress(host, port));
        Transport serverTransport = new Transport(serverSocket, pool);
        System.out.printf("Listening on Host: %s, Port:%d\n", host, port);

        FacilityService facService = new FacilityService(serverTransport);
        LruCache cache = new LruCache<>(atLeastOnce ? 0: 1024);
        Router router = new Router(cache);
        router.bind("viewFacilityAvailability", facService::processViewFacilityAvailability, new ViewFacilityAvailabilityRequest(){})
            .bind("viewFacilityAvailabilityArray", facService::processViewFacilityAvailabilityArray, new ViewFacilityAvailabilityArrayRequest(){})
            .bind("viewFacilityDetail", facService::processViewFacilityDetail, new ViewFacilityDetailRequest(){})
            .bind("viewPersonalBookings", facService::processViewPersonalBookings, new ViewPersonalBookingsRequest(){})
            .bind("addFacilityReview", facService::processAddFacilityReview, new AddFacilityReviewRequest(){})
            .bind("addFacilityBooking", facService::processAddFacilityBooking, new AddFacilityBookingRequest(){})
            .bind("modifyFacilityBooking", facService::processModifyFacilityBooking, new ModifyFacilityBookingRequest(){})
            .bind("monitor", facService::processMonitor, new MonitorFacilityAvailabilityRequest(){});

        for(; ; ){
            try(RawMessage req = serverTransport.recv()){
                //simulate chances of message loss during transmission
                if(Math.random() < packetLossProb){
                    System.out.printf("Simulated message loss. Dropped REQUEST from  %s.\n", req.remote);
                    continue;
                }
                Response<?> resp = router.route(req);
                //simulate chances of message loss during transmission
                if (Math.random() < packetLossProb) {
                    System.out.printf("Simulated message loss. Dropped RESPONSE to %s.\n", req.remote);
                } 
                else {
                    serverTransport.send(req.remote, resp);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
