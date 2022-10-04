import Lec05_ZMQ.PUB_SUB.Lec05Publisher;
import Lec05_ZMQ.PUB_SUB.Lec05Subscriber;
import Lec05_ZMQ.REQ_RES.Lec05BasicClient;
import Lec05_ZMQ.REQ_RES.Lec05BasicServer;

public class ZMQApplication {
    public static void main(String[] args){
        Lec05Publisher server = new Lec05Publisher();
        server.start();

        for(int i = 0; i<1000; i++){
            Lec05Subscriber client1 = new Lec05Subscriber("왕자님" + String.valueOf(i));
            client1.start();
        }
    }
}
