import java.util.Arrays;
import java.util.stream.IntStream;  //IntStream in JAVA 8 was used

public class Student implements Runnable {

    public Thread stuThread;

    private final ThreadGroup studentThreadGroup;
    private final Printer printer;
    private final String studentName;
    private final String studentID;

    private final int documentCount = 5;
    private int[] printedIndex = new int[documentCount];

    private int sleepIntensity = 500;
    private int docMaxLength = 10; // This is limited to 10 as the minimum replaceable toner value is 10


    private Document[] printingDocuments = new Document[documentCount];

    public Student(ThreadGroup studentThreadGroup, Printer printer, String studentName, String studentID) {
        this.studentThreadGroup = studentThreadGroup;
        this.printer = printer;
        this.studentName = studentName;
        this.studentID = studentID;
        Arrays.fill(printedIndex, -1);
        createDocuments();

        PrintingSystem.printSysThreadGroups.put("studentThreadGroup", new ThreadGroup(
                PrintingSystem.printSysThreadGroups.get("main_thread"),"students"));

        stuThread = new Thread(
                PrintingSystem.printSysThreadGroups.get("studentThreadGroup"),this);

    }

    private synchronized void createDocuments() {
        for (int docIndex = 0; docIndex < documentCount; docIndex++) {
            String docName = "stuID-" + studentID + "-doc-" + docIndex;
            int docLength = (int)Math.ceil(Math.random() * docMaxLength); // Math.ceil was used to get rid of zero
            this.printingDocuments[docIndex] = new Document(studentID, docName, docLength);
        }
    }

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
        int docIndex = 0;
        int curPrintingIndex = 0;

        //IntStream in JAVA 8 was used
        while (IntStream.of(printedIndex).anyMatch(i -> i == -1)) {

            try {
                //System.out.print("."); //loading cli animation java TODO
                Thread.sleep((int) (Math.random() * sleepIntensity));

            } catch (InterruptedException e) {
                System.err.println("ERROR:- Student: " + e);
            }

            if(docIndex >= documentCount) {
                docIndex = 0;
            }

            int checkDocIndex = docIndex;
            if(IntStream.of(printedIndex).anyMatch(i -> i == checkDocIndex)) {
                docIndex++;
                continue;
            }

            //displayMsg("PRINTING - "+printingDocuments[docIndex].toString());
            boolean success = printer.printDocument(printingDocuments[docIndex]);

            if(success) {
                printedIndex[curPrintingIndex] = docIndex;
                curPrintingIndex++;
            }

            docIndex++;
        }
        displayMsg("MY PRINTING JOBS ARE DONE!");
    }

    private synchronized void displayMsg(String message) {
        System.out.printf("%-18s: %s\n", studentID, message);
    }
}
