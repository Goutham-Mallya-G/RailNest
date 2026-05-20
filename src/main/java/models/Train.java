package models;

import enums.TrainStatus;

import java.util.ArrayList;
import java.util.List;

public class Train {
    private String train_id;
    private String train_name;
    private String source;
    private String destination;
    private int total_seats;
    private int available_seats;
    private TrainStatus status;

    public Train(String train_name, String source, String destination, int total_seats, int available_seats, TrainStatus status) {
        this.train_name = train_name;
        this.source = source;
        this.destination = destination;
        this.total_seats = total_seats;
        this.available_seats = available_seats;
        this.status = status;
    }

    public Train(String train_id, String train_name, String source, String destination, int total_seats, int available_seats, TrainStatus status) {
        this.train_id = train_id;
        this.train_name = train_name;
        this.source = source;
        this.destination = destination;
        this.total_seats = total_seats;
        this.available_seats = available_seats;
        this.status = status;
    }

    public String getTrain_id() {
        return train_id;
    }

    public String getTrain_id(int train_id){
        return "TRN-"+train_id;
    }

    public void setTrain_id(String train_id) {
        this.train_id = train_id;
    }

    public String getTrain_name() {
        return train_name;
    }

    public void setTrain_name(String train_name) {
        this.train_name = train_name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getTotal_seats() {
        return total_seats;
    }

    public void setTotal_seats(int total_seats) {
        this.total_seats = total_seats;
    }

    public int getAvailable_seats() {
        return available_seats;
    }

    public void setAvailable_seats(int available_seats) {
        this.available_seats = available_seats;
    }

    public TrainStatus getStatus() {
        return status;
    }

    public void setStatus(TrainStatus status) {
        this.status = status;
    }
}
