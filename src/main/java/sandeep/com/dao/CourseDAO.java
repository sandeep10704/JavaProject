package sandeep.com.dao;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CourseDAO {

    private String DB_URL;
    private String DB_USER;
    private String DB_PASSWORD;
    private Connection conn;

    public CourseDAO() throws SQLException {
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
                CREATE TABLE IF NOT EXISTS courses (
                    course_id INTEGER PRIMARY KEY,
                    title VARCHAR(150) NOT NULL,
                    duration VARCHAR(100) NOT NULL,
                    fee DECIMAL(10,2) CHECK(fee >= 0)
                )
                """;
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    public boolean insertCourse(Course c) throws SQLException {
        String sql = "INSERT INTO courses (course_id, title, duration, fee) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, c.getCourseId());
            pst.setString(2, c.getTitle());
            pst.setString(3, c.getDuration());
            pst.setDouble(4, c.getFee());
            return pst.executeUpdate() == 1;
        }
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_id";

        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new Course(
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getString("duration"),
                        rs.getDouble("fee")
                ));
            }
        }
        return list;
    }

    public boolean updateFee(int courseId, double newFee) throws SQLException {
        String sql = "UPDATE courses SET fee=? WHERE course_id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setDouble(1, newFee);
            pst.setInt(2, courseId);
            return pst.executeUpdate() == 1;
        }
    }

    public boolean deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, courseId);
            return pst.executeUpdate() == 1;
        }
    }

    public Course findByTitle(String title) throws SQLException {
        String sql = "SELECT * FROM courses WHERE title=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, title);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Course(
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getString("duration"),
                        rs.getDouble("fee")
                );
            }
        }
        return null;
    }

    public boolean existsId(int courseId) throws SQLException {
        String sql = "SELECT 1 FROM courses WHERE course_id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, courseId);
            return pst.executeQuery().next();
        }
    }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException ignore) {}
    }
}
