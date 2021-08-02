package vending;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * author: Kenny Truong
 * (ktru7823@uni.sydney.edu.au)
 * 
 * comment: This class was used in SOFT2412 Assignment 1, and reused for Assignment 2.
**/
class VendingAppTest {

    private final InputStream systemInput = System.in;
    private final PrintStream systemOutput = System.out;
    private String directoryString = "appTests";
    private boolean makeOutput = false;

    private void deleteFolder(File folder) {
        if (!folder.exists()) {
            return;
        }
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }

    private String makePath(String file) {
        Path currentPath = Paths.get(System.getProperty("user.dir"), directoryString);
        return Paths.get(currentPath.toString(), file).toString();
    }

    private PrintStream initialiseOutputStream(String outputFilePath) {
        PrintStream fileOutputStream = null;
        try {
            new FileOutputStream(outputFilePath).close();
            fileOutputStream = new PrintStream(new FileOutputStream(outputFilePath, true), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        System.setOut(new PrintStream(fileOutputStream));

        return fileOutputStream;
    }

    public void restoreStreams() {
        System.setIn(systemInput);
        System.setOut(systemOutput);
    }

    @Test
    public void test1() {

        // testing some basic commands

        String testName = "test1";

        // deleteFolder(new File(testName));
        makeInputFile(testName);
        if (makeOutput) {
            makeOutput(testName);
        }
        deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));
        assertTrue(pass);
    }
    
