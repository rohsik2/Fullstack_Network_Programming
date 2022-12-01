package Lec05_ZMQ.REQ_REP;

import Lec05_ZMQ.SleepUtility;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class Lec05BasicServer extends Thread {
    public void run() {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://*:5555");
            while (!Thread.currentThread().isInterrupted()) {
                byte[] reply = socket.recv(0);
                System.out.println(
                        "Received request: " + new String(reply, ZMQ.CHARSET)
                );
                SleepUtility.doSleep(1000);
                socket.send("World".getBytes(ZMQ.CHARSET), 0);
            }
        }
    }
}
