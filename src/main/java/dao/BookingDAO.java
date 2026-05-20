package dao;

import enums.Status;
import models.Booking;
import models.Train;
import models.User;
import util.DBConnection;
import view.AppView;

import java.sql.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookingDAO {
    private static int getIdNumber(String id) {
        return Integer.parseInt(id.substring(id.indexOf("-") + 1));
    }

    public static Booking booking(Train selectedTrain, int seatsNeeded,String dateString, User user){
        if (selectedTrain == null) {
            System.out.println("Invalid train data is entered,Please check again");
            return null;
        }
        int availableSeats = selectedTrain.getAvailable_seats();
        Date date = null;
        if (seatsNeeded <= 0) {
            AppView.printError("Please enter seats greater than 1");
            return null;
        }
        boolean limitedSeats = true;
        while (limitedSeats) {
            if (availableSeats <= 0) {
                limitedSeats = false;
                return null;
            } else if (availableSeats < seatsNeeded) {
                System.out.println("There are only " + availableSeats + " available in this train");
                seatsNeeded = AppView.willContinueWithLimitesSeats();
                if (seatsNeeded <= 0 ) {
                    return null;
                }
            }
            availableSeats -= seatsNeeded;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                date = Date.valueOf(localDate);
            } catch (DateTimeException e) {
                AppView.printError("Enter the Date in the correct format");
                return null;
            }

            selectedTrain.setAvailable_seats(availableSeats);

            Booking booking = new Booking(user.getUser_id(), selectedTrain.getTrain_id(), seatsNeeded, date, Status.BOOKED);

            String query = "update trains set available_seats = ? where train_id = ?";

            String query2 = "insert into bookings(user_id,train_id,seat_count,booking_date,status) values(?,?,?,?,?)";
            Connection con =  null;
            try {
                con = DBConnection.getConnection();
                con.setAutoCommit(false);
                PreparedStatement ps = con.prepareStatement(query);
                PreparedStatement ps2 = con.prepareStatement(query2);

                ps.setInt(1, availableSeats);
                ps.setInt(2, getIdNumber(selectedTrain.getTrain_id()));

                ps2.setInt(1, getIdNumber(user.getUser_id()));
                ps2.setInt(2, getIdNumber(selectedTrain.getTrain_id()));
                ps2.setInt(3, seatsNeeded);
                ps2.setDate(4, date);
                ps2.setString(5, Status.BOOKED.name());

                ps.executeUpdate();
                ps2.executeUpdate();
                con.commit();
                return booking;
            } catch (SQLException e) {
                if (con != null) {
                    try {
                        con.rollback();
                    }catch (SQLException er){
                        System.out.println("Error : " + er.getMessage());
                    }
                }
                String error = e.getMessage();
                if (error.contains("chk_seat_count_limit")) {
                    AppView.printError("Seats should be 1 to 200");
                }
                else if (error.contains("booking_date")) {
                    AppView.printError("Enter the Booking date in correct format (DD/MM/YYY)");
                }
                else {
                    System.out.println("Error : " + e.getMessage());
                }
                return null;
            }finally {
                if (con != null) {
                    try {
                        con.setAutoCommit(true);
                        con.close();
                    }catch (SQLException e){
                        System.out.println("Error : " + e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    public static void cancelBooking(Booking cancelBooking) {
        if (cancelBooking == null) {
            AppView.printError("Booking not found");
            return;
        }
        String query = "update bookings set status = 'CANCELLED' where booking_id = ?";
        cancelBooking.setStatus(Status.CANCELLED);
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query)){

            ps.setInt(1, getIdNumber(cancelBooking.getBooking_id()));

            ps.executeUpdate();

        }catch (SQLException e){
            System.out.println("Error : " + e.getMessage());
        }

    }

    public static Booking getBooking(String bookingId) {
        Booking booking = null;
        String query = "select * from bookings where booking_id = ?";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);){
            ps.setInt(1, getIdNumber(bookingId));
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                return null;
            }

            String booking_id = rs.getString("booking_id");
            String user_id = rs.getString("user_id");
            String train_id = "TRN-"+rs.getString("train_id");
            int seat_count = rs.getInt("seat_count");
            Date booking_date = rs.getDate("booking_date");
            Status status = Status.valueOf(rs.getString("status"));

            booking = new Booking(booking_id,user_id,train_id,seat_count,booking_date,status);

            return booking;

        }catch(SQLException e){
            System.out.println("Error : " + e.getMessage());
        }
        return booking;
    }
}
