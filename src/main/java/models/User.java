package models;

import java.util.List;

public class User {
    private String user_id;
    private String name;
    private String email;
    private String password;
    private List<Booking> bookingList;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String userId, String name, String email, String password) {
        this.user_id = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getUser_id() {
        return "USR-"+user_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
