package gather_data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import dto.Match.MatchDetail;
import dto.Match.Participant;

public class Transformer {

    private DataManager data_manager;
    private FileManager file_manager;

    public Transformer() {
        data_manager = DataManager.getInstance();
        file_manager = FileManager.getInstance();
    }

    @SuppressWarnings("unchecked")
    public void analyseWinFromChampions() throws FileNotFoundException {
        Map<Integer, Integer> champions_index_from_id = data_manager.getChampionsIndexFromId();
        List<String> champions_name = data_manager.getChampionsName();

        //file_manager.isExistingFile("analyse_win_from_champions.csv")
        //Create header
        StringBuilder string_builder = new StringBuilder(champions_name.size() * 6); //Improve to analyse all match_ids
        string_builder.append(champions_name.get(0));
        for (int i = 1; i < champions_name.size(); ++i) {
            string_builder.append("," + champions_name.get(i));
        }
        string_builder.append("\n");

        try {
            Set<MatchDetail> mds = (Set<MatchDetail>) file_manager.load("matches_detail");
            for (MatchDetail md : mds) {
                for (Participant p : md.getParticipants()) {
                    //TODO add team_id and champion_id to an object and add it to string_builder

                    /*System.out.println("Participant #" + (participant_number++) + ". Team_id: " + p.getTeamId() + ". Champion_id: "
                                       + p.getChampionId());*/
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void cleanMatchIds() {
        try {
            Set<MatchDetail> mds = (Set<MatchDetail>) file_manager.load("matches_detail");
            Set<Long> mds_id = new HashSet<Long>(mds.size());
            for (MatchDetail md : mds) {
                mds_id.add(md.getMatchId());
            }

            Scanner match_ids = new Scanner(file_manager.getFile("match_ids.csv"));
            StringBuilder new_match_ids = new StringBuilder(match_ids.nextLine() + "\n"); //Keep header

            String line;
            String[] line_splitted;
            while (match_ids.hasNextLine()) {
                line = match_ids.nextLine();
                line_splitted = line.split(",");

                if (line_splitted.length == 2 && !mds_id.contains(Long.parseLong(line_splitted[0]))) {
                    new_match_ids.append(line + "\n");
                }
            }
            
            match_ids.close();
            
            file_manager.overwrite("match_ids.csv", new_match_ids.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
