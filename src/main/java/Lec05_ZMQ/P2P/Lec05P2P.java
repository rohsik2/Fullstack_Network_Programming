package Lec05_ZMQ.P2P;

import Lec05_ZMQ.SleepUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

public class Lec05P2P extends Thread {
    public static Boolean global_flag_shutdown = false;

    String username;

    public Lec05P2P(String username) {
        this.username = username;
    }


    String searchNameServer(String ip_mask, String local_ip_addr, String port_nameserver) {
        ZContext context = new ZContext();
        Socket req = context.createSocket(SocketType.SUB);
        Poller poller = context.createPoller(1);
        System.out.println("name server searching " + ip_mask + " " + local_ip_addr + " " + port_nameserver);
        for (int last = 1; last < 256; last++) {
            String target_ip_addr = "tcp://%s.%d:%s".formatted(ip_mask, last, port_nameserver);
            System.out.println(target_ip_addr);
            if(req.connect(target_ip_addr)){
                req.subscribe("NAMESERVER");
                break;
            }
        }
        poller.register(req);

        try {
            if (poller.poll(3000) == 0) {
                throw new Exception("Connection time out");
            }
            String res = req.recvStr();
            String[] res_list = res.split(":");
            if (res_list[0].equals("NAMESERVER")) {
                return res_list[1];
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    void beacon_nameserver(String local_ip_addr, String port_nameserver) {
        ZContext context = new ZContext();
        Socket socket = context.createSocket(SocketType.PUB);
        socket.bind("tcp://%s:%s".formatted(local_ip_addr, port_nameserver));
        while (true) {
            try {
                SleepUtility.doSleep(1000);
                String msg = "NAMESERVER:%s".formatted(local_ip_addr);
                socket.send(msg);
            } catch (Exception e) {
                break;
            }
        }
    }

    void user_manager_nameserver(String local_ip_addr, String port_subscribe) {
        List<String[]> user_db = new ArrayList<>();
        ZContext context = new ZContext();
        Socket socket = context.createSocket(SocketType.REP);
        socket.bind("tcp://%s:%s".formatted(local_ip_addr, port_subscribe));
        System.out.println("local p2p db server activated ad tcp://%s:%s".formatted(local_ip_addr, port_subscribe));
        while (true) {
            try {
                String[] user_req = socket.recvStr().split(":");
                user_db.add(user_req);
                System.out.println("user registraion '%s' from '%s'".formatted(user_req[1], user_req[0]));
                socket.send("ok");
            } catch (Exception e) {
                break;
            }
        }
    }

    void relay_server_nameserver(String local_ip_addr, String port_chat_publisher, String port_chat_collector) {
        ZContext context = new ZContext();
        Socket publisher = context.createSocket(SocketType.PUB);
        publisher.bind("tcp://%s:%s".formatted(local_ip_addr, port_chat_publisher));
        Socket collector = context.createSocket(SocketType.PULL);
        collector.bind("tcp://%s:%s".formatted(local_ip_addr, port_chat_collector));
        System.out.println(
                "local p2p relay server activated at tcp://%s:%s & %s.".formatted(local_ip_addr, port_chat_publisher,
                        port_chat_collector));
        //TODO : 여기서부터작
        while (true) {
            try {
                String message = collector.recvStr();
                System.out.println("p2p-relay:<==> " + message);
                publisher.send("RELAY:%s".formatted(message));
            } catch (Exception e) {
                break;
            }
        }
    }

    public String get_local_ip() {
        return "127.0.0.1";
//        try {
//            // Java 방식으로 ip address를 얻어오겠습니다.
//            URL whatismyip = new URL("http://checkip.amazonaws.com");
//            BufferedReader in = new BufferedReader(new InputStreamReader(
//                    whatismyip.openStream()));
//            String ip = in.readLine();
//            // 해당 방식으로 얻어오면 공유기 ip 를 가져오게 되어서 local host로 하드코딩 진행했습니다.
//            return "172.30.1.11";
//        } catch (Exception e) {
//            return "127.0.0.1";
//        }
    }

    @Override
    public void run() {
        String ip_addr_p2p_server = "";
        String port_nameserver = "9001";
        String port_chat_publisher = "9002";
        String port_chat_collector = "9003";
        String port_subscribe = "9004";
        String ip_addr = get_local_ip();
        String ip_mask = ip_addr.substring(0, ip_addr.lastIndexOf("."));

        System.out.println("searching for p2p server.");
        String name_server_ip_addr = searchNameServer(ip_mask, ip_addr, port_nameserver);


        if (name_server_ip_addr == null) {
            ip_addr_p2p_server = ip_addr;
            System.out.println("p2p server is not found, and p2p server mode is activated.");
            String finalIp_addr = ip_addr;
            new Thread(() -> {
                beacon_nameserver(finalIp_addr, port_nameserver);
            }).start();
            System.out.println("p2p beacon server is activated.");
            new Thread(() -> {
                user_manager_nameserver(finalIp_addr, port_subscribe);
            }).start();
            System.out.println("p2p subsciber database server is activated.");
            new Thread(() -> {
                relay_server_nameserver(finalIp_addr, port_chat_publisher, port_chat_collector);
            }).start();
            System.out.println("p2p message relay server is activated.");
        } else {
            ip_addr_p2p_server = name_server_ip_addr;
            System.out.println(
                    "p2p server found at %s, and p2p client mode is activated.".formatted(ip_addr_p2p_server));
        }

        System.out.println("starting user registration procedure.");
        ZContext db_client_context = new ZContext();
        Socket db_client_socket = db_client_context.createSocket(SocketType.REQ);
        db_client_socket.connect("tcp://%s:%s".formatted(ip_addr_p2p_server, port_subscribe));
        db_client_socket.send("%s:%s".formatted(ip_addr, username));

        Poller db_client_poller = db_client_context.createPoller(0);
        db_client_poller.register(db_client_socket);
        if (db_client_poller.poll(3000) != 0) {
            if (db_client_socket.recvStr().equals("ok")) {
                System.out.println("user registration to p2p server completed.");
            }
        } else {
            System.out.println("user registration to p2p server failed");
        }
        System.out.println("starting message transfer procedure");

        ZContext relay_client = new ZContext();
        Socket p2p_rx = relay_client.createSocket(SocketType.SUB);
        p2p_rx.subscribe("RELAY");
        p2p_rx.connect("tcp://%s:%s".formatted(ip_addr_p2p_server, port_chat_publisher));
        Socket p2p_tx = relay_client.createSocket(SocketType.PUSH);
        p2p_tx.connect("tcp://%s:%s".formatted(ip_addr_p2p_server, port_chat_collector));

        System.out.println("starting autonomous message transmit and receive scenario.");
        Poller relayClientPoller = relay_client.createPoller(0);
        relayClientPoller.register(p2p_rx);

        while (true) {
            try {
                if (relayClientPoller.poll(100) != 0) {
                    String message = p2p_rx.recvStr();
                    System.out.printf("p2p-recv::<<== %s:%s\n", message.split(":")[1], message.split(":")[2]);
                } else {
                    int rand = new Random().nextInt(100);
                    if (rand < 10) {
                        SleepUtility.doSleep(3000);
                        String msg = "(" + username + "," + ip_addr + ":ON)";
                        p2p_tx.send(msg);
                        System.out.println("p2p-send::==>>%s".formatted(msg));
                    } else if (rand > 90) {
                        SleepUtility.doSleep(3000);
                        String msg = "(" + username + "," + ip_addr + ":OFF)";
                        p2p_tx.send(msg);
                        System.out.println("p2p-send::==>>%s".formatted(msg));
                    }
                }
            } catch (Exception e) {
                break;
            }
        }
        System.out.println("closing p2p chatting program.");

        global_flag_shutdown = true;
        db_client_socket.close();
        p2p_rx.close();
        p2p_tx.close();
        db_client_context.close();
        relay_client.close();
    }
}
