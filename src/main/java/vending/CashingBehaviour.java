package vending;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface CashingBehaviour {

    default boolean addCash(PointOfSale pos, Integer note, Integer qty) {
        return pos.addCash(note, qty);
    }

    default boolean removeCash(PointOfSale pos, Integer note, Integer qty) {
        return pos.removeCash(note, qty);
    }

    default void changeReport(String datafolder) {
        try {
            Path folderPath = Paths.get(System.getProperty("user.dir"), "cashier_reports");
            new File(folderPath.toString()).mkdirs();

            FileWriter writer = new FileWriter("cashier_reports/change_report.csv");

            // get cash saved in system
            Path cashPath = Paths.get(System.getProperty("user.dir"), datafolder); //need to get the data folder somehow
            FileInputStream fis = new FileInputStream(
                    Paths.get(cashPath.toString(), "pointOfSale.ser").toString());

            ObjectInputStream ois = new ObjectInputStream(fis);
            PointOfSale pos = (PointOfSale)ois.readObject();
            ois.close();
            fis.close();

            LinkedHashMap<Integer, Integer> cash = pos.getCash();
            writer.append("Note");
            writer.append(",");
            writer.append("Quantity");
            writer.append("\n");

            for(Integer note: cash.keySet()) {
                writer.append(String.format("$%.2f", (double)note/100.0));
                writer.append(",");
                writer.append(cash.get(note).toString());
                writer.append("\n");
            }

            writer.flush();
            writer.close();

            System.out.println("Created change report.");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error writing change report.");
            e.printStackTrace();
        }
    }

    default void transactionReport(String datafolder) {
        try {
            Path folderPath = Paths.get(System.getProperty("user.dir"), "cashier_reports");
            new File(folderPath.toString()).mkdirs();

            FileWriter writer = new FileWriter("cashier_reports/transactions_report.csv");

            // get users for transactions saved in system
            Path transPath = Paths.get(System.getProperty("user.dir"), datafolder); //need to get the data folder somehow
            FileInputStream fis = new FileInputStream(
                    Paths.get(transPath.toString(), "allUsers.ser").toString());

            ObjectInputStream ois = new ObjectInputStream(fis);

            List<User> allUsers = (ArrayList)ois.readObject();

            ois.close();
            fis.close();

            writer.append("Date");
            writer.append(",");
            writer.append("Time");
            writer.append(",");
            writer.append("Item Name");
            writer.append(",");
            writer.append("Quantity");
            writer.append(",");
            writer.append("Money paid ($)");
            writer.append(",");
            writer.append("Returned change ($)");
            writer.append(",");
            writer.append("Payment method");
            writer.append("\n");
            // loop through all users, get their transactions and write it
            List<Transaction> trans;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            HashMap<Product, Integer> products;

            for(User u:allUsers) {
                if(u instanceof Customer) {
                    trans = ((Customer) u).getTransactions();
                    for(Transaction t : trans) {
                        products = t.getProducts();
                        for(Product p : products.keySet()) {
                            writer.append(t.getDate().format(dateFormatter));
                            writer.append(",");
                            writer.append(t.getDate().format(timeFormatter));
                            writer.append(",");
                            writer.append(p.getName());
                            writer.append(",");
                            writer.append(products.get(p).toString());
                            writer.append(",");
                            writer.append(String.format("$%.2f", (double)t.getMoneyPaid()/100.0));
                            writer.append(",");
                            if(t.getChange() == null) {
                                // used card so no change
                                writer.append("$0.00");
                            } else {
                                writer.append(String.format("$%.2f", (double)t.getTotalChange()
                                        /100.0));
                            }
                            writer.append(",");
                            writer.append(t.getPaymentMethod());
                            writer.append("\n");
                        }
                    }
                }
            }

            writer.flush();
            writer.close();

            System.out.println("Created transactions report.");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error writing transactions report.");
            e.printStackTrace();
        }
    }

}