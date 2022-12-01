package Lec05_ZMQ.PULL_PUSH;

import java.util.Scanner;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.util.Random;
import org.zeromq.ZMQ.Poller;

public class Lec05PullPushClient extends Thread{
    public void run(){
        ZContext context = new ZContext();
        ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
        subscriber.subscribe(ZMQ.SUBSCRIPTION_ALL);
        subscriber.connect("tcp://localhost:5568");
        ZMQ.Socket publisher = context.createSocket(SocketType.PUSH);
        publisher.connect("tcp://localhost:5569");

        Random random = new Random();

        Poller poller = context.createPoller(2);
        poller.register(subscriber);
        poller.register(publisher);

        while(true){
            if (poller.poll(100) == 0){
                String message = subscriber.recvStr();
                System.out.println("I: received message " + message);
            }
            else{
                int rand = random.nextInt(100);
                if(rand < 10) {
                    publisher.send(String.valueOf(rand));
                    System.out.printf("I: sending message %d\n", rand);
                }
            }
        }
    }
}
