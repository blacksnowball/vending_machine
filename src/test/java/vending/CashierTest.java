package vending;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


public class CashierTest {
    Cashier cashier;
    PointOfSale pos;
    VendingApp app;

    @BeforeEach
    public void setup() {
        pos = new PointOfSale();
        cashier = new Cashier("Michael", "Kirby");
        app = new VendingApp();
        app.dataFolder = "CashierTest";
        app.initialise();
    }


    @Test
    public void testChangeReport() {
        // check content of the report compared to what is saved in the system
        cashier.changeReport("CashierTest");

        // load the csv file
        List<List<String>> records = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("cashier_reports/change_report.csv"));
            String line;
            while((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // all quantities should be 5 since we are starting with new data
        for(List<String> values : records) {
            if(values.get(0).equals("Note")) {
                // this is the header, skip it
                continue;
            } else {
                assertEquals(values.get(1), "5");
            }
        }

        // check that all the notes exist
        assertEquals(records.get(1).get(0), "$100.00");
        assertEquals(records.get(2).get(0), "$50.00");
        assertEquals(records.get(3).get(0), "$20.00");
        assertEquals(records.get(4).get(0), "$10.00");
        assertEquals(records.get(5).get(0), "$5.00");
        assertEquals(records.get(6).get(0), "$2.00");
        assertEquals(records.get(7).get(0), "$1.00");
        assertEquals(records.get(8).get(0), "$0.50");
        assertEquals(records.get(9).get(0), "$0.20");
        assertEquals(records.get(10).get(0), "$0.10");
        assertEquals(records.get(11).get(0), "$0.05");

        // check header
        assertEquals(records.get(0).get(0), "Note");
        assertEquals(records.get(0).get(1), "Quantity");

    }

    @Test
    public void testTransactionReport() {
        // create the transaction report
        cashier.transactionReport("CashierTest");

        // check that it doesnt contain data because there are not transactions yet
        // load the csv file
        List<List<String>> records = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("cashier_reports/transactions_report.csv"));
            String line;
            while((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(records.size(), 1); // only one line for the header

    }

}
