import java.util.Hashtable;
import java.util.Scanner;

public class PrintingSystem {

    private final static int STUDENTS_COUNT = 4;

    private static Printer laserPrinter; //Polymorphism

    static Hashtable<String, ThreadGroup> printSysThreadGroups = new Hashtable<>();
    //static Hashtable<String, Thread> printSysThreads = new Hashtable<String, Thread>();
    static Student[] students = new Student[STUDENTS_COUNT];

    //TRY TO EXTEND BOTH TECHNICIANS FROM ONE PARENT
    static PaperTechnician paperTechnician = null;
    static TonerTechnician tonerTechnician = null;

    public static void main(String[] args) {

        displayMsg("Starting Printer...");
        laserPrinter = new LaserPrinter("LP-0001", 10, 10, 0);
        displayMsg(laserPrinter.toString());
        menu();
        //TODO MAIN MENU!


        displayMsg("Creating instances...");
        createInstances();


        System.out.println("--------------------------------------------------------" +
                "--------------------------");

        displayMsg("Details of the randomly created DOCUMENTs of the the STUDENTs");

        students[0].displayDetails();
        students[1].displayDetails();
        students[2].displayDetails();
        students[3].displayDetails();

        displayMsg("Printer... Booting STARTING ALL THREADS....");
        System.out.println("-----------------------------------------------------------------" +
                "------------------------------------------------------------------------------");
        startAllThreads();

        System.out.println("-----------------------------------------------------------------" +
                "------------------------------------------------------------------------------");
        displayMsg("FINAL PRINTERS STATUS - "+laserPrinter.toString());
        displayMsg("ALL THREADS HAVE TERMINATED, SEE YOU AGAIN!");
        System.out.println("-----------------------------------------------------------------" +
                "------------------------------------------------------------------------------");

    }

    private static void menu(){
        int paperLevelInt = 0;
        int tonerLevelInt = 0;
        displayMsg("Press Y to customise printer initial status and press any other to continue with the default status...");
        displayMsg("Invalid inputs will cause to start the program with default values...");
        Scanner scn = new Scanner(System.in);
        if(scn.nextLine().toUpperCase().equals("Y")) {
            displayMsg("Enter initial paper level: ");
            String paperLevel = "-";
            paperLevel = scn.nextLine();
            if(!paperLevel.matches("^(0|[1-9][0-9]{0,9})$")) {
                displayMsg("Continuing with default values...");
                return;
            }
            paperLevelInt = Integer.parseInt(paperLevel);

            displayMsg("Enter initial toner level: ");
            String tonerLevel = "-";
            tonerLevel = scn.nextLine();
            if(!tonerLevel.matches("^(0|[1-9][0-9]{0,9})$")) {
                displayMsg("Continuing with default values...");
                return;
            }
            tonerLevelInt = Integer.parseInt(tonerLevel);

            laserPrinter = new LaserPrinter("LP-0001", paperLevelInt, tonerLevelInt, 0);

            displayMsg("Custom Settings successfully loaded");
            displayMsg(laserPrinter.toString());
        }else{
            displayMsg("Continuing with default values...");
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

        displayMsg("Done creating instances...");
    }

    private static void startAllThreads() {
        paperTechnician.paperTechThread.start();
        tonerTechnician.tonerTechThread.start();


        students[0].stuThread.start();
        students[1].stuThread.start();
        students[2].stuThread.start();
        students[3].stuThread.start();

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

    private static synchronized void displayMsg(String message) {
        System.out.printf("%-18s: %s\n","MAIN_PROGRAM",message);
    }
}
