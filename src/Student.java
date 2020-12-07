public class Student implements Runnable {

    public Thread stuThread;

    private final ThreadGroup studentThreadGroup;
    private final Printer printer;
    private final String studentName;
    private final String studentID;

    private final int documentCount = 5;

    private int sleepIntensity = 1000;
    private int docLengthMultiplier = 10;


    private Document[] printingDocuments = new Document[documentCount];

    public Student(ThreadGroup studentThreadGroup, Printer printer, String studentName, String studentID) {
        this.studentThreadGroup = studentThreadGroup;
        this.printer = printer;
        this.studentName = studentName;
        this.studentID = studentID;
        createDocuments();

        PrintingSystem.printSysThreadGroups.put("studentThreadGroup", new ThreadGroup(
                PrintingSystem.printSysThreadGroups.get("main_thread"),"students"));

        stuThread = new Thread(
                PrintingSystem.printSysThreadGroups.get("studentThreadGroup"),this);

    }

    private synchronized void createDocuments() {
        for (int docIndex = 0; docIndex < documentCount; docIndex++) {
            String docName = "stuID-" + studentID + "-doc-" + docIndex;
            int docLength = 10;
            this.printingDocuments[docIndex] = new Document(studentID, docName, docLength);
        }
    }

    @Override
    public void run() {
        for (Document printingDocument : printingDocuments) {
            printer.printDocument(printingDocument);
            System.out.println(studentID+": "+printer.toString());
            try {
                Thread.sleep((int) (Math.random() * sleepIntensity));
            } catch (InterruptedException e) {
                System.err.println("ERROR:- Student: " + e);
            }
        }
        System.out.println("STUDENT:"+studentID+"_"+studentName+" - "+"PRINTING DONE!");
    }
}
