package Lec05_ZMQ.PULL_PUSH2;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Lec05PullPushServerV2 extends Thread{
    public void run(){
        ZContext ctx = new ZContext();
        ZMQ.Socket publisher = ctx.createSocket(SocketType.PUB);
        publisher.bind("tcp://*:5557");
        ZMQ.Socket collector = ctx.createSocket(SocketType.PULL);
        collector.bind("tcp://*:5558");

        while(true){
            String message = collector.recvStr();
            System.out.println("server: publishing update ==> " + message);
            publisher.send(message);
        }

    }
}
