package sandeep.com.service;

import sandeep.com.dao.Course;
import sandeep.com.dao.CourseDAO;

import java.sql.SQLException;
import java.util.List;

public class CourseService {

    private final CourseDAO dao;

    public CourseService() throws SQLException {
        dao = new CourseDAO();
    }

    public boolean addCourse(Course c) throws SQLException {
        if (c.getCourseId() <= 0) throw new IllegalArgumentException("Course ID must be positive");
        if (c.getTitle().isBlank()) throw new IllegalArgumentException("Title cannot be empty");
        if (c.getFee() < 0) throw new IllegalArgumentException("Fee cannot be negative");
        if (dao.existsId(c.getCourseId())) throw new IllegalArgumentException("ID already exists");
        return dao.insertCourse(c);
    }

    public List<Course> listAll() throws SQLException {
        return dao.getAllCourses();
    }

    public boolean updateFee(int courseId, double newFee) throws SQLException {
        if (newFee < 0) throw new IllegalArgumentException("Fee cannot be negative");
        if (!dao.existsId(courseId)) throw new IllegalArgumentException("Course ID not found");
        return dao.updateFee(courseId, newFee);
    }

    public boolean deleteCourse(int id) throws SQLException {
        if (!dao.existsId(id)) throw new IllegalArgumentException("Course ID not found");
        return dao.deleteCourse(id);
    }

    public Course findByTitle(String title) throws SQLException {
        return dao.findByTitle(title);
    }

    public void close() { dao.close(); }
}
