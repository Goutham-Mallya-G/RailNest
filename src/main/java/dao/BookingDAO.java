package dao;

import enums.Status;
import models.Booking;
import models.Train;
import models.User;
import util.DBConnection;
import view.AppView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Date;

public class BookingDAO {

    public static Booking booking(Train selectedTrain, int seatsNeeded,String dateString, User user) {
        if (selectedTrain == null) {
            System.out.println("Invalid train data is entered,Please check again");
            return null;
        }
        int availableSeats = selectedTrain.getAvailable_seats();
        Date date = null;
        if (availableSeats == 0) {
            System.out.println("There are no seats available in this train at the moment");
            return null;
        }
        boolean limitedSeats = true;
        while (limitedSeats) {
            if (availableSeats == -1) {
                limitedSeats = false;
                return null;
            } else if (availableSeats < seatsNeeded) {
                System.out.println("There are only " + availableSeats + " available in this train");
                seatsNeeded = AppView.willContinueWithLimitesSeats();
            }
            availableSeats -= seatsNeeded;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                date = Date.valueOf(localDate);
            } catch (DateTimeException e) {
                AppView.printError("Enter the Date in the correct format");
            }

            selectedTrain.setAvailable_seats(availableSeats);

            Booking booking = new Booking(user.getUser_id(), selectedTrain.getTrain_id(), seatsNeeded, date, Status.BOOKED);

            String query = "update trains set available_seats = ? where train_id = ?";

            String query2 = "insert into bookings(user_id,train_id,seat_count,booking_date,status) values(?,?,?,?,?)";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(query);
                 PreparedStatement ps2 = con.prepareStatement(query2)) {

                ps.setInt(1, availableSeats);
                ps.setString(2, selectedTrain.getTrain_id());
                ps.executeUpdate();

                ps2.setString(1, user.getUser_id());
                ps2.setString(2, selectedTrain.getTrain_id());
                ps2.setInt(3, seatsNeeded);
                ps2.setDate(4, date);
                ps2.setString(5, Status.BOOKED.name());

                ps2.executeUpdate();
                return booking;
            } catch (SQLException e) {
                String error = e.getMessage();
                if (error.contains("chk_seat_count_limit")) {
                    AppView.printError("Seats should be 1 to 200");
                }
                //            else if (error.contains("booking_date")) {
                //                AppView.printError("Enter the Booking date in correct format (DD/MM/YYY)");
                //            }
                else {
                    System.out.println("Error : " + e.getMessage());
                }
            }
        }
        return null;
    }
    }