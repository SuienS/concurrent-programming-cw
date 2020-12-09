public class LaserPrinter extends Thread implements ServicePrinter {

    private final String printerID;
    private int paperLevel;
    private int tonerLevel;
    private int printedDocumentsCount;

    private final int maxPaperLevel = 250;
    private final int refillPaperLevel = 200;
    private final int replaceTonerLevel = 10;
    private final int waitingPeriod = 5000;

    private final int newPaperPackSize = 50;
    private final int newTonerSize = 500;


    public LaserPrinter(String printerID, int paperLevel, int tonerLevel, int printedDocumentsCount) {
        this.printerID = printerID;
        this.paperLevel = paperLevel;
        this.tonerLevel = tonerLevel;
        this.printedDocumentsCount = printedDocumentsCount;
    }

    @Override
    public synchronized void replaceTonerCartridge() {
        while (this.tonerLevel >= this.replaceTonerLevel) {
            try {
                wait(waitingPeriod);
                return; // To limit checks (successful + unsuccessful) to three
            } catch (InterruptedException e) {
                System.err.println("ERROR:- LaserPrinter.replaceTonerCartridge(): " + e);
            }
        }
        this.tonerLevel = newTonerSize;
        displayMsg("Printer TONER REPLACED - "+toString());

        notifyAll();
    }

    @Override
    public synchronized void refillPaper() {
        while (this.paperLevel >= this.refillPaperLevel) {
            try {
                wait(waitingPeriod);
                return; // To limit checks (successful + unsuccessful) to three
            } catch (InterruptedException e) {
                System.err.println("ERROR:- LaserPrinter.refillPaper(): " + e);
            }
        }
        this.paperLevel += newPaperPackSize;
        System.out.println("====================================================================" +
                "===========================================================================");
        displayMsg("Printer PAPER REFILLED - "+toString());
        System.out.println("====================================================================" +
                "===========================================================================");

        notifyAll();
    }

    @Override
    public synchronized boolean printDocument(Document document) {
        boolean success = false;
        //Printing the documents
        int paperCount = document.getNumberOfPages();

        if (paperCount > paperLevel || paperCount > tonerLevel) {
            return success;
        } else {
            success = true;
            reducePaperLevel(paperCount);
            reduceTonerLevel(paperCount);
            System.out.println("====================================================================" +
                    "===========================================================================");
            displayMsg("DONE PRINTING; "+document.toString());
            displayMsg(toString());
            displayMsg("[Printing Progress: "+(printedDocumentsCount/20.0)*100.0 +"% Done]");
            System.out.println("====================================================================" +
                    "===========================================================================");
        }
//        if(printedDocumentsCount == 20) {
//            displayMsg("ALL PRINTING JOBS COMPLETED!");
//            System.exit(0);
//        }
        return success;
    }

    @Override
    public synchronized String toString() {
        return "Printer Stats [" +
                "printerID: " + printerID +
                ", paperLevel: " + paperLevel +
                ", tonerLevel: " + tonerLevel +
                ", printedDocumentsCount: " + printedDocumentsCount +
                ']';
    }

    private synchronized void reducePaperLevel(int paperCount) {
        this.paperLevel -= paperCount;
        this.printedDocumentsCount += 1;
    }


    private synchronized void reduceTonerLevel(int paperCount) {
        this.tonerLevel -= paperCount;
    }

    private synchronized void displayMsg(String message) {
        System.out.printf("%-18s: %s\n","PRINTER",message);
    }

}
