package vending;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class VendingApp {
	final String[] userRoles = {"customer", "owner", "cashier", "seller"};
	String dataFolder = "data";
	Console console = System.console();
	User currentUser = null;
	User anonymousUser = null;
	List<String> validNotes = null;
	List<Integer> noteValue = new ArrayList<>(Arrays.asList(
			10000, 5000, 2000, 1000, 500, 200, 100, 50, 20, 10, 5));

	CardReader cardReader = null;
	List<User> allUsers = null;
	PointOfSale pos = null;
	ProductInventory inventory = null;

	void listProductsCommand() {

		for (ProductCategory pc : inventory.getInStock()) {

			System.out.println(pc.getName().toUpperCase());

			for (Map.Entry<Product, Integer> entry : pc.getProducts().entrySet()) {

				Product product = entry.getKey();
				Integer qty = entry.getValue();

				DecimalFormat df = new DecimalFormat("#.00");
				String price = df.format(product.getPrice());
				int length = 13;
				String paddedProduct = 
						String.format("%1$" + length + "s", product.getName());
				System.out.println(paddedProduct + " | $" + price + " | " + qty +  " available");


			}
			System.out.println();

		}
	}

	boolean setAnonymous() {
		if (allUsers == null) {
			return false;
		}

		for (User u : allUsers) {
			if (u.getUsername().equals("#anonymous")) {
				anonymousUser = u;
				return true;
			}
		}

		anonymousUser = new Customer("#anonymous", "");
		allUsers.add(anonymousUser);
		return false;
	}

	boolean confirmLogout(Scanner scanner) {
		if (currentUser != anonymousUser) {
			System.out.println("You are currently logged into another account.");
			System.out.println("Would you like to logout first (y/n)?");
			System.out.print(">> ");
			String response = null;

			if (scanner.hasNextLine()) {
				response = scanner.nextLine();
			}

			if (response == null) {
				response = "n";
			}

			if (response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes")) {
				System.out.println("Logging out...\n");
				setCurrentUserAsAnonymous();
				return true;
			} else if (response.equalsIgnoreCase("n") || response.equalsIgnoreCase("no")) {
				System.out.println("Returning to main menu.");
				return false;
			} else {
				System.out.println("Invalid response.");
				System.out.println("Returning to main menu.");
				return false;
			}
		}

		return true;
	}

	void removeCardCommand(Scanner scanner) {

		System.out.println("Removing card");

		System.out.print("Card number to remove: ");
		String cardNumber = null;
		if (scanner.hasNextLine()) {
			cardNumber = scanner.nextLine();
		} else {
			return;
		}

		if (!cardNumber.matches("^[0-9]{5}$")) {
			System.out.println("Invalid card number. (must be 5 digits)");
			return;
		}

		cardReader.removeCardFromList(cardNumber);
	}

	void modifyCardsCommand(Scanner scanner) {

		while (true) {
			System.out.println();

			System.out.println("Select function:");
			System.out.println("[1] Add card");
			System.out.println("[2] Remove card");
			System.out.println("[3] Modify existing card");
			System.out.println("[4] List all cards");
			System.out.println("[5] Return to main menu");
			System.out.println();
			System.out.print(">> ");

			String input = null;
			if (scanner.hasNextLine()) {
				input = scanner.nextLine();
			} else {
				return;
			}

			switch (input) {
			case "1":
				addCardCommand(scanner);
				break;
			case "2":
				removeCardCommand(scanner);
				break;
			case "3":
				System.out.println("Modifying card");

				System.out.print("Select a card number: ");
				String cardNumber = null;
				if (scanner.hasNextLine()) {
					cardNumber = scanner.nextLine();
				} else {
					break;
				}

				if (!cardNumber.matches("^[0-9]{5}$")) {
					System.out.println("Invalid card number. (must be 5 digits)");
					break;
				}

				CreditCard selectedCard = cardReader.getCard(cardNumber);
				if (selectedCard == null) {
					System.out.println("Card number does not exist.");
					break;
				}
				
				boolean updated = false;
				System.out.println("\nModifying " + selectedCard.name + "'s card ("
						+ selectedCard.number + ")");
				System.out.print("New owner (leave blank to keep unchanged): ");
				String newOwner = null;
				if (scanner.hasNextLine()) {
					newOwner = scanner.nextLine();
				} else {
					break;
				}

				if (!(newOwner.equals(""))) {
					selectedCard.name = newOwner;
					System.out.println("Cardholder name updated.");
					updated = true;
				}

				System.out.print("New card number (5 digits) (leave blank to keep unchanged): ");
				cardNumber = null;
				if (scanner.hasNextLine()) {
					cardNumber = scanner.nextLine();
				} else {
					break;
				}

				if (!(cardNumber.equals(""))) {
					if (!cardNumber.matches("^[0-9]{5}$")) {
						System.out.println("Invalid card number. (must be 5 digits)");
					} else {
						selectedCard.number = cardNumber;
						System.out.println("Card number updated.");
						updated = true;
					}
				}
				
				if (!updated) {
					System.out.println("Card was not updated.");
				}

				break;
			case "4":
				cardReader.listCards();
				break;
			case "5":
				return;
			default:
				System.out.println("Invalid input.");
				break;
			}
			saveData();
		}

	}

	void listCardsCommand() {
		cardReader.listCards();
	}

	void addCardCommand(Scanner scanner) {

		System.out.println("Adding card");

		System.out.print("New card number (5 digits): ");
		String cardNumber = null;
		if (scanner.hasNextLine()) {
			cardNumber = scanner.nextLine();
		} else {
			return;
		}

		if (!cardNumber.matches("^[0-9]{5}$")) {
			System.out.println("Invalid card number. (must be 5 digits)");
			return;
		}

		if (cardReader.cardListContains(cardNumber)) {
			System.out.println("Card number already exists.");
			return;
		}

		System.out.print("Name of new card owner: ");
		String owner = null;
		if (scanner.hasNextLine()) {
			owner = scanner.nextLine();
		} else {
			return;
		}

		if (owner.equals("")) {
			System.out.println("No name provided. Aborting.");
			return;
		}

		if (cardReader.addCardToList(owner, cardNumber)) {
			System.out.println("Card successfully added.");
		} else {
			System.out.println("An error occurred when adding the new card.");
		}
	}

	boolean removeUserCommand(Scanner scanner) {

		System.out.print("Username to remove: ");
		String username = null;
		if (scanner.hasNextLine()) {
			username = scanner.nextLine();
		} else {
			// keyboard interrupt
			return false;
		}

		if (currentUser.getUsername().equals(username)) {
			System.out.println("You are currently logged into this account. Aborting.");
			return false;
		}

		// check that the username does exist
		for (int i = 0; i < allUsers.size(); i++) {
			User u = allUsers.get(i);
			if (u.getUsername().equals(username)) {
				allUsers.remove(i);
				System.out.println("Account removed.");
				return true;
			}
		}

		System.out.println("Username does not exist.");
		return false;
	}

	boolean addUserCommand(Scanner scanner) {

		System.out.print("New user type: ");
		String userType = null;
		if (scanner.hasNextLine()) {
			userType = scanner.nextLine().toLowerCase();
		} else {
			// keyboard interrupt
			return false;
		}

		boolean validType = false;
		for (String role : userRoles) {
			if (role.equals(userType)) {
				validType = true;
				break;
			}
		}

		if (!validType) {
			System.out.println("Invalid role. Returning to menu.");
			return false;
		}

		// should it be case sensitive?
		String username = null;
		System.out.println("Enter new username (letters and numbers only)");
		System.out.print("Username: ");
		if (scanner.hasNextLine()) {
			username = scanner.nextLine();
			if (!username.matches("[a-zA-Z0-9]+")) {
				System.out.println("Invalid username (contains spaces or non-alphanumeric characters).");
				System.out.println("Returning to menu.");
				return false;
			}
		} else {
			// keyboard interrupt
			return false;
		}

		// check that the username does not exist
		for (int i = 0; i < allUsers.size(); i++) {
			User u = allUsers.get(i);
			if (u.getUsername().equalsIgnoreCase(username)) {
				System.out.println("Username already exists.");
				return false;
			}
		}

		System.out.print("Password: ");
		String password = null;
		if (console != null) {
			char[] pass = console.readPassword();
			password = new String(pass);
		} else {
			if (scanner.hasNextLine()) {
				password = scanner.nextLine();
			} else {
				// keyboard interrupt
				return false;
			}
		}

		User user;
		switch (userType) {
		case "customer":
			user = new Customer(username, password);
			break;
		case "cashier":
			user = new Cashier(username, password);
			break;
		case "owner":
			user = new Owner(username, password, allUsers);
			break;
		case "seller":
			user = new Seller(username, password);
			break;
		default:
			System.out.println("Invalid role.");
			return false;
		}
		allUsers.add(user);
		System.out.println("Successfully added account.");
		return true;
	}

	private void setCurrentUserAsAnonymous() {
		if (anonymousUser != null) {
			currentUser = anonymousUser;
		} else {
			// failed to find an anonymous user
			System.out.println("Creating anonymous user...");
			anonymousUser = new Customer("#anonymous", "");
			currentUser = anonymousUser;
		}
	}

	private boolean firstScreen(Scanner scanner) {
		System.out.println();

		System.out.println("Main menu");
		System.out.println("Select an option:");
		System.out.println("[1] Login as existing user");
		System.out.println("[2] Create new account as customer");
		System.out.println("[3] Continue as anonymous user");
		System.out.println("[4] Close the vending machine");
		System.out.println();
		System.out.print(">> ");

		String input = null;
		if (scanner.hasNextLine()) {
			input = scanner.nextLine();
		} else {
			return false;
		}

		switch (input) {
		case "1":
			loginScreen(scanner);
			break;
		case "2":
			createAccountAsCustomer(scanner);
			break;
		case "3":
			break;
		case "4":
			return false;
		default:
			System.out.println("Invalid input.");
			return false;
		}
		
		return true;
	}

	private void loginScreen(Scanner scanner) {

		String username = null;
		String password = null;
		boolean valid = false;

		if (!confirmLogout(scanner)) {
			return;
		}

		while (!valid) {
			System.out.println("Login menu");
			System.out.println("Enter username and password (case sensitive)");
			System.out.print("Username: ");
			if (scanner.hasNextLine()) {
				username = scanner.nextLine();
			}

			if (username.equals("#anonymous")) {
				System.out.println("Continuing as anonymous user...");
				setCurrentUserAsAnonymous();
				return;
			}

			System.out.print("Password: ");

			// this hides the password (doesnt run with gradle)
			if (console != null) {
				char[] pass = console.readPassword();
				password = new String(pass);
			} else {
				if (scanner.hasNextLine()) {
					password = scanner.nextLine();
				}
			}

			valid = validateLogin(username, password);
			if (!valid) {
				System.out.println("\nDetails do not match the credentials in the system." +
						"\nWould you like to try again?");
				System.out.print(">> ");
				String response = null;

				if (scanner.hasNextLine()) {
					response = scanner.nextLine().toLowerCase();
				}

				System.out.println();

				if (response == null) {
					response = "n";
				}

				if (response.equals("y") || response.equals("yes")) {
					continue;
				} else if (response.equals("n") || response.equals("no")) {
					System.out.println("Continuing as anonymous user...");
					setCurrentUserAsAnonymous();
					return;
				} else {
					System.out.println("Invalid response.");
					System.out.println("Continuing as anonymous user...");
					setCurrentUserAsAnonymous();
					return;
				}
			}
		}

		System.out.println("\nLogin successful.");
		recentPurchases();
	}

	private boolean validateLogin(String username, String password) {
		// set current role of account
		// return true if credentials match any account

		if (username == null || password == null) {
			return false;
		}

		for (User u : allUsers) {
			if (u.getUsername().equalsIgnoreCase(username) && u.getPassword().equals(password)) {
				// sets current user and returns true
				currentUser = u;
				return true;
			}
		}


		return false;
	}

	private boolean createAccountAsCustomer(Scanner scanner) {
		String username = null;
		String password = null;

		System.out.println("\nCreating Account");

		System.out.println("Enter new username (letters and numbers only)");
		System.out.print("Username: ");
		if (scanner.hasNextLine()) {
			// username not case sensitive
			username = scanner.nextLine();
			if (!username.matches("[a-zA-Z0-9]+")) {
				System.out.println("Invalid username (contains spaces or non-alphanumeric characters).");
				System.out.println("Returning to menu.");
				return false;
			}
		} else {
			// keyboard interrupt
			return false;
		}

		// check that the username does not already exist
		for (User u : allUsers) {
			if (u.getUsername().equalsIgnoreCase(username)) {
				System.out.println("Username already exists.\nReturning to main menu.");
				return false;
			}
		}

		System.out.print("Password: ");

		if (console != null) {
			char[] pass = console.readPassword();
			password = new String(pass);
		} else {
			if (scanner.hasNextLine()) {
				password = scanner.nextLine();
			} else {
				// keyboard interrupt
				return false;
			}
		}

		if (password == null) {
			System.out.println("Account was not created. Returning to main menu.");
			return false;
		}

		Customer c = new Customer(username, password);
		currentUser = c;
		allUsers.add(c);
		System.out.println("Account created successfully.\nYou have been logged in.");

		return true;
	}

	private void showInventoryCommand() {

		System.out.println();

		// TODO: show quantities sold
		List<ProductCategory> stock = inventory.getInStock();
		System.out.println("Item Code, Category, Item Name, Price, Quantity in Stock");
		for (ProductCategory category : stock) {
			String catString = category.getName();
			LinkedHashMap<Product, Integer> productMap = category.getProducts();
			Set<Product> productSet = productMap.keySet();
			for (Product p : productSet) {
				System.out.println(p.getCode() + ", " + catString + ", "
						+ p.getName() + ", $" + String.format("%.2f", p.getPrice())
						+ ", " + category.getProducts().get(p));
			}
		}
	}

	private void addProductCommand(Scanner scanner) {
		System.out.println("Adding product to database");
		System.out.print("Code of new product: ");
		String input = null;
		if (scanner.hasNextLine()) {
			input = scanner.nextLine();
		} else {
			return;
		}

		int code = -1;
		try {
			code = Integer.parseInt(input);
		} catch (NumberFormatException e) {

		}

		if (code < 0) {
			System.out.println("Invalid code.");
			System.out.println("Returning to function menu.");
			return;
		}

		if (inventory.getProductWithCode(code) != null) {
			System.out.println("A product with this code already exists.");
			System.out.println("Returning to function menu.");
			return;
		}

		System.out.print("Category of new product: ");
		String category = null;
		if (scanner.hasNextLine()) {
			category = scanner.nextLine();
		} else {
			return;
		}

		ProductCategory selectedCategory = inventory.getProductCategory(category);
		if (selectedCategory == null) {
			System.out.println("Category does not exist in the system.");
			System.out.println("Returning to function menu.");
			return;
		}

		System.out.print("Name of new product: ");
		String name = null;
		if (scanner.hasNextLine()) {
			name = scanner.nextLine();
		} else {
			return;
		}

		if (inventory.getProductWithName(name) != null) {
			System.out.println("A product with this name already exists.");
			System.out.println("Returning to function menu.");
			return;
		}

		System.out.print("Price of new product: $");
		input = null;
		if (scanner.hasNextLine()) {
			input = scanner.nextLine();
		} else {
			return;
		}

		String priceRegex = "([0-9]+[.]{1}[0-9]{2})|([0-9]+)";
		if (!input.matches(priceRegex)) {
			System.out.println("Invalid price.");
			System.out.println("Returning to function menu.");
			return;
		}

		double price = -1.0;
		try {
			price = Double.parseDouble(input);
		} catch (NumberFormatException e) {

		}

		if (price < 0.01) {
			System.out.println("Invalid price. (must be positive)");
			System.out.println("Returning to function menu.");
			return;
		}

		if (input.contains(".")) {
			try {
				String[] dollarCents = input.split("\\.");
				int cents = Integer.parseInt(dollarCents[1]);
				if (cents >= 100) {
					System.out.println("Invalid price.");
					System.out.println("Returning to function menu.");
					return;
				}
				
				if (cents % 5 != 0) {
					System.out.println("Invalid price (must be payable in coins).");
					System.out.println("Returning to function menu.");
					return;
				}
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Invalid price.");
				System.out.println("Returning to function menu.");
				return;
			} catch (NumberFormatException e) {
				System.out.println("Invalid price.");
				System.out.println("Returning to function menu.");
				return;
			}
		}

		System.out.print("Quantity of new product: ");
		input = null;
		if (scanner.hasNextLine()) {
			input = scanner.nextLine();
		} else {
			return;
		}

		int quantity = -1;
		try {
			quantity = Integer.parseInt(input);
		} catch (NumberFormatException e) {

		}

		if (quantity < 0 || quantity > 15) {
			System.out.println("Invalid quantity (must be a whole number between 0 and 15).");
			System.out.println("Returning to function menu.");
			return;
		}

		Product newProduct = new Product(selectedCategory, name, code, price);
		LinkedHashMap<Product, Integer> productList = selectedCategory.getProducts();
		productList.put(newProduct, quantity);
		System.out.println("Product created successfully.");

	}

	private void removeProductCommand(Scanner scanner) {
		System.out.println("Removing product from database");
		System.out.println("(note: this will also remove all stock of the product)");
		System.out.print("Code of product to remove: ");
		String input = null;
		if (scanner.hasNextLine()) {
			input = scanner.nextLine();
		} else {
			return;
		}

		int codeRemove = -1;
		try {
			codeRemove = Integer.parseInt(input);
		} catch (NumberFormatException e) {

		}

		Product productRemove = inventory.getProductWithCode(codeRemove);
		if (productRemove == null) {
			System.out.println("There is no product with this code.");
			System.out.println("Returning to function menu.");
			return;
		}

		ProductCategory pc = productRemove.getType();
		LinkedHashMap<Product, Integer> pcList = pc.getProducts();
		Integer success = pcList.remove(productRemove);
		if (success == null) {
			System.out.println("Product data removed.");
		} else {
			System.out.println("Product code " + productRemove.getCode()
								+ " ("+ productRemove.getName() +") removed successfully.");
		}
	}

	private void modifyProduct(Scanner scanner) {
		System.out.println("Modifying product in database");
		System.out.print("Code of product to modify: ");
		String input = null;
		if (scanner.hasNextLine()) {
			input = scanner.nextLine();
		} else {
			return;
		}

		int codeModify = -1;
		try {
			codeModify = Integer.parseInt(input);
		} catch (NumberFormatException e) {

		}

		Product p = inventory.getProductWithCode(codeModify);
		if (p == null) {
			System.out.println("There is no product with this code.");
			System.out.println("Returning to function menu.");
			return;
		}

		while (true) {

			ProductCategory pCategory = p.getType();
			Integer quantity = pCategory.getProducts().get(p);
			
			System.out.println();
			System.out.println("Selected product");
			System.out.println("Item code: " + p.getCode());
			System.out.println("Category: " + pCategory.getName());
			System.out.println("Item name: " + p.getName());
			System.out.println("Price: $" + String.format("%.2f", p.getPrice()));
			System.out.println("Quantity in stock: " + quantity);

			System.out.println();
			System.out.println("Select property to modify:");
			System.out.println("[1] Item code");
			System.out.println("[2] Category");
			System.out.println("[3] Item name");
			System.out.println("[4] Price");
			System.out.println("[5] Quantity in stock");
			System.out.println("[6] Return to function menu");
			System.out.println();
			System.out.print(">> ");

			input = null;
			if (scanner.hasNextLine()) {
				input = scanner.nextLine();
			} else {
				return;
			}

			System.out.println();
			switch (input) {
			case "1":
				System.out.print("New code: ");
				input = null;
				if (scanner.hasNextLine()) {
					input = scanner.nextLine();
				} else {
					return;
				}

				int newCode = -1;
				try {
					newCode = Integer.parseInt(input);
				} catch (NumberFormatException e) {

				}
				
				if (newCode == p.getCode()) {
					System.out.println("New item code is the same as the current code.");
					break;
				}

				if (newCode < 0) {
					System.out.println("Invalid code.");
					break;
				}

				if (inventory.getProductWithCode(newCode) != null) {
					System.out.println("A product with this code already exists.");
					System.out.println("The product was not updated.");
					break;
				}

				p.setCode(newCode);
				System.out.println("Product updated successfully.");
				break;
			case "2":
				System.out.print("New category: ");
				String category = null;
				if (scanner.hasNextLine()) {
					category = scanner.nextLine();
				} else {
					return;
				}

				ProductCategory pc = inventory.getProductCategory(category);
				if (pc == null) {
					System.out.println("Category does not exist in the system.");
					break;
				}
				
				((SellingBehaviour)currentUser).modifyItemCategory(inventory, p.getName(), category);
				
				System.out.println("Product updated successfully.");
				break;
			case "3":
				System.out.print("New name: ");
				String name = null;
				if (scanner.hasNextLine()) {
					name = scanner.nextLine();
				} else {
					return;
				}

				if (inventory.getProductWithName(name) != null) {
					System.out.println("A product with this name already exists.");
					System.out.println("The product was not updated.");
					break;
				}

				p.setName(name);
				System.out.println("Product updated successfully.");
				break;
			case "4":
				System.out.print("New price: $");
				input = null;
				if (scanner.hasNextLine()) {
					input = scanner.nextLine();
				} else {
					return;
				}

				String priceRegex = "([0-9]+[.]{1}[0-9]{2})|([0-9]+)";
				if (!input.matches(priceRegex)) {
					System.out.println("Invalid price.");
					break;
				}

				double price = -1.0;
				try {
					price = Double.parseDouble(input);
				} catch (NumberFormatException e) {

				}

				if (price < 0.01) {
					System.out.println("Invalid price. (must be positive)");
					break;
				}

				if (input.contains(".")) {
					try {
						String[] dollarCents = input.split("\\.");
						int cents = Integer.parseInt(dollarCents[1]);
						if (cents >= 100) {
							System.out.println("Invalid price.");
							break;
						}
						if (cents % 5 != 0) {
							System.out.println("Invalid price (must be payable in coins).");
							break;
						}
					} catch (IndexOutOfBoundsException e) {
						System.out.println("Invalid price.");
						break;
					} catch (NumberFormatException e) {
						System.out.println("Invalid price.");
						break;
					}
				}

				p.setPrice(price);
				System.out.println("Product updated successfully.");
				break;
			case "5":
				System.out.print("New quantity (max 15): ");
				input = null;
				if (scanner.hasNextLine()) {
					input = scanner.nextLine();
				} else {
					return;
				}

				int q = -1;
				try {
					q = Integer.parseInt(input);
				} catch (NumberFormatException e) {

				}

				if (q < 0 || q > 15) {
					System.out.println("Invalid quantity (must be a whole number between 0 and 15).");
					break;
				}

				p.getType().getProducts().put(p, q);
				System.out.println("Product updated successfully.");
				break;

			case "6":
				return;
			default:
				System.out.println("Invalid input.");
				break;
			}
			
			saveData();
		}

	}

	private void modifyInventoryCommand(Scanner scanner) {
		showInventoryCommand();

		while (true) {
			System.out.println();
			System.out.println("Modifying inventory data");
			System.out.println("Select function:");
			System.out.println("[1] Add new product to database");
			System.out.println("[2] Remove product from database");
			System.out.println("[3] Modify existing product (item code, name, stock levels, etc.)");
			System.out.println("[4] List products in database");
			System.out.println("[5] Return to main menu");
			System.out.println();
			System.out.print(">> ");

			String input = null;
			if (scanner.hasNextLine()) {
				input = scanner.nextLine();
			} else {
				return;
			}

			System.out.println();
			switch (input) {
			case "1":
				addProductCommand(scanner);
				break;
			case "2":
				removeProductCommand(scanner);
				break;
			case "3":
				modifyProduct(scanner);
				break;
			case "4":
				showInventoryCommand();
				break;
			case "5":
				return;
			default:
				System.out.println("Invalid input.");
				break;
			}
			
			saveData();
		}
	}

	private void recentPurchases() {
		if (currentUser instanceof Customer) {
			Customer c = (Customer) currentUser;
			if (c.getRecent().size() > 0) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				List<Transaction> recent = c.getRecent();
				System.out.println("Here are your most recent purchases: ");
				for (Transaction t : recent) {
					System.out.println(t.getDate().format(formatter) + ": ");
					HashMap<Product, Integer> products = t.getProducts();
					for(Product p : products.keySet()) {
						System.out.println("\t" + products.get(p) + " x " + p.getName());
					}
					System.out.println("---------------------");
				}
			}
		}
	}

	private Integer confirmInputInRange(String input, int min, int max) {
		try {
			int value = Integer.parseInt(input);
			if (value >= min && value <= max) {
				return value;
			}
		} catch (NumberFormatException e) {
			return null;
		}

		return null;
	}

	LinkedHashMap<Product, Integer> fillCart(Scanner scanner) {
		// TODO: timeout ends transaction

		LinkedHashMap<Product, Integer> cart = new LinkedHashMap<>();
		List<ProductCategory> stock = inventory.getInStock();

		boolean categoryLoop = true;
		while (categoryLoop) {
			boolean productLoop = true;
			boolean checkoutLoop = true;
			System.out.println("Category list:");

			for (int i = 0; i < stock.size(); i++) {
				ProductCategory category = stock.get(i);
				System.out.println("[" + (i + 1) + "] " + category.getName());
			}

			System.out.println();
			System.out.println("Select category number to buy from:");
			System.out.print(">> ");
			String input;
			if (scanner.hasNextLine()) {
				input = scanner.nextLine();
			} else {
				pos.cancelTransaction((Customer)currentUser, "user cancelled");
				return null;
			}

			if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("return")) {
				System.out.println("Cancelling transaction. Returning to main menu.\n");
				pos.cancelTransaction((Customer)currentUser, "user cancelled");
				return null;
			}

			if (input.equalsIgnoreCase("checkout")) {
				if (cart.isEmpty()) {
					System.out.println("Your cart is currently empty.\n");
					continue;
				} else {
					return cart;
				}
			}

			Integer categoryNumber = confirmInputInRange(input, 1, stock.size());
			if (categoryNumber == null) {
				System.out.println("Invalid input.\n");
				continue;
			}

			ProductCategory selectedCategory = stock.get(categoryNumber - 1);
			LinkedHashMap<Product, Integer> productMap = selectedCategory.getProducts();
			Set<Product> productSet = productMap.keySet();

			List<Product> productList = new ArrayList<>();
			for (Product p : productSet) {
				productList.add(p);
			}

			System.out.println();
			while (productLoop) {
				// list products in selected category
				System.out.println("Products in "
						+ selectedCategory.getName().toUpperCase() + " category");
				for (int i = 0; i < productList.size(); i++) {
					Product p = productList.get(i);
					int available = productMap.get(p);
					Integer quantityInCart = cart.get(p);
					if (quantityInCart != null) {
						available -= quantityInCart;
					}
					System.out.println("[" + (i + 1) + "] " + p.getName()
					+ " ($" + String.format("%.2f", p.getPrice()) + ")"
					+ " (" + available +" available)");
				}

				System.out.println();
				System.out.println("Select product number (or use 'RETURN' to go back to the category list)");
				System.out.print(">> ");
				if (scanner.hasNextLine()) {
					input = scanner.nextLine();
				} else {
					pos.cancelTransaction((Customer)currentUser, "user cancelled");
					return null;
				}

				if (input.equalsIgnoreCase("cancel")) {
					System.out.println("Cancelling transaction. Returning to main menu.\n");
					pos.cancelTransaction((Customer)currentUser, "user cancelled");
					return null;
				}

				if (input.equalsIgnoreCase("return")) {
					System.out.println("Returning to category list.\n");
					checkoutLoop = false;
					break;
				}

				if (input.equalsIgnoreCase("checkout")) {
					if (cart.isEmpty()) {
						System.out.println("Your cart is currently empty.\n");
						continue;
					} else {
						return cart;
					}
				}

				Integer productNumber = confirmInputInRange(input, 1, productList.size());
				if (productNumber == null) {
					System.out.println("Invalid input.\n");
					continue;
				}
				Product selectedProduct = productList.get(productNumber - 1);

				while (true) {
					int available = productMap.get(selectedProduct);
					Integer quantityInCart = cart.get(selectedProduct);
					if (quantityInCart != null) {
						available -= quantityInCart;
					}

					System.out.println("Selected: " + selectedProduct.getName()
					+ " (" + available + " available)");

					if (available <= 0) {
						System.out.println("There is no more of this item in stock.\n");
						break;
					}

					System.out.print("Specify quantity to buy: ");
					if (scanner.hasNextLine()) {
						input = scanner.nextLine();
					} else {
						pos.cancelTransaction((Customer)currentUser, "user cancelled");
						return null;
					}

					if (input.equalsIgnoreCase("cancel")) {
						System.out.println("Cancelling transaction. Returning to main menu.\n");
						pos.cancelTransaction((Customer)currentUser, "user cancelled");
						return null;
					}

					if (input.equalsIgnoreCase("return")) {
						System.out.println("Returning to product list.\n");
						break;
					}

					if (input.equalsIgnoreCase("checkout")) {
						if (cart.isEmpty()) {
							System.out.println("Your cart is currently empty.");
							System.out.println("Returning to product list.\n");
							break;
						} else {
							return cart;
						}
					}

					Integer quantityToBuy = confirmInputInRange(input, 0, Integer.MAX_VALUE);
					if (quantityToBuy == null) {
						System.out.println("Invalid input. Returning to product list.\n");
						break;
					}

					if (quantityToBuy == 0) {
						System.out.println("Returning to product list.\n");
						break;
					}

					if (quantityToBuy > available) {
						System.out.println("Insufficient stock to fulfil order.\n");
						continue;
					}

					if (quantityInCart == null) {
						cart.put(selectedProduct, quantityToBuy);
					} else {
						cart.put(selectedProduct, quantityInCart + quantityToBuy);
					}

					System.out.println();
					productLoop = false;
					break;
				}
			}

			while (checkoutLoop) {
				System.out.println("This is your order so far:");
				Set<Product> keySet = cart.keySet();
				for (Product p : keySet) {
					System.out.println(p.getName() + " x " + cart.get(p) + " = $"
							+ String.format("%.2f", cart.get(p) * p.getPrice()));
				}
				Integer moneyRequired = pos.calculateCost(cart);
				System.out.format("Total cost is: $%.2f\n", (double)moneyRequired/100.0); // shows the value in dollars
				System.out.println();

				System.out.println("Would you like to go to checkout?");
				System.out.println("Select one of the following options:");
				System.out.println("[1] Yes, take me to checkout");
				System.out.println("[2] Not yet, keep browsing items");
				System.out.println("[3] Not yet, remove some items from the order");
				System.out.println("[4] No, cancel my order");

				System.out.println();
				System.out.print(">> ");
				if (scanner.hasNextLine()) {
					input = scanner.nextLine();
				} else {
					System.out.println("Cancelling transaction. Returning to main menu.\n");
					pos.cancelTransaction((Customer)currentUser, "user cancelled");
					return null;
				}

				if (input.equalsIgnoreCase("cancel")) {
					System.out.println("Cancelling transaction. Returning to main menu.\n");
					pos.cancelTransaction((Customer)currentUser, "user cancelled");
					return null;
				}

				switch (input) {
				case "1":
					checkoutLoop = false;
					categoryLoop = false;
					break;
				case "2":
					checkoutLoop = false;
					categoryLoop = true;
					break;
				case "3":
					System.out.println();
					System.out.println("This is your order so far:");
					int counter = 1;
					List<Product> list = new ArrayList<>();
					for (Product p : keySet) {
						System.out.println("[" + counter + "] " + p.getName()
						+ " x " + cart.get(p) + " = $"
						+ String.format("%.2f", cart.get(p) * p.getPrice()));
						counter++;
						list.add(p);
					}
					System.out.format("Total cost is: $%.2f\n", (double)moneyRequired/100.0); // shows the value in dollars
					System.out.println();
					System.out.println("Select product (by number) to remove:");
					System.out.print(">> ");
					String response;
					if (scanner.hasNextLine()) {
						response = scanner.nextLine();
					} else {
						System.out.println("Cancelling transaction. Returning to main menu.\n");
						pos.cancelTransaction((Customer)currentUser, "user cancelled");
						return null;
					}

					if (response.equalsIgnoreCase("cancel")) {
						System.out.println("Cancelling transaction. Returning to main menu.\n");
						pos.cancelTransaction((Customer)currentUser, "user cancelled");
						return null;
					}

					Integer number = confirmInputInRange(response, 1, list.size());
					if (number == null) {
						System.out.println("Invalid input.\n");
						break;
					}

					Product selectedProduct = list.get(number - 1);
					Integer quantityInCart = cart.get(selectedProduct);
					System.out.println();
					while (true) {
						System.out.println("Selected: " + selectedProduct.getName()
						+ " (" + quantityInCart + " in cart)");
						System.out.println("Specify quantity to remove: ");
						System.out.print(">> ");
						if (scanner.hasNextLine()) {
							response = scanner.nextLine();
						} else {
							System.out.println("Cancelling transaction. Returning to main menu.\n");
							pos.cancelTransaction((Customer)currentUser, "user cancelled");
							return null;
						}

						if (response.equalsIgnoreCase("cancel")) {
							System.out.println("Cancelling transaction. Returning to main menu.\n");
							pos.cancelTransaction((Customer)currentUser, "user cancelled");
							return null;
						}

						Integer quantityToRemove = confirmInputInRange(response, 0, Integer.MAX_VALUE);
						if (quantityToRemove == null) {
							System.out.println("Invalid input.");
							break;
						}

						if (quantityToRemove > quantityInCart) {
							System.out.println("Number exceeds the quantity in cart.\n");
							continue;
						}

						if (quantityInCart != null) {
							int amountLeftInCart = quantityInCart - quantityToRemove;
							if (amountLeftInCart == 0) {
								cart.remove(selectedProduct);
							} else {
								cart.put(selectedProduct, quantityInCart - quantityToRemove);
							}
						}
						break;
					}

					break;
				case "4":
					System.out.println("Cancelling transaction. Returning to main menu.");
					pos.cancelTransaction((Customer)currentUser, "user cancelled");
					return null;
				default:
					System.out.println("Invalid input.");
					break;
				}
				System.out.println();
			}
		}

		return cart;
	}

	boolean makePurchaseCommand(Scanner scanner) {
		// code to buy multiple items
		System.out.println();
		System.out.println("Buying items");
		System.out.println("(use 'CANCEL' to cancel transaction at any time)");
		System.out.println("(use 'CHECKOUT' to go to checkout at any time)\n");
		LinkedHashMap<Product, Integer> shoppingCart = fillCart(scanner);
		if (shoppingCart == null) {
			if (currentUser != anonymousUser) {
				System.out.println("Logging out...");
			}
			setCurrentUserAsAnonymous();
			return true;
		}

		confirmPurchase(scanner, shoppingCart);
		if (currentUser != anonymousUser) {
			System.out.println("You will be automatically logged out.");
		}
		setCurrentUserAsAnonymous();
		
		if (!firstScreen(scanner)) {
			return false;
		}

		return true;
	}

	private boolean confirmPurchase(Scanner scanner, LinkedHashMap<Product, Integer> shoppingCart) {

		System.out.println("Checkout");
		System.out.println("This is your order:");
		Set<Product> keySet = shoppingCart.keySet();
		for (Product p : keySet) {
			System.out.println(p.getName() + " x " + shoppingCart.get(p) + " = $"
					+ String.format("%.2f", shoppingCart.get(p) * p.getPrice()));
		}
		Integer moneyRequired = pos.calculateCost(shoppingCart);

		String paymentMethod = null;
		Transaction trans = null;
		boolean paymentLoop = true;
		while (paymentLoop) {
			System.out.format("Total cost is: $%.2f\n", (double)moneyRequired/100.0); // shows the value in dollars
			System.out.println();
			System.out.println("How would you like to pay for your order?");
			System.out.println("[1] Cash");
			System.out.println("[2] Credit");
			System.out.println("[3] Cancel my order");
			System.out.println();

			System.out.println("Select an option:");
			System.out.print(">> ");

			String input;
			if (scanner.hasNextLine()) {
				input = scanner.nextLine();
			} else {
				pos.cancelTransaction((Customer)currentUser, "user cancelled");
				return false;
			}

			switch (input) {
			case "1":
				paymentMethod = "cash";
				break;
			case "2":
				paymentMethod = "credit";
				break;
			case "3":
				pos.cancelTransaction((Customer)currentUser, "user cancelled");
				System.out.println("Returning to main menu.");
				return false;
			default:
				System.out.println("Invalid input.\n");
				break;
			}

			System.out.println();

			if(paymentMethod.equals("cash")) {
				// get money (in this case for now cash)
				
				LinkedHashMap<Integer, Integer> cashMoney = new LinkedHashMap<>();
				int moneyOwed = moneyRequired;
				
				boolean cashLoop = false;
				do {
					cashMoney = new LinkedHashMap<>();
					moneyOwed = moneyRequired;
					cashLoop = false;
					
					System.out.println("Valid coins/notes:\n5c, 10c, 20c, 50c, $1, $2, $5, $10, $20, $50, $100");
	
					while (moneyOwed > 0) {
						System.out.format("\nCost remaining: $%.2f\n", (double)moneyOwed/100.0);
						System.out.println("Insert coins/notes");
						System.out.print(">> ");
	
						input = null;
						if (scanner.hasNextLine()) {
							input = scanner.nextLine().toLowerCase();
						} else {
							pos.cancelTransaction((Customer)currentUser, "user cancelled");
							return false;
						}
	
						if (input.equalsIgnoreCase("cancel")) {
							System.out.println("Cancelling transaction. Returning to main menu.\n");
							pos.cancelTransaction((Customer)currentUser, "user cancelled");
							return false;
						}
	
						if (validNotes.contains(input)) {
							int index = validNotes.indexOf(input);
							Integer value = noteValue.get(index);
							moneyOwed -= value;
	
							Integer count = cashMoney.get(value);
							if (count == null) {
								cashMoney.put(value, 1);
							} else {
								cashMoney.put(value, count + 1);
							}
						} else {
							System.out.println("Invalid input.\n");
						}
					}
	
					for (Integer note : cashMoney.keySet()) {
						pos.addCash(note, cashMoney.get(note));
					}
	
					// assuming current user is a customer (checked in main)
					trans = pos.createCashTransaction((Customer)currentUser, shoppingCart, moneyRequired - moneyOwed);
	
					System.out.println("Money received.\n");
					if (trans == null) {
						for (Integer note : cashMoney.keySet()) {
							pos.removeCash(note, cashMoney.get(note));
						}
						System.out.println("The machine has insufficient change. Your money has been refunded.");
						System.out.println("Select one of the following options:");
						System.out.println("[1] Insert new coins/notes");
						System.out.println("[2] Cancel the transaction");
						System.out.println();
						System.out.print(">> ");
						
						input = null;
						if (scanner.hasNextLine()) {
							input = scanner.nextLine();
						} else {
							pos.cancelTransaction((Customer)currentUser, "change not available");
							return false;
						}

						switch (input) {
						case "1":
							cashLoop = true;
							continue;
						case "2":
							System.out.println("Transaction cancelled. Returning to main menu.");
							pos.cancelTransaction((Customer)currentUser, "change not available");
							return false;
						default:
							System.out.println("Invalid input. Cancelling transaction.\n");
							pos.cancelTransaction((Customer)currentUser, "change not available");
							return false;
						}
					}
					
				} while (cashLoop);

				if (moneyOwed == 0) {
					System.out.println("No change required.");
				} else {
					System.out.println("Your change: $" + String.format("%.2f", moneyOwed/(-100.0)) );
					HashMap<Integer, Integer> change = trans.getChange();
					for (int i = 0; i < noteValue.size(); i++) {
						String noteString = validNotes.get(i);
						Integer quantity = change.get(noteValue.get(i));
						if (quantity == null) {
							continue;
						}
						System.out.println(noteString + " x " + quantity);
					}
					System.out.println();
				}
				paymentLoop = false;
			} else {
				// get card details
				CreditCard cc = ((Customer)currentUser).getCard();
				if (cc != null && !cardReader.getCardList().contains(cc)) {
					// card was deleted from database
					System.out.println("Your saved card is no longer valid.");
					System.out.println("Please input new card details.\n");
					cc = null;
					((Customer)currentUser).setCard(null);
				}
				if (cc == null) {
					String cardName = null;
					System.out.print("Cardholder name: ");
					if (scanner.hasNextLine()) {
						cardName = scanner.nextLine();
					} else {
						pos.cancelTransaction((Customer)currentUser, "user cancelled");
						return false;
					}
	
					if (cardName.equalsIgnoreCase("cancel")) {
						System.out.println("Cancelling transaction. Returning to main menu.\n");
						pos.cancelTransaction((Customer)currentUser, "user cancelled");
						return false;
					}
	
					String cardNumber = null;
					System.out.print("Card number: ");
					if (console != null) {
						char[] in = console.readPassword();
						cardNumber = new String(in);
					} else {
						if (scanner.hasNextLine()) {
							cardNumber = scanner.nextLine();
						} else {
							// keyboard interrupt
							pos.cancelTransaction((Customer)currentUser, "user cancelled");
							return false;
						}
					}

					if (cardNumber.equalsIgnoreCase("cancel")) {
						System.out.println("Cancelling transaction. Returning to main menu.\n");
						pos.cancelTransaction((Customer)currentUser, "user cancelled");
						return false;
					}
					
					if (!pos.checkCard(cardName, cardNumber)) {
						System.out.println("Details do not match any cards in the system.");
						System.out.println("Cancelling transaction. Returning to main menu.\n");
						pos.cancelTransaction((Customer)currentUser, "invalid card");
						return false;
					}
					trans = pos.createCardTransaction((Customer)currentUser, shoppingCart, cardName, cardNumber);
					if (trans == null) {
						System.out.println("Error handling payment.");
					} else {
						System.out.println("Payment processed.");
						if (currentUser != anonymousUser) {
							System.out.println("\nWould you like to save your card details (y/n)?");
							System.out.print(">> ");
							input = null;
							if (scanner.hasNextLine()) {
								input = scanner.nextLine();
							} else {
								input = "n";
							}
							if (input.equalsIgnoreCase("y")) {
								cc = cardReader.getCard(cardNumber);
								((Customer)currentUser).setCard(cc);
								System.out.println("Card saved successfully.\n");
							}
						}
						paymentLoop = false;
					}
				} else {
					System.out.println("Payment will proceed with saved credit card details.");
					trans = pos.createCardTransaction((Customer)currentUser, shoppingCart, cc.name, cc.number);
					if (trans != null) {
						System.out.println("Payment processed successfully.");
					} else {
						System.out.println("There was an error processing payment.");
						((Customer)currentUser).setCard(null);
					}
					paymentLoop = false;
				}
			}
		}

		if(trans == null) {
			//transaction has been unsuccessful
			return false;
		}
		//transaction has been successful
		((Customer) currentUser).addTransaction(trans);
		for (Product p : shoppingCart.keySet()) {
			Integer quantityInCart = shoppingCart.get(p);
			inventory.removeProduct(p, quantityInCart);
		}

		// TODO: record/update amount sold

		System.out.println("Thank you for shopping at the VENDING MACHINE.");
		return true;
	}

	void listChangeCommand() {
		System.out.println();
		pos.reportContents();
	}

	void modifyChangeCommand(Scanner scanner) {
		System.out.println();
		pos.reportContents();
		System.out.println();

		while (true) {

			System.out.println("Select function:");
			System.out.println("[1] Add coins/notes");
			System.out.println("[2] Remove coins/notes");
			System.out.println("[3] Return to main menu");
			System.out.println();
			System.out.print(">> ");

			String input = null;
			if (scanner.hasNextLine()) {
				input = scanner.nextLine();
			} else {
				return;
			}

			System.out.println();
			switch (input) {
			case "1":
				System.out.println("Adding coins/notes (use 'RETURN' to return to function menu)");
				System.out.println("Valid coins/notes:"
						+ " 5c, 10c, 20c, 50c, $1, $2, $5, $10, $20, $50, $100\n");

				System.out.print("Coin/note to add: ");
				input = null;
				if (scanner.hasNextLine()) {
					input = scanner.nextLine().toLowerCase();
				} else {
					return;
				}

				if (input.equalsIgnoreCase("return")) {
					System.out.println("Returning to function list.\n");
					break;
				}

				if (validNotes.contains(input)) {
					int index = validNotes.indexOf(input);
					Integer value = noteValue.get(index);
					Integer currentQuantity = pos.getCash().get(value);

					System.out.print("Quantity to add (" + currentQuantity + " in machine): ");
					input = null;
					if (scanner.hasNextLine()) {
						input = scanner.nextLine();
					} else {
						return;
					}

					if (input.equalsIgnoreCase("return")) {
						System.out.println("Returning to function list.\n");
						break;
					}

					Integer amount = confirmInputInRange(input, 0, Integer.MAX_VALUE);
					if (amount == null) {
						System.out.println("Invalid quantity.\n");
					} else if (amount == 0) {
						System.out.println();
					} else {
						if (pos.addCash(value, amount)) {
							System.out.println("Quantity updated successfully.\n");
						}
					}
				} else {
					System.out.println("Invalid coin/note.\n");
				}
				break;
			case "2":
				System.out.println("Removing coins/notes (use 'RETURN' to return to function menu)");
				System.out.println("Valid coins/notes:"
						+ " 5c, 10c, 20c, 50c, $1, $2, $5, $10, $20, $50, $100\n");

				System.out.print("Coin/note to remove: ");
				input = null;
				if (scanner.hasNextLine()) {
					input = scanner.nextLine().toLowerCase();
				} else {
					return;
				}

				if (input.equalsIgnoreCase("return")) {
					System.out.println("Returning to function list.\n");
					break;
				}

				if (validNotes.contains(input)) {
					int index = validNotes.indexOf(input);
					Integer value = noteValue.get(index);
					Integer currentQuantity = pos.getCash().get(value);

					System.out.print("Quantity to remove (" + currentQuantity + " in machine): ");
					input = null;
					if (scanner.hasNextLine()) {
						input = scanner.nextLine();
					} else {
						return;
					}

					if (input.equalsIgnoreCase("return")) {
						System.out.println("Returning to function list.\n");
						break;
					}

					Integer amount = confirmInputInRange(input, 0, Integer.MAX_VALUE);
					if (amount == null) {
						System.out.println("Invalid quantity.\n");
					} else if (amount == 0) {
						System.out.println();
					} else if (amount > currentQuantity) {
						System.out.println("Quantity exceeds the amount available in the machine.\n");
					} else {
						if (pos.removeCash(value, amount)) {
							System.out.println("Quantity updated successfully.\n");
						}
					}
				} else {
					System.out.println("Invalid coin/note.\n");
				}
				break;
			case "3":
				return;
			default:
				System.out.println("Invalid input.");
				break;
			}
			
			saveData();
		}
	}
	
	// HELP: list commands
	private void helpCommand() {

		System.out.println();
		System.out.println("MENU COMMANDS");
		System.out.println("help: show this list of commands");
		System.out.println("exit (or quit): shut down the machine");
		System.out.println("login");
		System.out.println("logout");
		System.out.println();

		if (currentUser instanceof Customer || currentUser instanceof Owner) {
			System.out.println("CUSTOMER COMMANDS");
			System.out.println("create account");
			System.out.println("list products: preview of the vending machine contents");
			System.out.println("make purchase");
			System.out.println();
		}

		if (currentUser instanceof Seller || currentUser instanceof Owner) {
			System.out.println("SELLER COMMANDS");
			System.out.println("show inventory");
			System.out.println("modify inventory / modify products");
			System.out.println("inventory report");
			System.out.println("sales report");
			System.out.println();
		}

		if (currentUser instanceof Cashier || currentUser instanceof Owner) {
			System.out.println("CASHIER COMMANDS");
			System.out.println("list change");
			System.out.println("modify change");
			System.out.println("change report");
			System.out.println("transactions report");
			System.out.println();
		}

		if (currentUser instanceof Owner) {
			System.out.println("OWNER COMMANDS");
			System.out.println("list cards");
			System.out.println("modify cards");
			System.out.println("add user");
			System.out.println("remove user");
			System.out.println("list users");
			System.out.println("user report");
			System.out.println("cancelled order report");
		}
	}

	// main engine
	void mainEngine(Scanner scanner) {

		// setCurrentUserAsAnonymous();
		if (!firstScreen(scanner)) {
			System.out.println("Shutting down the VENDING MACHINE.");
			return;
		}

		// main screen should be login/create account and 5 most recent purchases

		System.out.println("\nHello " + currentUser.getUsername() + "!");
		recentPurchases();

		if (currentUser.getUsername().equals("#anonymous")) {
			System.out.println("Main Menu (anonymous customer)");
		} else {
			System.out.println("Main Menu (Username: " + currentUser.getUsername() + ") ("
					+ currentUser.getRole().toString() + ")");
		}
		
		if (currentUser instanceof Customer) {
			System.out.println("\nHere is a list of products available in the machine:");
			listProductsCommand();
		}
		
		System.out.println("Enter command (use \"HELP\" for a full list of commands):");
		System.out.print(">> ");
		
		boolean showProducts = true;
		while (scanner.hasNextLine()) {
			String command = scanner.nextLine().trim().toLowerCase();
			
			switch (command) {
			case "exit": case "quit":
				System.out.println("Shutting down the VENDING MACHINE.");
				return;
			case "help":
				helpCommand();
				showProducts = false;
				break;
			case "login":
				System.out.println();
				loginScreen(scanner);
				break;
			case "logout":
				if (currentUser == anonymousUser) {
					System.out.println("You are not logged in.");
				} else {
					System.out.println("Logging out...");
					setCurrentUserAsAnonymous();
				}
				break;
			case "create account":
				if (confirmLogout(scanner)) {
					createAccountAsCustomer(scanner);
				}
				break;
			case "list products":
				System.out.println();
				listProductsCommand();
				showProducts = false;
				break;
			case "list cards":
				if (currentUser instanceof Owner) {
					listCardsCommand();
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "modify cards": case "modify card":
				if (currentUser instanceof Owner) {
					modifyCardsCommand(scanner);
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "add user":
				if (currentUser instanceof Owner) {
					addUserCommand(scanner);
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "remove user":
				if (currentUser instanceof Owner) {
					removeUserCommand(scanner);
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "list users":
				if (currentUser instanceof Owner) {
					Owner o = (Owner) currentUser;
					o.printUsers();
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "make purchase":
				if (currentUser instanceof Customer) {
					if (!makePurchaseCommand(scanner)) {
						System.out.println("Shutting down the VENDING MACHINE.");
						return;
					}
				} else {
					System.out.println("Only customers may make purchases.");
				}
				break;
			case "show inventory":
				if (currentUser instanceof Owner || currentUser instanceof Seller) {
					showInventoryCommand();
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "modify inventory": case "modify product": case "modify products":
				if (currentUser instanceof Owner || currentUser instanceof Seller) {
					modifyInventoryCommand(scanner);
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "list change":
				if (currentUser instanceof Owner || currentUser instanceof Cashier) {
					listChangeCommand();
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "modify change":
				if (currentUser instanceof Owner || currentUser instanceof Cashier) {
					modifyChangeCommand(scanner);
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "change report":
				if (currentUser instanceof Owner || currentUser instanceof Cashier) {
					((CashingBehaviour) currentUser).changeReport(dataFolder);
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "transactions report":
				if (currentUser instanceof Owner || currentUser instanceof Cashier) {
					((CashingBehaviour) currentUser).transactionReport(dataFolder);
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "inventory report":
				if (currentUser instanceof Owner || currentUser instanceof Seller) {
					((SellingBehaviour) currentUser).inventoryReport(inventory);
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "sales report":
				if (currentUser instanceof Owner || currentUser instanceof Seller) {
					((SellingBehaviour) currentUser).salesReport(inventory);
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "user report":
				if (currentUser instanceof Owner) {
					((Owner) currentUser).generateUserReport();
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			case "cancelled order report": case "cancelled orders report":
				if (currentUser instanceof Owner) {
					((Owner) currentUser).cancelledReport(pos);
				} else {
					System.out.println("You don't have permission for this command.");
				}
				break;
			default:
				System.out.println("Invalid command. (use \"HELP\" for a list of commands)");
			}

			saveData();

			if (currentUser.getUsername().equals("#anonymous")) {
				System.out.println("\nMain Menu (anonymous user)");
			} else {
				System.out.println("\nMain Menu (Username: " + currentUser.getUsername() + ") ("
						+ currentUser.getRole().toString() + ")");
			}
			
			if (currentUser instanceof Customer && showProducts) {
				System.out.println("\nHere is a list of products available in the machine:");
				listProductsCommand();
			}

			showProducts = true;
			
			System.out.println("Enter command (use \"HELP\" for a full list of commands):");
			System.out.print(">> ");
		}

		System.out.println("Shutting down the VENDING MACHINE.");
	}

	void greeting() {

		System.out.println("#############################");
		System.out.println("#                           #");
		System.out.println("#   $$ VENDING MACHINE $$   #");
		System.out.println("#                           #");
		System.out.println("#############################");
		System.out.println();
	}

	boolean loadData() {
		// small issue: data loads sequentially, so if one fails early the rest won't load and will instead be initialised
		try {
			validNotes = new ArrayList<>();
			validNotes.add("$100");
			validNotes.add("$50");
			validNotes.add("$20");
			validNotes.add("$10");
			validNotes.add("$5");
			validNotes.add("$2");
			validNotes.add("$1");
			validNotes.add("50c");
			validNotes.add("20c");
			validNotes.add("10c");
			validNotes.add("5c");
			
			cardReader = new CardReader(dataFolder);

			Path folderPath = Paths.get(System.getProperty("user.dir"), dataFolder);
			File folder = new File(folderPath.toString());

			// if the folder does not exist
			if (Files.notExists(folderPath)) {
				System.out.println("No data found. Initialising new data...");
				return initialise();
			}

			folder.mkdir();

			FileInputStream fis = new FileInputStream(
					Paths.get(folderPath.toString(), "allUsers.ser").toString());
			ObjectInputStream ois = new ObjectInputStream(fis);
			allUsers = (ArrayList) ois.readObject();
			ois.close();
			fis.close();
			// System.out.println("User data loaded.");

			fis = new FileInputStream(
					Paths.get(folderPath.toString(), "inventory.ser").toString());
			ois = new ObjectInputStream(fis);
			inventory = (ProductInventory)ois.readObject();
			ois.close();
			fis.close();
			// System.out.println("Inventory data loaded.");

			fis = new FileInputStream(
					Paths.get(folderPath.toString(), "pointOfSale.ser").toString());
			ois = new ObjectInputStream(fis);
			pos = (PointOfSale)ois.readObject();
			ois.close();
			fis.close();
			// System.out.println("Point of sale data loaded.");

			// initialise();
			setAnonymous();
			currentUser = anonymousUser;
			System.out.println("Data loaded successfully from memory.");
		} catch (IOException e) {
			System.out.println("Could not load data. Initialising new data...");
			return initialise();
		} catch (ClassNotFoundException e) {
			System.out.println("Could not load data. Initialising new data...");
			return initialise();
		}

		return true;
	}

	boolean initialise() {
		Path folderPath = Paths.get(System.getProperty("user.dir"), dataFolder);
		File folder = new File(folderPath.toString());
		folder.mkdir();

		if (allUsers == null) {
			Customer anon = new Customer("#anonymous", "");
			currentUser = anon;
			anonymousUser = anon;
			allUsers = new ArrayList<>();
			allUsers.add(anon);

			Owner owner = new Owner("owner", "", allUsers);
			allUsers.add(owner);
		}

		if (cardReader == null) {
			cardReader = new CardReader(dataFolder);
		}

		if (pos == null) {
			pos = new PointOfSale();
		}

		if (inventory == null) {
			initialiseInventory();
		}

		if (saveData()) {
			System.out.println("Data initialised successfully.");
			return true;
		} else {
			System.out.println("Error with data initialisation.");
			return false;
		}

	}

	void initialiseInventory() {
		List<ProductCategory> categoryList = new ArrayList<>();
		/*
		 *  Drinks: Mineral Water, Sprite, Coca cola, Pepsi, Juice.
		 *  Chocolates: Mars, M&M, Bounty, Sneakers(???).
		 *  Chips: Smiths, Pringles, Kettle, Thins.
		 *  Candies: Mentos, Sour Patch, Skittles.
		 *  (starting with 7 of each product)
		 */
		ProductCategory[] catList = {
				new ProductCategory("drinks"),
				new ProductCategory("chocolates"),
				new ProductCategory("chips"),
				new ProductCategory("candies")
		};

		Product[][] startingProductList = {
				{
					new Product(catList[0], "Mineral Water", 1001, 2.00),
					new Product(catList[0], "Sprite", 1002, 2.00),
					new Product(catList[0], "Coca cola", 1003, 2.00),
					new Product(catList[0], "Pepsi", 1004, 2.00),
					new Product(catList[0], "Juice", 1005, 2.00)
				},
				{
					new Product(catList[1], "Mars", 2001, 3.00),
					new Product(catList[1], "M&M", 2002, 3.00),
					new Product(catList[1], "Bounty", 2003, 3.00),
					new Product(catList[1], "Snickers", 2004, 3.00) // Sneakers????
				},
				{
					new Product(catList[2], "Smiths", 3001, 2.50),
					new Product(catList[2], "Pringles", 3002, 2.50),
					new Product(catList[2], "Kettle", 3003, 2.50),
					new Product(catList[2], "Thins", 3004, 2.50)
				},
				{
					new Product(catList[3], "Mentos", 4001, 3.50),
					new Product(catList[3], "Sour Patch", 4002, 3.50),
					new Product(catList[3], "Skittles", 4003, 3.50),
				}
		};

		for (int i = 0; i < 4; i++) {
			LinkedHashMap<Product, Integer> productMap = new LinkedHashMap<>();
			Product[] productArray = startingProductList[i];
			for (Product p : productArray) {
				productMap.put(p, 7);
			}
			catList[i].setProducts(productMap);;
			categoryList.add(catList[i]);
		}

		inventory = new ProductInventory(categoryList);
	}

	boolean saveData() {
		try {
			Path currentPath = Paths.get(System.getProperty("user.dir"), dataFolder);
			new File(currentPath.toString()).mkdirs();
			Path usersPath = Paths.get(currentPath.toString(), "allUsers.ser");

			FileOutputStream fos = new FileOutputStream(usersPath.toString());
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(allUsers);
			oos.close();
			fos.close();

			Path inventoryPath = Paths.get(currentPath.toString(), "inventory.ser");
			fos = new FileOutputStream(inventoryPath.toString());
			oos = new ObjectOutputStream(fos);
			oos.writeObject(inventory);
			oos.close();
			fos.close();

			Path posPath = Paths.get(currentPath.toString(), "pointOfSale.ser");
			fos = new FileOutputStream(posPath.toString());
			oos = new ObjectOutputStream(fos);
			oos.writeObject(pos);
			oos.close();
			fos.close();

			cardReader.saveCardList();
			//System.out.println("Data has been saved.");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("Failed to save data.");
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		VendingApp app = new VendingApp();
		Scanner scanner = new Scanner(System.in);

		app.greeting();
		if (app.loadData()) {
			app.mainEngine(scanner);
		}

		scanner.close();
	}
}


