package gather_data;

import java.io.FileNotFoundException;
import java.time.Instant;

import constant.Region;

@SuppressWarnings("unused")
public class Main {

    private static Extracter extracter;
    private static Transformer transformer;
    private static Region region;

    public static void main(String[] args) { //TODO Add an enum of files in FileManager

        if (API_key.KEY.isEmpty()) {
            System.out.println("Please, insert your API key in var KEY in class API_key");
            return;
        }

        extracter = new Extracter();
        transformer = new Transformer();

        region = Region.NA;


        System.out.println("start");
        //extractMatchIdsOnce();
        //System.out.println("match ids extracted once");
        
        extracter.extractMatchDetails(3000, region, false);
        System.out.println("match details extracted");
        
        transformer.cleanMatchIds();
        System.out.println("match ids cleaned");
        
        transformer.analyseWinFromChampions();
        System.out.println("analyse done");

        //extractMatchIdsForever();
        //extracter.extractMatchData(match_id, region, false);
    }

    private static void extractMatchIdsOnce() {
        extracter.extractMatchIds(region);
    }

    private static void extractMatchIdsForever() {
        long sleep_time = 1000 * 3600; //Sleep 1000 ms * 3600 = 1 hour
        while (true) {
            System.out.println("Awake at " + Instant.now().toString());
            extractMatchIdsOnce();
            try {
                System.out.println("Going to sleep at " + Instant.now().toString());
                Thread.sleep(sleep_time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
