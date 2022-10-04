package Lec05_ZMQ.PUB_SUB;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Lec05Publisher extends Thread{
    public void run(){
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.PUB);
        socket.bind("tcp://*:5556");

        while(true){
            String message = "아무거나 보낸다!";
            socket.send(message);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
