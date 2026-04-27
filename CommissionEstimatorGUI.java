import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CommissionEstimatorGUI extends Application {

    //input fields
    private TextField tfWidth = new TextField();
    private TextField tfHeight = new TextField();
    private TextField tfBaseRate = new TextField();

    //comboboxes for enums
    private ComboBox<Medium> cbMedium = new ComboBox<>();
    private ComboBox<Subject> cbSubject = new ComboBox<>();
    private ComboBox<Complexity> cbComplexity = new ComboBox<>();
    private ComboBox<Frame> cbFrame = new ComboBox<>();

    //output area
    private TextArea taOutput = new TextArea();

    @Override
    public void start(Stage primaryStage) {

        cbMedium.getItems().setAll(Medium.values());
        cbSubject.getItems().setAll(Subject.values());
        cbComplexity.getItems().setAll(Complexity.values());
        cbFrame.getItems().setAll(Frame.values());

        //layout grid
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Width (inches):"), 0, 0);
        grid.add(tfWidth, 1, 0);

        grid.add(new Label("Height (inches):"), 0, 1);
        grid.add(tfHeight, 1, 1);

        grid.add(new Label("Base Rate ($/hour):"), 0, 2);
        grid.add(tfBaseRate, 1, 2);

        grid.add(new Label("Medium:"), 0, 3);
        grid.add(cbMedium, 1, 3);

        grid.add(new Label("Subject:"), 0, 4);
        grid.add(cbSubject, 1, 4);

        grid.add(new Label("Complexity:"), 0, 5);
        grid.add(cbComplexity, 1, 5);

        grid.add(new Label("Frame:"), 0, 6);
        grid.add(cbFrame, 1, 6);

        //buttons
        Button btnCalculate = new Button("Calculate Estimate");
        Button btnClear = new Button("Clear");
        Button btnExit = new Button("Exit");

        HBox buttonBox = new HBox(10, btnCalculate, btnClear, btnExit);
        buttonBox.setPadding(new Insets(10));

        //output area
        taOutput.setEditable(false);
        taOutput.setPrefHeight(200);

        VBox root = new VBox(10, grid, buttonBox, taOutput);
        root.setPadding(new Insets(10));

        //calculate estimate
        btnCalculate.setOnAction(e -> {
            try {
                double width = Double.parseDouble(tfWidth.getText());
                double height = Double.parseDouble(tfHeight.getText());
                double baseRate = Double.parseDouble(tfBaseRate.getText());

                Medium medium = cbMedium.getValue();
                Subject subject = cbSubject.getValue();
                Complexity complexity = cbComplexity.getValue();
                Frame frame = cbFrame.getValue();

                CommissionEstimator estimator = new CommissionEstimator(
                        width, height, baseRate, medium, subject, complexity, frame
                );

                double price = estimator.calculatePrice();

                taOutput.setText("Estimated Price: $" + price);

            } catch (Exception ex) {
                taOutput.setText("Error: " + ex.getMessage());
            }
        });

        //clear fields
        btnClear.setOnAction(e -> {
            tfWidth.clear();
            tfHeight.clear();
            tfBaseRate.clear();
            cbMedium.setValue(null);
            cbSubject.setValue(null);
            cbComplexity.setValue(null);
            cbFrame.setValue(null);
            taOutput.clear();
        });

        //exit button
        btnExit.setOnAction(e -> primaryStage.close());

        Scene scene = new Scene(root, 500, 550);
        primaryStage.setTitle("Commission Estimator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
