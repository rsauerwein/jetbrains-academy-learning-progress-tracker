package tracker;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Student {
    int id;
    String eMail;
    String firstName;
    String lastName;

    Student(int id, String eMail, String firstName, String lastName) {
        this.id = id;
        this.eMail = eMail;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    String getFullName() {
        return firstName + " " + lastName;
    }
}
