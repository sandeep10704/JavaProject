package sandeep.com;

import sandeep.com.dao.Room;
import sandeep.com.service.RoomService;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    private final RoomService service;
    private final Scanner sc = new Scanner(System.in);

    public Main() throws SQLException {
        service = new RoomService();
    }

    public static void main(String[] args) {
        try {
            Main m = new Main();
            m.run();
        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
        }
    }

    private void run() throws SQLException {
        while (true) {
            printMenu();
            int ch = readInt("Enter choice: ");
            switch (ch) {
                case 1 -> addRoom();
                case 2 -> viewRooms();
                case 3 -> updateAvailability();
                case 4 -> deleteRoom();
                case 5 -> searchByType();
                case 6 -> exit();
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Hostel Room Management System ===");
        System.out.println("1. Add Room");
        System.out.println("2. View All Rooms");
        System.out.println("3. Update Availability");
        System.out.println("4. Delete Room");
        System.out.println("5. Search Room by Type");
        System.out.println("6. Exit");
    }

    private void addRoom() throws SQLException {
        int roomNo = readInt("Room No: ");
        String type = readString("Room Type (AC/NON-AC): ");
        double price = readDouble("Price: ");
        String availability = readString("Availability (YES/NO): ");



        boolean ok = service.addRoom(new Room(roomNo, type, price, availability));
        System.out.println(ok ? "Room added." : "Failed to add room.");
    }

    private void viewRooms() throws SQLException {
        var list = service.listAll();
        if (list.isEmpty()) System.out.println("No rooms found.");
        else list.forEach(System.out::println);
    }

    private void updateAvailability() throws SQLException {
        int roomNo = readInt("Room No: ");
        String availability = readString("New Availability (YES/NO): ");
        boolean ok = service.updateAvailability(roomNo, availability);
        System.out.println(ok ? "Availability updated." : "Update failed.");
    }

    private void deleteRoom() throws SQLException {
        int roomNo = readInt("Room No: ");
        System.out.println(service.deleteRoom(roomNo) ? "Deleted." : "Failed.");
    }

    private void searchByType() throws SQLException {
        String type = readString("Enter room type: ");
        var r = service.findByType(type);
        System.out.println(r == null ? "Not found." : r);
    }

    private void exit() {
        System.out.println("Closing app...");
        service.close();
        sc.close();
        System.exit(0);
    }

    private int readInt(String msg) {
        while (true) {
            System.out.print(msg);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (Exception e) { System.out.println("Invalid number."); }
        }
    }

    private double readDouble(String msg) {
        while (true) {
            System.out.print(msg);
            try { return Double.parseDouble(sc.nextLine().trim()); }
            catch (Exception e) { System.out.println("Invalid number."); }
        }
    }

    private String readString(String msg) {
        while (true) {
            System.out.print(msg);
            var s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Input cannot be empty.");
        }
    }
}
