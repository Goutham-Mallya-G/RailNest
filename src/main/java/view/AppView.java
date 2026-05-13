package view;

import dao.BookingDAO;
import dao.TrainDAO;
import dao.UserDAO;
import enums.Role;
import models.Booking;
import models.Train;
import models.User;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static dao.UserDAO.getPasswordValidationErrors;

public class AppView {
    static Scanner sc = new Scanner(System.in);
    static UserDAO userDAO = new UserDAO();
    static TrainDAO trainDAO = new TrainDAO();
    public static void menu(){
        while(true){
            printDashboardMenu();
            int choice = getIntInput();
            switch (choice){
                case 1:
                    printRegsiterDetails();
                    break;
                case 2:
                    printLoginDetails();
                    break;
                default:
                    System.out.println("Enter valid option...");
            }
        }
    }

    private static int getIntInput() {
        int choice = 0;
        try{
            choice = sc.nextInt();
            sc.nextLine();
        }catch(IllegalArgumentException | InputMismatchException e) {
            sc.nextLine();
            System.out.println("Enter the valid input...");
        }
        return choice;
    }

    private static void printRegsiterDetails() {
        System.out.println();
        String name;
        String email;
        String password = "Default";
        Role role = Role.USER;
        System.out.print("Enter the name : ");
        name = sc.nextLine();
        System.out.print("Enter the email : ");
        email = sc.nextLine();
        boolean loop = true;
        while(loop){
            System.out.print("Enter the password : ");
            password = sc.nextLine();
            List<String> passwordErrors = getPasswordValidationErrors(password);
            if(!passwordErrors.isEmpty()){
                AppView.printError("Password is not strong:");
                for(String error : passwordErrors){
                    AppView.printError("- " + error);
                }
                continue;
            }
            System.out.print("Enter the password again : ");
            String passwordCheck = sc.nextLine();
            if(!password.equals(passwordCheck)){
                System.out.println("Password mismatch try again");
            }else{
                loop = false;
            }
        }
        loop = true;
        while(loop){
            try{
                System.out.print("Enter the role (Admin/User): ");
                role = Role.valueOf(sc.nextLine().toUpperCase());
                loop = false;
            }catch (IllegalArgumentException e) {
                System.out.println("Invalid role");
            }
        }
        if(userDAO.registerUser(name,email,password,role)){
            System.out.println("Registered " + role +" Successfully !");
        }else{
            System.out.println("No new "+ role +" registered !");
        }
    }

    private static void printLoginDetails(){
        System.out.println();
        String email;
        String password;
        boolean loop = true;
        int choice;
        while(loop){
            System.out.print("Enter the email : ");
            email = sc.nextLine();
            System.out.print("Enter the password : ");
            password = sc.nextLine();
            User user = userDAO.login(email,password);
            if(user == null){
                System.out.println("Invalid Username or Password");
                loop = false;
                continue;
            };
            if(user.getRole() == Role.USER){
                boolean loggedIn = true;
                while(loggedIn){
                    printUserMenu(user.getName());
                    choice = getIntInput();
                    switch (choice){
                        case 1:
                            List<Train> trainList = trainDAO.viewTrains();
                            printAllTrains(trainList);
                            break;
                        case 2:
                            printSearchOptions();
                            choice = getIntInput();
                            switch (choice){
                                case 1:
                                    printSearchById();
                                    break;
                                case 2:
                                    printSearchByName();
                                    break;
                                case 3:
                                    printSearchBySourceAndDestination();
                                    break;
                            }
                            break;
                        case 3:
                            Train selectedTrain = selectTrain();
                            bookTrain(selectedTrain,user);
                            break;
                        case 4:
                            List<Booking> bookingList = userDAO.getAllBookings(user);
                            printAllBookings(bookingList);
                            break;
                        case 5:
                            break;
                        case 6:
                            user = null;
                            loggedIn = false;
                            loop = false;
                            break;
                        default:
                            System.out.println("Enter the valid option...");
                    }
                }
            }else{
                boolean loggedIn = true;
                while(loggedIn){
                    printAdminMenu(user.getName());
                    choice = getIntInput();
                    switch (choice) {
                        case 1:
                            addTrainDetails();
                            break;
                        case 2:
                            List<Train> trainList = trainDAO.viewTrains();
                            printAllTrains(trainList);
                            break;
                        case 3:
                            printSearchOptions();
                            choice = getIntInput();
                            switch (choice){
                                case 1:
                                    printSearchById();
                                    break;
                                case 2:
                                    printSearchByName();
                                    break;
                                case 3:
                                    printSearchBySourceAndDestination();
                                    break;
                            }
                            break;
                        case 4:
                            user = null;
                            loggedIn = false;
                            loop = false;
                            break;
                        default:
                            System.out.println("Enter the valid option...");
                    }
                }
            }
        }
    }

