package cz4013.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class UDPClient {
    /*
     * The server port to which the client socket is going to connect
     */
    public final static int SERVICE_PORT = 50001;

    public static void main(String[] args) throws IOException{
        try{
            /* Instantiate client socket. 
            No need to bind to a specific port */
            DatagramSocket clientSocket = new DatagramSocket();

            // Set client socket timeout to 1000ms
            clientSocket.setSoTimeout(5000);
            
            // Get the IP address of the server
            InetAddress serverIPAddress = InetAddress.getByName("172.20.236.89");

            // Get the IP address and hostname of the client
            InetAddress clientIPAddress = InetAddress.getLocalHost();
            String hostname = clientIPAddress.getHostName();
            
            // Creating corresponding buffers
            byte[] sendingDataBuffer = new byte[1024];
            byte[] receivingDataBuffer = new byte[1024];
            
            /* Converting data to bytes and 
            storing them in the sending buffer */
            String connectedSentence = "Client " + hostname + " with IP Address: " + clientIPAddress + " has connected to the server.";
            sendingDataBuffer = connectedSentence.getBytes();
            
            // Creating a UDP packet 
            DatagramPacket sendingPacket = new DatagramPacket(sendingDataBuffer,sendingDataBuffer.length,serverIPAddress, SERVICE_PORT);
            
            // sending UDP packet to the server
            clientSocket.send(sendingPacket);
            
            // Get the server response 
            DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer,receivingDataBuffer.length);
            clientSocket.receive(receivingPacket);
            
            // Printing the received data
            String receivedData = new String(receivingPacket.getData());
            System.out.println("Sent from the server: \n"+receivedData);

            try{
                Scanner sc = new Scanner(System.in);
                while(true){
                    // Creating corresponding buffers
                    sendingDataBuffer = new byte[1024];
                    receivingDataBuffer = new byte[1024];
                    String message = "Default message";
                    System.out.println("message");
                    int choice = sc.nextInt();
                    boolean loopChoice = true;
                    boolean resendMsg = true;

                    while(loopChoice){
                        switch(choice){
                            case 1:
                                message = "Client selected 1";
                                loopChoice = false;
                                break;
                            case 2:
                                message = "Client selected 2";
                                loopChoice = false;
                                break;
                            case 3:
                                message = "Client selected 3, exiting menu";
                                loopChoice = false;
                                break;
                            default:
                                System.out.println("Please select a valid number");
                                loopChoice = true;
                                break;
                        }
                    }
                    
                    while(resendMsg){
                        try{
                            sendingDataBuffer = new byte[1024];
                            receivingDataBuffer = new byte[1024];

                            sendingDataBuffer = message.getBytes();

                            // Creating a UDP packet 
                            sendingPacket = new DatagramPacket(sendingDataBuffer,sendingDataBuffer.length,serverIPAddress, SERVICE_PORT);
                            
                            // Sending UDP packet to the server
                            clientSocket.send(sendingPacket);

                            // Get the server response 
                            receivingPacket = new DatagramPacket(receivingDataBuffer,receivingDataBuffer.length);
                            clientSocket.receive(receivingPacket);
                            
                            // Printing the received data
                            receivedData = new String(receivingPacket.getData());
                            System.out.println("Sent from the server: \n"+receivedData);
                            
                            // Break out of loop if message is received
                            resendMsg = false;
                            break;
                        }
                        catch(SocketTimeoutException e){
                            System.out.println("Timeout reached: " + e);
                            System.out.println("Resending data package... ");
                        }
                    }

                    // Exit menu loop if user selects "Quit"
                    if(choice == 3){
                        break;
                    }
                }
                sc.close();
            }
            catch(SocketException e) {
                e.printStackTrace();
            }
            
            // Closing the socket connection with the server
            clientSocket.close();
        }
        catch(SocketException e) {
            e.printStackTrace();
        }
    }
}
