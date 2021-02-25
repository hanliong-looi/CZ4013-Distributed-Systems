package cz4013.client;

import cz4013.shared.container.BufferPool;
import cz4013.shared.rpc.Transport;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.time.Duration;
import java.util.Map;

public class Main {
  public static void main(String[] args) throws SocketException {
    Map<String, String> env = System.getenv();
    String clientHost = env.getOrDefault("CLIENT_HOST", "0.0.0.0");
    String serverHost = env.getOrDefault("SERVER_HOST", "127.0.0.1");
    int clientPort = Integer.parseInt(env.getOrDefault("CLIENT_PORT", "12741"));
    int serverPort = Integer.parseInt(env.getOrDefault("SERVER_PORT", "12740"));
    Duration timeout = Duration.ofSeconds(Integer.parseInt(env.getOrDefault("TIMEOUT_SEC", "5")));
    int maxAttempts = Integer.parseInt(env.getOrDefault("MAX_ATTEMPTS", "5"));
    DatagramSocket socket = new DatagramSocket(new InetSocketAddress(clientHost, clientPort));
    socket.setSoTimeout((int) timeout.toMillis());
    String MANUAL = "----------------------------------------------------------------\n" +
      "Please choose a service by typing [1-8]:\n" +
      "1: View availability of a facility\n" +
      "2: Book facility\n" +
      "3: Change booking\n" +
      "4: Monitor availability of a facility\n" +
      "5: View facility information\n" +
      "6: Print the manual\n" +
      "0: Stop the client\n";

    BankClient bankClient = new BankClient(new Client(
      new Transport(socket, new BufferPool(8192, 1024)),
      new InetSocketAddress(serverHost, serverPort), maxAttempts));

    boolean shouldStop = false;
    System.out.print(MANUAL);
    while (!shouldStop) {
      int userChoice = askUserChoice();
      try {
        switch (userChoice) {
          case 1:
            bankClient.runViewFacilityAvailability();
            break;
          case 2:
            bankClient.runBookFacility();
            break;
          case 3:
            bankClient.runChangeBooking();
            break;
          case 4:
            bankClient.runMonitorFacility();
            break;
          case 5:
            bankClient.runViewFacilityInformation();
            break;
          case 6:
            System.out.println(MANUAL);
            break;
          case 0:
            shouldStop = true;
            break;
          default:
            System.out.println("Invalid choice!");
            break;
        }
      } catch (NoResponseException e) {
        System.out.println("No response received.");
      } catch (FailedRequestException e) {
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
}
