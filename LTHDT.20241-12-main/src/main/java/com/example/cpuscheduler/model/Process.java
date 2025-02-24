package com.example.cpuscheduler.model;

public class Process {
    private int id;
    private double arrivalTime;
    private double burstTime;
    private int priority;

    private double startTime; 
    private double completionTime;
    private double turnaroundTime;
    private double waitingTime;

    public Process(){}

    public Process(int id, double arrivalTime, double burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }
    public double getArrivalTime() {
        return arrivalTime;
    }
    public double getBurstTime() {
        return burstTime;
    }
    public int getPriority() {return priority;}

    public void setId(int id) {
        this.id = id;
    }
    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    public void setBurstTime(double burstTime) {
        this.burstTime = burstTime;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(double completionTime) {
        this.completionTime = completionTime;
    }

    public double getTurnaroundTime() {
        return completionTime - arrivalTime;
    }

    public double getWaitingTime() {
        return completionTime - arrivalTime - burstTime;
    }
}
