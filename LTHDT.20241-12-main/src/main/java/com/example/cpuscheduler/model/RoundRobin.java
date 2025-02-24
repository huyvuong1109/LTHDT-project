package com.example.cpuscheduler.model;

import javafx.util.Pair;
import java.util.*;

public class RoundRobin extends Scheduler {
    private double quantumTime;

    public RoundRobin(List<Process> processes, double quantumTime) {
        super(processes);
        this.quantumTime = quantumTime;
    }

    public double getQuantumTime() {
        return quantumTime;
    }

    public void setQuantumTime(double quantumTime) {
        this.quantumTime = quantumTime;
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

        // Sắp xếp theo arrival time
        processes.sort(Comparator.comparingDouble(Process::getArrivalTime));

        // remainingTime cho từng process
        Map<Integer, Double> remainingTimes = new HashMap<>();
        for (Process p : processes) {
            remainingTimes.put(p.getId(), p.getBurstTime());
        }

        Queue<Process> readyQueue = new LinkedList<>();
        double currentTime = 0.0;
        int completed = 0;
        int n = processes.size();

        while (completed < n) {
            // Thêm process vào hàng đợi nếu đã đến
            for (Process p : processes) {
                if (p.getArrivalTime() <= currentTime && p.getCompletionTime() == 0 && !readyQueue.contains(p) && remainingTimes.get(p.getId()) > 0) {
                    readyQueue.offer(p);
                }
            }

            if (!readyQueue.isEmpty()) {
                Process current = readyQueue.poll();

                double startTime = currentTime;
                double execTime = Math.min(quantumTime, remainingTimes.get(current.getId()));
                double completionTime = startTime + execTime;

                // Ghi lại khoảng thời gian chạy vào gantt chart
                result.add(new Pair<>(current.getId(), new Pair<>(startTime, completionTime)));

                currentTime = completionTime;
                double newRemaining = remainingTimes.get(current.getId()) - execTime;
                remainingTimes.put(current.getId(), newRemaining);

                if (newRemaining == 0) {
                    // Process hoàn thành
                    current.setCompletionTime(currentTime);
                    // Gán startTime lần đầu nếu chưa có
                    if (current.getStartTime() == 0) {
                        current.setStartTime(startTime);
                    }
                    completed++;
                } else {
                    // Chưa hoàn thành, cho quay lại hàng đợi
                    // Trước khi cho quay lại, kiểm tra xem có process mới nào đến
                    for (Process p : processes) {
                        if (p.getArrivalTime() <= currentTime && p.getCompletionTime() == 0 && !readyQueue.contains(p) && remainingTimes.get(p.getId()) > 0 && p != current) {
                            readyQueue.offer(p);
                        }
                    }
                    // Trả lại current vào queue
                    if (current.getStartTime() == 0) {
                        current.setStartTime(startTime);
                    }
                    readyQueue.offer(current);
                }
            } else {
                // Không có process trong queue, nhích thời gian lên
                currentTime += 0.5;
            }
        }

        return result;
    }
}
