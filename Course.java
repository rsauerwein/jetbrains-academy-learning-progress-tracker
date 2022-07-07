package tracker;

import java.util.*;
import java.util.stream.Collectors;

public class Course {
    String name;
    int requiredPoints; // Points needed for finishing this course
    Map<Student, Integer> enrolledStudents = new LinkedHashMap<>(); // Student, current points
    Deque<Student> notificationQueue = new ArrayDeque<>();
    Set<Student> graduates = new HashSet<>();
    double avgScore = 0;
    int totalScore = 0;
    int completedTasks = 0;


    Course(String name, int requiredPoints) {
        this.name = name;
        this.requiredPoints = requiredPoints;
    }

    /**
     * Updates the course points for a specific Student
     * @param student
     * @param points
     */
    void updatePoints(Student student, int points) {
        if (points < 1) {
            return;
        }

        this.enrolledStudents.put(student, enrolledStudents.getOrDefault(student, 0) + points);
        this.completedTasks++;
        this.totalScore += points;
        this.avgScore = totalScore / completedTasks;

        if(this.enrolledStudents.get(student) >= this.requiredPoints && !graduates.contains(student)) {
            notificationQueue.add(student);
        }
    }

    int getEnrolledStudentCount() {
        return enrolledStudents.size();
    }

    /**
     *
     * @return Map<Student, currentScore> - sorted descending
     */
    Map<Student, Integer> getTopStudents() {
        return enrolledStudents.entrySet()
                .stream()
                .sorted(Map.Entry.<Student, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry :: getKey,
                        Map.Entry :: getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}
