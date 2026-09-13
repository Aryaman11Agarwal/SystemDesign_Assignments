
import java.awt.event.MouseAdapter;
import java.time.LocalDate;
import java.util.*;

public class MyMainClass {

    public static void main(String[] args) {

        System.out.println("===== APPLICATION STARTED =====");

        System.out.println("\nCreating lessons...");

        Lesson lesson1 = new Lesson(1, "Lesson1", "Lesson1Url.com", 140);
        System.out.println("Lesson created: " + lesson1.name);

        Lesson lesson2 = new Lesson(2, "Lesson2", "Lesson2Url.com", 56);
        System.out.println("Lesson created: " + lesson2.name);

        List<Lesson> lessonList = new ArrayList<>();

        System.out.println("\nAdding lessons to lesson list...");
        lessonList.add(lesson1);
        System.out.println("Added Lesson1");

        lessonList.add(lesson2);
        System.out.println("Added Lesson2");

        System.out.println("Total lessons: " + lessonList.size());

        System.out.println("\nCreating module...");

        Module module1 = new Module(1, "Module1", lessonList);

        System.out.println("Module created: " + module1.name);
        System.out.println("Module contains " + module1.getLessonList().size() + " lessons");

        List<Module> moduleList = new ArrayList<>();

        System.out.println("\nAdding module to module list...");
        moduleList.add(module1);

        System.out.println("Total modules: " + moduleList.size());

        System.out.println("\nCreating course...");

        Course course1 = new Course(
                1,
                "Course 1",
                "THis course is about AI",
                moduleList
        );

        System.out.println("Course created: " + course1.getTitle());
        System.out.println("Course description: " + course1.getDescription());
        System.out.println("Course contains " + course1.getModuleList().size() + " modules");

        System.out.println("\nCreating course repository...");

        ICourseRepository courseRepository = new CourseRepository();

        System.out.println("Saving course to repository...");
        courseRepository.saveCourse(course1);

        System.out.println("Course saved successfully");

        List<Course> courseList = new ArrayList<>();

        System.out.println("\nAdding course to instructor course list...");
        courseList.add(course1);

        System.out.println("Instructor course list size: " + courseList.size());

        System.out.println("\nCreating instructor...");

        Instructor instructor = new Instructor(
                1,
                "Instructor1",
                "instructor1@gmail.com",
                courseList
        );

        System.out.println("Instructor created: " + instructor.getName());
        System.out.println("Instructor email: " + instructor.getEmail());
        System.out.println("Instructor courses: " + instructor.getCourseList().size());

        System.out.println("\nCreating student...");

        Student student = new Student(
                2,
                "Student2",
                "student2@gmail.com"
        );

        System.out.println("Student created: " + student.getName());
        System.out.println("Student email: " + student.getEmail());

        System.out.println("\nCreating enrollment service...");

        INotificationService notificationService =
                new EmailNotificationService();
        System.out.println("Creating enrollment repository...");
        IEnrollmentRepository enrollmentRepository = new EnrollmentRepository();
        EnrollmentService enrollmentService = new EnrollmentService(notificationService,enrollmentRepository);



        System.out.println("\n===== ENROLLMENT PROCESS =====");

        System.out.println("Enrolling student " + student.getName()
                + " into course " + course1.getTitle());

        Enrollment enrollment = enrollmentService.EnrollStudent(
                student,
                course1
        );

        System.out.println("Enrollment created with ID: " + enrollment.getId());
        System.out.println("Student ID: " + enrollment.getStudentID());
        System.out.println("Course ID: " + enrollment.getCourseID());
        System.out.println("Enrollment date: " + enrollment.enrollmentDate);

        System.out.println("\n===== LESSON COMPLETION =====");

        System.out.println("Attempting to complete: " + lesson1.name);
        enrollmentService.completeLesson(enrollment, lesson1);

        System.out.println("Current progress: "
                + enrollment.getProgressPercent() + "%");

        System.out.println("\nAttempting to complete: " + lesson2.name);
        enrollmentService.completeLesson(enrollment, lesson2);

        System.out.println("Current progress: "
                + enrollment.getProgressPercent() + "%");

        System.out.println("\n===== NOTIFICATION =====");

        System.out.println("Creating email notification service...");



        System.out.println("Notification service created");

        String message = "You have complete "
                + enrollment.getProgressPercent()
                + " % of your enrollment";

        System.out.println("Preparing notification...");
        System.out.println("Message: " + message);

        notificationService.sendMessage(
                student,
                message
        );

        System.out.println("\n===== APPLICATION COMPLETED =====");
    }
}

