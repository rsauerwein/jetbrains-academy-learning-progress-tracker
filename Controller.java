package tracker;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    private View view = new View();
    private Students students = new Students();
    private Courses courses = new Courses();
    boolean isRunning = true;

    public Controller() {
        // Default course set
        courses.addNewCourse("Java", 600);
        courses.addNewCourse("DSA", 400);
        courses.addNewCourse("Databases", 480);
        courses.addNewCourse("Spring", 550);
    }

    /**
     * Controller start method
     */
    public void start() {
        view.printWelcomeScreen();
        while (isRunning) {
            this.startMainMenu();
        }
    }

    /**
     * Application Main Menu
     */
    private void startMainMenu() {
        String userInput = view.prompt();
        if (userInput.isBlank()) {
            view.printMsg("No input.");
            return;
        }

        switch (userInput) {
            case "exit":
                view.printMsg("Bye!");
                this.isRunning = false;
                break;
            case "back":
                view.printMsg("Enter 'exit' to exit the program.");
                break;
            case "find":
                startFindMenu();
                break;
            case "list":
                this.startListApp();
                break;
            case "add points":
                this.startAddPointsMenu();
                break;
            case "add students":
                this.startAddStudentsMenu();
                break;
            case "statistics":
                this.startStatisticsMenu();
                break;
            case "notify":
                startNotifyApp();
                break;
            default:
                view.printMsg("Error: unknown command!");
                break;
        }
    }

    /**
     * add students command
     */
    private void startAddStudentsMenu() {
        view.printMsg("Enter student credentials or 'back' to return:");
        String userInput = view.prompt();

        int studentsAdded = 0;
        while (!"back".equals(userInput)) {
            try {
                addStudent(userInput);
                studentsAdded++;
            } catch (IllegalArgumentException e) {
                view.printMsg(e.getMessage());
            }

            userInput = view.prompt();
        }
        String closingMsg = String.format("Total %d students have been added.", studentsAdded);
        view.printMsg(closingMsg);
    }

    /**
     * add points command
     */
    private void startAddPointsMenu() {
        view.printMsg("Enter an id and points or 'back' to return:");
        String userInput = view.prompt();

        while (!"back".equals(userInput)) {
            try {
                this.updatePoints(userInput);
            } catch (IllegalArgumentException e) {
                view.printMsg(e.getMessage());
            }

            userInput = view.prompt();
        }
    }

    /**
     * find command
     */
    private void startFindMenu() {
        view.printMsg("Enter an id or 'back' to return:");

        String userInput = view.prompt();

        while (!"back".equals(userInput)) {
            int userId = 0;
            Student student = null;

            try {
                userId = Integer.parseInt(userInput);
                student = students.getStudentById(userId);
            } catch (NumberFormatException e) {
                view.printMsg(e.getMessage());
            }

            if (student == null) {
                String msg = String.format("No student is found for id=%d", userId);
                view.printMsg(msg);
            } else {
                view.printStudentPoints(student, courses.courses);
            }

            userInput = view.prompt();
        }
    }

    /**
     * statistics command
     */
    private void startStatisticsMenu() {
        List<Course> sortedByPopularity = courses.getSortedByPopularity();
        List<Course> sortedByActivity = courses.getSortedByActivity();
        List<Course> sortedByAvgScore = courses.getSortedByAvgScore();

        view.printMsg("Type the name of a course to see details or 'back' to quit:");
        view.printStatisticsOverview(sortedByPopularity, sortedByActivity, sortedByAvgScore);

        String userInput = view.prompt();
        while (!"back".equals(userInput)) {
            char[] c = userInput.toCharArray();
            c[0] = Character.toUpperCase(c[0]);
            Course course = courses.getCourseByName(new String(c));
            if (course == null) {
                view.printMsg("Unknown course.");
            } else {
                Map<Student, Integer> topStudents = course.getTopStudents();
                view.printTopStudents(topStudents, course.requiredPoints, course.name);
            }
            userInput = view.prompt();
        }
    }

    /**
     * list command
     */
    private void startListApp() {
        view.printStudentsList(students.students);
    }

    private void startNotifyApp() {
        Set<Student> notifiedStudents = new HashSet<>();

        for (Course course :
                courses.courses.values()) {
            while (!course.notificationQueue.isEmpty()) {
                Student graduate = course.notificationQueue.poll();
                String msg = String.format("To: %s\nRe: Your Learning Progress\nHello, %s! You have accomplished our %s course!", graduate.eMail, graduate.getFullName(), course.name);
                view.printMsg(msg);
                notifiedStudents.add(graduate);
                course.graduates.add(graduate);
            }
        }

        String result = String.format("Total %d students have been notified", notifiedStudents.size());
        view.printMsg(result);
    }

    /**
     * [firstName] [lastName] [email]
     * <p>
     * Correct input examples:
     * John Doe jdoe@mail.net
     * Jean-Clause van Helsing jc@google.it
     * Mary Luise Johnson maryj@google.com
     * <p>
     * <p>
     * the first part of the full name before the first blank space is the first name, and the rest of the full name
     * should be treated as the last name.
     *
     * @param userInput
     */
    private void addStudent(String userInput) {
        String[] args = userInput.split(" ");

        // args has to contain at least 3 elements
        // for example [max,mustermann,foo@bar.ch]
        if (args.length < 3) {
            throw new IllegalArgumentException("Incorrect credentials.");
        }

        // Check if the E-Mail is valid - The Email-address is always located in args[len -1]
        String eMail = args[args.length - 1];

        if (!isValidEMail(eMail)) {
            throw new IllegalArgumentException("Incorrect email.");
        }

        // Check if the firstname is valid - args[0]
        String firstName = args[0];

        if (!isValidName(firstName)) {
            throw new IllegalArgumentException("Incorrect first name.");
        }

        // Check if the lastnames are valid - args[from 1 to len -2]
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= args.length - 2; i++) {
            String currentLastName = args[i];
            if (!isValidName(currentLastName)) {
                throw new IllegalArgumentException("Incorrect last name.");
            }

            sb.append(currentLastName + " ");
        }

        String lastName = new String(sb);

        // Create Student object
        students.createStudent(eMail, firstName, lastName);

        view.printMsg("The student has been added");
    }

    /**
     * @param userInput [studentID] [javaPoints] [dsaPoints] [databasePoints] [springPoints]
     */
    private void updatePoints(String userInput) {

        String[] args = userInput.split("\\s+");

        // Unfortunately an ugly workaround for testcase #23
        // As it turned out I have to print out 'No student found' for inputs like imsurethereisnosuchstudentid
        int userId = 0;
        try {
            userId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            String msg = String.format("No student is found for id=%s", args[0]);
            throw new IllegalArgumentException(msg);
        }


        // Verify that userInput contains 5 numbers separated by whitespace
        if (!isValidPointsString(userInput)) {
            throw new IllegalArgumentException("Incorrect points format.");
        }

        Student student = this.students.getStudentById(userId);
        if (student == null) {
            throw new IllegalArgumentException("No student is found for id=" + userId);
        }

        courses.getCourseByName("Java").updatePoints(student, Integer.parseInt(args[1]));
        courses.getCourseByName("DSA").updatePoints(student, Integer.parseInt(args[2]));
        courses.getCourseByName("Databases").updatePoints(student, Integer.parseInt(args[3]));
        courses.getCourseByName("Spring").updatePoints(student, Integer.parseInt(args[4]));

        view.printMsg("Points updated.");
    }

    /**
     * @param userInput 5 numbers separated by whitespace.
     * @return
     */
    boolean isValidPointsString(String userInput) {
        final String regex = "\\d+\\s\\d+\\s\\d+\\s\\d+\\s\\d+";

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(userInput);

        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Verifies whether the input meets the following criteria:
     * The name String must start and end with A-Za
     * Min length: 2
     * Allowed special characters: \' and \-
     * Special character aren't allowed as first or last character of any part of the name.
     * Also special character cannot be adjacent to each other
     *
     * @param userInput String with a (sur)name
     * @return input is valid
     */
    boolean isValidName(String userInput) {
        // This pattern assures, that the String starts and ends with letters and is at least two characters long
        Pattern namePattern = Pattern.compile("^\\w[A-Za-z\\'\\-]*\\w$");
        Matcher matcher = namePattern.matcher(userInput);

        // This pattern verifies that special characters aren't adjacent to each other
        // Pattern illegalSpecialChar = Pattern.compile("(?<=\\-)\\'|(?<=\\')\\-");
        Pattern illegalSpecialChar = Pattern.compile("\\-(?![A-Za-z])|\\'(?![A-Za-z])");
        Matcher illegalSpecialCharMatcher = illegalSpecialChar.matcher(userInput);

        // Todo lesson learned: Note the difference between matches() and find() - knowing this beforehand, would have saved me a lot of headache
        if (!matcher.matches() || illegalSpecialCharMatcher.find()) {
            return false;
        }

        return true;
    }

    /**
     * E-Mail format: It should contain the name part, the @ symbol, and the domain part.
     *
     * @param eMail
     * @return E-Mail is valid
     */
    boolean isValidEMail(String eMail) {
        Pattern emailPattern = Pattern.compile("^([\\w\\.]+)@(\\w+)\\.(\\w+)$");
        Matcher matcher = emailPattern.matcher(eMail);

        if (!matcher.matches()) {
            return false;
        }

        return true;
    }
}
