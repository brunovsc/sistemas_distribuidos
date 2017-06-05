package third_lab;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MulticastReceiver {
    
    public static void main(String[] args) {
        MulticastSocket socket = null;
        DatagramPacket inPacket = null;
        DatagramPacket outPacket = null;
        byte[] inBuf = new byte[256];
        byte[] outBuf = new byte[256];
        final int PORT = 8888;

        try {
            //join multicast group
            socket = new MulticastSocket(PORT);
            InetAddress address = InetAddress.getByName("224.2.2.3");
            socket.joinGroup(address);

            Scanner s = new Scanner(System.in);
            String answer;
            while (true) {
                inPacket = new DatagramPacket(inBuf, inBuf.length);
                System.out.println("Waiting for message... ");
                socket.receive(inPacket);
                String msg = new String(inBuf, 0, inPacket.getLength());
                System.out.println("From " + inPacket.getAddress() + " Msg : " + msg);

                System.out.print("Answer: ");
                answer = s.nextLine();
                outBuf = answer.getBytes();

                outPacket = new DatagramPacket(outBuf, outBuf.length, inPacket.getAddress(), inPacket.getPort());
                socket.send(outPacket);
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}