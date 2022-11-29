package Lec05_ZMQ.PUB_SUB;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.util.Random;

public class Lec05Publisher extends Thread {
    public void run() {
        System.out.println("Publishing updates at weather server...");

        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.PUB);
        socket.bind("tcp://localhost:6001");
        Random random = new Random();

        while (true) {
            int zipcode = random.nextInt(100000 - 1) + 1;
            int temperature = random.nextInt(80 + 135) - 80;
            int relhumidity = random.nextInt(50) + 10;
            socket.send(String.format("%05d", zipcode), ZMQ.SNDMORE);
            socket.send(String.format("%s %s %s", zipcode, temperature, relhumidity));
        }

    }
}
