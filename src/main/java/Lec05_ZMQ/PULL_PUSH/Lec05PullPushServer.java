package Lec05_ZMQ.PULL_PUSH;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Lec05PullPushServer extends Thread{
    public void run(){
        ZContext context = new ZContext();
        ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
        publisher.bind("tcp://*:5568");
        ZMQ.Socket collector = context.createSocket(SocketType.PULL);
        collector.bind("tcp://*:5569");

        while(true){
            String message = collector.recvStr();
            System.out.println("I: publishing update " + message);
            publisher.send(message);
        }
    }
}
