package gather_data;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import constant.Region;

@SuppressWarnings("unused")
public class Main {

    private static FileManager file_manager;
    private static Extracter extracter;
    private static Transformer transformer;
    private static Region region;
    private static Map<Integer, Integer> champions_index_from_id;
    private static List<String> champions_name;

    public static void main(String[] args) {

        if (API_key.KEY.isEmpty()) {
            System.out.println("Please, insert your API key in var KEY in class API_key");
            return;
        }

        file_manager = FileManager.getInstance();
        extracter = new Extracter();
        transformer = new Transformer();
        region = Region.NA;

        loadChampInfos();

        System.out.println(champions_name.get(champions_index_from_id.get(412)));

        
        extractMatchIdsForever();
        //extracter.extractMatchData(match_id, region, false);
    }

    @SuppressWarnings({ "unchecked" })
    private static void loadChampInfos() {

        try {
            champions_index_from_id = (Map<Integer, Integer>) file_manager.load("champions_index_from_id");
            champions_name = (List<String>) file_manager.load("champions_name");
        } catch (IOException e) {
            extracter.extractChampionsInfo();
            try {
                champions_index_from_id = (Map<Integer, Integer>) file_manager.load("champions_index_from_id");
                champions_name = (List<String>) file_manager.load("champions_name");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    private static void extractMatchIdsForever() {
        long sleep_time = 1000 * 3600; //Sleep 1000 ms * 3600 = 1 hour
        while (true) {            
            System.out.println("Awake at " + Instant.now().toString());
            extracter.extractMatchIds(region);
            try {
                System.out.println("Going to sleep at " + Instant.now().toString());
                Thread.sleep(sleep_time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
