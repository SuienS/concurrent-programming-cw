import java.util.Hashtable;

public class PrintingSystem {

    private final static int THREADS_COUNT = 6;
    private final static int STUDENTS_COUNT = 4;

    private final static Printer laserPrinter = new LaserPrinter(
            "LP-0001", 10, 10, 0); //Polymorphism

    static Hashtable<String, ThreadGroup> printSysThreadGroups = new Hashtable<>();
    //static Hashtable<String, Thread> printSysThreads = new Hashtable<String, Thread>();
    static Student[] students = new Student[STUDENTS_COUNT];

    //TRY TO EXTEND BOTH TECHNICIANS FROM ONE PARENT
    static PaperTechnician paperTechnician = null;
    static TonerTechnician tonerTechnician = null;

    public static void main(String[] args) {

        //ADD A MENU! TODO
        createInstances();

        students[0].displayDetails();
        students[1].displayDetails();
        students[2].displayDetails();
        students[3].displayDetails();

        System.out.println("Printer Booting ....");

        paperTechnician.paperTechThread.start();
        tonerTechnician.tonerTechThread.start();


        students[0].stuThread.start();
        students[1].stuThread.start();
        students[2].stuThread.start();
        students[3].stuThread.start();

        // Setup the graceful exit of the program TODO
        try {
            paperTechnician.paperTechThread.join();
            tonerTechnician.tonerTechThread.join();
            students[0].stuThread.join();
            students[1].stuThread.join();
            students[2].stuThread.join();
            students[3].stuThread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void createInstances() {
        printSysThreadGroups.put("main_thread", Thread.currentThread().getThreadGroup());
        printSysThreadGroups.put("students",
                new ThreadGroup(printSysThreadGroups.get("main_thread"), "ThreadGroup: Students")
        );
        printSysThreadGroups.put("technicians",
                new ThreadGroup(printSysThreadGroups.get("main_thread"), "ThreadGroup: Technicians")
        );

        for (int stuIndex = 0; stuIndex < 4; stuIndex++) {
            students[stuIndex] = new Student(printSysThreadGroups.get("students"), laserPrinter,
                    "Student_" + stuIndex, "STU-00000" + stuIndex);
        }

        paperTechnician = new PaperTechnician(printSysThreadGroups.get("technicians"),
                laserPrinter, "Ravidu", "TECH-P-001");

        tonerTechnician = new TonerTechnician(printSysThreadGroups.get("technicians"),
                laserPrinter, "Silva", "TECH-T-001");

        System.out.println("Done creating instances...");
    }
}
