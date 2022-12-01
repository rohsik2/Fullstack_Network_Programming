package Lec05_ZMQ.DEALER_ROUTER;

import Lec05_ZMQ.SleepUtility;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;

public class Lec05DealerRouterClient extends Thread{
    Integer id ;
    public int reqs = 0;
    private Poller poller;
    public Lec05DealerRouterClient(int id){
        this.id = id;
    }

    public void recvHandler(){
        while(true){
            if(poller.poll(1000) == 0){
                String msg = poller.getSocket(0).recvStr();
                System.out.println("%d received: %d".formatted(id, msg));
            }
        }
    }

    public void run(){
        ZContext context = new ZContext();

        String ident = id.toString();

        ZMQ.Socket socket = context.createSocket(SocketType.DEALER);
        socket.setIdentity(ident.getBytes(ZMQ.CHARSET));
        socket.connect("tcp://localhost:5570");

        System.out.printf("Client %d started\n", id);
        poller = context.createPoller(1);
        poller.register(socket, ZMQ.Poller.POLLIN);

        new Thread(() ->{
            recvHandler();
        }).start();

        while(true) {
            socket.send(String.valueOf(reqs++));
            System.out.println("Client %d send Req #%d..".formatted(id, reqs));
            SleepUtility.doSleep(1000);
        }
    }
}
