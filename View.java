package tracker;

import java.util.*;

public class View {
    private final Scanner scanner = new Scanner(System.in);

    String prompt() {
        return scanner.nextLine();
    }

    void printMsg(String msg) {
        System.out.println(msg);
    }

    void printWelcomeScreen() {
        System.out.println("Learning Progress Tracker");
    }

    void printStudentsList(Map<Integer, Student> students) {
        if (students.size() == 0) {
            System.out.println("No students found.");
            return;
        }

        System.out.println("Students:");
        for (Student student :
                students.values()) {
            System.out.println(student.id);
        }
    }

    void printStudentPoints(Student student, Map<String, Course> courses) {
        StringBuilder sb = new StringBuilder();
        int studentId = student.id;

        for (Course course :
                courses.values()) {
            String str = String.format("%s=%d;", course.name, course.enrolledStudents.getOrDefault(student, 0));
            sb.append(str);
        }

        sb.deleteCharAt(sb.length() - 1); //Delete ';' at last pos

        System.out.printf("%d points: %s\n", studentId, sb.toString());
    }

    void printStatisticsOverview(List<Course> sortedByPopularity, List<Course> sortedByActivity, List<Course> sortedByDifficulty) {

        Collections.reverse(sortedByPopularity);
        String mostPopular = formatPopularity(sortedByPopularity);
        Collections.reverse(sortedByPopularity);
        String leastPopular = formatPopularity(sortedByPopularity);

        Collections.reverse(sortedByActivity);
        String mostActive = formatActivity(sortedByActivity);
        Collections.reverse(sortedByActivity);
        String leastActive = formatActivity(sortedByActivity);

        Collections.reverse(sortedByDifficulty);
        String easiest = formatDifficulty(sortedByDifficulty);
        Collections.reverse(sortedByDifficulty);
        String hardest = formatDifficulty(sortedByDifficulty);

        System.out.printf("Most popular: %s\n", mostPopular);
        System.out.printf("Least popular: %s\n", leastPopular);
        System.out.printf("Highest activity: %s\n", mostActive);
        System.out.printf("Lowest activity: %s\n", leastActive);
        System.out.printf("Easiest course: %s\n", easiest);
        System.out.printf("Hardest course: %s\n", hardest);
    }

    void printTopStudents(Map<Student, Integer> topStudents, int courseMaxPoint, String courseName) {
        System.out.printf("%s\n", courseName);
        System.out.printf("id\tpoints\tcompleted\n");

        for (var student : topStudents.entrySet()) {
            double percentageCompleted = (double) student.getValue() / (double) courseMaxPoint * 100;
            System.out.printf("%d\t%d\t%.1f%%\n", student.getKey().id, student.getValue(), percentageCompleted);
        }
    }

    private String formatPopularity(List<Course> courseList) {
        if (courseList.size() == 0) {
            return "n/a";
        }

        List<String> result = new ArrayList<>();
        int prevStudentCount = courseList.get(0).getEnrolledStudentCount();
        int i = 0;
        while (courseList.size() > 0 && courseList.get(i).getEnrolledStudentCount() == prevStudentCount) {
            result.add(courseList.get(i).name);
            courseList.remove(i);
        }

        return String.join(", ", result);
    }

    private String formatActivity(List<Course> courseList) {
        if (courseList.size() == 0) {
            return "n/a";
        }

        List<String> result = new ArrayList<>();
        int prev = courseList.get(0).completedTasks;
        int i = 0;
        while (courseList.size() > 0 && courseList.get(i).completedTasks == prev) {
            result.add(courseList.get(i).name);
            courseList.remove(i);
        }

        return String.join(", ", result);
    }

    private String formatDifficulty(List<Course> courseList) {
        if (courseList.size() == 0) {
            return "n/a";
        }

        List<String> result = new ArrayList<>();
        double prev = courseList.get(0).avgScore;
        int i = 0;
        while (courseList.size() > 0 && courseList.get(i).avgScore == prev) {
            result.add(courseList.get(i).name);
            courseList.remove(i);
        }

        return String.join(", ", result);
    }
}
