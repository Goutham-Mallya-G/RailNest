Railway Reservation System

A console-based Railway Reservation System built using Java, JDBC, and MySQL.
This project was created to understand how backend applications actually work without relying on frameworks.

The main focus of this project was learning:
JDBC
SQL
database design
authentication flow
layered architecture
DAO pattern
backend logic handling

Features:
User Registration
User Login Authentication
Role-based access (Admin / User)
Add Train
View Available Trains
Search Train
Ticket Booking
Booking History
Delete Train
Ticket Cancellation
Input Validation
Password Hashing using BCrypt
Relational Database Design using MySQL

Tech Stack :
Java
JDBC
MySQL
Maven
BCrypt

Project Structure :
src/main/java
│
├── dao
├── enums
├── models
├── util
└── view

dao → Database operations
models → Entity classes
util → Utility classes
view → Console interaction
Database Design

The project uses 4 main tables:
users
admins
trains
bookings

Relationships are maintained using foreign keys.

What I Learned :
how JDBC works internally
how to connect Java with MySQL
how authentication systems work
how to structure backend applications
how to write and manage SQL queries
how ResultSet mapping works
how foreign keys and constraints work
basic backend architecture principles
Future Improvements

Some improvements that can still be added:
Seat allocation
Collecting all passenger information
Transaction management for booking flow
Waiting list system
Train route/station handling
REST API version using Spring Boot
Better session handling
UI version using React or JSP/Servlet

How to Run :
Clone the repository
Create MySQL database
Run the SQL schema file
Configure database credentials in db.properties
Run the Main.java file

Note:
This project was built mainly for learning backend development fundamentals and understanding how database-driven applications work before moving to frameworks like Spring Boot.
