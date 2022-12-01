package Lec05_ZMQ.DEALER_ROUTER;

import org.zeromq.*;

import java.util.ArrayList;
import java.util.List;

public class Lec05DealerRouterServer extends Thread{
    int num_server;
    public Lec05DealerRouterServer(int num_server){
        this.num_server = num_server;
    }
    @Override
    public void run(){
        ZContext context = new ZContext();
        ZMQ.Socket frontend = context.createSocket(SocketType.ROUTER);
        frontend.bind("tcp://localhost:5570");

        ZMQ.Socket backend = context.createSocket(SocketType.DEALER);
        backend.bind("inproc://backend");

        List<ServerWorker> workers = new ArrayList<>();
        for(int i = 0; i< num_server; i++){
            ServerWorker worker = new ServerWorker(context, i);
            worker.start();
            workers.add(worker);
        }

        ZMQ.proxy(frontend, backend, null);

        frontend.close();
        backend.close();
        context.close();

    }
}

class ServerWorker extends Thread{
    ZContext context;
    Integer id;
    public ServerWorker (ZContext context, int id){
        this.context = context;
        this.id = id;
    }

    public void run(){
        ZMQ.Socket worker = this.context.createSocket(SocketType.DEALER);
        worker.setIdentity(String.valueOf(id).getBytes(ZMQ.CHARSET));
        worker.connect("inproc://backend");

        System.out.printf("Worker#%d started \n", id);

        while(!Thread.currentThread().isInterrupted()){
            ZMsg msg = ZMsg.recvMsg(worker);
            ZFrame ident = msg.pop();
            ZFrame message = msg.pop();

            System.out.format("Worker#%d received %s from %s\n", id, message.getString(ZMQ.CHARSET), ident.getString(ZMQ.CHARSET));
            msg.destroy();;
            ident.destroy();
            message.destroy();
        }
    }
}
