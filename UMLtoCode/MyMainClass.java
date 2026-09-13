import java.time.LocalDate;
import java.util.*;

public class MyMainClass {

    public static void main(String[] args) {

        // ---------------- COURSE SETUP ----------------

        Lesson lesson1 = new Lesson(1, "Lesson1", "Lesson1Url.com", 140);
        Lesson lesson2 = new Lesson(2, "Lesson2", "Lesson2Url.com", 56);

        List<Lesson> lessons = new ArrayList<>();
        lessons.add(lesson1);
        lessons.add(lesson2);

        Module module1 = new Module(1, "Module1", lessons);

        List<Module> modules = new ArrayList<>();
        modules.add(module1);

        Course course = new Course(
                1,
                "Course 1",
                "This course is about AI",
                modules
        );

        // ---------------- INSTRUCTOR ----------------

        List<Course> instructorCourses = new ArrayList<>();
        instructorCourses.add(course);

        Instructor instructor = new Instructor(
                1,
                "Instructor1",
                "instructor1@gmail.com",
                instructorCourses
        );

        // ---------------- STUDENT ----------------

        Student student = new Student(
                2,
                "Student2",
                "student2@gmail.com"
        );

        // ---------------- REPOSITORIES ----------------

        ICourseRepository courseRepository = new CourseRepository();
        IEnrollmentRepository enrollmentRepository =
                new EnrollmentRepository();

        courseRepository.saveCourse(course);

        // ---------------- NOTIFICATION ----------------

        INotificationService notificationService =
                new EmailNotificationService();

        // ---------------- SERVICE ----------------

        EnrollmentService enrollmentService =
                new EnrollmentService(
                        notificationService,
                        enrollmentRepository
                );

        // ---------------- ENROLLMENT ----------------

        System.out.println("===== ENROLLMENT =====");

        Enrollment enrollment =
                enrollmentService.EnrollStudent(student, course);

        System.out.println(
                "Enrollment ID: " + enrollment.getId()
        );

        System.out.println(
                "Status: " + enrollment.enrollmentStatus
        );

        System.out.println(
                "Progress: " + enrollment.getProgressPercent() + "%"
        );

        // ---------------- LESSON COMPLETION ----------------

        System.out.println("\n===== LESSON COMPLETION =====");

        enrollmentService.completeLesson(enrollment, lesson1);

        System.out.println(
                "After Lesson 1: "
                        + enrollment.getProgressPercent() + "%"
        );

        enrollmentService.completeLesson(enrollment, lesson2);

        System.out.println(
                "After Lesson 2: "
                        + enrollment.getProgressPercent() + "%"
        );

        // ---------------- FINAL RESULT ----------------

        System.out.println("\n===== FINAL RESULT =====");

        System.out.println(
                "Enrollment Status: "
                        + enrollment.enrollmentStatus
        );

        System.out.println(
                "Final Progress: "
                        + enrollment.getProgressPercent()
                        + "%"
        );
    }
}


// =====================================================
// NOTIFICATION
// =====================================================

interface INotificationService {

    void sendMessage(User user, String message);
}


class EmailNotificationService implements INotificationService {

    @Override
    public void sendMessage(User user, String message) {

        System.out.println(
                "Email to " + user.getEmail() + ": " + message
        );
    }
}


class SMSNotificationService implements INotificationService {

    @Override
    public void sendMessage(User user, String message) {

        System.out.println(
                "SMS to " + user.getName() + ": " + message
        );
    }
}


// =====================================================
// USER
// =====================================================

abstract class User {

    public int id;
    public String name;
    public String email;

    User(int id, String name, String email) {

        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}


// =====================================================
// ENROLLMENT SERVICE
// =====================================================

class EnrollmentService {

    private INotificationService notificationService;
    private IEnrollmentRepository enrollmentRepository;

    EnrollmentService(
            INotificationService notificationService,
            IEnrollmentRepository enrollmentRepository) {

        this.notificationService = notificationService;
        this.enrollmentRepository = enrollmentRepository;
    }

    public Enrollment EnrollStudent(
            Student student,
            Course course) {

        Enrollment enrollment =
                new Enrollment(student, course);

        student.addEnrollment(enrollment);

        enrollmentRepository.saveEnrollment(enrollment);

        notificationService.sendMessage(
                student,
                "You have been enrolled in "
                        + course.getTitle()
        );

        return enrollment;
    }

    public void completeLesson(
            Enrollment enrollment,
            Lesson lesson) {

        enrollment.markLessonCompleted(
                lesson,
                notificationService
        );
    }
}


// =====================================================
// ENROLLMENT REPOSITORY
// =====================================================

interface IEnrollmentRepository {

    Enrollment findByID(int id);

    Enrollment findByUserAndCourse(
            User user,
            Course course
    );

    void saveEnrollment(Enrollment enrollment);
}


class EnrollmentRepository implements IEnrollmentRepository {

    private List<Enrollment> enrollmentList =
            new ArrayList<>();

