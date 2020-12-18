/*===========================================================================
 * File     : LaserPrinter.java  (Thread Class)
 * Author   : Rammuni Ravidu Suien Silva
 * IIT No   : 2016134
 * UoW No   : 16267097
 * Contents : 6SENG002W / 6SENG004C CWK
 *            This Thread class represents the actual printer in the system
 * Date     : 04/01/2021
 ===========================================================================*/

import java.util.Arrays;

public class LaserPrinter extends Thread implements ServicePrinter {

    private final String printerID; // Identification of the Printer
    private int paperLevel; // Represents the current paper level
    private int tonerLevel; // Represents the current toner level
    private int printedDocumentsCount; // Keeps the count of total printed docs
    private int totalDocs;

    private final int refillPaperLevel = Full_Paper_Tray - SheetsPerPack; // Maximum refill possible paper level
    private final int waitingPeriod = 5000; // Waiting period (in ms) after each replacing or refilling attempt

    // Variables for the progress bar illustration
    char[] progressBar;
    private int pbPointer = 0;


    // Only Constructor
    public LaserPrinter(String printerID, int paperLevel, int tonerLevel, int printedDocumentsCount) {
        this.printerID = printerID;
        this.paperLevel = paperLevel;
        this.tonerLevel = tonerLevel;
        this.printedDocumentsCount = printedDocumentsCount;
        this.totalDocs = PrintingSystem.STUDENTS_COUNT * Student.documentCount;
        progressBar = new char[this.totalDocs];
        Arrays.fill(progressBar, ' ');
    }

    // Method for printer toner cartridge replacement
    @Override
    public synchronized void replaceTonerCartridge() {
        while (this.tonerLevel >= Minimum_Toner_Level) {
            try {
                wait(waitingPeriod);
                return; // To limit checks (successful + unsuccessful replacement attempts) to three
            } catch (InterruptedException e) {
                System.err.println("ERROR:- LaserPrinter.replaceTonerCartridge(): " + e);
            }
        }
        this.tonerLevel = PagesPerTonerCartridge;
        displayMsg("Printer TONER REPLACED - "+toString());

    }

    @Override
    public synchronized void refillPaper() {
        while (this.paperLevel >= this.refillPaperLevel) {
            try {
                wait(waitingPeriod);
                return; // To limit checks (successful + unsuccessful refill attempts) to three
            } catch (InterruptedException e) {
                System.err.println("ERROR:- LaserPrinter.refillPaper(): " + e);
            }
        }
        this.paperLevel += SheetsPerPack; // Refilling the paper tray
        System.out.println("====================================================================" +
                "===========================================================================");
        displayMsg("Printer PAPER REFILLED - "+toString());
        System.out.println("====================================================================" +
                "===========================================================================");

    }

    // Printing the documents
    @Override
    public synchronized boolean printDocument(Document document) {
        boolean success = false;
        int paperCount = document.getNumberOfPages();

        System.out.println("====================================================================" +
                "===========================================================================");

        displayMsg("PRINTING: "+document.toString());
        if (paperCount > paperLevel || paperCount > tonerLevel) {
            // Checks whether the printing of the requested document is possible
            displayMsg("INSUFFICIENT RESOURCES FOR: "+document.toString());
            displayMsg("Retrying another document...");
        } else {
            // Possible documents to print
            success = true;
            progressBarIncrease();
            reducePaperLevel(paperCount); // Consumption of paper
            reduceTonerLevel(paperCount); // Consumption of ink
            displayMsg("DONE PRINTING; "+document.toString());
            displayMsg(toString());
            displayMsg("Printing Progress: ["+ new String(progressBar) +"] "
                    + (printedDocumentsCount/ (double) totalDocs)*100.0 +"% Done");
        }

        System.out.println("====================================================================" +
                "===========================================================================");
        return success;
    }

    // toString method of printer
    @Override
    public synchronized String toString() {
        return "Printer Stats [" +
                "printerID: " + printerID +
                ", paperLevel: " + paperLevel +
                ", tonerLevel: " + tonerLevel +
                ", printedDocumentsCount: " + printedDocumentsCount +
                ']';
    }

    // Method for consuming paper
    private synchronized void reducePaperLevel(int paperCount) {
        this.paperLevel -= paperCount;
    }

    // Method for consuming ink
    private synchronized void reduceTonerLevel(int paperCount) {
        this.tonerLevel -= paperCount;
        this.printedDocumentsCount += 1;
    }

    // Console message display method for Printer
    private synchronized void displayMsg(String message) {
        System.out.printf("%-18s: %s\n","PRINTER ("+printerID+")",message);
    }

    // Progress bar for the illustration of the printing progress on the console
    private synchronized void progressBarIncrease(){
        progressBar[pbPointer]='=';
        if(pbPointer >= totalDocs-1 ) {
            return;
        }
        progressBar[pbPointer+1]='>';
        pbPointer = (pbPointer + 1) % totalDocs;
    }

}
