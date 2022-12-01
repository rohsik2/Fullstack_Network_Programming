package Lec05_ZMQ;

import static java.lang.Thread.sleep;

public class SleepUtility {
    public static void doSleep(long timeMills){
        try{
            sleep(timeMills);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
