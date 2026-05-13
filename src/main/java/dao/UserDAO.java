package dao;

import enums.Role;
import enums.Status;
import models.Booking;
import models.User;
import org.apache.commons.validator.routines.EmailValidator;
import org.mindrot.jbcrypt.BCrypt;
import util.DBConnection;
import util.IDGenerator;
import view.AppView;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDAO {
    public static boolean registerUser(String name, String email, String password, Role role){
        if(!EmailValidator.getInstance().isValid(email)){
            AppView.printError("Email is not valid");
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password,BCrypt.gensalt(12));
        String query = "insert into users(user_id,name,email,password,role) values(?,?,?,?,?)";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);){
            ps.setString(1,IDGenerator.generateAdminID(role));
            ps.setString(2,name);
            ps.setString(3,email.toLowerCase());
            ps.setString(4,hashedPassword);
            ps.setString(5,role.toString());

            return ps.executeUpdate() > 0;

        }catch(SQLException | NullPointerException e){
            String errorMessage = e.getMessage();
//            if(errorMessage.contains("Duplicate entry")) {
//                AppView.printError("User already exists with this email");
            if(errorMessage.contains("null")) {
                AppView.printError("Please provide necessary details");
            }else if(errorMessage.contains("chk_name_length")){
                AppView.printError("Please provide name length 3 - 50");
            }else{
                System.out.println("Error : " + e.getMessage());
            }
        }
        return false;
    }

    public static List<String> getPasswordValidationErrors(String password) {
        List<String> errors = new ArrayList<>();
        if(password == null || password.isEmpty()){
            errors.add("Password is required");
            return errors;
        }
        if(password.length() < 8){
            errors.add("Password must be at least 8 characters long");
        }
        if(!password.matches(".*[a-z].*")){
            errors.add("Password must contain at least one lowercase letter");
        }
        if(!password.matches(".*[A-Z].*")){
            errors.add("Password must contain at least one uppercase letter");
        }
        if(!password.matches(".*\\d.*")){
            errors.add("Password must contain at least one number");
        }
        if(!password.matches(".*[@$!%*?&.].*")){
            errors.add("Password must contain at least one special character: @$!%*?&.");
        }
        if(!password.matches("[A-Za-z\\d@$!%*?&.]+")){
            errors.add("Password can only contain letters, numbers, and these special characters: @$!%*?&.");
        }
        return errors;
    }

    public static User login(String email, String password) {
        email = email.toLowerCase();
        String query = "select * from users where email = ?";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query);){

            ps.setString(1,email);
            ResultSet rs = ps.executeQuery();

            if(!rs.next()){
                return null;
            }

            String user_id = rs.getString("user_id");
            String name = rs.getString("name");
            String role = rs.getString("role");
            String hashedPassword = rs.getString("password");

            if(!BCrypt.checkpw(password,hashedPassword)){
                return null;
            }

            User user = new User(user_id,name,email,hashedPassword, Role.valueOf(role));

            return user;
        }catch(SQLException e){
            System.out.println("Error : " + e.getMessage());
        }
        return null;
    }

    public List<Booking> getAllBookings(User user) {
        List<Booking> bookingList = new ArrayList<>();
        String userId = user.getUser_id();
        String query = "Select * from bookings where user_id = ?";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(query)){
            ps.setString(1,userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String bookingId = rs.getString("booking_id");
                String trainId = Booking.getTrain_id(rs.getInt("train_id"));
                int seatCount = rs.getInt("seat_count");
                Date bookedDate = rs.getDate("booking_date");
                Status bookingStatus = Status.valueOf(rs.getString("status"));

                Booking booking = new Booking(bookingId,trainId,seatCount,bookedDate,bookingStatus);
                bookingList.add(booking);
            }

        }catch (SQLException e){
            System.out.println("Error : " + e.getMessage());
        }
        return bookingList;
    }
}
