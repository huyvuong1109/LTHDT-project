package com.example.cpuscheduler.model;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SJN extends Scheduler {

    public SJN(List<Process> processes) {
        super(processes);
    }

    @Override
    public List<Pair<Integer, Pair<Double, Double>>> schedule(){
        for (Process p : processes) {
            p.setCompletionTime(0.0);
            p.setStartTime(0.0);
        }
        List<Pair<Integer, Pair<Double,Double>>> result = new ArrayList<>();
        if (processes.isEmpty()) {
            return result;
        }

        // Sắp xếp process theo thời gian đến
        processes.sort(Comparator.comparingDouble(Process::getArrivalTime));

        List<Process> readyQueue = new ArrayList<>();
        double currentTime = 0.0;
        int completed = 0;
        int n = processes.size();

        while (completed < n) {
            // Thêm process vào hàng đợi nếu đã đến
            for (Process p : processes) {
                if (p.getArrivalTime() <= currentTime && p.getCompletionTime() == 0 && !readyQueue.contains(p)) {
                    readyQueue.add(p);
                }
            }

            // Nếu hàng đợi có process
            if (!readyQueue.isEmpty()) {
                // Chọn process có burstTime ngắn nhất
                readyQueue.sort(Comparator.comparingDouble(Process::getBurstTime));
                Process current = readyQueue.remove(0);

                double startTime = Math.max(currentTime, current.getArrivalTime());
                double completionTime = startTime + current.getBurstTime();

                current.setStartTime(startTime);
                current.setCompletionTime(completionTime);

                result.add(new Pair<>(current.getId(), new Pair<>(startTime, completionTime)));

                currentTime = completionTime;
                completed++;
            } else {
                // Nếu không có process nào sẵn sàng, nhảy thời gian lên
                currentTime += 0.5;
            }
        }

        return result;
    }
}
