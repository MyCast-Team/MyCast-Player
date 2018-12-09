package sample.model;

public class SystemOutputPrinterService implements OutputPrinterService {
    @Override
    public void print(String s) {
        System.out.println(s);
    }
}