interface INotificationService {

    void sendMessage(User user, String message);
}

class EmailNotificationService implements INotificationService {

    public void sendMessage(User user, String message) {

        System.out.println("[EmailNotificationService] sendMessage() called");
        System.out.println("[EmailNotificationService] User: " + user.getName());
        System.out.println("[EmailNotificationService] Email: " + user.getEmail());
        System.out.println("[EmailNotificationService] Message: " + message);

        System.out.println(
                " Sending message: " + message
                        + " to emailID  " + user.getEmail()
        );

        System.out.println("[EmailNotificationService] Message sent successfully");
    }
}

class SMSNotificationService implements INotificationService {

    public void sendMessage(User user, String message) {

        System.out.println("[SMSNotificationService] sendMessage() called");
        System.out.println("[SMSNotificationService] User: " + user.getName());
        System.out.println("[SMSNotificationService] Message: " + message);

        System.out.println(
                " Sending message: " + message
                        + " to the username  " + user.getName()
        );

        System.out.println("[SMSNotificationService] Message sent successfully");
    }
}

abstract class User {

    public int id;
    public String name;
    public String email;

    User() {

        System.out.println("[User] Default constructor called");
    }

    User(int id, String name, String email) {

        System.out.println("[User] Parameterized constructor called");

        this.id = id;
        this.name = name;
        this.email = email;

        System.out.println("[User] User initialized: " + name);
    }

    public int getId() {

        System.out.println("[User] getId() called for " + name);

        return id;
    }

    public String getEmail() {

        System.out.println("[User] getEmail() called for " + name);

        return email;
    }

    public String getName() {

        System.out.println("[User] getName() called");

        return name;
    }
}

class EnrollmentService {


    public INotificationService notificationService;
    public IEnrollmentRepository enrollmentRepository;

    EnrollmentService (INotificationService notificationService,IEnrollmentRepository enrollmentRepository){
        this.notificationService=notificationService;
        this.enrollmentRepository=enrollmentRepository;
    }

    public Enrollment EnrollStudent(
            Student student,
            Course course) {

        System.out.println("[EnrollmentService] EnrollStudent() called");

        System.out.println(
                "[EnrollmentService] Student: "
                        + student.getName()
        );

        System.out.println(
                "[EnrollmentService] Course: "
                        + course.getTitle()
        );

        System.out.println("[EnrollmentService] Creating enrollment...");

        Enrollment enrollment = new Enrollment(student, course);

        System.out.println(
                "[EnrollmentService] Enrollment created with ID: "
                        + enrollment.getId()
        );

        System.out.println(
                "[EnrollmentService] Adding enrollment to student..."
        );

        student.addEnrollment(enrollment);

        System.out.println(
                "[EnrollmentService] Saving enrollment to repository..."
        );

        enrollmentRepository.saveEnrollment(enrollment);

        System.out.println(
                "[EnrollmentService] Enrollment process completed"
        );

        notificationService.sendMessage(student,"You have been enrolled for the course"+ course.getId());

        return enrollment;
    }

    public void completeLesson(
            Enrollment enrollment,
            Lesson lesson) {

        System.out.println("[EnrollmentService] completeLesson() called");
        System.out.println(
                "[EnrollmentService] Lesson: " + lesson.name
        );

        enrollment.markLessonCompleted(lesson,notificationService);

        System.out.println(
                "[EnrollmentService] Lesson completion operation finished"
        );
    }
}

interface IEnrollmentRepository {

    public Enrollment findByID(int id);

    public Enrollment findByUserAndCourse(
            User user,
            Course course
    );

    public void saveEnrollment(Enrollment enrollment);
}

