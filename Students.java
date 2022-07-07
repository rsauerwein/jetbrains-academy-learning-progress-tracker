package tracker;

import java.util.*;

public class Students {
    // DB which contains all Student objects Map<Integer(id), Student>
    // Only 1 account allowed per eMail address
    // I use a LinkedHashMap because commands like "list" require the App to remember the creation order
    Map<Integer, Student> students = new LinkedHashMap<>();

    // Every eMail address has to be unique
    Set<String> eMailAddresses = new HashSet<>();

    private int nextStudentId = 10000;

    /**
     * Creates a new Student object when the eMail isn't already in use
     * @param eMail
     * @param firstName
     * @param lastName
     */
    void createStudent(String eMail, String firstName, String lastName) {
        if (studentAlreadyExists(eMail)) {
            throw new IllegalArgumentException("This email is already taken.");
        }

        Student student = new Student(this.nextStudentId, eMail, firstName, lastName);
        this.students.put(nextStudentId, student);
        this.eMailAddresses.add(eMail);
        this.nextStudentId++;
    }

    public Student getStudentById(int id) {
        return students.get(id);
    }

    boolean studentAlreadyExists(String eMail) {
        return this.eMailAddresses.contains(eMail);
    }
}
