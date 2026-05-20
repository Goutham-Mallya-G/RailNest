package view;

import dao.BookingDAO;
import dao.TrainDAO;
import dao.UserDAO;
import enums.TrainStatus;
import models.Booking;
import models.Train;
import models.User;

import java.io.Console;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static dao.UserDAO.getPasswordValidationErrors;

public class AppView {
    static Scanner sc = new Scanner(System.in);
    static UserDAO userDAO = new UserDAO();
    static TrainDAO trainDAO = new TrainDAO();
    static Console console = System.console();
    public static void menu(){
        while(true){
            printDashboardMenu();
            int choice = getIntInput();
            switch (choice){
                case 1:
                    printRegsiterDetails("user");
                    break;
                case 2:
                    printLoginDetails();
                    break;
                case 3:
                    System.out.println("Thank you for using our app...");
                    return;
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

    private static void printRegsiterDetails(String role) {
        System.out.println();
        String name;
        String email;
        String password = "Default";
        String passwordCheck = "New";
        System.out.print("Enter the name : ");
        name = getStringInput();
        System.out.print("Enter the email : ");
        email = getStringInput();
        boolean loop = true;
        while(loop){
            if(console != null){
                char[] passwordArray = console.readPassword("Enter Password : ");
                password = new String(passwordArray);
            }else{
                System.out.print("Enter password : ");
                password = getStringInput();
            }
            List<String> passwordErrors = getPasswordValidationErrors(password);
            if(!passwordErrors.isEmpty()){
                AppView.printError("Password is not strong:");
                for(String error : passwordErrors){
                    AppView.printError("- " + error);
                }
                continue;
            }
            System.out.print("Enter the password again : ");
            if(console != null){
                char[] passwordArray = console.readPassword("");
                passwordCheck = new String(passwordArray);
            }else{
                passwordCheck = getStringInput();
            }
            if(!password.equals(passwordCheck)){
                System.out.println("Password mismatch try again");
            }else{
                loop = false;
            }
        }
        if(role.equals("admin") && userDAO.registerAdmin(name,email,password)){
            System.out.println("admin registered Successfully !");
        }else if(role.equals("user") && userDAO.registerUser(name,email,password)){
            System.out.println("user registered successfully !");
        }
        else{
            System.out.println("No new mail is registered !");
        }
    }

    private static void printLoginDetails(){
        System.out.println();
        String email;
        String password;
        boolean loop = true;
        User user = null;
        User admin = null;
        int choice;
        while(loop){
            System.out.println("1.User Login");
            System.out.println("2.Admin Login");
            System.out.println("3.Back");
            System.out.print("Enter your choice : ");
            choice = getIntInput();
            switch(choice){
                case 1:
                    System.out.print("Enter the email : ");
                    email = getStringInput();
                    if(console != null){
                        char[] passwordArray = console.readPassword("Enter Password : ");
                        password = new String(passwordArray);
                    }else{
                        System.out.print("Enter password : ");
                        password = getStringInput();
                    }
                    user = userDAO.userLogin(email,password);
                    if(user == null){
                        System.out.println("Invalid Username or Password");
                        continue;
                    }
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
                                if(selectedTrain != null){
                                    System.out.println("selected " +selectedTrain.getTrain_name());
                                    bookTrain(selectedTrain,user);
                                }
                                break;
                            case 4:
                                List<Booking> bookingList = userDAO.getAllBookings(user);
                                printAllBookings(bookingList);
                                break;
                            case 5:
                                Booking booking = selectCancellingBooking();
                                BookingDAO.cancelBooking(booking , user);
                                break;
                            case 6:
                                user = null;
                                loggedIn = false;
                                break;
                            default:
                                System.out.println("Enter the valid option...");
                        }
                    }
                    break;
                case 2:
                    System.out.print("Enter the email : ");
                    email = getStringInput();
                    if(console != null){
                        char[] passwordArray = console.readPassword("Enter Password : ");
                        password = new String(passwordArray);
                    }else{
                        System.out.print("Enter password : ");
                        password = getStringInput();
                    }
                    admin = UserDAO.adminLogin(email,password);
                    if(admin == null){
                        System.out.println("Invalid Username or Password");
                        continue;
                    }
                    loggedIn = true;
                    while(loggedIn){
                        printAdminMenu(admin.getName());
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
                                printRegsiterDetails("admin");
                                break;
                            case 5:
                                printDeleteTrain();
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
                    break;
                case 3:
                    user = null;
                    admin = null;
                    loop = false;
                default:
                    System.out.println("Enter valid option...");
            }
        }
    }

    private static void printDeleteTrain() {
        Train train = selectTrain();
        if(train != null){
            trainDAO.deleteTrain(train);
        }else{
            System.out.println("No train deleted");
        }
    }

    private static Booking selectCancellingBooking() {
        System.out.print("Enter the booking id : ");
        String bookingId = getStringInput();
        Booking booking = BookingDAO.getBooking(bookingId);
        return booking;
    }

    private static String getStringInput() {
        String string = "";
        try{
            string = sc.nextLine();
        }catch(IllegalArgumentException | InputMismatchException e) {
            System.out.println("Enter the valid input...");
        }
        return string;
    }

    private static void printAllBookings(List<Booking> bookingList) {
        System.out.println();
        if(bookingList.size() == 0){
            System.out.println("There are no bookings yet");
            return;
        }
        System.out.printf("%-10s | %-10s | %-15s | %-15s | %-10s","Booking id","Train id","Seats Booked","Booked Date","Status");
        System.out.println();
        System.out.println("---------------------------------------------------------------------------");
        for(Booking booking : bookingList){
            System.out.printf("%-10s | %-10s | %-15s | %-15s | %-10s" , booking.getBooking_id(),booking.getTrain_id(),booking.getSeat_count(),booking.getBooking_date(),booking.getStatus());
            System.out.println();
        }
    }

    private static void bookTrain(Train selectedTrain, User user) {
        if(selectedTrain == null){
            System.out.println("No train is selected");
            return;
        }
        System.out.print("Enter the number of seats to book : ");
        int seatsNeeded = getIntInput();
        System.out.print("Enter the date of the ride (DD/MM/YYYY) : ");
        String dateString = getStringInput();
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
            String train_id = getStringInput();
            int train_id_num = Integer.parseInt(train_id.substring(train_id.indexOf("-")+1));
            train = selectById(train_id_num);
            if(train == null){
                printError("No train is selected with this ID");
            }
        }else if(option == 2){
            System.out.print("Enter the Train Name : ");
            String trainName = getStringInput();
            train = selectByName(trainName);
            if(train == null){
                printError("No train is selected with this name");
            }
        }else if(option == 3){
            System.out.print("Enter the Train Source : ");
            String trainSource = getStringInput();
            System.out.print("Enter the Train destination : ");
            String trainDestination = getStringInput();
            train = selectBySourceAndDestination(trainSource,trainDestination);
            if(train == null){
                printError("No train is selected with this source and destination");
            }
        }
        return train;
    }

    private static Train selectBySourceAndDestination(String trainSource, String trainDestination) {
        List<Train> trainList = trainDAO.searchTrain(-1,"",trainSource,trainDestination ,3);
        if(trainList.isEmpty())return null;
        return trainList.get(0);
    }

    private static Train selectByName(String trainName) {
        List<Train> trainList = trainDAO.searchTrain(-1,trainName,"","" ,2);
        if(trainList.isEmpty())return null;
        return trainList.get(0);
    }

    private static Train selectById(int trainId) {
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
        String trainId = getStringInput();
        int trainIdNum = Integer.parseInt(trainId.substring(trainId.indexOf("-")+1));
        List<Train> trainList = trainDAO.searchTrain(trainIdNum,"","","",1);
        printAllTrains(trainList);
    }
    private static void printSearchByName() {
        System.out.print("Enter the Train Name : ");
        String trainName = getStringInput();
        List<Train> trainList = trainDAO.searchTrain(-1,trainName,"","",2);
        printAllTrains(trainList);
    }
    private static void printSearchBySourceAndDestination() {
        System.out.print("Enter the Train Source : ");
        String trainSource = getStringInput();
        System.out.println("Enter the Train Destination : ");
        String trainDestination = getStringInput();
        List<Train> trainList = trainDAO.searchTrain(-1,"",trainSource,trainDestination,3);
        printAllTrains(trainList);
    }

    private static void addTrainDetails(){
        System.out.print("Enter the name of the train : ");
        String trainName = getStringInput();
        System.out.print("Enter the source of the train : ");
        String source = getStringInput();
        System.out.print("Enter the destination of the train : ");
        String destination = getStringInput();
        System.out.print("Set the total number of seats : ");
        int total_seats = getIntInput();
        List<Booking> bookings = new ArrayList<>();
        TrainStatus status = TrainStatus.ACTIVE;
        Train train = trainDAO.createTrain(trainName, source, destination, total_seats,status);
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
        System.out.println("3.Exit");
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
        System.out.println("4.Add new admin");
        System.out.println("5.Delete Train");
        System.out.println("6.Logout");
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
