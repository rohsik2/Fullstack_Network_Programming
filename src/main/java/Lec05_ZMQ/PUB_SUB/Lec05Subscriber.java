package Lec05_ZMQ.PUB_SUB;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Lec05Subscriber extends Thread{
    String myName;

    public Lec05Subscriber(String myname){
        myName = myname;
    }

    public void run(){
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.SUB);
        socket.connect("tcp://localhost:5556");
        socket.subscribe(ZMQ.SUBSCRIPTION_ALL);
        while(true){
            System.out.println(myName + " : " + socket.recvStr(0));
        }

    }
}
