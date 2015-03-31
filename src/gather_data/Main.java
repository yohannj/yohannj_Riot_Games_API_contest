package gather_data;

import java.util.Map;

import constant.Region;
import dto.Summoner.Summoner;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;


public class Main {

    public static void main(String[] args) {
        
        if(API_key.KEY.isEmpty()) {
            System.out.println("Please, insert your API key in var KEY in class API_key");
            return;
        }
        
        RiotApi api = new RiotApi(API_key.KEY);
        
        

        Map<String, Summoner> summoners;
        try {
            summoners = api.getSummonersByName(Region.NA, "rithms, tryndamere");
            Summoner summoner = summoners.get("rithms");
            long id = summoner.getId();
            System.out.println(id);
        } catch (RiotApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
