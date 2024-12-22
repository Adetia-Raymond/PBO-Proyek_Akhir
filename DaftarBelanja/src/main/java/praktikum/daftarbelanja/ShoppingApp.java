package praktikum.daftarbelanja;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ShoppingApp {
    private List<ShoppingItem> shoppingList;

    public ShoppingApp() {
        shoppingList = new ArrayList<>();
    }

    public void addItem(String name, int quantity, String description) {
        shoppingList.add(new ShoppingItem(name, quantity, description));
    }

    public List<ShoppingItem> getShoppingList() {
        return shoppingList;
    }

    public void markItemAsPurchased(int index) {
        if (index >= 0 && index < shoppingList.size()) {
            shoppingList.get(index).markAsPurchased();
        }
    }

    public void removeItem(ShoppingItem item) {
        shoppingList.remove(item);
    }

    public void exportToFile(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (ShoppingItem item : shoppingList) {
                String line = String.format("%s;%d;%s;%b",
                        item.getName(),
                        item.getQuantity(),
                        item.getDescription() != null ? item.getDescription() : "",
                        item.isPurchased());
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public void importFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    String name = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    String description = parts[2].isEmpty() ? null : parts[2];
                    boolean purchased = parts.length > 3 && Boolean.parseBoolean(parts[3]);
                    ShoppingItem item = new ShoppingItem(name, quantity, description);
                    item.setPurchased(purchased);
                    shoppingList.add(item);
                }
            }
        }
    }
}
