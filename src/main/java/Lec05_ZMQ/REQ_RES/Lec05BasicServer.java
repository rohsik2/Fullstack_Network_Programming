package Lec05_ZMQ.REQ_RES;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class Lec05BasicServer extends Thread {
    public void run(){
        try(ZContext context = new ZContext() ){
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://*:5555");

            while(!Thread.currentThread().isInterrupted()){
                byte[] reply = socket.recv(0);

                System.out.println(
                        "Recieved : " + new String(reply, ZMQ.CHARSET)
                );
                String res = "Good "+new String(reply, ZMQ.CHARSET);
                socket.send(res.getBytes(ZMQ.CHARSET), 0);
            }
        }
    }
}