class EnrollmentRepository implements IEnrollmentRepository {

    List<Enrollment> enrollmentList;

    EnrollmentRepository() {

        System.out.println(
                "[EnrollmentRepository] Constructor called"
        );

        enrollmentList = new ArrayList<>();

        System.out.println(
                "[EnrollmentRepository] Repository initialized"
        );
    }

    public Enrollment findByID(int id) {

        System.out.println(
                "[EnrollmentRepository] findByID() called with ID: " + id
        );

        for (Enrollment e : enrollmentList) {

            System.out.println(
                    "[EnrollmentRepository] Checking enrollment ID: "
                            + e.getId()
            );

            if (e.getId() == id) {

                System.out.println(
                        "[EnrollmentRepository] Enrollment found"
                );

                return e;
            }
        }

        System.out.println(
                "[EnrollmentRepository] Enrollment not found"
        );

        return null;
    }

    public Enrollment findByUserAndCourse(
            User user,
            Course course) {

        System.out.println(
                "[EnrollmentRepository] findByUserAndCourse() called"
        );

        for (Enrollment e : enrollmentList) {

            System.out.println(
                    "[EnrollmentRepository] Checking enrollment ID: "
                            + e.getId()
            );

            if (e.getStudentID() == user.getId()
                    && e.getCourseID() == course.getId()) {

                System.out.println(
                        "[EnrollmentRepository] Matching enrollment found"
                );

                return e;
            }
        }

        System.out.println(
                "[EnrollmentRepository] Matching enrollment not found"
        );

        return null;
    }

    public void saveEnrollment(Enrollment e) {

        System.out.println(
                "[EnrollmentRepository] saveEnrollment() called"
        );

        System.out.println(
                "[EnrollmentRepository] Enrollment ID: "
                        + e.getId()
        );

        enrollmentList.add(e);

        System.out.println(
                "[EnrollmentRepository] Enrollment list size: "
                        + enrollmentList.size()
        );

        System.out.println(
                "Enrollment with id: "
                        + e.getId()
                        + " saved successfully"
        );
    }
}

interface ICourseRepository {

    public Course findByID(int id);

    public List<Course> findByInstructor(Instructor instructor);

    public void saveCourse(Course course);
}

class CourseRepository implements ICourseRepository {

    List<Course> courses;

    CourseRepository() {

        System.out.println("[CourseRepository] Constructor called");

        courses = new ArrayList<>();

        System.out.println("[CourseRepository] Repository initialized");
    }

    @Override
    public Course findByID(int id) {

        System.out.println(
                "[CourseRepository] findByID() called with ID: " + id
        );

        for (Course course : courses) {

            System.out.println(
                    "[CourseRepository] Checking course: "
                            + course.getTitle()
            );

            if (course.getId() == id) {

                System.out.println(
                        "[CourseRepository] Course found"
                );

                return course;
            }
        }

        System.out.println(
                "[CourseRepository] Course not found"
        );

        return null;
    }

    @Override
    public List<Course> findByInstructor(
            Instructor instructor) {

        System.out.println(
                "[CourseRepository] findByInstructor() called"
        );

        System.out.println(
                "[CourseRepository] Instructor: "
                        + instructor.getName()
        );

        List<Course> result = instructor.getCourseList();

        System.out.println(
                "[CourseRepository] Courses found: "
                        + result.size()
        );

        return result;
    }

    @Override
    public void saveCourse(Course course) {

        System.out.println(
                "[CourseRepository] saveCourse() called"
        );

        System.out.println(
                "[CourseRepository] Saving course: "
                        + course.getTitle()
        );

        courses.add(course);

        System.out.println(
                "[CourseRepository] Total courses: "
                        + courses.size()
        );
    }
}

enum EnrollmentStatus{
    ACTIVE, COMPLETED, CANCELLED;
}

class Enrollment {

    public int id;
    public int studentID, courseID;

    public EnrollmentStatus enrollmentStatus;

    public LocalDate enrollmentDate;
    public Student student;


    public Map<Lesson, Boolean> lessonCompleted;

    static int idpnt = 0;

    public int getId() {

        System.out.println(
                "[Enrollment] getId() called"
        );

        return id;
    }

