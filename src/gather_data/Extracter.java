package gather_data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import constant.Region;
import dto.Match.MatchDetail;
import dto.Static.Champion;
import dto.Static.ChampionList;

public class Extracter {

    private static Requester requester;
    private static FileManager file_manager;

    public Extracter() {
        requester = Requester.getInstance();
        file_manager = FileManager.getInstance();
    }

    private String parseMatchId(List<Long> match_ids, Region region) {
        String res = "";
        for (int i = 0; i < match_ids.size(); ++i) {
            res += match_ids.get(i) + "," + region.getName() + "\n";
        }
        return res;
    }

    public void extractChampionsInfo() {
        ChampionList cl = requester.getChampionsInfo();
        int nb_champ = cl.getData().size();
        Map<Integer, Integer> champions_index_from_id = new HashMap<Integer, Integer>(nb_champ);
        List<String> champions_name = new ArrayList<String>(nb_champ);

        Map<String, Integer> champions_id_from_name = new HashMap<String, Integer>(nb_champ);

        for (Champion champion : cl.getData().values()) {
            champions_name.add(champion.getName());
            champions_id_from_name.put(champion.getName(), champion.getId());
        }

        Collections.sort(champions_name);

        for (int i = 0; i < champions_name.size(); ++i) {
            champions_index_from_id.put(champions_id_from_name.get(champions_name.get(i)), i);
        }

        file_manager.save(champions_index_from_id, "champions_index_from_id");
        file_manager.save(champions_name, "champions_name");
    }

    /**
     * @param region
     * 
     */
    public void extractMatchIds(Region region) {
        long last_request_epoch_sec;
        try {
            last_request_epoch_sec = (long) file_manager.load("extractMatchIds_last_request_time").get(0);
        } catch (IOException e1) {
            //First time using the request
            LocalDateTime ldt = LocalDateTime.of(2015, 04, 01, 11, 00);
            last_request_epoch_sec = ldt.toEpochSecond(ZoneOffset.of("-05"));
        }
        Instant lastRequest = Instant.ofEpochSecond(last_request_epoch_sec);

        while (lastRequest.isBefore(Instant.now())) {
            String print_match_ids = "";
            List<Long> match_ids = requester.getChallengeMatchIds(region, lastRequest.getEpochSecond());
            if (match_ids != null) {
                print_match_ids += parseMatchId(match_ids, region);
                file_manager.append("match_ids.csv", print_match_ids);

                //Adjust to next request: 300s = 5 minute
                lastRequest = lastRequest.plusSeconds(300);
            }
        }

        //Avoid missing matches between now and next lastRequest (i.e. for 20:03, got matches for 20:00 to 20:03 instead of 20:00 to 20:05, and next "lastRequest" is for 20:05 to 20:10.)
        lastRequest = lastRequest.minusSeconds(300);

        file_manager.save(lastRequest.getEpochSecond(), "extractMatchIds_last_request_time");
    }

    public MatchDetail extractMatchDetail(long match_id, Region region, boolean is_timeline_include) {
        return requester.getMatch(region, match_id, is_timeline_include);
    }

    public void extractMatchDetails(int nb_match_to_extract, Region region, boolean is_timeline_include) {
        try {
            Set<MatchDetail> matches_detail = new HashSet<MatchDetail>();

            Scanner match_ids = new Scanner(file_manager.getFile("match_ids.csv"));
            String[] line_splitted;
            int index = 0;
            while (match_ids.hasNextLine() && ++index <= nb_match_to_extract) {
                line_splitted = match_ids.nextLine().split(",");
                if (line_splitted.length == 2 && region.getName().equals(line_splitted[1])) { //This is a correct line
                    MatchDetail md = extractMatchDetail(Long.parseLong(line_splitted[0]), region, is_timeline_include);
                    if (md != null) {
                        matches_detail.add(md);
                    }
                }
            }
            match_ids.close();
            file_manager.save(matches_detail, "matches_detail");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
