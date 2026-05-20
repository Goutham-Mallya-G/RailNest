package dao;

import models.Train;
import util.DBConnection;
import view.AppView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainDAO {
    public Train createTrain(String trainName, String source, String destination, int totalSeats) {
        Train train = new Train(trainName,source,destination,totalSeats,totalSeats);

        String query = "insert into trains(train_name,source,destination,total_seats,available_seats) values(?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);){

            ps.setString(1, train.getTrain_name());
            ps.setString(2, train.getSource());
            ps.setString(3, train.getDestination());
            ps.setInt(4, train.getTotal_seats());
            ps.setInt(5, train.getAvailable_seats());

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
        String query = "select * from trains";
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
                Train train = new Train(train_id,train_name,source,destination,total_seats,available_seats);
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
                String train_id = rs.getString("train_id");
                String train_name = rs.getString("train_name");
                String source = rs.getString("source");
                String destination = rs.getString("destination");
                int total_seats = rs.getInt("total_seats");
                int available_seats = rs.getInt("available_seats");
                train = new Train(train_id,train_name,source,destination,total_seats,available_seats);
                trainList.add(train);
            }
            return trainList;
        }catch (SQLException e){
            AppView.printError("No trains found");
            AppView.printError("Error : " + e.getMessage());
        }
        return new ArrayList<>();
    }
}
