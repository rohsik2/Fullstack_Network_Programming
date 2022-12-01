package Lec05_ZMQ.REQ_REP;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.util.Scanner;

public class Lec05BasicClient extends Thread{
    public void run(){
        ZContext context = new ZContext();
        ZMQ.Socket sock = context.createSocket(SocketType.REQ);
        sock.connect("tcp://localhost:5555");
        Scanner s1 = new Scanner(System.in);
        for(int i =0; i<10; i++){
            System.out.printf("Sending request %d …\n", i);
            sock.send("Hello");
            String message = sock.recvStr();
            System.out.printf("Received reply %d [ %s ]\n" , i, message);
            try{sleep(1000);} catch(Exception e){e.printStackTrace();}
        }
    }
}
