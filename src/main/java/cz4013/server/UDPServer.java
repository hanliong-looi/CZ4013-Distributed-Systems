package cz4013.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
// import java.io.BufferedReader;
// import java.io.InputStreamReader;

public class UDPServer {
    // Server UDP socket runs at this port
    public final static int SERVICE_PORT = 50001;

    public static void main(String[] args) throws IOException {
        try {
            // Instantiate a new DatagramSocket to receive responses from the client
            DatagramSocket serverSocket = new DatagramSocket(SERVICE_PORT);

            /*
             * Create buffers to hold sending and receiving data. It temporarily stores data
             * in case of communication delays
             */
            byte[] receivingDataBuffer = new byte[1024];
            byte[] sendingDataBuffer = new byte[1024];

            /*
             * Instantiate a UDP packet to store the client data using the buffer for
             * receiving data
             */
            DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            System.out.println("Waiting for a client to connect...");

            // Receive data from the client and store in inputPacket
            serverSocket.receive(inputPacket);

            // Printing out the client sent data
            String receivedData = new String(inputPacket.getData());
            System.out.println("Sent from the client: " + receivedData);

            /*
             * Convert client sent data string to upper case, Convert it to bytes and store
             * it in the corresponding buffer.
             */
            sendingDataBuffer = receivedData.toUpperCase().getBytes();

            // Obtain client's IP address and the port
            InetAddress senderAddress = inputPacket.getAddress();
            int senderPort = inputPacket.getPort();

            // Create new UDP packet with data to send to the client
            DatagramPacket outputPacket = new DatagramPacket(
            sendingDataBuffer, sendingDataBuffer.length,
            senderAddress,senderPort
            );

            // Send the created packet to client
            serverSocket.send(outputPacket);

            // attempt at while loop
            receivedData = "";
            // byte[] buf = new byte[1024];

            while (true) {
                sendingDataBuffer = new byte[1024];
                receivingDataBuffer = new byte[1024];

                inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
                System.out.println("Waiting for a client to send data pack...");

                // Receive data from the client and store in inputPacket
                serverSocket.receive(inputPacket);

                // Printing out the client sent data
                receivedData = new String(inputPacket.getData());
                System.out.println("Sent from the client: " + receivedData);

                /*
                * Convert client sent data string to upper case, Convert it to bytes and store
                * it in the corresponding buffer.
                */
                sendingDataBuffer = receivedData.toUpperCase().getBytes();

                // Obtain client's IP address and the port
                senderAddress = inputPacket.getAddress();
                senderPort = inputPacket.getPort();

                // Create new UDP packet with data to send to the client
                outputPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, senderAddress,senderPort);

                // Send the created packet to client
                serverSocket.send(outputPacket);

                System.out.println(receivedData);
                System.out.println(receivedData.length());
                
                if ("Client selected 3, exiting menu".equals(receivedData))
                    break;

            }
            serverSocket.close();
            // end of while loop attempt

            // Close the socket connection
            // serverSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}

