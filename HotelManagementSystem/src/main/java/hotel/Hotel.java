package hotel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Hotel {

    Connection con;

    public Hotel(Connection con) {
        this.con = con;
    }

    // show all rooms
    public void showAllRooms() {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from rooms");

            System.out.println("\nRoom\tType\tPrice\tStatus");
            System.out.println("-----------------------------");
            while (rs.next()) {
                System.out.println(rs.getInt("room_no") + "\t" + rs.getString("type") + "\t"
                        + rs.getDouble("price") + "\t" + rs.getString("status"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // show only rooms that are free
    public void showAvailableRooms() {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from rooms where status = 'Available'");

            System.out.println("\nAvailable rooms:");
            boolean found = false;
            while (rs.next()) {
                System.out.println("Room " + rs.getInt("room_no") + " - " + rs.getString("type")
                        + " - Rs." + rs.getDouble("price") + " per night");
                found = true;
            }
            if (found == false) {
                System.out.println("No rooms available.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // book a room
    public void bookRoom(String name, String phone, int roomNo, int nights) {
        try {
            // first check if room is there and free
            PreparedStatement ps = con.prepareStatement("select * from rooms where room_no = ?");
            ps.setInt(1, roomNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next() == false) {
                System.out.println("Room not found.");
                return;
            }
            if (rs.getString("status").equals("Booked")) {
                System.out.println("Room is already booked.");
                return;
            }
            if (nights <= 0) {
                System.out.println("Nights should be at least 1.");
                return;
            }

            double price = rs.getDouble("price");

            // save the booking
            PreparedStatement ins = con.prepareStatement(
                    "insert into bookings (name, phone, room_no, nights, status) values (?, ?, ?, ?, 'Active')",
                    Statement.RETURN_GENERATED_KEYS);
            ins.setString(1, name);
            ins.setString(2, phone);
            ins.setInt(3, roomNo);
            ins.setInt(4, nights);
            ins.executeUpdate();

            ResultSet keys = ins.getGeneratedKeys();
            keys.next();
            int bookingId = keys.getInt(1);

            // mark room as booked
            PreparedStatement upd = con.prepareStatement("update rooms set status = 'Booked' where room_no = ?");
            upd.setInt(1, roomNo);
            upd.executeUpdate();

            System.out.println("\nRoom booked successfully!");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Total (without tax): Rs." + (price * nights));

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // check out and print bill
    public void checkOut(int bookingId) {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "select b.name, b.phone, b.room_no, b.nights, r.type, r.price "
                    + "from bookings b, rooms r "
                    + "where b.room_no = r.room_no and b.booking_id = ? and b.status = 'Active'");
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next() == false) {
                System.out.println("No active booking with this ID.");
                return;
            }

            String name = rs.getString("name");
            String phone = rs.getString("phone");
            int roomNo = rs.getInt("room_no");
            int nights = rs.getInt("nights");
            String type = rs.getString("type");
            double price = rs.getDouble("price");

            // update booking and room
            PreparedStatement p1 = con.prepareStatement("update bookings set status = 'Checked out' where booking_id = ?");
            p1.setInt(1, bookingId);
            p1.executeUpdate();

            PreparedStatement p2 = con.prepareStatement("update rooms set status = 'Available' where room_no = ?");
            p2.setInt(1, roomNo);
            p2.executeUpdate();

            // bill
            double amount = price * nights;
            double gst = amount * 0.12;
            double total = amount + gst;

            System.out.println("\n========== BILL ==========");
            System.out.println("Booking ID : " + bookingId);
            System.out.println("Name       : " + name);
            System.out.println("Phone      : " + phone);
            System.out.println("Room       : " + roomNo + " (" + type + ")");
            System.out.println("Nights     : " + nights);
            System.out.println("Amount     : Rs." + amount);
            System.out.println("GST 12%    : Rs." + gst);
            System.out.println("Total      : Rs." + total);
            System.out.println("==========================");
            System.out.println("Thank you, visit again!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // show all bookings
    public void showAllBookings() {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from bookings");

            System.out.println("\nID\tName\tPhone\t\tRoom\tNights\tStatus");
            System.out.println("-----------------------------------------------------");
            boolean found = false;
            while (rs.next()) {
                System.out.println(rs.getInt("booking_id") + "\t" + rs.getString("name") + "\t"
                        + rs.getString("phone") + "\t" + rs.getInt("room_no") + "\t"
                        + rs.getInt("nights") + "\t" + rs.getString("status"));
                found = true;
            }
            if (found == false) {
                System.out.println("No bookings yet.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // search guest by name
    public void searchGuest(String name) {
        try {
            PreparedStatement ps = con.prepareStatement("select * from bookings where name like ?");
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            while (rs.next()) {
                System.out.println("Booking " + rs.getInt("booking_id") + " | " + rs.getString("name")
                        + " | Room " + rs.getInt("room_no") + " | " + rs.getString("status"));
                found = true;
            }
            if (found == false) {
                System.out.println("No guest found with that name.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
