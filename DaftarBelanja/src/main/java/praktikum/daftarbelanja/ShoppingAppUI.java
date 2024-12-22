package praktikum.daftarbelanja;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ShoppingAppUI extends Application {

    private ShoppingApp shoppingApp = new ShoppingApp();
    private BorderPane rootPane = new BorderPane();
    private ListView<ShoppingItem> shoppingListView;

    @Override
    public void start(Stage primaryStage) {
        rootPane.setCenter(createAddItemPage());
        Scene scene = new Scene(rootPane, 600, 400);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setTitle("Aplikasi Daftar Belanja");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Pane createAddItemPage() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        Label titleLabel = new Label("Tambah Barang Baru");
        titleLabel.getStyleClass().add("title");

        TextField nameField = new TextField();
        nameField.setPromptText("Nama Barang");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Jumlah");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Deskripsi (Opsional)");
        descriptionField.setPrefRowCount(3);

        Button addButton = new Button("Tambah Barang");
        addButton.getStyleClass().add("primary-button");

        Button viewListButton = new Button("Lihat Daftar");
        viewListButton.setOnAction(e -> showViewListPage());

        addButton.setOnAction(e -> {
            String name = nameField.getText();
            String quantityText = quantityField.getText();
            String description = descriptionField.getText();

            if (name.isEmpty() || quantityText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Informasi Hilang", "Isi Nama Barang dan Jumlah!");
                return;
            }

            try {
                int quantity = Integer.parseInt(quantityText);
                shoppingApp.addItem(name, quantity, description.isEmpty() ? null : description);

                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Barang berhasil ditambahkan!");
                clearFields(nameField, quantityField, descriptionField);

                updateListView();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Jumlah Tidak Valid", "Jumlah harus berupa angka!");
            }
        });

        vbox.getChildren().addAll(titleLabel, nameField, quantityField, descriptionField, addButton, viewListButton);
        return vbox;
    }

    private Pane createViewListPage() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
    
        Label titleLabel = new Label("Daftar Belanja");
        titleLabel.getStyleClass().add("title");
    
        shoppingListView = new ListView<>();
        shoppingListView.setPrefHeight(200);
    
        shoppingListView.setCellFactory(new Callback<ListView<ShoppingItem>, ListCell<ShoppingItem>>() {
            @Override
            public ListCell<ShoppingItem> call(ListView<ShoppingItem> listView) {
                return new ListCell<ShoppingItem>() {
                    private final CheckBox checkBox = new CheckBox();
                    private final Button deleteButton = new Button("X");
    
                    @Override
                    protected void updateItem(ShoppingItem item, boolean empty) {
                        super.updateItem(item, empty);
    
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Label itemLabel = new Label(item.getName() + " (" + item.getQuantity() + ")");
    
                            String description = item.getDescription();
                            boolean hasDescription = description != null && !description.isEmpty();
    
                            Label descriptionLabel = null;
                            if (hasDescription) {
                                descriptionLabel = new Label(description);
                                descriptionLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;"); 
                            }
    
                            checkBox.setSelected(item.isPurchased());
                            checkBox.setOnAction(e -> item.setPurchased(checkBox.isSelected()));
    
                            deleteButton.getStyleClass().add("delete-button");
                            deleteButton.setOnAction(e -> {
                                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                                confirmAlert.setTitle("Konfirmasi Penghapusan");
                                confirmAlert.setHeaderText(null);
                                confirmAlert.setContentText("Apakah Anda yakin ingin menghapus barang ini?");
    
                                confirmAlert.showAndWait().ifPresent(response -> {
                                    if (response == ButtonType.OK) {
                                        shoppingApp.removeItem(item);
                                        shoppingListView.setItems(FXCollections.observableList(shoppingApp.getShoppingList()));
                                        showAlert(Alert.AlertType.INFORMATION, "Barang Dihapus", item.getName() + " telah dihapus.");
                                    }
                                });
                            });
    
                            Region spacer = new Region();
                            HBox.setHgrow(spacer, Priority.ALWAYS);
    
                            HBox hbox = new HBox(10, checkBox, itemLabel, spacer, deleteButton);
                            hbox.setSpacing(10);
                            hbox.setAlignment(Pos.CENTER_LEFT); 
    
                            VBox itemBox = new VBox(5);
                            itemBox.getChildren().add(hbox); 
    
                            if (descriptionLabel != null) {
                                itemBox.getChildren().add(descriptionLabel); 
                            }
    
                            setText(null); 
                            setGraphic(itemBox); 
                        }
                    }
                };
            }
        });
    
        Button backButton = new Button("Kembali ke Tambah Barang");
        backButton.setOnAction(e -> rootPane.setCenter(createAddItemPage()));
    
        Button exportButton = new Button("Simpan List Sebagai File");
        exportButton.setOnAction(e -> exportList()); 
    
        Button importButton = new Button("Impor File List");
        importButton.setOnAction(e -> importList()); 
    
        HBox buttonBox = new HBox(10, backButton, exportButton, importButton);
        buttonBox.setAlignment(Pos.CENTER);
    
        vbox.getChildren().addAll(titleLabel, shoppingListView, buttonBox);
        return vbox;
    }
    

    private void updateListView() {
        if (shoppingListView != null) {
            shoppingListView.setItems(FXCollections.observableList(shoppingApp.getShoppingList()));
        }
    }

    private void showViewListPage() {
        rootPane.setCenter(createViewListPage());
        updateListView();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields(TextField nameField, TextField quantityField, TextArea descriptionField) {
        nameField.clear();
        quantityField.clear();
        descriptionField.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void exportList() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Daftar Belanja");

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (ShoppingItem item : shoppingApp.getShoppingList()) {
                    writer.write(item.getName() + ";" + item.getQuantity() + ";" +
                            (item.getDescription() != null ? item.getDescription() : ""));
                    writer.newLine();
                }
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Daftar berhasil diekspor!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Terjadi kesalahan saat mengekspor daftar.");
            }
        }
    }

    private void importList() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Daftar Belanja");

        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";", 3);
                    if (parts.length >= 2) {
                        String name = parts[0];
                        int quantity = Integer.parseInt(parts[1]);
                        String description = parts.length == 3 ? parts[2] : null;
                        shoppingApp.addItem(name, quantity, description);
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Daftar berhasil diimpor!");
                updateListView();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Terjadi kesalahan saat mengimpor daftar.");
            }
        }
    }


}