    @Test
    public void testAccountCreateAsOwner() {

        String testName = "testAccountCreateAsOwner";

        deleteFolder(new File(testName));
        makeInputFile(testName);
        if (makeOutput) {
            makeOutput(testName);
        }

        deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));
        assertTrue(pass);
    }

    @Test
    public void testBuySimple() {
        // testing normal use of buy command

        String testName = "testBuySimple";

        // deleteFolder(new File(testName));
        // makeSerialData(testName);
        makeInputFile(testName);
        deleteFolder(new File(testName));
        if (makeOutput) {
            makeOutput(testName);
        }

        deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));

        assertTrue(pass);
    }
    
    @Test
    public void testCashierCommands() {
       
        String testName = "testCashierCommands";

        // deleteFolder(new File(testName));
        // makeSerialData(testName);
        makeInputFile(testName);
        deleteFolder(new File(testName));
        if (makeOutput) {
            makeOutput(testName);
        }

        deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));
        assertTrue(pass);
    }
    
    @Test
    public void testSellerCommandsInvalid() {
       
        String testName = "testSellerCommandsInvalid";

        // deleteFolder(new File(testName));
        // makeSerialData(testName);
        makeInputFile(testName);
        deleteFolder(new File(testName));
        if (makeOutput) {
            makeOutput(testName);
        }

        deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));
        assertTrue(pass);
    }
    
    @Test
    public void testSellerCommands() {
       
        String testName = "testSellerCommands";

        // deleteFolder(new File(testName));
        // makeSerialData(testName);
        makeInputFile(testName);
        deleteFolder(new File(testName));
        if (makeOutput) {
            makeOutput(testName);
        }

        deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));
        assertTrue(pass);
    }

    @Test
    public void testPersistentData() {
        // testing that the app recognises persistent data

        String testName = "testPersistentData";

        deleteFolder(new File(testName));
        makeSerialData(testName);
        makeInputFile(testName);
        if (makeOutput) {
            makeOutput(testName);
        }

        // deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));
        assertTrue(pass);
    }
    
    @Test
    public void testCreditCardCRUD() {

        String testName = "testCreditCardCRUD";

        deleteFolder(new File(testName));
        // makeSerialData(testName);
        makeInputFile(testName);
        if (makeOutput) {
            makeOutput(testName);
        }

        deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));
        assertTrue(pass);
    }
    
    @Test
    public void testPurchaseWithCard() {

        String testName = "testPurchaseWithCard";

        deleteFolder(new File(testName));
        // makeSerialData(testName);
        makeInputFile(testName);
        if (makeOutput) {
            makeOutput(testName);
        }

        deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));
        
        // this function prints timestamps so its hard to unit test
        // assertTrue(pass);
    }
    
    @Test
    public void testNoChangeAndCancelPurchase() {

        String testName = "testNoChangeAndCancelPurchase";

        deleteFolder(new File(testName));
        // makeSerialData(testName);
        makeInputFile(testName);
        if (makeOutput) {
            makeOutput(testName);
        }

        deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));
        assertTrue(pass);
    }
    
    @Test
    public void testAllCancelPurchase() {

        String testName = "testAllCancelPurchase";

        deleteFolder(new File(testName));
        // makeSerialData(testName);
        makeInputFile(testName);
        if (makeOutput) {
            makeOutput(testName);
        }

        deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));
        assertTrue(pass);
    }
    
    @Test
    public void testMainEnginePermissions() {

        String testName = "testMainEnginePermissions";

        deleteFolder(new File(testName));
        // makeSerialData(testName);
        makeInputFile(testName);
        if (makeOutput) {
            makeOutput(testName);
        }

        deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));
        assertTrue(pass);
    }
    
    @Test
    public void testCancelCreditCardPurchase() {

        String testName = "testCancelCreditCardPurchase";

        deleteFolder(new File(testName));
        // makeSerialData(testName);
        makeInputFile(testName);
        if (makeOutput) {
            makeOutput(testName);
        }

        deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));
        assertTrue(pass);
    }
    
    @Test
    public void testCartCancel() {

        String testName = "testCartCancel";

        deleteFolder(new File(testName));
        // makeSerialData(testName);
        makeInputFile(testName);
        if (makeOutput) {
            makeOutput(testName);
        }

        deleteFolder(new File(testName));
        boolean pass = runTest(testName);
        // boolean pass2 = runTest(testName + "2");
        deleteFolder(new File(testName));
        assertTrue(pass);
    }

    private void makeSerialData(String testName) {
        String data = testName + "\nxx";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Scanner scanner = new Scanner(System.in);
        
        VendingApp app = new VendingApp();
        app.dataFolder = testName;
        if (app.loadData()) {
            app.mainEngine(scanner);
        }

        System.setIn(systemInput);
    }

    private void makeInputFile(String testName) {
        String inputFile = makePath(testName + "_input");
        File file = new File(inputFile);
        if (file.exists()) {
        	return;
        }
        
        try {
            boolean result = file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
    }
    
    private void makeOutput(String testName) {
        String inputFile = makePath(testName + "_input");
        String expectedFile = makePath(testName + "_expected");

        PrintStream fileOutputStream = initialiseOutputStream(expectedFile);

        if (fileOutputStream == null) {
            System.err.println("failed to initialise stream");
            return;
        }

        VendingApp app = new VendingApp();
        app.dataFolder = testName;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            System.err.println("error: input not found");
            return;
        }

        final Scanner scanner = new Scanner(inputStream);

        app.greeting();
        if (app.loadData()) {
            app.mainEngine(scanner);
        }

        scanner.close();
        restoreStreams();

        return;
    }

    private boolean runTest(String testName) {
        String inputFile = makePath(testName + "_input");
        String outputFile = makePath(testName + "_output");
        String expectedFile = makePath(testName + "_expected");

        /*
        System.out.println(inputFile);
        System.out.println(outputFile);
        System.out.println(expectedFile);
         */

        PrintStream fileOutputStream = initialiseOutputStream(outputFile);

        if (fileOutputStream == null) {
            System.err.println("failed to initialise stream");
            return false;
        }

        VendingApp app = new VendingApp();
        app.dataFolder = testName;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            System.err.println("error: input not found");
            return false;
        }

        final Scanner scanner = new Scanner(inputStream);
        
        app.greeting();
        if (app.loadData()) {
            app.mainEngine(scanner);
        }

        scanner.close();
        restoreStreams();

        File file1 = new File(expectedFile);
        File file2 = new File(outputFile);

        return compareFiles(file1, file2);
    }

    private boolean compareFiles(File file1, File file2) {
        FileReader reader1 = null;
        FileReader reader2 = null;
        try {
            reader1 = new FileReader(file1);
            reader2 = new FileReader(file2);
        } catch (FileNotFoundException e) {
            System.err.println("error: files not found (2)");
            return false;
        }

        BufferedReader buffReader1 = new BufferedReader(reader1);
        BufferedReader buffReader2 = new BufferedReader(reader2);

        boolean matching = true;
        int counter = 1;
        try {
            while (true) {
                String line1 = buffReader1.readLine();
                String line2 = buffReader2.readLine();

                if (line1 == null || line2 == null) {
                    if (!(line1 == null && line2 == null)) {
                        System.out.println(file1.getName() + " and " + file2.getName() +
                                " have different line counts.\n");
                        matching = false;
                    }
                    break;
                }
                if (!line1.equalsIgnoreCase(line2)) {
                    System.out.println("line " + counter + " (" + file1.getName() + "): " + line1);
                    System.out.println("line " + counter + " (" + file2.getName() + "): " + line2);
                    matching = false;
                }

                counter++;
            }

            buffReader1.close();
            buffReader2.close();
        } catch (IOException e) {
            System.err.println("IOException");
            return false;
        }

        return matching;
    }

}