    Enrollment(Student user, Course course) {
        this.student=user;

        System.out.println(
                "[Enrollment] Constructor called"
        );

        this.id = Enrollment.idpnt++;
        enrollmentStatus=EnrollmentStatus.ACTIVE;

        System.out.println(
                "[Enrollment] Generated enrollment ID: " + this.id
        );

        this.studentID = user.getId();
        this.courseID = course.getId();

        System.out.println(
                "[Enrollment] Student ID: " + this.studentID
        );

        System.out.println(
                "[Enrollment] Course ID: " + this.courseID
        );

        this.enrollmentDate = LocalDate.now();

        System.out.println(
                "[Enrollment] Enrollment date: "
                        + this.enrollmentDate
        );

        lessonCompleted = new HashMap<>();

        System.out.println(
                "[Enrollment] Initializing lesson completion map..."
        );

        List<Module> moduleList = course.getModuleList();

        System.out.println(
                "[Enrollment] Number of modules: "
                        + moduleList.size()
        );

        for (Module module : moduleList) {

            System.out.println(
                    "[Enrollment] Processing module: "
                            + module.name
            );

            List<Lesson> lessonList = module.getLessonList();

            System.out.println(
                    "[Enrollment] Lessons in module: "
                            + lessonList.size()
            );

            for (Lesson lesson : lessonList) {

                System.out.println(
                        "[Enrollment] Adding lesson to completion map: "
                                + lesson.name
                );

                lessonCompleted.put(lesson, false);
            }
        }

        System.out.println(
                "[Enrollment] Lesson completion map initialized"
        );

        System.out.println(
                "[Enrollment] Total lessons tracked: "
                        + lessonCompleted.size()
        );
    }

    public int getCourseID() {

        System.out.println(
                "[Enrollment] getCourseID() called"
        );

        return courseID;
    }

    public int getStudentID() {

        System.out.println(
                "[Enrollment] getStudentID() called"
        );

        return studentID;
    }

    public void markLessonCompleted(Lesson lesson,INotificationService notificationService) {

        System.out.println(
                "[Enrollment] markLessonCompleted() called"
        );

        System.out.println(
                "[Enrollment] Lesson: " + lesson.name
        );

        System.out.println(
                "[Enrollment] Checking whether lesson belongs to enrollment..."
        );

        if (lessonCompleted.containsKey(lesson)) {

            System.out.println(
                    "[Enrollment] Lesson found in enrollment"
            );

            lessonCompleted.put(lesson, true);

            System.out.println(
                    "[Enrollment] Lesson status changed to COMPLETED"
            );

            System.out.println("Lesson completed");

        } else {

            System.out.println(
                    "[Enrollment] Lesson NOT found in enrollment"
            );

            System.out.println(
                    "This lesson is not a part of this enrollment"
            );
        }

        if(getProgressPercent()==100.0){
            enrollmentStatus=EnrollmentStatus.COMPLETED;

            notificationService.sendMessage(student,"Enrollment with ID: "+ this.getId()+ " completed");
        }
    }

    public double getProgressPercent() {

        System.out.println(
                "[Enrollment] getProgressPercent() called"
        );

        int n = lessonCompleted.size();

        System.out.println(
                "[Enrollment] Total lessons: " + n
        );

        int completed = 0;

        for (Map.Entry<Lesson, Boolean> entry :
                lessonCompleted.entrySet()) {

            Lesson lesson = entry.getKey();
            Boolean val = entry.getValue();

            System.out.println(
                    "[Enrollment] Lesson: "
                            + lesson.name
                            + " | Completed: "
                            + val
            );

            if (val == true) {
                completed++;

                System.out.println(
                        "[Enrollment] Completed lesson count: "
                                + completed
                );
            }
        }

        double progress = (100.0 * completed) / n;

        System.out.println(
                "[Enrollment] Progress calculated: "
                        + progress + "%"
        );

        return progress;
    }
}

class Student extends User {

    List<Enrollment> enrollmentList;

