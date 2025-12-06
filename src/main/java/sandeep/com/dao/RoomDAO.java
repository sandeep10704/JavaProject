package sandeep.com.dao;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RoomDAO {

    private String DB_URL;
    private String DB_USER;
    private String DB_PASSWORD;
    private Connection conn;

    public RoomDAO() throws SQLException {
        loadConfig();
        connect();
        createTableIfNotExists();
    }

    private void loadConfig() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (in == null) throw new RuntimeException("application.properties not found");

            Properties props = new Properties();
            props.load(in);

            DB_URL = props.getProperty("db.url");
            DB_USER = props.getProperty("db.user");
            DB_PASSWORD = props.getProperty("db.password");

        } catch (Exception e) {
            throw new RuntimeException("Cannot load DB config: " + e.getMessage());
        }
    }

    private void connect() throws SQLException {
        if ((DB_USER == null || DB_USER.isBlank()) && (DB_PASSWORD == null || DB_PASSWORD.isBlank())) {
            conn = DriverManager.getConnection(DB_URL);
        } else {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        conn.setAutoCommit(true);
        System.out.println("Connected to database.");
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS rooms (
                    room_no INTEGER PRIMARY KEY,
                    type VARCHAR(100) NOT NULL,
                    price DECIMAL(10,2) CHECK(price >= 0),
                    availability VARCHAR(20) NOT NULL CHECK(availability IN ('YES', 'NO'))
                                                                                                    
                )
                """;
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    public boolean insertRoom(Room r) throws SQLException {
        String sql = "INSERT INTO rooms(room_no, type, price, availability) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, r.getRoomNo());
            pst.setString(2, r.getType());
            pst.setDouble(3, r.getPrice());
            pst.setString(4, r.getAvailability());
            return pst.executeUpdate() == 1;
        }
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_no";

        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new Room(
                        rs.getInt("room_no"),
                        rs.getString("type"),
                        rs.getDouble("price"),
                        rs.getString("availability")
                ));
            }
        }
        return list;
    }

    public boolean updateAvailability(int roomNo, String availability) throws SQLException {
        String sql = "UPDATE rooms SET availability=? WHERE room_no=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, availability);
            pst.setInt(2, roomNo);
            return pst.executeUpdate() == 1;
        }
    }

    public boolean deleteRoom(int roomNo) throws SQLException {
        String sql = "DELETE FROM rooms WHERE room_no=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, roomNo);
            return pst.executeUpdate() == 1;
        }
    }

    public Room findByType(String type) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE type=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, type);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Room(
                        rs.getInt("room_no"),
                        rs.getString("type"),
                        rs.getDouble("price"),
                        rs.getString("availability")
                );
            }
        }
        return null;
    }

    public boolean existsId(int roomNo) throws SQLException {
        String sql = "SELECT 1 FROM rooms WHERE room_no=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, roomNo);
            return pst.executeQuery().next();
        }
    }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException ignore) {}
    }
}
