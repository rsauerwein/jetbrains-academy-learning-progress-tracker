package tracker;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Model which contains a list of all Courses
 * Provided sorting methods are ascending
 */
public class Courses {
    Map<String, Course> courses = new LinkedHashMap<>();

    void addNewCourse(String name, int requiredPoints) {
        courses.putIfAbsent(name, new Course(name, requiredPoints));
    }

    Course getCourseByName(String name) {
        return courses.get(name);
    }

    List<Course> getSortedByPopularity() {
        return courses.values()
                .stream()
                .filter(course -> course.getEnrolledStudentCount() > 0)
                .sorted(Comparator.comparing(Course :: getEnrolledStudentCount))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Course> getSortedByActivity() {
        return courses.values()
                .stream()
                .filter(course -> course.getEnrolledStudentCount() > 0)
                .sorted((course1, course2) -> Integer.compare(course1.completedTasks, course2.completedTasks))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    List<Course> getSortedByAvgScore() {
        return courses.values()
                .stream()
                .filter(course -> course.getEnrolledStudentCount() > 0)
                .sorted((course1, course2) -> Double.compare(course1.avgScore, course2.avgScore))
                .collect(Collectors.toCollection(LinkedList::new));
    }

}
