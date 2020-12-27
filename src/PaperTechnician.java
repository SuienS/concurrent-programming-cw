/*======================================================================================
 * File     : PaperTechnician.java  (Class)
 * Author   : Rammuni Ravidu Suien Silva
 * IIT No   : 2016134
 * UoW No   : 16267097
 * Contents : 6SENG002W / 6SENG004C CWK
 *            This Runnable class represents the Paper Technician of the system
 * Date     : 04/01/2021
 ======================================================================================*/

public class PaperTechnician implements Runnable{

    private Thread paperTechThread; // Thread of the class

    private final Printer printer ; // Polymorphism is used

    // Identification information of the PaperTechnician
    private final String paperTechName ;
    private final String paperTechID ;

    private static final int maxAttempts = 3; // Maximum attempts of refilling the paper tray (As per CW spec.)

    private int sleepIntensity = 3000; // Represents the maximum duration of the random sleep period

    // Only constructor
    public PaperTechnician(Printer printer, String paperTechName, String paperTechID) {
        this.printer = printer;
        this.paperTechName = paperTechName;
        this.paperTechID = paperTechID;

        // Creating the thread instance and placing it in the thread group
        paperTechThread = new Thread(
                PrintingSystem.getPrintSysThreadGroups().get("technicians"),this);
    }

    @Override
    public void run() {

        // Limiting refilling attempts to three
        for(int attempt = 0; attempt<maxAttempts; attempt++) {
            displayMsg("Printer Paper refill checking...");
            ((ServicePrinter)printer).refillPaper();
            try {
                Thread.sleep((int)(Math.random()*sleepIntensity)); // Random sleeps
            } catch (InterruptedException e) {
                System.err.println("ERROR:- PaperTechnician: " + e);
            }
            displayMsg("Printer Paper refill checked - "+(attempt+1));
        }
        displayMsg("3 PAPER REFILL ATTEMPTS COMPLETED!");
    }

    // Console message display method for PaperTechnician
    private synchronized void displayMsg(String message) {
        System.out.printf("%-18s: %s\n", "PaperTechnician", message);
    }

    // Getters
    public Thread getPaperTechThread() {
        return paperTechThread;
    }

    public static int getMaxAttempts() {
        return maxAttempts;
    }
}
