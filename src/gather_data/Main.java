package gather_data;

import java.time.Instant;
import java.util.List;

import constant.Region;
import dto.Match.MatchDetail;
import dto.Match.Participant;
import dto.MatchHistory.MatchSummary;

public class Main {

    public static void main(String[] args) {

        if (API_key.KEY.isEmpty()) {
            System.out.println("Please, insert your API key in var KEY in class API_key");
            return;
        }

        Requester requester = new Requester();
        Region region = Region.EUW;

        System.out.println("Current time epoch: " + Instant.now().toEpochMilli());

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