    @Override
    public Enrollment findByID(int id) {

        for (Enrollment enrollment : enrollmentList) {

            if (enrollment.getId() == id) {
                return enrollment;
            }
        }

        return null;
    }

    @Override
    public Enrollment findByUserAndCourse(
            User user,
            Course course) {

        for (Enrollment enrollment : enrollmentList) {

            if (enrollment.getStudentID() == user.getId()
                    && enrollment.getCourseID() == course.getId()) {

                return enrollment;
            }
        }

        return null;
    }

    @Override
    public void saveEnrollment(Enrollment enrollment) {

        enrollmentList.add(enrollment);
    }
}


// =====================================================
// COURSE REPOSITORY
// =====================================================

interface ICourseRepository {

    Course findByID(int id);

    List<Course> findByInstructor(Instructor instructor);

    void saveCourse(Course course);
}


class CourseRepository implements ICourseRepository {

    private List<Course> courses =
            new ArrayList<>();

    @Override
    public Course findByID(int id) {

        for (Course course : courses) {

            if (course.getId() == id) {
                return course;
            }
        }

        return null;
    }

    @Override
    public List<Course> findByInstructor(
            Instructor instructor) {

        return instructor.getCourseList();
    }

    @Override
    public void saveCourse(Course course) {

        courses.add(course);
    }
}


// =====================================================
// ENROLLMENT
// =====================================================

enum EnrollmentStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED
}


class Enrollment {

    public int id;
    public int studentID;
    public int courseID;

    public EnrollmentStatus enrollmentStatus;
    public LocalDate enrollmentDate;

    public Student student;

    public Map<Lesson, Boolean> lessonCompleted;

    private static int idpnt = 0;

    Enrollment(Student user, Course course) {

        this.id = idpnt++;
        this.student = user;

        this.studentID = user.getId();
        this.courseID = course.getId();

        this.enrollmentStatus =
                EnrollmentStatus.ACTIVE;

        this.enrollmentDate =
                LocalDate.now();

        lessonCompleted =
                new HashMap<>();

        for (Module module : course.getModuleList()) {

            for (Lesson lesson : module.getLessonList()) {

                lessonCompleted.put(lesson, false);
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getCourseID() {
        return courseID;
    }

    public int getStudentID() {
        return studentID;
    }

    public void markLessonCompleted(
            Lesson lesson,
            INotificationService notificationService) {

        if (!lessonCompleted.containsKey(lesson)) {

            System.out.println(
                    "Lesson is not part of this enrollment."
            );

            return;
        }

        lessonCompleted.put(lesson, true);

        if (getProgressPercent() == 100.0) {

            enrollmentStatus =
                    EnrollmentStatus.COMPLETED;

            notificationService.sendMessage(
                    student,
                    "Enrollment with ID "
                            + id
                            + " completed."
            );
        }
    }

    public double getProgressPercent() {

        int totalLessons =
                lessonCompleted.size();

        if (totalLessons == 0) {
            return 0.0;
        }

        int completedLessons = 0;

        for (Boolean completed :
                lessonCompleted.values()) {

            if (completed) {
                completedLessons++;
            }
        }

        return (100.0 * completedLessons)
                / totalLessons;
    }
}


// =====================================================
// STUDENT
// =====================================================

class Student extends User {

    private List<Enrollment> enrollmentList =
            new ArrayList<>();

    Student(int id, String name, String email) {

        super(id, name, email);
    }

    public List<Enrollment> getEnrollmentList() {
        return enrollmentList;
    }

    public void addEnrollment(
            Enrollment enrollment) {

        enrollmentList.add(enrollment);
    }
}


// =====================================================
// INSTRUCTOR
// =====================================================

class Instructor extends User {

    private List<Course> courseList;

    Instructor(
            int id,
            String name,
            String email,
            List<Course> courseList) {

        super(id, name, email);

        this.courseList = courseList;
    }

    public List<Course> getCourseList() {
        return courseList;
    }
}


// =====================================================
// COURSE
// =====================================================

class Course {

    public int id;
    public String title;
    public String description;

    public List<Module> moduleList;

    Course(
            int id,
            String title,
            String description,
            List<Module> moduleList) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.moduleList = moduleList;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Module> getModuleList() {
        return moduleList;
    }

    public void addModule(Module module) {
        moduleList.add(module);
    }
}


// =====================================================
// MODULE
// =====================================================

class Module {

    public int id;
    public String name;

    private List<Lesson> lessonList;

    Module(
            int id,
            String name,
            List<Lesson> lessonList) {

        this.id = id;
        this.name = name;
        this.lessonList = lessonList;
    }

    public List<Lesson> getLessonList() {
        return lessonList;
    }

    public void addLesson(Lesson lesson) {
        lessonList.add(lesson);
    }
}


// =====================================================
// LESSON
// =====================================================

class Lesson {

    public int id;
    public int contentDuration;

    public String name;
    public String contentUrl;

    Lesson(
            int id,
            String name,
            String contentUrl,
            int contentDuration) {

        this.id = id;
        this.name = name;
        this.contentUrl = contentUrl;
        this.contentDuration = contentDuration;
    }
}