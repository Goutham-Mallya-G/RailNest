package models;

import enums.Status;

import java.time.LocalDate;
import java.util.Date;

public class Booking {
    private String booking_id;
    private String user_id;
    private String train_id;
    private int seat_count;
    private Date booking_date;
    private Status status;

    public Booking(String user_id, String train_id, int seat_count, Date booking_date, Status status) {
        this.user_id = user_id;
        this.train_id = train_id;
        this.seat_count = seat_count;
        this.booking_date = booking_date;
        this.status = status;
    }

    public String getBooking_id() {
        return "BK-"+booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getUser_id() {
        return "USR-"+user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public static String getTrain_id(int train_id) {
        return "TRN-"+train_id;
    }

    public String getTrain_id() {
        return train_id;
    }

    public void setTrain_id(String train_id) {
        this.train_id = train_id;
    }

    public int getSeat_count() {
        return seat_count;
    }

    public void setSeat_count(int seat_count) {
        this.seat_count = seat_count;
    }

    public Date getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(Date booking_date) {
        this.booking_date = booking_date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
