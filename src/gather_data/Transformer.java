package gather_data;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
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
    public void analyseWinFromChampions() {
        Map<Integer, Integer> champions_index_from_id = data_manager.getChampionsIndexFromId();
        List<String> champions_name = data_manager.getChampionsName();

        //Create header
        StringBuilder string_builder = new StringBuilder(champions_name.size() * 6); //Improve to analyse all match_ids
        for (int i = 0; i < champions_name.size(); ++i) {
            string_builder.append(champions_name.get(i).replaceAll("'", " ") + ",");
        }
        string_builder.append("Winner\n");

        try {
            Set<MatchDetail> mds = (Set<MatchDetail>) file_manager.load("matches_detail");

            for (MatchDetail md : mds) {
                Map<Integer, Integer> team_id_from_champion_index = new HashMap<Integer, Integer>();
                for (Participant p : md.getParticipants()) {
                    team_id_from_champion_index.put(champions_index_from_id.get(p.getChampionId()), p.getTeamId());
                }

                Object[] indexes = team_id_from_champion_index.keySet().toArray();
                Arrays.sort(indexes);

                int current_index = 0;
                int next_champion_index = (int) indexes[current_index];
                for (int i = 0; i < champions_name.size(); ++i) {
                    if (i == next_champion_index) {
                        string_builder.append(team_id_from_champion_index.get(i) + ",");
                        next_champion_index = ++current_index < indexes.length ? (int) indexes[current_index] : Integer.MAX_VALUE;
                    } else {
                        string_builder.append(-1 + ",");
                    }
                }

                int winner_id = -1;
                for (dto.Match.Team t : md.getTeams()) {
                    if (t.isWinner()) {
                        winner_id = t.getTeamId();
                    }
                }
                string_builder.append(winner_id + "\n");

            }

            file_manager.overwrite("analyse_win_from_champions.csv", string_builder.toString());
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
