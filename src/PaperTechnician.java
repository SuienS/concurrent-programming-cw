public class PaperTechnician implements Runnable{
//TRY TO EXTEND BOTH TECHNICIANS FROM ONE PARENT

    public Thread paperTechThread;


    private final ThreadGroup paperTechThreadGroup ;
    private final Printer printer ;
    private final String paperTechName ;
    private final String paperTechID ;

    private final int maxAttempts = 3;

    private int sleepIntensity = 10000;

    public PaperTechnician(ThreadGroup paperTechThreadGroup, Printer printer, String paperTechName, String paperTechID) {
        this.paperTechThreadGroup = paperTechThreadGroup;
        this.printer = printer;
        this.paperTechName = paperTechName;
        this.paperTechID = paperTechID;

        PrintingSystem.printSysThreadGroups.put("technicianThreadGroup", new ThreadGroup(
                PrintingSystem.printSysThreadGroups.get("main_thread"),"technician"));

        paperTechThread = new Thread(
                PrintingSystem.printSysThreadGroups.get("technicianThreadGroup"),this);

    }

    @Override
    public void run() {
        for(int attempt = 0; attempt<maxAttempts; attempt++) {
            ((ServicePrinter)printer).refillPaper();
            try {
                Thread.sleep((int)(Math.random()*sleepIntensity));
            } catch (InterruptedException e) {
                System.err.println("ERROR:- PaperTechnician: " + e);
            }
        }

        System.out.println("PaperTechnician:"+paperTechID+"_"+paperTechName+" - "+
                "3 PAPER REFILLING ATTEMPTS FINISHED!");
    }
}
