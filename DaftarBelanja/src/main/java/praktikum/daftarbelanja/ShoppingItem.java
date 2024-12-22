package praktikum.daftarbelanja;

public class ShoppingItem {
    private String name;
    private int quantity;
    private String description;
    private boolean purchased;

    public ShoppingItem(String name, int quantity, String description) {
        this.name = name;
        this.quantity = quantity;
        this.description = (description != null && !description.trim().isEmpty()) ? description : null; // Set null if description is empty or null
        this.purchased = false; 
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public void markAsPurchased() {
        this.purchased = true;
    }
}
