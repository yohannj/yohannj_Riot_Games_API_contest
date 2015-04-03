package gather_data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constant.Region;
import dto.Match.MatchDetail;
import dto.Match.Participant;
import dto.MatchHistory.MatchSummary;
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
        //Used for the first request
        //LocalDateTime ldt = LocalDateTime.of(2015, 04, 01, 11, 00);
        //Instant lastRequest = ldt.toInstant(ZoneOffset.of("-05"));

        Instant lastRequest = Instant.ofEpochSecond(1428006600);
        long sleep_time = 1000 * 3600; //Sleep 1000 ms * 3600 = 1 hour

        try {

            while (true) {
                FileWriter fw = new FileWriter("resources/match_ids.csv", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);
                System.out.println("Awake at " + Instant.now().toString());
                while (lastRequest.isBefore(Instant.now())) {
                    String print_match_ids = "";
                    List<Long> match_ids = requester.getChallengeMatchIds(region, lastRequest.getEpochSecond());
                    print_match_ids += parseMatchId(match_ids, region);
                    out.println(print_match_ids);

                    lastRequest = lastRequest.plusSeconds(300); // Adjust to next requst: 300s = 5 minute
                }
                System.out.println("lastRequest.getEpochSecond()=" + lastRequest.getEpochSecond());
                out.close();
                System.out.println("Going to sleep at " + Instant.now().toString());
                lastRequest = lastRequest.minusSeconds(300); //Might miss matches between now and next lastRequest (example: 20:03, got matches for 20:00 to 20:03 instead of 20:00 to 20:05, and next "lastRequest" is for 20:05 to 20:10. So I'll just redo the last request.
                Thread.sleep(sleep_time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public MatchDetail extractMatchData(long match_id, Region region, boolean is_timeline_include) {
        return requester.getMatch(region, match_id, is_timeline_include);
    }
    
    public void extractMatchData_old(Region region) {
        long summoner_id = requester.getSummonnerIdFromName(region, "amendile");
        List<MatchSummary> match_summaries = requester.getMatchHistory(region, summoner_id).getMatches();
        MatchSummary ms = match_summaries.get(match_summaries.size() - 1); //Get last match
        MatchDetail md = requester.getMatch(region, ms.getMatchId(), true);

        //Display info for text purpose
        System.out.println(summoner_id);
        System.out.println(ms.getMatchId());
        System.out.println(md.getMatchId());
        System.out.println("Creation date: " + md.getMatchCreation());
        int participant_number = 1;
        for (Participant p : md.getParticipants()) {
            System.out.println("Participant #" + (participant_number++) + ". Team_id: " + p.getTeamId() + ". Champion_id: "
                               + p.getChampionId());
        }
        System.out.println(md.getTimeline().getFrames().get(0).getParticipantFrames().size());

        /* Example of result (Need a class champion with champion static data ?)
        Current time epoch: 1427866403926
        31759410
        2041215809
        2041215809
        Creation date: 1427855094110
        Participant #1. Team_id: 100. Champion_id: 7
        Participant #2. Team_id: 100. Champion_id: 86
        Participant #3. Team_id: 100. Champion_id: 64
        Participant #4. Team_id: 100. Champion_id: 37
        Participant #5. Team_id: 100. Champion_id: 22
        Participant #6. Team_id: 200. Champion_id: 432
        Participant #7. Team_id: 200. Champion_id: 104
        Participant #8. Team_id: 200. Champion_id: 17
        Participant #9. Team_id: 200. Champion_id: 39
        Participant #10. Team_id: 200. Champion_id: 101
        10*/
    }
}
