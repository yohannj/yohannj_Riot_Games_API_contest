package gather_data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import constant.Region;
import dto.Game.RecentGames;
import dto.Match.MatchDetail;
import dto.MatchHistory.PlayerHistory;
import dto.Static.ChampionList;
import dto.Summoner.Summoner;
import main.java.riotapi.Request;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;

/**
 * @author Amendil
 * Singleton
 * Kind of a proxy design pattern to interact with RiotApi, respecting the number of request permitted by riot 
 */
public class Requester {
    
    private static Requester instance;
    
    public static Requester getInstance() {
        if(instance == null) {
            instance = new Requester();
        }
        
        return instance;
    }

    private final double nbRequestPer10s = 10.0;
    private final double nbRequestPer10m = 500.0;
    private final long msBetweenRequest = 1 + (long) (1000 * Math.max(nbRequestPer10s / 10, nbRequestPer10m / 6000)); //Could be optimized

    private Instant lastRequestsTime;
    private RiotApi api;

    private Requester() {
        lastRequestsTime = Instant.EPOCH;
        api = new RiotApi(API_key.KEY);
    }

    private void goingToSendARequest() {
        long sleepTime = (lastRequestsTime.toEpochMilli() + msBetweenRequest) - Instant.now().toEpochMilli();
        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void justSentARequest() {
        lastRequestsTime = Instant.now();
    }

    public long getSummonnerIdFromName(Region region, String name) {
        goingToSendARequest();
        Summoner s;
        long id = -1;
        try {
            s = api.getSummonerByName(region, name).get(name);
            id = s.getId();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            justSentARequest();
        }
        return id;
    }

    public RecentGames getRecentGames(Region region, long summoner_id) {
        goingToSendARequest();
        RecentGames res = null;
        try {
            res = api.getRecentGames(region, summoner_id);
        } catch (RiotApiException e) {
            e.printStackTrace();
        } finally {
            justSentARequest();
        }
        return res;
    }

    public PlayerHistory getMatchHistory(Region region, long summoner_id) {
        goingToSendARequest();
        PlayerHistory res = null;
        try {
            res = api.getMatchHistory(region, summoner_id);
        } catch (RiotApiException e) {
            e.printStackTrace();
        } finally {
            justSentARequest();
        }
        return res;
    }

    public MatchDetail getMatch(Region region, long match_id, boolean include_timeline) {
        goingToSendARequest();
        MatchDetail res = null;
        try {
            res = api.getMatch(region, match_id, include_timeline);
        } catch (RiotApiException e) {
            e.printStackTrace();
        } finally {
            justSentARequest();
        }
        return res;
    }

    public List<Long> getChallengeMatchIds(Region region, Long epoch_begin_time) {
        goingToSendARequest();
        List<Long> res = new ArrayList<Long>();
        String url = "";
        try {
            url = region.getEndpoint() + region.getName() + "/v4.1/game/ids?beginDate=" + epoch_begin_time + "&api_key="
                  + API_key.KEY;
            res = new Gson().fromJson(Request.execute(url), new TypeToken<List<Long>>() {
            }.getType());
        } catch (RiotApiException e) {
            System.out.println(url);
            e.printStackTrace();
        } finally {
            justSentARequest();
        }
        return res;
    }
    
    public ChampionList getChampionsInfo() {
        goingToSendARequest();
        ChampionList res = null;
        try {
            res = api.getDataChampionList();
        } catch (RiotApiException e) {
            e.printStackTrace();
        } finally {
            justSentARequest();
        }
        return res;
    }

}
