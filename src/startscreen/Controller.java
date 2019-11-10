package startscreen;

import backend.SudokuMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import sudoku.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    Button startButton;

    @FXML private RadioButton f1; //easy
    @FXML private RadioButton f2; //med
    @FXML private RadioButton f3; //hard

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        f2.setSelected(true);
        startButton.setOnMouseClicked(event -> {
            try {
                int difficulty = 0;
                if(f1.isSelected()) {
                    difficulty = 10;
                }
                else if(f2.isSelected()) {
                    difficulty = 30;
                }
                else if(f3.isSelected()) {
                    difficulty = 50;
                }
                SudokuMap map = SudokuMap.getInstance(difficulty);
                Parent root = FXMLLoader.load(getClass().getResource("../sudoku/sample.fxml"));
                Stage stageTheEventSourceNodeBelongs = (Stage) ((Node)event.getSource()).getScene().getWindow();
                stageTheEventSourceNodeBelongs.setScene(new Scene(root, 650, 650));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

}
