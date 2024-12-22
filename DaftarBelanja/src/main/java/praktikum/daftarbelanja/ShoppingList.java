package praktikum.daftarbelanja;

import java.util.ArrayList;

public class ShoppingList {
    private ArrayList<ShoppingItem> items;

    public ShoppingList() {
        items = new ArrayList<>();
    }

    public void addItem(ShoppingItem item) {
        items.add(item);
    }

    public ShoppingItem getItemById(int itemId) {
        for (ShoppingItem item : items) {
            if (items.indexOf(item) == itemId) {
                return item;
            }
        }
        return null;
    }

    public void removeItem(ShoppingItem item) {
        items.remove(item);
    }

    public ArrayList<ShoppingItem> getItems() {
        return items;
    }
}
