package Lec05_ZMQ.PULL_PUSH2;

import Lec05_ZMQ.SleepUtility;
import java.util.Random;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

public class Lec05PullPushClientV2 extends Thread{

    String clientID;
    public Lec05PullPushClientV2(String clientID){
        this.clientID = clientID;
    }

    public void run(){

        ZContext ctx = new ZContext();

        Socket subscriber = ctx.createSocket(SocketType.SUB);
        subscriber.connect("tcp://localhost:5557");

        Socket publisher = ctx.createSocket(SocketType.PUSH);
        publisher.connect("tcp://localhost:5558");

        Poller poller = ctx.createPoller(2);
        poller.register(subscriber);
        poller.register(publisher);

        Random random = new Random();
        while(true){
            if (poller.poll(100) == 0){
                String message = subscriber.recvStr();
                System.out.printf("%s: receive status =? %s\n", clientID, message);
            }
            else{
                int rand = random.nextInt(100) + 1;
                if(rand < 10){
                    SleepUtility.doSleep(1000);
                    String msg = "(" + clientID + ":ON)";
                    publisher.send(msg);
                    System.out.println("%s: send status - activated".formatted(clientID));
                }
                else if (rand > 90){
                    SleepUtility.doSleep(1000);
                    String msg = "(" + clientID + ":OFF)";
                    publisher.send(msg);
                    System.out.println("%s: send status - deactivated".formatted(clientID));
                }
            }
        }
    }
}
