/*======================================================================================
 * File     : PrintingSystem.java  (Class)
 * Author   : Rammuni Ravidu Suien Silva
 * IIT No   : 2016134
 * UoW No   : 16267097
 * Contents : 6SENG002W / 6SENG004C CWK
 *            This class combines all the processes. Contains the 'main' method.
 * Date     : 04/01/2021
 ======================================================================================*/

import java.util.Hashtable; // Hashtable was used to store thread groups
import java.util.Scanner;

public class PrintingSystem {

    public static int STUDENTS_COUNT = 4; // Count of the students

    private static Printer laserPrinter; // Polymorphism

    static Hashtable<String, ThreadGroup> printSysThreadGroups = new Hashtable<>(); // Hashtable for ThreadGroups
    static Student[] students; // Array for Students objects

    //Initializing the technician variables
    static PaperTechnician paperTechnician = null;
    static TonerTechnician tonerTechnician = null;

    public static void main(String[] args) {

        displayMsg("Starting Printer...");
        laserPrinter = new LaserPrinter("LP-0001", 250, 50, 0);
        displayMsg(laserPrinter.toString());
        menu(); // Simulation start menu

        displayMsg("Creating instances...");
        createInstances(); // Creating instances of Students and Technicians

        displayMsg("Details of the randomly created DOCUMENTs of the the STUDENTs");
        displayStudentDetails(); // Displaying the details of the automatically created document

        displayMsg("Printer... Booting STARTING ALL THREADS....");

        startAllThreads(); // Starting the created threads

        displayMsg("FINAL PRINTER STATUS - "+laserPrinter.toString());
        displayMsg("ALL THREADS HAVE TERMINATED, SEE YOU AGAIN!");
        System.out.println("---------------------------------------------------------------------" +
                "//------------------------------------------------------------------------");
    }

    private static void menu(){
        int paperLevelInt = 0;
        int tonerLevelInt = 0;
        int stuCountInt = 0;
        int pageCountInt = 0;
        displayMsg("Press Y to customise system initial status and press any other to continue with the default status...");
        displayMsg("Invalid inputs will cause to start the program with default values...");

        // Input prompts
        Scanner scn = new Scanner(System.in);
        if(scn.nextLine().toUpperCase().equals("Y")) {
            System.out.println("====================================================================" +
                    "===========================================================================");
            displayMsg("                                  Settings Menu for Shared Printer Simulation");
            System.out.println("====================================================================" +
                    "===========================================================================");

            displayMsg("Enter initial paper level (max 250): ");
            String paperLevel = "-";
            paperLevel = scn.nextLine();
            if(!paperLevel.matches("^(0|[1-9][0-9]{0,9})$")) { // Input validation
                displayMsg("Continuing with default values...");
                return;
            }
            paperLevelInt = Integer.parseInt(paperLevel);

            displayMsg("Enter initial toner level (max 500): ");
            String tonerLevel = "-";
            tonerLevel = scn.nextLine();
            if(!tonerLevel.matches("^(0|[1-9][0-9]{0,9})$")) { // Input validation
                displayMsg("Continuing with default values...");
                return;
            }
            tonerLevelInt = Integer.parseInt(tonerLevel);

            // Input quantity validation
            if(paperLevelInt>ServicePrinter.Full_Paper_Tray || tonerLevelInt>ServicePrinter.Full_Toner_Level){
                displayMsg("Continuing with default values...");
                return;
            }

            // Setting maximum pages per automatically generated document
            displayMsg("Enter maximum document page count per document you want to simulate: ");
            String pageCount = "-";
            pageCount = scn.nextLine();
            if(!pageCount.matches("^(0|[1-9][0-9]{0,9})$")) {
                displayMsg("Continuing with default values...");
                return;
            }
            pageCountInt = Integer.parseInt(pageCount);
            Student.docMaxLength = pageCountInt;

            displayMsg("Enter number of Students you want to simulate: ");
            String stuCount = "-";
            stuCount = scn.nextLine();
            if(!stuCount.matches("^(0|[1-9][0-9]{0,9})$")) {
                displayMsg("Continuing with default values...");
                return;
            }
            stuCountInt = Integer.parseInt(stuCount);

            // Overcoming a potential deadlock
            // Checking whether the resources would be sufficient for the provided settings
            int totDocs = stuCountInt*Student.documentCount*Student.docMaxLength;
            boolean deadLock =
                    ((totDocs) > (paperLevelInt + ( ServicePrinter.SheetsPerPack * PaperTechnician.maxAttempts)))
                            || ((totDocs) > (tonerLevelInt + ( ServicePrinter.Full_Toner_Level * TonerTechnician.maxAttempts)));
            if(deadLock) {
                displayMsg("Entered student count with max document page count may cause the program to go into a DEADLOCK!");
                displayMsg("Continuing with default values...");
                return;
            }
            STUDENTS_COUNT = stuCountInt;
            laserPrinter = new LaserPrinter("LP-0001", paperLevelInt, tonerLevelInt, 0);

            displayMsg("Custom Settings successfully loaded");
            displayMsg(laserPrinter.toString());
        }else{
            displayMsg("Continuing with default values...");
        }
    }

    // Method for creating necessary Instances
    private static void createInstances() {
        displayMsg("Creating instances...");
        students = new Student[STUDENTS_COUNT];
        displayMsg("Creating ThreadGroups...");

        // adding 'main' thread to the ThreadGroup
        printSysThreadGroups.put("main_thread", Thread.currentThread().getThreadGroup());
        // Student ThreadGroup
        printSysThreadGroups.put("students",
                new ThreadGroup(printSysThreadGroups.get("main_thread"), "ThreadGroup: Students")
        );
        // Technicians ThreadGroup
        printSysThreadGroups.put("technicians",
                new ThreadGroup(printSysThreadGroups.get("main_thread"), "ThreadGroup: Technicians")
        );

        displayMsg("Creating Student instances...");
        for (int stuIndex = 0; stuIndex < STUDENTS_COUNT; stuIndex++) {
            students[stuIndex] = new Student(laserPrinter, "Student_" + stuIndex,
                    "STU-00000" + stuIndex);
        }

        displayMsg("Creating Technician instances...");
        paperTechnician = new PaperTechnician(laserPrinter, "Ravidu", "TECH-P-001");
        tonerTechnician = new TonerTechnician(laserPrinter, "Silva", "TECH-T-001");

        displayMsg("Done creating instances...");
        System.out.println("--------------------------------------------------------" +
                "--------------------------");
    }

    // Method for iteratively start all the necessary Threads
    private static void startAllThreads() {
        System.out.println("-----------------------------------------------------------------" +
                "------------------------------------------------------------------------------");
        paperTechnician.paperTechThread.start();
        tonerTechnician.tonerTechThread.start();

        for (Student student : students) {
            student.stuThread.start();
        }

        try {

            paperTechnician.paperTechThread.join();
            tonerTechnician.tonerTechThread.join();
            for (Student student : students) {
                student.stuThread.join();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-----------------------------------------------------------------" +
                "------------------------------------------------------------------------------");
    }

    // Method for displaying all the generated students and their details
    private static void displayStudentDetails() {
        for (Student student : students) {
            student.displayDetails();
        }
    }

    // Console message display method for PrintingSystem main class
    private static synchronized void displayMsg(String message) {
        System.out.printf("%-18s: %s\n","MAIN_PROGRAM",message);
    }
}
