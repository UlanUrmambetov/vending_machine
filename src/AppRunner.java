import enums.ActionLetter;
import model.*;
import util.UniversalArray;
import util.UniversalArrayImpl;

import java.util.OptionalInt;
import java.util.Scanner;

public class AppRunner {

    private final UniversalArray<Product> products = new UniversalArrayImpl<>();

    private static boolean isExit = false;

    private final CoinAcceptor coinAcceptor;

    private final Scanner sc = new Scanner(System.in);

    private AppRunner() {
        products.addAll(new Product[]{
                new Water(ActionLetter.B, 20),
                new CocaCola(ActionLetter.C, 50),
                new Soda(ActionLetter.D, 30),
                new Snickers(ActionLetter.E, 80),
                new Mars(ActionLetter.F, 80),
                new Pistachios(ActionLetter.G, 130)
        });
        coinAcceptor = new CoinAcceptor(100);
    }

    public static void run() throws IllegalAccessException {
        AppRunner app = new AppRunner();
        while (!isExit) {
            app.startSimulation();
        }

    }

    private void startSimulation() throws IllegalAccessException {
        print("В автомате доступны:");
        showProducts(products);

        print("Монет на сумму: " + coinAcceptor.getAmount());

        UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
        allowProducts.addAll(getAllowedProducts().toArray());
        chooseAction(allowProducts);

    }

    private UniversalArray<Product> getAllowedProducts() {
        UniversalArray<Product> allowProducts = new UniversalArrayImpl<>();
        for (int i = 0; i < products.size(); i++) {
            if (coinAcceptor.getAmount() >= products.get(i).getPrice()) {
                allowProducts.add(products.get(i));
            }
        }
        return allowProducts;
    }

    private static OptionalInt tryParse(String text){
        try {
            int value = Integer.parseInt(text);
            return OptionalInt.of(value);
        } catch (NumberFormatException e) {
            System.out.println("Введено не число");
            return OptionalInt.empty();
        }
    }

    private void moneyBox() throws IllegalAccessException{
        try {
            print("Машина может принять до 5 тысяч");
            System.out.print("Введите сумму пополнения: ");
            String input = sc.nextLine();
            OptionalInt result = tryParse(input);
            if (result.isEmpty()){
                throw new IllegalAccessException("Введите число");
            }
            int amount = result.getAsInt();
            coinAcceptor.setAmount(coinAcceptor.getAmount() + amount);
            print("Вы пополнили баланс на " + amount);
        }catch (Exception e){
            System.out.println("Введите сумму");
        }
    }

    private void chooseAction(UniversalArray<Product> products) throws IllegalAccessException {
        while (true) {
            print(" a - Пополнить баланс");
            showActions(products);
            print(" h - Выйти");

            String input = fromConsole().trim();
            if (input.isEmpty()) {
                print("Вы ввели пустую строку");
                chooseAction(products);
                continue;
            }

            String action;
            try {
                action = input.substring(0, 1).toLowerCase();
            } catch (IndexOutOfBoundsException e) {
                print("Ошибка ввода. Попробуйте еще раз.");
                continue;
            }

            if ("a".equalsIgnoreCase(action)) {
                moneyBox();
                return;
            }

            try {
                for (int i = 0; i < products.size(); i++) {
                    if (products.get(i).getActionLetter().equals(ActionLetter.valueOf(action.toUpperCase()))) {
                        coinAcceptor.setAmount(coinAcceptor.getAmount() - products.get(i).getPrice());
                        print("Вы купили " + products.get(i).getName());
                        break;
                    }
                }
            } catch (IllegalArgumentException e) {
                if ("h".equalsIgnoreCase(action)) {
                    isExit = true;
                } else {
                    print("Недопустимая буква. Попрбуйте еще раз.");
                    chooseAction(products);
                }
            }
        }
    }

    private void showActions(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(String.format(" %s - %s", products.get(i).getActionLetter().getValue(), products.get(i).getName()));
        }
    }

    private String fromConsole() {
        return new Scanner(System.in).nextLine();
    }

    private void showProducts(UniversalArray<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            print(products.get(i).toString());
        }
    }

    private void print(String msg) {
        System.out.println(msg);
    }
}
