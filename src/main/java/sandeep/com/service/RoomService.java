package sandeep.com.service;

import sandeep.com.dao.Room;
import sandeep.com.dao.RoomDAO;
import java.sql.SQLException;
import java.util.List;

public class RoomService {

    private final RoomDAO dao;

    public RoomService() throws SQLException {
        dao = new RoomDAO();
    }

    public boolean addRoom(Room r) throws SQLException {
        if (r.getRoomNo() <= 0) throw new IllegalArgumentException("Room number must be positive");
        if (r.getType().isBlank()) throw new IllegalArgumentException("Room type cannot be empty");
        if (r.getPrice() < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (!isValidAvailability(r.getAvailability()))
            throw new IllegalArgumentException("Availability must be YES or NO");
        if (dao.existsId(r.getRoomNo())) throw new IllegalArgumentException("Room number already exists");

        return dao.insertRoom(r);
    }

    public List<Room> listAll() throws SQLException {
        return dao.getAllRooms();
    }

    public boolean updateAvailability(int roomNo, String availability) throws SQLException {
        if (!isValidAvailability(availability))
            throw new IllegalArgumentException("Availability must be YES or NO");
        return dao.updateAvailability(roomNo, availability);
    }

    public boolean deleteRoom(int roomNo) throws SQLException {
        return dao.deleteRoom(roomNo);
    }

    public Room findByType(String type) throws SQLException {
        return dao.findByType(type);
    }

    public void close() { dao.close(); }

    private boolean isValidAvailability(String v) {
        return v.equalsIgnoreCase("YES") || v.equalsIgnoreCase("NO");
    }
}
