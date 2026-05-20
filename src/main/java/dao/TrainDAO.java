package dao;

import enums.Status;
import enums.TrainStatus;
import models.Booking;
import models.Train;
import util.DBConnection;
import view.AppView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static enums.Status.TRAIN_CANCELLED;

public class TrainDAO {
    public Train createTrain(String trainName, String source, String destination, int totalSeats, TrainStatus status) {
        Train train = new Train(trainName,source,destination,totalSeats,totalSeats,status);

        String query = "insert into trains(train_name,source,destination,total_seats,available_seats,status) values(?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);){

            ps.setString(1, train.getTrain_name());
            ps.setString(2, train.getSource());
            ps.setString(3, train.getDestination());
            ps.setInt(4, train.getTotal_seats());
            ps.setInt(5, train.getAvailable_seats());
            ps.setString(6,status.toString());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                AppView.printError("Creating train failed, no rows affected.");
                return null;
            }
            return train;
        } catch (SQLException e) {
            String error = e.getMessage();
            if (error.contains("chk_total_seats_limit")) {
                AppView.printError("Seats should be 101 to 200");
            } else if (error.contains("chk_train_name_length")) {
                AppView.printError("Train name length should be 3 to 50");
            } else if (error.contains("chk_destination_length")) {
                AppView.printError("Train destination length should be 3 to 50");
            } else if (error.contains("chk_source_length")) {
                AppView.printError("Train source length should be 3 to 50");
            } else {
                AppView.printError("Error : " + e.getMessage());
            }
        }
        return null;
    }

    public List<Train> viewTrains() {
        String query = "select * from trains where status = 'ACTIVE'";
        List<Train> trainList = new ArrayList<>();
        try(Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();){

            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                String train_id = "TRN-" + rs.getString("train_id");
                String train_name = rs.getString("train_name");
                String source = rs.getString("source");
                String destination = rs.getString("destination");
                int total_seats = rs.getInt("total_seats");
                int available_seats = rs.getInt("available_seats");
                TrainStatus status = TrainStatus.valueOf(rs.getString("status"));
                Train train = new Train(train_id,train_name,source,destination,total_seats,available_seats,status);
                trainList.add(train);
            }
            return trainList;
        }catch (SQLException e){
            AppView.printError("No trains found");
            AppView.printError("Error : " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<Train> searchTrain(int trainId,String trainName, String trainSource, String trainDestination, int option) {
        String query = "";
        List<Train> trainList = new ArrayList<>();
        if(option == 1){
            query = "select * from trains where train_id = ?";
        }else if(option == 2){
            query = "select * from trains where train_name = ?";
        }else if(option == 3){
            query = "select * from trains where source = ? and destination = ?";
        }
        Train train = null;
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);){

            if(option == 1){
                ps.setInt(1,trainId);
            }else if(option == 2){
                ps.setString(1,trainName);
            }else if(option == 3){
                ps.setString(1,trainSource);
                ps.setString(2,trainDestination);
            }

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String train_id = "TRN-" + rs.getString("train_id");
                String train_name = rs.getString("train_name");
                String source = rs.getString("source");
                String destination = rs.getString("destination");
                int total_seats = rs.getInt("total_seats");
                int available_seats = rs.getInt("available_seats");
                TrainStatus status = TrainStatus.valueOf(rs.getString("status"));
                train = new Train(train_id,train_name,source,destination,total_seats,available_seats,status);
                trainList.add(train);
            }
            return trainList;
        }catch (SQLException e){
            AppView.printError("No trains found");
            AppView.printError("Error : " + e.getMessage());
        }
        return new ArrayList<>();
    }
    private static int getIdNumber(String id) {
        try {
            if (id == null || !id.contains("-")) {
                return -1;
            }
            return Integer.parseInt(id.substring(id.indexOf("-") + 1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    public void deleteTrain(Train train) {
        int trainId = getIdNumber(train.getTrain_id());

        String cancelBookingsQuery = "update bookings set status = 'TRAIN_CANCELLED' where train_id = ?";
        String cancelTrainQuery = "update trains set status = 'CANCELLED' where train_id = ?";

        Connection con = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            PreparedStatement ps1 = con.prepareStatement(cancelBookingsQuery);
            PreparedStatement ps2 = con.prepareStatement(cancelTrainQuery);

            ps1.setInt(1, trainId);
            ps1.executeUpdate();

            ps2.setInt(1, trainId);
            int affectedRows = ps2.executeUpdate();

            if (affectedRows > 0) {
                con.commit();
                AppView.printError("Train deleted");
            } else {
                con.rollback();
                AppView.printError("No train is deleted");
            }

        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException er) {
                System.out.println("Error : " + er.getMessage());
            }

            System.out.println("Error : " + e.getMessage());

        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Error : " + e.getMessage());
            }
        }
    }
}
