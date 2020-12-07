public class TonerTechnician implements Runnable{

    public Thread tonerTechThread;


    private final ThreadGroup tonerTechThreadGroup ;
    private final Printer printer ; //Polymorphism is used
    private final String tonerTechName ;
    private final String tonerTechID ;

    private final int maxAttempts = 3;

    private int sleepIntensity = 10000;


    public TonerTechnician(ThreadGroup tonerTechThreadGroup, Printer printer, String tonerTechName, String tonerTechID) {
        this.tonerTechThreadGroup = tonerTechThreadGroup;
        this.printer = printer;
        this.tonerTechName = tonerTechName;
        this.tonerTechID = tonerTechID;

        PrintingSystem.printSysThreadGroups.put("technicianThreadGroup", new ThreadGroup(
                PrintingSystem.printSysThreadGroups.get("main_thread"),"technician"));

        tonerTechThread = new Thread(
                PrintingSystem.printSysThreadGroups.get("technicianThreadGroup"),this);

    }

    @Override
    public void run() {

        for(int attempt = 0; attempt<maxAttempts; attempt++) {
            ((ServicePrinter)printer).replaceTonerCartridge();
            try {
                Thread.sleep((int)(Math.random()*sleepIntensity));
            } catch (InterruptedException e) {
                System.err.println("ERROR:- TonerTechnician: " + e);
            }
        }

        System.out.println("TonerTechnician:"+tonerTechID+"_"+tonerTechName+" - "+
                "3 TONER REPLACEMENT ATTEMPTS FINISHED!");
    }
}
