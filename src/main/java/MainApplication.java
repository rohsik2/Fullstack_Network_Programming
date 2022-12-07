import Lec05_ZMQ.DEALER_ROUTER.Lec05DealerRouterClient;
import Lec05_ZMQ.DEALER_ROUTER.Lec05DealerRouterServer;
import Lec05_ZMQ.P2P.Lec05P2P;
import Lec05_ZMQ.PUB_SUB.Lec05Publisher;
import Lec05_ZMQ.PUB_SUB.Lec05Subscriber;
import Lec05_ZMQ.PULL_PUSH.Lec05PullPushServer;
import Lec05_ZMQ.PULL_PUSH.Lec05PullPushClient;
import Lec05_ZMQ.PULL_PUSH2.Lec05PullPushClientV2;
import Lec05_ZMQ.PULL_PUSH2.Lec05PullPushServerV2;
import Lec05_ZMQ.REQ_REP.Lec05BasicClient;
import Lec05_ZMQ.REQ_REP.Lec05BasicServer;
import java.net.SocketException;
import java.util.Random;
import java.util.Scanner;
public class MainApplication {

    public static void main(String[] args) throws SocketException {
        System.out.println("실행하고 싶은 프로그래밍의 번호를 알려주세요.");
        int menu = getMenu();
        switch (menu){
            case 1:
                REQ_REP();
                break;
            case 2:
                SUB_PUB();
                break;
            case 3:
                PULL_PUSH();
                break;
            case 4:
                PULL_PUSH_V2();
                break;
            case 5:
                ROUTER_DEALER();
                break;
            case 6:
                P2P_CHAT();
                break;
        }
    }

    public static int getClientOrServer(){
        Scanner s1 = new Scanner(System.in);
        try{
            System.out.println("서버를 실행할지 클라이언트를 실행할지 정해주세요.");
            System.out.println("1.서버\n2.클라이언트");
            int menu = s1.nextInt();
            if(menu != 1 && menu != 2){
                throw new IllegalArgumentException();
            }
            return menu;
        }
        catch(Exception e)
        {
            return getClientOrServer();
        }
    }

    public static void REQ_REP(){
        int menu = getClientOrServer();
        if(menu == 1) {
            Lec05BasicServer basicServer = new Lec05BasicServer();
            basicServer.start();
        }
        if(menu == 2) {
            Lec05BasicClient basicClient = new Lec05BasicClient();
            basicClient.start();
        }
    }

    public static void PULL_PUSH() {

        int menu = getClientOrServer();
        if(menu == 1) {
            Lec05PullPushServer server = new Lec05PullPushServer();
            server.start();
        }
        else {
            Lec05PullPushClient client = new Lec05PullPushClient();
            client.start();
        }
    }

    public static void PULL_PUSH_V2(){

        int menu = getClientOrServer();
        if(menu == 1) {
            Lec05PullPushServerV2 server = new Lec05PullPushServerV2();
            server.start();
        }
        else{
            Random random = new Random();
            String cliId = "Mr handsome" + random.nextInt(100);
            System.out.println("Your Client ID : " + cliId);
            Lec05PullPushClientV2 client = new Lec05PullPushClientV2(cliId);
            client.start();
        }
    }


    public static void SUB_PUB(){

        int menu = getClientOrServer();
        if(menu == 1) {
            Lec05Publisher server = new Lec05Publisher();
            server.start();
        }
        else{
            Random random = new Random();
            String cliId = ""+random.nextInt(10001)+1;
            System.out.println("Your Zip code : " + cliId);

            Lec05Subscriber client1 = new Lec05Subscriber(Integer.parseInt(cliId));
            client1.start();

        }

    }

    public static void ROUTER_DEALER(){

        int menu = getClientOrServer();
        if(menu == 1) {
            Lec05DealerRouterServer server = new Lec05DealerRouterServer(3);
            server.start();
        }
        else
            for(int i =0; i<2; i++) {
                Lec05DealerRouterClient client = new Lec05DealerRouterClient(i);
                client.start();
            }
    }

    public static void P2P_CHAT(){
        Random random = new Random();
        String cliId = "Mr handsome" + random.nextInt(100);
        System.out.println("Your CliId is " + cliId);
        Lec05P2P lec05P2P = new Lec05P2P(cliId);
        lec05P2P.start();
    }

    public static int getMenu(){
        System.out.println("원하시는 메뉴의 번호를 넣어주세요.");
        System.out.println("1. Request, Response pattern");
        System.out.println("2. Publish, Subscribe pattern");
        System.out.println("3. Pull, Push pattern");
        System.out.println("4. Pull, Push pattern");
        System.out.println("5. Dealer, Router Pattern");
        System.out.println("6. P2P dechat");

        Scanner s1 = new Scanner(System.in);
        String menu = s1.nextLine();
        int noMenu = 0;
        try{
            noMenu = Integer.parseInt(menu);
            if (noMenu > 6 || noMenu < 1) {
                throw new NumberFormatException();
            }
            return noMenu;
        }
        catch(NumberFormatException e){
            System.out.println("[ERROR] 1~6중 하나의 숫자를 입력해 주세요.");
            return getMenu();
        }
    }
}
