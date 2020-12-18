/*======================================================================================
 * File     : Student.java  (Runnable Class)
 * Author   : Rammuni Ravidu Suien Silva
 * IIT No   : 2016134
 * UoW No   : 16267097
 * Contents : 6SENG002W / 6SENG004C CWK
 *            This Runnable class represents a Student of the system who has Documents to print
 * Date     : 04/01/2021
 ======================================================================================*/

import java.util.Arrays;
import java.util.stream.IntStream;  // 'IntStream' in JAVA 8 was used

public class Student implements Runnable {

    public Thread stuThread; // Thread of the student

    private final Printer printer; // Printer object

    // Student details
    private final String studentName;
    private final String studentID;

    private final int documentCount = 5;  // Total documents count in a single student

    private int[] printedIndex = new int[documentCount]; // Collects the indices of the printed documents

    // This is limited to 10 as the minimum replaceable toner value is 10 (as per CW specification)
    private int docMaxLength = 10; // Maximum document length

    private int sleepIntensity = 3000; // Represents the maximum duration of the random sleep period


    // Array holding the documents
    private Document[] printingDocuments = new Document[documentCount];

    // Only constructor
    public Student(Printer printer, String studentName, String studentID) {
        this.printer = printer;
        this.studentName = studentName;
        this.studentID = studentID;

        Arrays.fill(printedIndex, -1); // Fills the printed indices array with -1
        createDocuments(); // Creating documents objects with valid random information

        // Creating the thread instance and placing it in the thread group
        stuThread = new Thread(
                PrintingSystem.printSysThreadGroups.get("students"),this);

    }

    // Method for creating documents objects with valid random information
    private synchronized void createDocuments() {
        for (int docIndex = 0; docIndex < documentCount; docIndex++) {
            String docName = "stuID-" + studentID + "-doc-" + docIndex;
            int docLength = (int)Math.ceil(Math.random() * docMaxLength); // Math.ceil was used to get rid of zero
            this.printingDocuments[docIndex] = new Document(studentID, docName, docLength);
        }
    }

    // Displays information of the student and the belonging documents
    public synchronized void displayDetails() {
        System.out.println("==================================================================================");
        System.out.println("                            STUDENT ID: "+studentID);
        System.out.println("==================================================================================");
        System.out.println("                              Documents Details ");
        System.out.println("----------------------------------------------------------------------------------");
        for (int docIndex=0; docIndex<printingDocuments.length; docIndex++) {
            System.out.println("Document "+docIndex+":"+printingDocuments[docIndex].toString());
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println();

    }

    @Override
    public void run() {
        int docIndex = 0; // Index of the printingDocument array
        int curPrintingIndex = 0; // Index of the printedIndex array

        // 'IntStream' in JAVA 8 was used
        // Iterate through the documents array until all the documents get printed
        while (IntStream.of(printedIndex).anyMatch(i -> i == -1)) {

            try {
                Thread.sleep((int) (Math.random() * sleepIntensity)); // Random sleeps
            } catch (InterruptedException e) {
                System.err.println("ERROR:- Student: " + e);
            }

            int checkDocIndex = docIndex;
            // Check whether the document has already been printed
            if(IntStream.of(printedIndex).anyMatch(i -> i == checkDocIndex)) {
                docIndex = (docIndex+1) % documentCount;
                continue;
            }

            // Setting the success boolean flag
            boolean success = printer.printDocument(printingDocuments[docIndex]);

            // Adding the index to the printed index only if the document is successfully printed
            if(success) {
                printedIndex[curPrintingIndex] = docIndex;
                curPrintingIndex++;
            }

            docIndex = (docIndex+1) % documentCount; // Mod operator used to overcome any NullPointerException errors
        }
        displayMsg("MY PRINTING JOBS ARE DONE!");
    }

    // Console message display method for Student
    private synchronized void displayMsg(String message) {
        System.out.printf("%-18s: %s\n", studentID, message);
    }
}
