package vending;

public class Product implements java.io.Serializable {

    protected ProductCategory type;
    protected String name;
    protected int code;
    protected double price;
    protected int amountSold;

    public Product(ProductCategory type, String name, int code, Double price) {
        this.type = type;
        this.name = name;
        this.code = code;
        this.price = price;
        this.amountSold = 0;

    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public double getPrice() {
        return price;
    }
    
    public int getAmountSold() {
    	return amountSold;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setType(ProductCategory type) {
        this.type = type;
    }

    public ProductCategory getType() {
        return type;
    }
    
    public void setAmountSold(int amount) {
    	this.amountSold = amount;
    }

    // TO DO
}