    Student(int id, String name, String email) {

        super(id, name, email);

        System.out.println(
                "[Student] Constructor called for: " + name
        );

        enrollmentList = new ArrayList<>();

        System.out.println(
                "[Student] Enrollment list initialized"
        );
    }

    public List<Enrollment> getEnrollmentList() {

        System.out.println(
                "[Student] getEnrollmentList() called"
        );

        return enrollmentList;
    }

    public void addEnrollment(Enrollment enrollment) {

        System.out.println(
                "[Student] addEnrollment() called"
        );

        System.out.println(
                "[Student] Adding enrollment ID: "
                        + enrollment.getId()
        );

        enrollmentList.add(enrollment);

        System.out.println(
                "[Student] Total enrollments: "
                        + enrollmentList.size()
        );

        System.out.println(
                "Enrollment added successfully"
        );
    }
}

class Instructor extends User {

    List<Course> courseList;

    Instructor(
            int id,
            String name,
            String email,
            List<Course> courseList) {

        super(id, name, email);

        System.out.println(
                "[Instructor] Constructor called for: " + name
        );

        this.courseList = courseList;

        System.out.println(
                "[Instructor] Courses assigned: "
                        + courseList.size()
        );
    }

    public List<Course> getCourseList() {

        System.out.println(
                "[Instructor] getCourseList() called"
        );

        return courseList;
    }
}

class Course {

    public int id;
    public String title, description;

    public List<Module> moduleList;

    Course(
            int id,
            String title,
            String description,
            List<Module> moduleList) {

        System.out.println(
                "[Course] Constructor called"
        );

        this.id = id;
        this.moduleList = moduleList;
        this.title = title;
        this.description = description;

        System.out.println(
                "[Course] Course created: " + title
        );

        System.out.println(
                "[Course] Modules assigned: "
                        + moduleList.size()
        );
    }

    public int getId() {

        System.out.println(
                "[Course] getId() called for: " + title
        );

        return id;
    }

    public String getDescription() {

        System.out.println(
                "[Course] getDescription() called"
        );

        return description;
    }

    public String getTitle() {

        System.out.println(
                "[Course] getTitle() called"
        );

        return title;
    }

    public List<Module> getModuleList() {

        System.out.println(
                "[Course] getModuleList() called"
        );

        return moduleList;
    }

    public void addModule(Module module) {

        System.out.println(
                "[Course] addModule() called"
        );

        System.out.println(
                "[Course] Adding module: " + module.name
        );

        moduleList.add(module);

        System.out.println(
                "[Course] Total modules: "
                        + moduleList.size()
        );
    }
}

class Module {

    public int id;
    public String name;

    List<Lesson> lessonList;

    Module(
            int id,
            String name,
            List<Lesson> lessonList) {

        System.out.println(
                "[Module] Constructor called"
        );

        this.id = id;
        this.name = name;
        this.lessonList = lessonList;

        System.out.println(
                "[Module] Module created: " + name
        );

        System.out.println(
                "[Module] Lessons assigned: "
                        + lessonList.size()
        );
    }

    public List<Lesson> getLessonList() {

        System.out.println(
                "[Module] getLessonList() called for: "
                        + name
        );

        return lessonList;
    }

    public void addLesson(Lesson lesson) {

        System.out.println(
                "[Module] addLesson() called"
        );

        System.out.println(
                "[Module] Adding lesson: "
                        + lesson.name
        );

        lessonList.add(lesson);

        System.out.println(
                "[Module] Total lessons: "
                        + lessonList.size()
        );
    }
}

class Lesson {

    public int id, contentDuration;

    public String name, contentUrl;

    Lesson(
            int id,
            String name,
            String contentUrl,
            int contentDuration) {

        System.out.println(
                "[Lesson] Constructor called"
        );

        this.id = id;
        this.contentDuration = contentDuration;
        this.contentUrl = contentUrl;
        this.name = name;

        System.out.println(
                "[Lesson] Lesson created: " + name
        );

        System.out.println(
                "[Lesson] ID: " + id
        );

        System.out.println(
                "[Lesson] Duration: "
                        + contentDuration
        );

        System.out.println(
                "[Lesson] URL: "
                        + contentUrl
        );
    }
}

