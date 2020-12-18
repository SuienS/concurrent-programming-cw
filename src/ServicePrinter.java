public interface ServicePrinter extends Printer{
    // from Printer:
    //    public void printDocument( Document document ) ;

    // Constants for the Printer

    public final int Full_Paper_Tray  = 250 ;
    public final int SheetsPerPack    = 50  ;

    public final int Full_Toner_Level        = 500 ;
    public final int Minimum_Toner_Level     = 10  ;
    public final int PagesPerTonerCartridge  = 500 ;


    // Methods for the Technician

    public void replaceTonerCartridge( ) ;

    public void refillPaper( ) ;
}
