package hotel;

import java.sql.Connection;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Connection con = DBConnection.getConnection();
        if (con == null) {
            System.out.println("Cannot start, database not connected.");
            return;
        }

        Hotel hotel = new Hotel(con);
        int choice = 0;

        System.out.println("===== HOTEL MANAGEMENT SYSTEM =====");

        while (choice != 7) {
            System.out.println("\n1. View all rooms");
            System.out.println("2. View available rooms");
            System.out.println("3. Book a room");
            System.out.println("4. Check out");
            System.out.println("5. View all bookings");
            System.out.println("6. Search guest");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                hotel.showAllRooms();
            } else if (choice == 2) {
                hotel.showAvailableRooms();
            } else if (choice == 3) {
                System.out.print("Guest name: ");
                String name = sc.nextLine();
                System.out.print("Phone number: ");
                String phone = sc.nextLine();
                System.out.print("Room number: ");
                int roomNo = sc.nextInt();
                System.out.print("Number of nights: ");
                int nights = sc.nextInt();
                hotel.bookRoom(name, phone, roomNo, nights);
            } else if (choice == 4) {
                System.out.print("Enter booking ID: ");
                int id = sc.nextInt();
                hotel.checkOut(id);
            } else if (choice == 5) {
                hotel.showAllBookings();
            } else if (choice == 6) {
                System.out.print("Enter name to search: ");
                String name = sc.nextLine();
                hotel.searchGuest(name);
            } else if (choice == 7) {
                System.out.println("Thank you. Bye!");
            } else {
                System.out.println("Wrong choice, try again.");
            }
        }

        try {
            con.close();
        } catch (Exception e) {
            System.out.println("Error closing connection.");
        }
        sc.close();
    }
}
