package sample;

import javafx.scene.control.ChoiceDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AyChoiceDialog {
    private String nazwa;
    public AyChoiceDialog(String tmp[], String os, dataframe.DataFrame df){

        List<String> choices = new ArrayList<>();
        for (int i=0;i<tmp.length;i++){
            if(df.getElement(i,0) instanceof dataframe.Value.DataTimeValue || df.getElement(i,0) instanceof dataframe.Value.StringValue)
                continue;
            else
                choices.add(tmp[i]);

        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>("wybierz", choices);
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
