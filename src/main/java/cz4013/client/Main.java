package cz4013.client;

import cz4013.shared.container.BufferPool;
import cz4013.shared.rpc.Transport;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.time.Duration;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws SocketException{
        
        //gets system env (inherits a clone environment of its parent process), return keys to values pairs
        Map<String, String> env = System.getenv(); 
        
        //returns client host/server host; if no mapping, use 0.0.0.0/127.0.0.1
        String clientHost = env.getOrDefault("CLIENT_HOST", "0.0.0.0");
        String serverHost = env.getOrDefault("SERVER_HOST", "127.0.0.1");
        
        //returns client and server port numbers
        int clientPort = Integer.parseInt(env.getOrDefault("CLIENT_PORT", "12741"));
        int serverPort = Integer.parseInt(env.getOrDefault("SERVER_PORT", "12740"));

        //set timeout of 5 secs
        Duration timeout = Duration.ofSeconds(Integer.parseInt(env.getOrDefault("TIMEOUT_SEC", "5")));
        //set max no of attempts to connect
        int maxAttempts = Integer.parseInt(env.getOrDefault("MAX_ATTEMPTS", "5"));

        //Inetsocket: Creates a socket address from a hostname and a port number.
        //Datagramsocket: Creates a datagram socket, bound to the specified local socket address.
        DatagramSocket socket = new DatagramSocket(new InetSocketAddress(clientHost, clientPort));
        
        //call to receive() this DatagramSocket will block for 5 seconds
        socket.setSoTimeout((int) timeout.toMillis());

        String MANUAL = "----------------------------------------------------------------\n" +
        "Please choose a service by typing [1-7]:\n" +
        "1: View availability of a facility\n" +
        "2: Book facility\n" +
        "3: Change booking\n" +
        "4: Monitor availability of a facility\n" +
        "5: View facility information\n" +
        "6: Write review for facility\n" +
        "7: Print the manual\n" +
        "0: Stop the client\n";
        
        FacilityClient facilityClient = new FacilityClient(new Client(
            new Transport(socket, new BufferPool(8192, 1024)), 
            new InetSocketAddress(serverHost, serverPort), maxAttempts)
        );
        
        boolean shouldStop = false;
        while (!shouldStop) {
            System.out.print(MANUAL);
            int userChoice = askUserChoice();
            try {
              switch (userChoice) {
                case 1:
                    facilityClient.runViewFacilityAvailability();
                    break;
                case 2:
                    facilityClient.runAddFacilityBooking();
                    break;
                case 3:
                    facilityClient.runModifyFacilityBooking();
                    break;
                case 4:
                    facilityClient.runMonitorFacilityAvailability();
                    break;
                case 5:
                    facilityClient.runViewFacilityDetail();
                    break;
                case 6:
                    facilityClient.runAddFacilityReview();
                    break;
                case 7:
                    System.out.println(MANUAL);
                    break;
                case 0:
                    shouldStop = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
                    break;
              }
              backToManual();
            }
            catch (NoResponseException e) {
                System.out.println("No response received.");
            }
            catch (FailedRequestException e) {
                System.out.printf("Failed to send request with error %s \n", e.status);
            }
        }
        Util.closeReader();
        System.out.println("Stopping client...");
    }

    private static int askUserChoice() {
        System.out.print("\n----------------------------------------------------------------\n" +
          "Your choice = ");
        return Util.safeReadInt();
    }

    private static void backToManual(){
        while(true){
            System.out.println("\n----------------------------------------------------------------");
            System.out.println("Enter '1' to go back to the menu");
            int choice = Util.safeReadInt();
            if(choice == 1){
                return;
            }
            else{
                System.out.println("Invalid input!");
            }
        }
    }
}
