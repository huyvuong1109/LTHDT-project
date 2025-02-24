package com.example.cpuscheduler.model;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FCFS extends Scheduler {

    public FCFS(List<Process> processes){
        super(processes);
    }

    @Override
    public List<Pair<Integer, Pair<Double, Double>>> schedule(){
        List<Pair<Integer, Pair<Double,Double>>> ganttChart = new ArrayList<>();
        for (Process p : processes) {
            p.setCompletionTime(0.0);
            p.setStartTime(0.0);
        }

        // Sắp xếp theo thời gian đến
        processes.sort(Comparator.comparingDouble(Process::getArrivalTime));

        double currentTime = 0.0;

        for (Process p : processes) {
            // Nếu currentTime < arrivalTime, CPU sẽ nhàn rỗi đến lúc process đến
            if (currentTime < p.getArrivalTime()) {
                currentTime = p.getArrivalTime();
            }

            double startTime = currentTime;
            double completionTime = startTime + p.getBurstTime();

            p.setStartTime(startTime);
            p.setCompletionTime(completionTime);

            // Cập nhật Gantt Chart
            ganttChart.add(new Pair<>(p.getId(), new Pair<>(startTime, completionTime)));

            // Cập nhật currentTime
            currentTime = completionTime;
        }

        return ganttChart;
    }
}