    private static void printAllBookings(List<Booking> bookingList) {
        System.out.println();
        if(bookingList.size() == 0){
            System.out.println("There are no bookings yet");
            return;
        }
        System.out.printf("%-10s | %-10s | %-10s | %-10s | %-10s","Booking id","Train id","Seats Booked","Booked Date","Status");
        System.out.println();
        for(Booking booking : bookingList){
            System.out.printf("%-10s | %-10s | %-10s | %-10s | %-10s" , booking.getBooking_id(),"TRN-" + booking.getTrain_id(),booking.getSeat_count(),booking.getBooking_date(),booking.getStatus());
            System.out.println();
        }
    }

    private static void bookTrain(Train selectedTrain, User user) {
        System.out.print("Enter the number of seats to book : ");
        int seatsNeeded = getIntInput();
        System.out.print("Enter the date of the ride (DD/MM/YYYY) : ");
        String dateString = sc.nextLine();
        if(BookingDAO.booking(selectedTrain,seatsNeeded,dateString,user) != null){
            System.out.println("Booking Success");
        }else{
            System.out.println("Booking Rejected");
        }
    }

    private static Train selectTrain() {
        printSelectOptions();
        int option = getIntInput();
        Train train = null;
        if(option == 1){
            System.out.print("Enter the Train Id : ");
            String train_id = sc.nextLine();
            train = selectById(train_id);
        }else if(option == 2){
            System.out.print("Enter the Train Name : ");
            String trainName = sc.nextLine();
            train = selectByName(trainName);
        }else if(option == 3){
            System.out.print("Enter the Train Source : ");
            String trainSource = sc.nextLine();
            System.out.print("Enter the Train destination : ");
            String trainDestination = sc.nextLine();
            train = selectBySourceAndDestination(trainSource,trainDestination);
        }
        return train;
    }

    private static Train selectBySourceAndDestination(String trainSource, String trainDestination) {
        List<Train> trainList = trainDAO.searchTrain("","",trainSource,trainDestination ,3);
        if(trainList.isEmpty())return null;
        return trainList.get(0);
    }

    private static Train selectByName(String trainName) {
        List<Train> trainList = trainDAO.searchTrain("",trainName,"","" ,2);
        if(trainList.isEmpty())return null;
        return trainList.get(0);
    }

    private static Train selectById(String trainId) {
        List<Train> trainList = trainDAO.searchTrain(trainId,"","","" ,1);
        if(trainList.isEmpty())return null;
        return trainList.get(0);
    }

    private static void printSelectOptions() {
        System.out.println("1.Book by Train ID");
        System.out.println("2.Book by Train name");
        System.out.println("3.Book by source & destination");
        System.out.print("Enter the option : ");
    }

    private static void printUserMenu(String name) {
        System.out.println();
        System.out.println("Welcome User "+ name);
        System.out.println("1.View Trains");
        System.out.println("2.Search Trains");
        System.out.println("3.Book train");
        System.out.println("4.My Bookings");
        System.out.println("5.Cancel My Bookings");
        System.out.println("6.Logout");
        System.out.print("Enter your option : ");
    }

