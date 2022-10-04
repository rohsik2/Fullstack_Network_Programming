package Lec05_ZMQ.REQ_RES;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.util.Scanner;

public class Lec05BasicClient extends Thread{
    public void run(){
        ZContext context = new ZContext();
        ZMQ.Socket queue = context.createSocket(ZMQ.REQ);
        queue.connect("tcp://localhost:5555");
        Scanner s1 = new Scanner(System.in);
        while(true){
            String message = s1.next();
            queue.send(message.getBytes(ZMQ.CHARSET));
            String recv = queue.recvStr(0);
            System.out.println("Received reply " + recv);
        }
    }

}
