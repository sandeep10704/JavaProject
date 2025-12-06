package sandeep.com.dao;

public class Room {

    private int roomNo;
    private String type;
    private double price;
    private String availability;

    public Room() {}

    public Room(int roomNo, String type, double price, String availability) {
        this.roomNo = roomNo;
        this.type = type;
        this.price = price;
        this.availability = availability;
    }

    public int getRoomNo() { return roomNo; }
    public void setRoomNo(int roomNo) { this.roomNo = roomNo; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    @Override
    public String toString() {
        return "%d | %s | %.2f | %s".formatted(roomNo, type, price, availability);
    }
}