    private static void printSearchById() {
        System.out.print("Enter the Train Id : ");
        String trainId = sc.nextLine();
        List<Train> trainList = trainDAO.searchTrain(trainId,"","","",1);
        printAllTrains(trainList);
    }
    private static void printSearchByName() {
        System.out.print("Enter the Train Name : ");
        String trainName = sc.nextLine();
        List<Train> trainList = trainDAO.searchTrain("",trainName,"","",2);
        printAllTrains(trainList);
    }
    private static void printSearchBySourceAndDestination() {
        System.out.print("Enter the Train Source : ");
        String trainSource = sc.nextLine();
        System.out.println("Enter the Train Destination : ");
        String trainDestination = sc.nextLine();
        List<Train> trainList = trainDAO.searchTrain("","",trainSource,trainDestination,3);
        printAllTrains(trainList);
    }

    private static void addTrainDetails(){
        System.out.print("Enter the name of the train : ");
        String trainName = sc.nextLine();
        System.out.print("Enter the source of the train : ");
        String source = sc.nextLine();
        System.out.print("Enter the destination of the train : ");
        String destination = sc.nextLine();
        System.out.print("Set the total number of seats : ");
        int total_seats = getIntInput();
        Train train = trainDAO.createTrain(trainName, source, destination, total_seats);
        if (train != null) {
            System.out.println("Train added successfully");
        } else {
            System.out.println("Train did not added");
        }
    }

    private static void printDashboardMenu() {
        System.out.println( "   ___       _ ___  __        __ \n" +
                            "  / _ \\___ _(_) / |/ /__ ___ / /_\n" +
                            " / , _/ _ `/ / /    / -_|_-</ __/\n" +
                            "/_/|_|\\_,_/_/_/_/|_/\\__/___/\\__/ \n" +
                            "                                 ");
        System.out.println("1.Register");
        System.out.println("2.Login");
        System.out.print("Enter your choice : ");
    }

    private static void printAllTrains(List<Train> trainList){
        System.out.println();
        if(trainList.isEmpty()){
            System.out.println("No Trains to List");
            return;
        }
        System.out.printf("%-10s | %-20s | %-20s | %-20s | %-15s | %-15s" , "Train id" , "Train name" , "Source" , "Destination" , "Total_seats" , "Available seats");
        System.out.println();
        System.out.println("--------------------------------------------------------------------------------------------------------------------------");
        for(int i = 0 ; i < trainList.size() ; i++){
            System.out.printf("%-10s | %-20s | %-20s | %-20s | %-15s | %-15s",trainList.get(i).getTrain_id(), trainList.get(i).getTrain_name(), trainList.get(i).getSource(), trainList.get(i).getDestination(), trainList.get(i).getTotal_seats(), trainList.get(i).getAvailable_seats());
            System.out.println();
        }
    }

    public static void printAdminMenu(String name){
        System.out.println();
        System.out.println("Welcome Admin "+ name);
        System.out.println("1.Add Train");
        System.out.println("2.View All Trains");
        System.out.println("3.Search train");
        System.out.println("4.Logout");
        System.out.print("Enter your option : ");
    }

    public static void printSearchOptions(){
        System.out.println("1.Search by Train ID");
        System.out.println("2.Search by Train name");
        System.out.println("3.Search by source & destination");
        System.out.println("4.Back");
        System.out.print("Enter the option : ");
    }

    public static void printError(String message){
        System.out.println(message);
    }

    public static int willContinueWithLimitesSeats() {
        System.out.println("Are you willing to coninue ? (Y/N)");
        char willContinue = sc.next().toUpperCase().charAt(0);
        if(willContinue == 'Y'){
            System.out.print("Enter the number of seats you need : ");
            int seatsNeeded = sc.nextInt();
            return seatsNeeded;
        }else{
            return -1;
        }
    }
}