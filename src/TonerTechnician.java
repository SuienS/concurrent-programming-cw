/*======================================================================================
 * File     : TonerTechnician.java  (Runnable Class)
 * Author   : Rammuni Ravidu Suien Silva
 * IIT No   : 2016134
 * UoW No   : 16267097
 * Contents : 6SENG002W / 6SENG004C CWK
 *            This class Runnable represents the Toner Technician of the system
 * Date     : 04/01/2021
 ======================================================================================*/

public class TonerTechnician implements Runnable{

    private Thread tonerTechThread; // Thread of the class

    private final Printer printer ; // Polymorphism is used

    // Identification information of the TonerTechnician
    private final String tonerTechName ;
    private final String tonerTechID ;

    private static final int maxAttempts = 3; // Maximum attempts of replacing the toner (As per CW spec.)

    private int sleepIntensity = 3000; // Represents the maximum duration of the random sleep period

    // Only constructor
    public TonerTechnician(Printer printer, String tonerTechName, String tonerTechID) {
        this.printer = printer;
        this.tonerTechName = tonerTechName;
        this.tonerTechID = tonerTechID;

        // Creating the thread instance and placing it in the thread group
        tonerTechThread = new Thread(
                PrintingSystem.getPrintSysThreadGroups().get("technicians"),this);
    }

    @Override
    public void run() {

        // Limiting replacing attempts to three
        for(int attempt = 0; attempt<maxAttempts; attempt++) {
            displayMsg("Printer Toner replace checking...");
            ((ServicePrinter)printer).replaceTonerCartridge();
            try {
                Thread.sleep((int)(Math.random()*sleepIntensity)); // Random sleeps
            } catch (InterruptedException e) {
                System.err.println("ERROR:- TonerTechnician: " + e);
            }
            displayMsg("Printer Toner checked - "+(attempt+1));
        }
        displayMsg("3 TONER REPLACEMENT ATTEMPTS COMPLETED!");
    }

    // Console message display method for TonerTechnician
    private synchronized void displayMsg(String message) {
        System.out.printf("%-18s: %s\n", "TonerTechnician", message);
    }

    // Getters
    public Thread getTonerTechThread() {
        return tonerTechThread;
    }

    public static int getMaxAttempts() {
        return maxAttempts;
    }
}
