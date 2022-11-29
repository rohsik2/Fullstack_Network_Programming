package Lec05_ZMQ.PUB_SUB;

import java.nio.charset.StandardCharsets;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Lec05Subscriber extends Thread{
    int zip_filter;

    public Lec05Subscriber(){
        zip_filter = 10001;
    }

    public Lec05Subscriber(int zip_filter){
        this.zip_filter = zip_filter;
    }

    public void run(){
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.SUB);

        System.out.println("Collecting updates from weather server...");
        socket.connect("tcp://localhost:6001");

        // Subscribe to zipcode, default is NYC, 10001
        socket.subscribe(String.format("%05d", zip_filter).getBytes(ZMQ.CHARSET));

        int total_temp =0;
        for(int i =0; i<20; i++){
            String code = socket.recvStr();
            if(code == null)
                continue;
            String result = socket.recvStr();
            String[] codes = result.split(" ");
            String zipcode = codes[0];
            String temperature = codes[1];
            String relhumidity = codes[2];
            total_temp += Integer.parseInt(temperature);
            System.out.printf("Receive temparature for zipcode '%d' was %s F\n", zip_filter, temperature);
        }
        System.out.printf("    Average temperature for zipcode '%d' was %d F\n", zip_filter, total_temp/21);

    }
}
