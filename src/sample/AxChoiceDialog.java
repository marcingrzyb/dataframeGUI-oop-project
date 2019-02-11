package sample;

import javafx.scene.control.ChoiceDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AxChoiceDialog {
    private String nazwa;
    public AxChoiceDialog(String tmp[],String os){

        List<String> choices = new ArrayList<>();
        for (int i=0;i<tmp.length;i++){
            choices.add(tmp[i]);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Wybierz", choices);
        dialog.setTitle("Choice Dialog");
        dialog.setHeaderText("Wybierz dane osi: "+os);
        dialog.setContentText("Typ danych:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            nazwa=result.get();
        }
    }
    public String answer(){
        return nazwa;
    }
}
