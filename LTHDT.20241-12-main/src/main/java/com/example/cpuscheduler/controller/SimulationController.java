package com.example.cpuscheduler.controller;

import com.example.cpuscheduler.model.*;
import com.example.cpuscheduler.model.Process;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SimulationController implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    private Label titleResultLabel;

    @FXML
    private TableView<Process> resultTable;

    @FXML
    private TableColumn<Process, Integer> idResultColumn;

    @FXML
    private TableColumn<Process, Double> burstTimeResultColumn;

    @FXML
    private TableColumn<Process, Double> finishTimeResultColumn;

    @FXML
    private TableColumn<Process, Double> turnaroundTimeResultColumn;

    @FXML
    private TableColumn<Process, Double> waitingTimeResultColumn;

    @FXML
    private TableColumn<Process, Double> arrivalTimeResultColumn;

    @FXML
    private Button resetButton;

    @FXML
    private Button calculateButton;

    @FXML
    private Button skipButton;

    @FXML
    private Label priorityLabel;

    @FXML
    private Pane pane;

    @FXML
    private Label quantumTimeLabel;

    @FXML
    private TextField quantumTimeTextField;

    @FXML
    private TextField burstTimeTextField;

    @FXML
    private TextField arrivalTimeTextField;

    @FXML
    private TableColumn<Process, Integer> priorityColumn;

    @FXML
    private TableColumn<Process, Double> arrivalTimeColumn;

    @FXML
    private TextField priorityTextField;

    @FXML
    private TableColumn<Process, Integer> idColumn;

    @FXML
    private TableColumn<Process, Double> burstTimeColumn;

    @FXML
    private TableView<Process> table;

    @FXML
    private Label titleLabel;

    @FXML
    private Label averageWaitingTimeLabel;

    @FXML
    private Label turnaroundTimeLabel;

    @FXML
    private Label cpuUtilizationLabel;

    private ObservableList<Process> processList;

    private ObservableList<Process> resultProcessList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khởi tạo simulaton stage
        processList = FXCollections.observableArrayList(
                // các ví dụ có sẵn trong bảng tiến trình
                new Process(1, 0.0,5.0,1),
                new Process(2, 2.0,6.0,2),
                new Process(3, 3.0, 7.0, 3)
        );
        idColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("id"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Double>("arrivalTime"));
        burstTimeColumn.setCellValueFactory(new PropertyValueFactory<Process, Double>("burstTime"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("priority"));
        table.setItems(processList);
        skipButton.setVisible(false);

        if(MainMenuController.currentStage.equals("FCFS")){
            // thuật toán lựa chọn là FCFS, ẩn đi các đối tượng không sử dụng
            quantumTimeLabel.setVisible(false);
            quantumTimeTextField.setVisible(false);
            priorityColumn.setVisible(false);
            priorityTextField.setVisible(false);
            priorityLabel.setVisible(false);
            idColumn.setPrefWidth(idColumn.getWidth() + 24);
            burstTimeColumn.setPrefWidth(burstTimeColumn.getWidth() + 50);
            arrivalTimeColumn.setPrefWidth(arrivalTimeColumn.getWidth() + 50);
            titleLabel.setText("""
                FCFS(First Come First Serve)
                """);
        }

        if(MainMenuController.currentStage.equals("SJN")){
            // thuật toán sử dụng là SJN, ẩn đi các đối tượng không sử dụng
            quantumTimeLabel.setVisible(false);
            quantumTimeTextField.setVisible(false);
            titleLabel.setText("""
                SJN(Short Job Next)
                """);
        }

        if(MainMenuController.currentStage.equals("Round Robin")) {
            // thuật toán sử dụng là Round Robin, ẩn đi các đối tượng không sử dụng
            priorityColumn.setVisible(false);
            priorityTextField.setVisible(false);
            priorityLabel.setVisible(false);
            idColumn.setPrefWidth(idColumn.getWidth() + 24);
            burstTimeColumn.setPrefWidth(burstTimeColumn.getWidth() + 50);
            arrivalTimeColumn.setPrefWidth(arrivalTimeColumn.getWidth() + 50);
            titleLabel.setText("""
                    RR(Round Robin)
                    """);
        }
    }

    private void visualGanttChart(List<Pair<Integer, Pair<Double, Double>>> resultProcess){
        // mô phỏng gantt chart
        List<Pair<Integer, Pair<Double, Double>>> processes = resultProcess;

        List<Double> timestamps = new ArrayList<Double>(); // các khoảng thời gian làm việc của CPU
        List<Integer> currentProcess = new ArrayList<Integer>(); // list thứ tự làm việc của tiến trình
        List<Double> se1 = new ArrayList<Double>(); // thời điểm vào ứng với từng khoang làm việc
        List<Double> se2 = new ArrayList<Double>(); // thời điểm ra ứng với từng khoang làm việc
        List<Pair<Double, Pair<Double, Double>>> color = new ArrayList<Pair<Double, Pair<Double, Double>>>(); // bảng màu phục vụ mô phỏng

        for(int i = 0; i < processes.size(); i++){
            // chọn màu ngẫu nhiên
            Double colorRed = Math.random();
            Double colorGreen = Math.random();
            Double colorYellow = Math.random();
            color.add(new Pair<>(colorRed, new Pair<>(colorGreen, colorYellow)));
        }

        for (Pair<Integer, Pair<Double, Double>> process : processes) {
            // thiết lập các list
            timestamps.add(process.getValue().getValue() - process.getValue().getKey());
            currentProcess.add(process.getKey());
            se1.add(process.getValue().getKey());
            se2.add(process.getValue().getValue());
        }


        SequentialTransition sequence = new SequentialTransition();
        // tạo sequence pause để tiến trình xuất hiện lần lượt
        int index = 0;
        double sumWidth = 0, cnt = 0;
        // thêm skip button khi cac tiến trình bắt đầu được mô phỏng
        skipButton.setVisible(true);

        skipButton.setOnAction(event -> {
            // Khi nhấn Skip, bỏ qua hiệu ứng
            skipSequence(sequence, processes, color);
        });

        resetButton.setOnAction(event -> {
            skipButton.setVisible(false);
            pane.getChildren().clear();
            sequence.stop();  // Dừng toàn bộ sequence
            sequence.getChildren().clear();
        });

        calculateButton.setOnAction(event -> {
            skipButton.setVisible(false);
            pane.getChildren().clear();
            sequence.stop();  // Dừng toàn bộ sequence
            sequence.getChildren().clear();
            handleCalculate(event);
        });


        for (double timestamp : timestamps) {
            double squareWidth;
            if (timestamp >= 10) squareWidth = 30 * 10;
            else squareWidth = 30 * timestamp;
            double squareHeight = 40;
            double waitTime;
            if(index == 0)waitTime = 0;
            else {
                if (timestamps.get(index - 1) >= 10) waitTime = 10;
                else waitTime = timestamps.get(index - 1);
            }
            PauseTransition pause = new PauseTransition(Duration.seconds(0.3 * waitTime));
            // chờ 1 khoảng 0.5 * với khoảng thời gian làm việc của tiến trình trước để xuất hiện
            double x, y;
            // nếu độ dài vượt quá thực hiện xuống dòng
            if(sumWidth + squareWidth > pane.getWidth()) {
                sumWidth = 0;
                cnt++;
            }
            x = sumWidth;
            y = cnt * 60;
            sumWidth += squareWidth;
            String stringSecond1 = String.valueOf(se1.get(index));
            String stringSecond2 = String.valueOf(se2.get(index));
            String curProcess = String.valueOf(currentProcess.get(index));
            Pair<Double, Pair<Double, Double>> currentColor = color.get(currentProcess.get(index) - 1);
            // tiến trình xuất hiện sau khi chờ tiến trình kế trước xuất hiện
            pause.setOnFinished(event -> {
                Rectangle square = createSquare(currentColor, squareHeight);

                square.setX(5 + x);
                square.setY(20 + y);

                pane.getChildren().add(square);

                Text text = new Text("P" + curProcess);
                text.setFont(new Font("Arial",13));
                text.setFill(Color.WHITE);

                // tên tiến trình
                text.setX(square.getX() + squareWidth / 2 - text.getLayoutBounds().getWidth() / 2); // Căn giữa theo chiều ngang
                text.setY(square.getY() + squareHeight / 2 + text.getLayoutBounds().getHeight() / 4); // Căn giữa theo chiều dọc

                Text second1 = new Text(stringSecond1);
                second1.setFont(new Font("Times New Roman",8));
                second1.setFill(Color.RED);
                // thời gian vào
                second1.setX(square.getX());
                second1.setY(square.getY());
                // thời gian ra
                Text second2 = new Text(stringSecond2);
                second2.setFont(new Font("Times New Roman",8));
                second2.setFill(Color.RED);

                second2.setX(square.getX() + squareWidth - text.getLayoutBounds().getWidth());
                second2.setY(square.getY() + squareHeight + 3 + text.getLayoutBounds().getHeight() / 4);

                pane.getChildren().addAll(text, second1, second2);
                // hiệu ứng cho tiến trình xuất hiện từ từ
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(square.widthProperty(), 0)), // Bắt đầu từ 0
                        new KeyFrame(Duration.seconds(0.01 * squareWidth), new KeyValue(square.widthProperty(), squareWidth)) // Kết thúc tại squarewidth
                );
                timeline.setCycleCount(1);
                timeline.play();
            });

            sequence.getChildren().add(pause);
            index++;
        }

        sequence.play();
        // khi chỉ còn 1 tiến trình xuất hiện, xóa button skip
        sequence.setOnFinished(event -> {
            skipButton.setVisible(false);
        });
    }

    //square ứng với tiến trình trong ganttchart
    private Rectangle createSquare(Pair<Double, Pair<Double, Double>> currentColor, double squareHeight) {
        Rectangle square = new Rectangle(0, 0, 0, squareHeight);
        square.setFill(Color.color(currentColor.getKey(), currentColor.getValue().getKey(), currentColor.getValue().getValue()));
        return square;
    }

    public void handleAdd(ActionEvent actionEvent) {
        // thêm tiến trình
        Process newProcess = new Process();
        // nếu các ô dữ liệu bị để trống, thông báo lỗi
        if(arrivalTimeTextField.getText().equals("") || arrivalTimeTextField.getText().equals("")) {
            alertNullTextField();
            return;
        }
        Double arrivalTime = Double.parseDouble(arrivalTimeTextField.getText());
        Double burstTime = Double.parseDouble(burstTimeTextField.getText());
        if(arrivalTime < 0 || burstTime < 0){
            alertErrorInput();
            return;
        }
        if(burstTime == 0){
            alertZeroBurstTimeInput();
            return;
        }
        newProcess.setArrivalTime(Double.parseDouble(arrivalTimeTextField.getText()));
        newProcess.setBurstTime(Double.parseDouble(burstTimeTextField.getText()));
        if(MainMenuController.currentStage.equals("SJN")){
            int priority = Integer.parseInt(priorityTextField.getText());
            if(priority < 0){
                alertErrorInput();
                return;
            }
            newProcess.setPriority(Integer.parseInt(priorityTextField.getText()));
        }
        else newProcess.setPriority(0);
        // id được set tự động
        if(processList.isEmpty())newProcess.setId(1);
        else{
            newProcess.setId(processList.getLast().getId() + 1);
        }
        processList.add(newProcess);
    }

    public void handleDelete(ActionEvent actionEvent) {
        // chọn tiến trình trên bảng và xóa
        List <Process> selectedProcess = table.getSelectionModel().getSelectedItems();
        int curId = 0;
        for(Process process : processList) {
            if(selectedProcess.contains(process)) {
                curId = process.getId();
                processList.remove(process);
                break;
            }
        }
        // cập nhật id
        for(Process process : processList) {
            if(process.getId() > curId) {
                process.setId(process.getId() - 1);
            }
        }
    }

    public void handleGoBack(ActionEvent event) throws IOException {
        // trở lại stage main menu, hiện thông báo
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Thoát " + MainMenuController.currentStage + " simulation?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Exit simulation Confirmation");
        alert.setHeaderText(null);

        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/cpuscheduler/MainMenuView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            MainMenuController controller = fxmlLoader.getController();
            controller.closeStage(stage);
            stage.setResizable(false);
            stage.setTitle("CPUScheduler");
            stage.setScene(scene);
            stage.show();
        }
    }

    public void handleDeleteAll() {
        // xóa hết tiến trình trên bảng
        processList.clear();
    }

    public void handleCalculate(ActionEvent event) {
        // reset ganttchart và các giá trị time metrics trước khi tính
        handleReset();
        Scheduler scheduler;
        if(MainMenuController.currentStage.equals("FCFS")) {
            // ép kiểu FCFS
            scheduler = new FCFS(processList);
        }else if(MainMenuController.currentStage.equals("SJN")){
            // ép kiểu SJN
            scheduler = new SJN(processList);
        }else{
            // ép kiểu Round Robin
            if(quantumTimeTextField.getText().equals("")) {
                alertNullTextField();
                return;
            }
            double quantumTime = Double.parseDouble(quantumTimeTextField.getText());
            if(quantumTime <= 0) {
                alertErrorInput();
                return;
            }
            scheduler = new RoundRobin(processList, quantumTime);
        }
        List<Pair<Integer, Pair<Double, Double>>> resultProcess = scheduler.schedule();
        visualResultTable(scheduler);
        visualGanttChart(resultProcess);
        averageWaitingTimeLabel.setText("Average Waiting Time: " + scheduler.calWaitingTime());
        turnaroundTimeLabel.setText("Turnaround Time: " + scheduler.calTurnaroundTime());
        cpuUtilizationLabel.setText("CPU Utilization: " + scheduler.calCPUUtilization());
    }

    public void handleReset(){
        // reset màn hình gantt chart
        pane.getChildren().clear();
        cpuUtilizationLabel.setText("CPU Utilization: ");
        averageWaitingTimeLabel.setText("Average Waiting Time: ");
        turnaroundTimeLabel.setText("Turnaround Time: ");
    }

    public void handleHelp(){
        // giải thích thuật toán hiện tại
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Giải thích thuật toán");
        alert.setHeaderText(null);
        alert.setResizable(false);
        alert.getDialogPane().setPrefSize(500, 200);
        if(MainMenuController.currentStage.equals("FCFS")) {
            alert.setContentText("""
                FCFS(First Come First Serve): thực hiện các tiến trình theo thời gian xuất hiện trong CPU, tức là
                tiến trình nào có arrival time nhỏ nhất sẽ được thực hiện trước.
                """);
        }else if(MainMenuController.currentStage.equals("SJN")){
            alert.setContentText("""
                SJN(Short Job Next): mỗi khi có 1 tiến trình mới xuất hiện trong hệ thống, tiến trình hiện tại được
                đưa lại vào ready queue, sau đó tiến trình có burst time nhỏ nhất sẽ được lựa chọn để thực thi, khi
                có nhiều tiến trình có cùng burst time nhỏ nhất, tiến trình có độ ưu tiên cao hơn được lựa chọn.
                """);
        }else{
            alert.setContentText("""
                RR(Round Robin): mỗi tiến trình được cấp một khoảng thời gian cố định quantum time, các tiến trình
                xuất hiện trong hệ thống được xếp vào cuối hàng đợi, khi 1 tiến trình hết quantum time mà chưa
                hoàn thành, nó được xếp vào cuối hàng đợi.
                """);
        }
        alert.showAndWait().ifPresent(response -> {
            if(response == ButtonType.YES){
                System.out.println("yes");
            }else{
                System.out.println("no");
            }
        });
    }

    private void skipSequence(SequentialTransition sequence, List<Pair<Integer, Pair<Double, Double>>> processes, List<Pair<Double, Pair<Double, Double>>> color) {
        // skip hiệu ứng bằng cách hủy các sequene pause và tạo square ngay lập tức
        skipButton.setVisible(false);
        pane.getChildren().clear();
        sequence.stop();  // Dừng toàn bộ sequence
        sequence.getChildren().clear();  // Xóa các sự kiện trong sequence
        double sumWidth = 0, cnt = 0;
        int index = 0;

        List<Double> timestamps = new ArrayList<Double>();
        List<Integer> currentProcess = new ArrayList<Integer>();
        List<Double> se1 = new ArrayList<Double>();
        List<Double> se2 = new ArrayList<Double>();

        for (Pair<Integer, Pair<Double, Double>> process : processes) {
            timestamps.add(process.getValue().getValue() - process.getValue().getKey());
            currentProcess.add(process.getKey());
            se1.add(process.getValue().getKey());
            se2.add(process.getValue().getValue());
        }


        for (double timestamp : timestamps) {
            double x, y;
            double squareWidth;
            double squareHeight = 40;
            if (timestamp >= 10) squareWidth = 30 * 10;
            else squareWidth = 30 * timestamp;
            if (sumWidth + squareWidth > pane.getWidth()) {
                sumWidth = 0;
                cnt++;
            }

            x = sumWidth;
            y = cnt * 60;
            sumWidth += squareWidth;

            String stringSecond1 = String.valueOf(se1.get(index));
            String stringSecond2 = String.valueOf(se2.get(index));
            String curProcess = String.valueOf(currentProcess.get(index));
            Pair<Double, Pair<Double, Double>> currentColor = color.get(currentProcess.get(index) - 1);
            // Tạo hình và hiển thị ngay lập tức
            Rectangle square = createSquare(currentColor, squareHeight);
            square.setX(x + 5);
            square.setY(y + 20);
            square.setWidth(squareWidth);
            pane.getChildren().add(square);

            Text text = new Text("P" + curProcess);
            text.setFont(new Font("Arial", 13));
            text.setFill(Color.WHITE);

            text.setX(square.getX() + squareWidth / 2 - text.getLayoutBounds().getWidth() / 2);
            text.setY(square.getY() + squareHeight / 2 + text.getLayoutBounds().getHeight() / 4);

            Text second1 = new Text(stringSecond1);
            second1.setFont(new Font("Times New Roman", 8));
            second1.setFill(Color.RED);

            second1.setX(square.getX());
            second1.setY(square.getY());

            Text second2 = new Text(stringSecond2);
            second2.setFont(new Font("Times New Roman", 8));
            second2.setFill(Color.RED);

            second2.setX(square.getX() + squareWidth - text.getLayoutBounds().getWidth());
            second2.setY(square.getY() + squareHeight + 3 + text.getLayoutBounds().getHeight() / 4);

            pane.getChildren().addAll(text, second1, second2);

            index++;
        }
    }

    private void alertNullTextField(){
        // thông báo khi có không có giá trị nhập vào trong text field
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input error");
        alert.setHeaderText(null);
        alert.setContentText("Không được để trống các giá trị.");
        alert.showAndWait();
    }

    private void alertZeroBurstTimeInput(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input error");
        alert.setHeaderText(null);
        alert.setContentText("Burst time phải lớn hơn 0.");
        alert.showAndWait();
    }

    private void alertErrorInput(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input error");
        alert.setHeaderText(null);
        alert.setContentText("Các giá trị phải không âm.");
        alert.showAndWait();
    }

    public void handleSwap(){
        stackPane.getChildren().getFirst().toFront();
        if(stackPane.getChildren().getFirst() != resultTable){
            titleResultLabel.setText("Result Table");
        }else{
            titleResultLabel.setText("Gantt Chart");
        }
    }

    private void visualResultTable(Scheduler scheduler){
        resultProcessList = FXCollections.observableArrayList(scheduler.getProcesses());
        idResultColumn.setCellValueFactory(new PropertyValueFactory<Process, Integer>("id"));
        arrivalTimeResultColumn.setCellValueFactory(new PropertyValueFactory<Process, Double>("arrivalTime"));
        burstTimeResultColumn.setCellValueFactory(new PropertyValueFactory<Process, Double>("burstTime"));
        finishTimeResultColumn.setCellValueFactory(new PropertyValueFactory<Process, Double>("completionTime"));
        turnaroundTimeResultColumn.setCellValueFactory(new PropertyValueFactory<Process, Double>("turnaroundTime"));
        waitingTimeResultColumn.setCellValueFactory(new PropertyValueFactory<Process, Double>("waitingTime"));
        resultTable.setItems(resultProcessList);
    }
}

