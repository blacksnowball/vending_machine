package vending;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class Owner extends User implements SellingBehaviour, CashingBehaviour {

    private List<User> users;
    public Owner(String username, String password, List<User> users) {
        super(username, password, Role.OWNER);
        this.users = users;
    }

    public boolean addUser(String username, String password, String role) {

            // assume successful operation unless no match case meet
            boolean success = true;

            if (!users.contains(retrieveUser(username))) {

                // only add user if username isn't already taken

                if (Role.OWNER.toString().equalsIgnoreCase(role)) {
                    users.add(new Owner(username, password, users));
                } else if (Role.CASHIER.toString().equalsIgnoreCase(role)) {
                    users.add(new Cashier(username, password));
                } else if (Role.SELLER.toString().equalsIgnoreCase(role)) {
                    users.add(new Seller(username, password));
                } else if (Role.CUSTOMER.toString().equalsIgnoreCase(role)) {
                    users.add(new Customer(username, password));
                }

            } else {
                success = false;
            }

            return success;

    }

    public boolean removeUser(String username) {

        User userToRemove = retrieveUser(username);

        if (userToRemove != null) {
            users.remove(userToRemove);
            return true;
        }

        return false;

    }

    public User retrieveUser(String username) {
        for (User u : users) {
            if (u.getUsername().matches(username)) {
                return u;
            }
        }
        return null;
    }

    public void cancelledReport(PointOfSale pos) {
    	try {
            Path folderPath = Paths.get(System.getProperty("user.dir"), "owner_reports");
            new File(folderPath.toString()).mkdirs();

            FileWriter writer = new FileWriter("owner_reports/cancelled_orders_report.csv");

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            writer.append("Date,Time,Username,Cancel Reason\n");
    		
            List<CancelledTransaction> ct_list = pos.getCancelledTransactions();
    		for (CancelledTransaction ct : ct_list) {
    			writer.append(ct.date.format(dateFormatter) + "," + ct.date.format(timeFormatter)
    			+ "," + ct.username + "," + ct.cancelReason + "\n");
    		}

            writer.flush();
            writer.close();

            System.out.println("Created report.");

        } catch (IOException e) {
            System.out.println("Error writing report.");
            e.printStackTrace();
        }
    }
    
    public void generateUserReport() {
    	try {
            Path folderPath = Paths.get(System.getProperty("user.dir"), "owner_reports");
            new File(folderPath.toString()).mkdirs();

            FileWriter writer = new FileWriter("owner_reports/user_report.csv");

            writer.append("Username,Role\n");
    		
            for (User user : users) {
                if (user.username.equalsIgnoreCase("#anonymous")) {
                    continue;
                }
            
                writer.append(user.username + "," + user.role + "\n");
            }

            writer.flush();
            writer.close();

            System.out.println("Created report.");

        } catch (IOException e) {
            System.out.println("Error writing report.");
            e.printStackTrace();
        }
    }

    public void printUsers() {
        int count = 1;
        for (User user : users) {
            System.out.println(count + ". username: " + user.username + " | role: " + user.role);
            count++;
        }
    }
}


