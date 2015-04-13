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
            List<Object> all_mds = file_manager.load("matches_detail");
            Set<MatchDetail> mds = new HashSet<MatchDetail>(30000); //Just save some time...
            for (Object o : all_mds) {
                mds.addAll((Set<MatchDetail>) o);
            }
            all_mds = null; //Free memory for a System.gc() ?

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
    public void analyseWinFromChampionsV2() {
        Map<Integer, Integer> champions_index_from_id = data_manager.getChampionsIndexFromId();
        List<String> champions_name = data_manager.getChampionsName();

        //Create header
        StringBuilder string_builder = new StringBuilder("top t1,jungle t1,mid t1,adc t1,support t1,top t2,jungle t2,mid t2,adc t2,support t2,Winner\n");

        try {
            List<Object> all_mds = file_manager.load("matches_detail");
            Set<MatchDetail> mds = new HashSet<MatchDetail>(30000); //Just save some time...
            for (Object o : all_mds) {
                mds.addAll((Set<MatchDetail>) o);
            }
            all_mds = null; //Free memory for a System.gc() ?

            for (MatchDetail md : mds) {
                Participant[] line = new Participant[10];

                boolean write_current_line = true;
                for (Participant p : md.getParticipants()) {

                    String lane = p.getTimeline().getLane().substring(0, 3);
                    int cell_index = p.getTeamId() == 100 ? 0 : 5;
                    switch (lane) {
                    case "TOP":
                        cell_index += 0;
                        break;
                    case "JUN":
                        cell_index += 1;
                        break;
                    case "MID":
                        cell_index += 2;
                        break;
                    case "BOT":
                        cell_index += 3;
                        if (line[cell_index] != null && line[cell_index + 1] == null) {
                            long my_minion = p.getStats().getMinionsKilled();
                            long other_minion = line[cell_index].getStats().getMinionsKilled();
                            if (my_minion > other_minion) {
                                //Previous was support
                                line[cell_index + 1] = line[cell_index];
                                line[cell_index] = null;
                            } else {
                                //Current is support
                                ++cell_index;
                            }
                        }
                        break;
                    }

                    if (line[cell_index] == null) {
                        line[cell_index] = p;
                    } else {
                        //Meta 1 top, 1 jungle, 1 mid, 2 bot not respected
                        write_current_line = false;
                    }
                }

                if (write_current_line) {
                    for (int i = 0; i < line.length; ++i) {
                        string_builder.append(champions_name.get(champions_index_from_id.get(line[i].getChampionId())).replaceAll("'", " ")
                                              + ",");
                    }

                    String winner = "";
                    for (dto.Match.Team t : md.getTeams()) {
                        if (t.isWinner()) {
                            winner = "t" + (t.getTeamId() / 100);
                        }
                    }
                    string_builder.append(winner + "\n");
                }

            }
            file_manager.overwrite("analyse_win_from_champions_v2.csv", string_builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void cleanMatchIds() {
        try {
            List<Object> all_mds = file_manager.load("matches_detail");
            Set<MatchDetail> mds = new HashSet<MatchDetail>();
            for (Object o : all_mds) {
                mds.addAll((Set<MatchDetail>) o);
            }
            all_mds = null; //Free memory for a System.gc() ?

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
