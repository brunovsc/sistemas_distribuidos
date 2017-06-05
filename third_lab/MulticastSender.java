package third_lab;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MulticastSender {
    
    public static void main(String[] args) {
        DatagramSocket socket = null;
        DatagramPacket outPacket = null;
        DatagramPacket inPacket = null;
        byte[] outBuf;
        byte[] inBuf = new byte[256];
        final int PORT = 8888;
 
        try {
            socket = new DatagramSocket();
            
            System.out.print("Message: ");
            Scanner s = new Scanner(System.in);
            String msg = s.nextLine();
            outBuf = msg.getBytes();

            InetAddress address = InetAddress.getByName("224.2.2.3");
            outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT);

            socket.send(outPacket);

            System.out.println("Sending message: " + msg);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {}
           
            // Answer
            System.out.println("Waiting for answer... ");
            inPacket = new DatagramPacket(inBuf, inBuf.length);
            socket.receive(inPacket);
            String answer = new String(inBuf, 0, inPacket.getLength());
            System.out.println("Answer: " + answer);
            socket.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}
