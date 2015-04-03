package gather_data;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import dto.Match.MatchDetail;

public class Transformer {

    private DataManager data_manager;
    private FileManager file_manager;

    public Transformer() {
        data_manager = DataManager.getInstance();
        file_manager = FileManager.getInstance();
    }

    public void analyseWinFromChampions() throws FileNotFoundException {
        Map<Integer, Integer> champions_index_from_id = data_manager.getChampionsIndexFromId();
        List<String> champions_name = data_manager.getChampionsName();

        //file_manager.isExistingFile("analyse_win_from_champions.csv")
        //Create header
        StringBuilder sb = new StringBuilder(champions_name.size() * 6); //Improve to analyse all match_ids
        sb.append(champions_name.get(0));
        for (int i = 1; i < champions_name.size(); ++i) {
            sb.append("," + champions_name.get(i));
        }
        sb.append("\n");

        System.out.println("done");
    }
}
