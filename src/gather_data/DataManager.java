package gather_data;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Amendil
 * Singleton
 */
public class DataManager {

    private static DataManager instance;
    private static FileManager file_manager;
    private static Extracter extracter;

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }

        return instance;
    }
    
    private Map<Integer, Integer> champions_index_from_id;
    private List<String> champions_name;

    private DataManager() {
        file_manager = FileManager.getInstance();
        extracter = new Extracter();
        loadChampInfos();
    }
    
    @SuppressWarnings({ "unchecked" })
    private void loadChampInfos() {

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
    
    public List<String> getChampionsName() {
        return champions_name;
    }
    
    public Map<Integer, Integer> getChampionsIndexFromId() {
        return champions_index_from_id;
    }

}
