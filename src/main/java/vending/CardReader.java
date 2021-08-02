package vending;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CardReader {

	String dataFolder;
    private List<CreditCard> cardList;

    public CardReader() {
    	dataFolder = "data";
        loadCardList();
    }
    
    public CardReader(String dataFolder) {
    	this.dataFolder = dataFolder;
        loadCardList();
    }
    
    void listCards() {
    	String paddedName = 
        		String.format("%1$" + 11 + "s", "Name");
    	System.out.println(paddedName + " | " + "Card Number");
    	System.out.println("---------------------------");
        for (CreditCard card : cardList) {
            System.out.println(card);
        }
    }

    List<CreditCard> getCardList() {
        return cardList;
    }
    
    CreditCard getCard(String number) {
    	for (int i = 0; i < cardList.size(); i++) {
            CreditCard c = cardList.get(i);
            if (c.number.equals(number)) {
                return c;
            }
        }
    	
    	return null;
    }
    
    boolean cardListContains(String number) {
        for (int i = 0; i < cardList.size(); i++) {
            CreditCard c = cardList.get(i);
            if (c.number.equals(number)) {
                return true;
            }
        }
        
        return false;
    }
    
    boolean addCardToList(String name, String number) {
    	if (!cardListContains(number)) {
    		cardList.add(new CreditCard(name, number));
        	return saveCardList();
    	}
    	
    	return false;
    }
    
    boolean removeCardFromList(String number) {
        for (int i = 0; i < cardList.size(); i++) {
            CreditCard card = cardList.get(i);
            if (card.number.equals(number)) {
                cardList.remove(i);
                System.out.println("Removed " + card.name + "'s card (" + card.number + ")");
                return saveCardList();
            }
        }
        
        System.out.println("Card number not found in database. No cards were removed.");
        return false;
    }
    
    boolean loadCardList() {
    
        // this only should be used once when the app starts
        if (cardList != null) {
            return false;
        }
        
        Gson gson = new Gson();
        
        try {
        	Path folderPath = Paths.get(System.getProperty("user.dir"), dataFolder);
            String filePath = Paths.get(folderPath.toString(), "credit_cards.json").toString();
        	
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            CreditCard[] cardArray = gson.fromJson(br, CreditCard[].class);
            cardList = new ArrayList<>(Arrays.asList(cardArray));
            br.close();
        } catch (FileNotFoundException e) {
            // System.out.println("Creating credit card file...");
            return createCreditCardFile();
        } catch (IOException e) {
            System.out.println("error reading credit card file");
            return false;
        }
        
        return true;
    }
    
    boolean saveCardList() {
        if (cardList == null) {
            return false;
        }
        
        try {
        	Path folderPath = Paths.get(System.getProperty("user.dir"), dataFolder);
        	new File(folderPath.toString()).mkdirs();
        	String filePath = Paths.get(folderPath.toString(), "credit_cards.json").toString();
        	
            Gson gson = new Gson();
            String content = gson.toJson(cardList);
            FileWriter fw = new FileWriter(filePath);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            System.out.println("error updating credit card file");
            return false;
        }
        
        return true;
    }
    
    boolean createCreditCardFile() {
    	cardList = new ArrayList<>();
    	String[] defaultList = {
    		"Charles,40691", 
    		"Sergio,42689",
    		"Kasey,60146",
    		"Vincent,59141", 
    		"Ruth,55134",
    		"Donald,23858", 
    		"Christine,35717", 
    		"Helene,72500",
    		"Brian,44756", 
    		"Wanda,97523", 
    		"Elaine,48685", 
    		"Blake,14138", 
    		"Debbie,92090", 
    		"Felix,31093", 
    		"John,90669", 
    		"Deena,95953", 
    		"Joan,77852", 
    		"Kenneth,60632", 
    		"Audrey,45925", 
    		"Francisco,27402", 
    		"Christopher,28376", 
    		"Manuel,53477", 
    		"Mark,66192", 
    		"William,67707", 
    		"Rebecca,54981", 
    		"Arthur,41696", 
    		"Robert,85202", 
    		"Christopher,87286", 
    		"Edwin,23842", 
    		"Stacey,26436", 
    		"Michael,24531", 
    		"Janet,69655", 
    		"Jeremy,74061", 
    		"Patricia,30690", 
    		"Julie,56907", 
    		"Linda,38409", 
    		"Evelyn,64820", 
    		"Liana,75183", 
    		"Simone,89037", 
    		"Jeffrey,98708", 
    		"Elizabeth,96667", 
    		"Andy,82050", 
    		"Chad,34572", 
    		"James,33527", 
    		"Ruby,78073", 
    		"Naomi,43114", 
    		"James,20565", 
    		"Leonard,72238", 
    		"Marguerite,30831", 
    		"Maxine,34402"
    	};
    	for (String s : defaultList) {
    		cardList.add(new CreditCard(s));
    	}
    	return saveCardList();
    }
}

class CreditCard implements java.io.Serializable {
    String name;
    String number;

    public CreditCard(String name, String number) {
        this.name = name;
        this.number = number;
    }
    
    public CreditCard(String nameAndNumber) {	
        this.name = nameAndNumber.split(",")[0];
        this.number = nameAndNumber.split(",")[1];
    }

    @Override
    public String toString() {
        String paddedName = 
        		String.format("%1$" + 11 + "s", this.name);
        return paddedName + " | " + this.number;
        
    }
}




