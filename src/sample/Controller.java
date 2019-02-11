package sample;

import com.jfoenix.controls.JFXButton;
import dataframe.Avg;
import dataframe.Std;
import dataframe.Sum;
import dataframe.Value.*;
import dataframe.War;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller {
    dataframe.DataFrame loaded = null;
    @FXML
    public ScrollPane graphshower;
    @FXML
    public ScrollPane scroll;
    @FXML
    public Label ShowPath;
    @FXML
    private Label textfield;
    @FXML
    private JFXButton AddButton;
    @FXML
    private JFXButton StatButton;
    @FXML
    private JFXButton GraphButton;
    @FXML
    private JFXButton ExitButton;

    public Controller() throws NoSuchMethodException {
    }

    public void setLabelText(String text) {
        ShowPath.setText("Wybrany plik: " + text);
    }

    @FXML
    public void initialize() {
        textfield.textOverrunProperty();
        scroll.setFitToWidth(true);
        StatButton.setDisable(true);
        GraphButton.setDisable(true);
        AddButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV FIles", "*.csv"));

                File selected = fileChooser.showOpenDialog(AddButton.getScene().getWindow());
                if (selected != null) {
                    String path = selected.getAbsolutePath();
                    String[] ile = new String[]{};
                    FileInputStream fstream = null;
                    try {
                        fstream = new FileInputStream(selected);
                        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                        ile = br.readLine().split(",");
                    } catch (Exception e) {
                        errorDisplay(e, "Blad wczytywania dataframe");
                        return;
                    }


                    List<String> choices = new ArrayList<>();
                    choices.add("Int");
                    choices.add("Double");
                    choices.add("Float");
                    choices.add("DataTime");
                    choices.add("String");

                    boolean header = true;
                    String resultt[] = new String[ile.length];
                    Class<? extends Value>[] typy = new Class[ile.length];
                    for (int i = 0; i < ile.length; i++) {
                        ChoiceDialog<String> dialog = new ChoiceDialog<>("Int", choices);
                        dialog.setTitle("Choice Dialog");
                        dialog.setHeaderText("Wybierz typ kolumny: " + ile[i] + " !");
                        dialog.setContentText("Typ:");

                        Optional<String> result = dialog.showAndWait();
                        if (result.isPresent()) {
                            resultt[i] = result.get();
                        }
                        if (resultt[i] == "Int") {
                            typy[i] = IntValue.class;
                        } else if (resultt[i] == "Double") {
                            typy[i] = DoubleValue.class;
                        } else if (resultt[i] == "Float") {
                            typy[i] = FloatValue.class;
                        } else if (resultt[i] == "String") {
                            typy[i] = StringValue.class;
                        } else if (resultt[i] == "DataTime") {
                            typy[i] = DataTimeValue.class;
                        } else {
                            typy = null;
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error Dialog");
                            alert.setHeaderText("Błąd");
                            alert.setContentText("Podaj odpowiedni plik i wybierz typy! ");
                            alert.showAndWait();
                            break;
                        }
                    }
                    if (typy != null) {
                        try {
                            loaded = new dataframe.DataFrame(path, typy, header);
                            StatButton.setDisable(false);
                            GraphButton.setDisable(false);
                        } catch (Exception e) {
                            errorDisplay(e, "Blad tworzenia dataframe");
                        }
                        setLabelText(path);

                    }
                }

            }
        });
        ExitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) ExitButton.getScene().getWindow();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Komunikat");
                alert.setContentText("Czy na pewno chcesz wyjść?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    stage.close();
                }

            }
        });
        StatButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                scroll.setVisible(true);
                graphshower.setVisible(false);
                textfield.setVisible(true);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog with Custom Actions");
                alert.setHeaderText("Look, a Confirmation Dialog with Custom Actions");
                alert.setContentText("Choose your option.");

                ButtonType buttonTypeOne = new ButtonType("Avg");
                ButtonType buttonTypeTwo = new ButtonType("Sum");
                ButtonType buttonTypeThree = new ButtonType("Std");
                ButtonType buttonTypeFour = new ButtonType("Var");
                ButtonType buttonTypeFive = new ButtonType("Min");
                ButtonType buttonTypeSix = new ButtonType("Max");
                ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree, buttonTypeFour, buttonTypeFive, buttonTypeSix, buttonTypeCancel);
                try {
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonTypeOne) {
                        String[] pod = choseCol(loaded);
                        String strSrednia = "Sortowanie po kolumnach: ";
                        for (int i = 0; i < pod.length; i++) {
                            strSrednia += (pod[i] + " ");
                        }
                        strSrednia += "\n";
                        for (int i = 0; i < loaded.getcolNames().length; i++)
                            strSrednia += loaded.getcolNames()[i] + " ";
                        strSrednia += "\n " + loaded.groupby(pod).apply(new Avg()).toString();
                        System.out.println(strSrednia);
                        textfield.setWrapText(true);
                        textfield.prefHeightProperty().bind(scroll.getScene().heightProperty());
                        textfield.setText(strSrednia);
                    } else if (result.get() == buttonTypeTwo) {
                        String[] pod = choseCol(loaded);
                        String strSum = "Sortowanie po kolumnach: ";
                        for (int i = 0; i < pod.length; i++) {
                            strSum += (pod[i] + " ");
                        }
                        strSum += "\n";
                        for (int i = 0; i < loaded.getcolNames().length; i++)
                            strSum += loaded.getcolNames()[i] + " ";
                        strSum += "\n " + loaded.groupby(pod).apply(new Sum()).toString();
                        System.out.println(strSum);
                        textfield.setWrapText(true);
                        textfield.prefHeightProperty().bind(scroll.getScene().heightProperty());
                        textfield.setText(strSum);

                    } else if (result.get() == buttonTypeThree) {
                        String[] pod = choseCol(loaded);
                        String strStd = "Sortowanie po kolumnach: ";
                        for (int i = 0; i < pod.length; i++) {
                            strStd += (pod[i] + " ");
                        }
                        strStd += "\n";
                        for (int i = 0; i < loaded.getcolNames().length; i++)
                            strStd += loaded.getcolNames()[i] + " ";
                        strStd += "\n " + loaded.groupby(pod).apply(new Std()).toString();
                        System.out.println(strStd);
                        textfield.setWrapText(true);
                        textfield.prefHeightProperty().bind(scroll.getScene().heightProperty());
                        textfield.setText(strStd);
                    } else if (result.get() == buttonTypeFour) {
                        String[] pod = choseCol(loaded);
                        String strVar = "Sortowanie po kolumnach: ";
                        for (int i = 0; i < pod.length; i++) {
                            strVar += (pod[i] + " ");
                        }
                        strVar += "\n";
                        for (int i = 0; i < loaded.getcolNames().length; i++)
                            strVar += loaded.getcolNames()[i] + " ";
                        strVar += "\n " + loaded.groupby(pod).apply(new War()).toString();
                        System.out.println(strVar);
                        textfield.setWrapText(true);
                        textfield.prefHeightProperty().bind(scroll.getScene().heightProperty());
                        textfield.setText(strVar);
                    } else if (result.get() == buttonTypeFive) {
                        String[] pod = choseCol(loaded);
                        String strMin = "Sortowanie po kolumnach: ";
                        for (int i = 0; i < pod.length; i++) {
                            strMin += (pod[i] + " ");
                        }
                        strMin += "\n";
                        for (int i = 0; i < loaded.getcolNames().length; i++)
                            strMin += loaded.getcolNames()[i] + " ";
                        strMin += "\n " + loaded.groupby(pod).apply(new dataframe.Min()).toString();
                        System.out.println(strMin);
                        textfield.setWrapText(true);
                        textfield.prefHeightProperty().bind(scroll.getScene().heightProperty());
                        textfield.setText(strMin);
                    } else if (result.get() == buttonTypeSix) {
                        String[] pod = choseCol(loaded);
                        String strMax = "Sortowanie po kolumnach: ";
                        for (int i = 0; i < pod.length; i++) {
                            strMax += (pod[i] + " ");
                        }
                        strMax += "\n";
                        for (int i = 0; i < loaded.getcolNames().length; i++)
                            strMax += loaded.getcolNames()[i] + " ";
                        strMax += "\n " + loaded.groupby(pod).apply(new dataframe.Max()).toString();
                        System.out.println(strMax);
                        textfield.setWrapText(true);
                        textfield.prefHeightProperty().bind(scroll.getScene().heightProperty());
                        textfield.setText(strMax);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                    errorDisplay(e, "Blad  grupowania dataframe");
        }
            }
        });
        GraphButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                scroll.setVisible(false);
                textfield.setVisible(false);

                graphshower.setVisible(true);

                textfield.setVisible(false);

                int nrx=0,nry=0;
                AxChoiceDialog osx = new AxChoiceDialog (loaded.getcolNames(),"x");
                AyChoiceDialog  osy = new AyChoiceDialog (loaded.getcolNames(),"y",loaded);

                NumberAxis NxAxis = new NumberAxis();
                NumberAxis NyAxis = new NumberAxis();

                CategoryAxis CxAxis = new CategoryAxis();
                CategoryAxis CyAxis = new CategoryAxis();
                try {
                    for (int i = 0; i < loaded.getcolNames().length; i++) {
                        if (loaded.getcolNames()[i].equals(osx.answer())) {
                            nrx = i;
                            break;
                        }
                    }
                    for (int i = 0; i < loaded.getcolNames().length; i++) {
                        if (loaded.getcolNames()[i].equals(osy.answer())) {
                            nry = i;
                            break;
                        }
                    }
                    if ((loaded.getElement(nrx, 0) instanceof dataframe.Value.IntValue || loaded.getElement(nrx, 0) instanceof dataframe.Value.DoubleValue || loaded.getElement(nrx, 0) instanceof dataframe.Value.FloatValue) && (loaded.getElement(nry, 0) instanceof dataframe.Value.IntValue || loaded.getElement(nry, 0) instanceof dataframe.Value.DoubleValue || loaded.getElement(nry, 0) instanceof dataframe.Value.FloatValue)) {
                        //creating the chart
                        ScatterChart<Number, Number> lineChart = new ScatterChart<Number, Number>(NxAxis, NyAxis);

                        lineChart.setTitle("Wykres");

                        XYChart.Series series = new XYChart.Series();

                        for (int i = 0; i < loaded.getcols(0).size(); i++) {
                            series.getData().add(new XYChart.Data(Double.parseDouble(loaded.getElement(nrx, i).toString()), Double.parseDouble(loaded.getElement(nry, i).toString())));
                        }
                        lineChart.setPrefSize(900, 450);
                        lineChart.getData().add(series);

                        graphshower.setContent(lineChart);
                    } else if ((loaded.getElement(nry, 0) instanceof dataframe.Value.IntValue || loaded.getElement(nry, 0) instanceof dataframe.Value.DoubleValue || loaded.getElement(nry, 0) instanceof dataframe.Value.FloatValue) && (loaded.getElement(nrx, 0) instanceof dataframe.Value.StringValue || loaded.getElement(nrx, 0) instanceof dataframe.Value.DataTimeValue)) {
                        //creating the chart
                        ScatterChart<String, Number> lineChart = new ScatterChart<String, Number>(CxAxis, NyAxis);

                        lineChart.setTitle("Wykres");

                        XYChart.Series series = new XYChart.Series();

                        for (int i = 0; i < loaded.getcols(0).size(); i++) {
                            series.getData().add(new XYChart.Data(loaded.getElement(nrx, i).toString(), Double.parseDouble(loaded.getElement(nry, i).toString())));
                        }
                        lineChart.setPrefSize(900, 450);
                        lineChart.getData().add(series);

                        graphshower.setContent(lineChart);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    errorDisplay(e, "Blad tworzenia wykresu");
        }
            }
        });

    }
    public String[] choseCol(dataframe.DataFrame load) {
        String nazwykol = "";
        String[] nazwykolumn = null;
        try {
            for (int i = 0; i < load.getcolNames().length; i++) {
                nazwykol += (load.getcolNames()[i] + " ");
            }
            TextInputDialog dialog = new TextInputDialog("podaj nazwy kolumn oddzielajac je przecinkiem");
            dialog.setTitle("Text Input Dialog");
            dialog.setHeaderText("Wybierz kolumny");
            dialog.setContentText("Nazwy kolumn: " + nazwykol);
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                nazwykolumn = (result.get().split(","));
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorDisplay(e, "Blad wyboru kolumny");
        }
     return nazwykolumn;
    }

    private void errorDisplay(Exception e, String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(s);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

}