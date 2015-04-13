package view;

import main.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class TeamCompositorController {

    //My team champions
    @FXML
    private ComboBox<String> my_team_1;
    @FXML
    private ComboBox<String> my_team_2;
    @FXML
    private ComboBox<String> my_team_3;
    @FXML
    private ComboBox<String> my_team_4;
    @FXML
    
    //Opponent team champions
    private ComboBox<String> their_team_1;
    @FXML
    private ComboBox<String> their_team_2;
    @FXML
    private ComboBox<String> their_team_3;
    @FXML
    private ComboBox<String> their_team_4;
    @FXML
    private ComboBox<String> their_team_5;
    
    //Team proposal
    @FXML
    private Label t1_top;
    @FXML
    private Label t1_jgl;
    @FXML
    private Label t1_mid;
    @FXML
    private Label t1_adc;
    @FXML
    private Label t1_sup;
    @FXML
    private Label t2_top;
    @FXML
    private Label t2_jgl;
    @FXML
    private Label t2_mid;
    @FXML
    private Label t2_adc;
    @FXML
    private Label t2_sup;
    @FXML
    private Label t3_top;
    @FXML
    private Label t3_jgl;
    @FXML
    private Label t3_mid;
    @FXML
    private Label t3_adc;
    @FXML
    private Label t3_sup;

    // Reference to the main application.
    private MainApp mainApp;

    public TeamCompositorController() {
    }
    
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        ObservableList<String> champions_names = FXCollections.observableArrayList();
        champions_names.addAll(gather_data.DataManager.getInstance().getChampionsName());
        my_team_1.setItems(champions_names);
        my_team_2.setItems(champions_names);
        my_team_3.setItems(champions_names);
        my_team_4.setItems(champions_names);
        their_team_1.setItems(champions_names);
        their_team_2.setItems(champions_names);
        their_team_3.setItems(champions_names);
        their_team_4.setItems(champions_names);
        their_team_5.setItems(champions_names);
    }
    
    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
