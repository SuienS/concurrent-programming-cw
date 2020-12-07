public class LaserPrinter extends Thread implements ServicePrinter {

    private final String printerID;
    private int paperLevel;
    private int tonerLevel;
    private int printedDocumentsCount;

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
            } catch (InterruptedException e) {
                System.err.println("ERROR:- LaserPrinter.replaceTonerCartridge(): " + e);
            }
        }
        this.tonerLevel += newTonerSize;
        System.out.println("Printer Toner replaced");
        System.out.println(toString());
        notifyAll();
    }

    @Override
    public synchronized void refillPaper() {
        while (this.paperLevel > this.refillPaperLevel) {
            try {
                wait(waitingPeriod);
            } catch (InterruptedException e) {
                System.err.println("ERROR:- LaserPrinter.refillPaper(): " + e);
            }
        }
        this.paperLevel += newPaperPackSize;
        System.out.println("Printer Paper refilled...");
        System.out.println(toString());

        notifyAll();
    }

    @Override
    public synchronized void printDocument(Document document) {
        //Printing the documents
        int paperCount = document.getNumberOfPages();
        boolean canPrint = paperCount <= paperLevel && paperCount <= tonerLevel;

        while (!canPrint) {
            try {
                System.out.println("Insufficient Resources...");
                wait();
            } catch (InterruptedException e) {
                System.err.println("ERROR:- LaserPrinter.printDocument(): " + e);
            }
        }
        reducePaperLevel(paperCount);
        reduceTonerLevel(paperCount);
        notifyAll();
    }

    @Override
    public synchronized String toString() {
        return "LaserPrinter Stat [" +
                "printerID: " + printerID +
                ", paperLevel: " + paperLevel +
                ", tonerLevel: " + tonerLevel +
                ", printedDocumentsCount: " + printedDocumentsCount +
                ']';
    }

    private synchronized void reducePaperLevel(int paperCount) {
        this.paperLevel -= paperCount;
        this.printedDocumentsCount +=1;
    }

    private synchronized void reduceTonerLevel(int paperCount) {
        this.tonerLevel -= paperCount;
    }

}
