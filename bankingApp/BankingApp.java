import service.BankingService;

import java.util.Scanner;

public class BankingApp {

    public static void main(String[] args) {

        Scanner scanner =
                new Scanner(System.in);

        BankingService service =
                new BankingService(scanner);

        service.start();

        scanner.close();
    }
}
