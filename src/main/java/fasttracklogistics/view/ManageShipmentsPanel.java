package fasttracklogistics.view;

import fasttracklogistics.model.Shipment;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ManageShipmentsPanel extends JPanel {
    private JTable shipmentsTable;
    private DefaultTableModel tableModel;
    // Corrected fields to match Shipment model and DB schema
    private JTextField trackingNumberField, senderNameField, senderAddressField, senderContactField,
            receiverNameField, receiverAddressField, receiverContactField,
            packageContentsField, weightField, dimensionsField, currentLocationField,
            estimatedDeliveryTimeField, actualDeliveryTimeField;
    private JComboBox<String> deliveryStatusComboBox; // Changed to JComboBox
    private JTextArea specialInstructionsArea;
    private JButton addButton, updateButton, removeButton, clearButton;

    public ManageShipmentsPanel() {
        setLayout(new BorderLayout(10, 10));

        // --- Table for displaying shipments ---
        tableModel = new DefaultTableModel(new Object[]{
                "ID", "Tracking No.", "Sender", "Sender Addr", "Sender Contact",
                "Receiver", "Receiver Addr", "Receiver Contact", "Contents", "Weight", "Dimensions",
                "Status", "Location", "Est. Delivery", "Actual Delivery", "Special Instr.", "Created On"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        shipmentsTable = new JTable(tableModel);
        shipmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(shipmentsTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Input form ---
        JPanel inputFormPanel = new JPanel(new GridBagLayout());
        inputFormPanel.setBorder(BorderFactory.createTitledBorder("Shipment Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Row 1: Tracking Number
        inputFormPanel.add(new JLabel("Tracking Number:"), gbc(0, row));
        trackingNumberField = new JTextField(25);
        inputFormPanel.add(trackingNumberField, gbc(1, row++));

        // Row 2: Sender Name
        inputFormPanel.add(new JLabel("Sender Name:"), gbc(0, row));
        senderNameField = new JTextField(25);
        inputFormPanel.add(senderNameField, gbc(1, row++));

        // Row 3: Sender Address
        inputFormPanel.add(new JLabel("Sender Address:"), gbc(0, row));
        senderAddressField = new JTextField(25);
        inputFormPanel.add(senderAddressField, gbc(1, row++));

        // Row 4: Sender Contact
        inputFormPanel.add(new JLabel("Sender Contact:"), gbc(0, row));
        senderContactField = new JTextField(25);
        inputFormPanel.add(senderContactField, gbc(1, row++));

        // Row 5: Receiver Name
        inputFormPanel.add(new JLabel("Receiver Name:"), gbc(0, row));
        receiverNameField = new JTextField(25);
        inputFormPanel.add(receiverNameField, gbc(1, row++));

        // Row 6: Receiver Address
        inputFormPanel.add(new JLabel("Receiver Address:"), gbc(0, row));
        receiverAddressField = new JTextField(25);
        inputFormPanel.add(receiverAddressField, gbc(1, row++));

        // Row 7: Receiver Contact
        inputFormPanel.add(new JLabel("Receiver Contact:"), gbc(0, row));
        receiverContactField = new JTextField(25);
        inputFormPanel.add(receiverContactField, gbc(1, row++));

        // Row 8: Package Contents
        inputFormPanel.add(new JLabel("Package Contents:"), gbc(0, row));
        packageContentsField = new JTextField(25);
        inputFormPanel.add(packageContentsField, gbc(1, row++));

        // Row 9: Weight
        inputFormPanel.add(new JLabel("Weight (kg):"), gbc(0, row));
        weightField = new JTextField(25);
        inputFormPanel.add(weightField, gbc(1, row++));

        // Row 10: Dimensions
        inputFormPanel.add(new JLabel("Dimensions (LxWxH):"), gbc(0, row));
        dimensionsField = new JTextField(25);
        inputFormPanel.add(dimensionsField, gbc(1, row++));

        // Row 11: Delivery Status (JComboBox)
        inputFormPanel.add(new JLabel("Delivery Status:"), gbc(0, row));
        String[] statuses = {"Pending", "Scheduled", "In Transit", "Out for Delivery", "Delivered", "Canceled", "Failed"};
        deliveryStatusComboBox = new JComboBox<>(statuses);
        inputFormPanel.add(deliveryStatusComboBox, gbc(1, row++));

        // Row 12: Current Location
        inputFormPanel.add(new JLabel("Current Location:"), gbc(0, row));
        currentLocationField = new JTextField(25);
        inputFormPanel.add(currentLocationField, gbc(1, row++));

        // Row 13: Estimated Delivery Time
        inputFormPanel.add(new JLabel("Est. Delivery (YYYY-MM-DD HH:MM:SS):"), gbc(0, row));
        estimatedDeliveryTimeField = new JTextField(25);
        inputFormPanel.add(estimatedDeliveryTimeField, gbc(1, row++));

        // Row 14: Actual Delivery Time
        inputFormPanel.add(new JLabel("Actual Delivery (YYYY-MM-DD HH:MM:SS):"), gbc(0, row));
        actualDeliveryTimeField = new JTextField(25);
        inputFormPanel.add(actualDeliveryTimeField, gbc(1, row++));

        // Row 15: Special Instructions (JTextArea)
        inputFormPanel.add(new JLabel("Special Instructions:"), gbc(0, row, GridBagConstraints.NORTHEAST));
        specialInstructionsArea = new JTextArea(3, 25);
        specialInstructionsArea.setLineWrap(true);
        specialInstructionsArea.setWrapStyleWord(true);
        JScrollPane specialInstructionsScrollPane = new JScrollPane(specialInstructionsArea);
        inputFormPanel.add(specialInstructionsScrollPane, gbc(1, row++, 1.0)); // weighty for JTextArea


        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Add Shipment");
        updateButton = new JButton("Update Shipment");
        removeButton = new JButton("Remove Shipment");
        clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputFormPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    // Helper for GridBagConstraints
    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.anchor = (x == 0) ? GridBagConstraints.EAST : GridBagConstraints.WEST;
        return gbc;
    }
    private GridBagConstraints gbc(int x, int y, int anchor) { // Overload for specific anchor
        GridBagConstraints gbc = gbc(x,y);
        gbc.anchor = anchor;
        return gbc;
    }
    private GridBagConstraints gbc(int x, int y, double weighty) { // Overload for weighty
        GridBagConstraints gbc = gbc(x,y);
        gbc.weighty = weighty;
        return gbc;
    }


    // --- Methods to attach listeners (called by Controller) ---
    public void addAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void addUpdateButtonListener(ActionListener listener) {
        updateButton.addActionListener(listener);
    }

    public void addRemoveButtonListener(ActionListener listener) { // Renamed from addDeleteButtonListener for consistency
        removeButton.addActionListener(listener);
    }

    public void addClearButtonListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }

    public void addTableSelectionListener(ListSelectionListener listener) {
        shipmentsTable.getSelectionModel().addListSelectionListener(listener);
    }

    // --- Methods to update the View (called by Controller) ---
    public void displayShipments(List<Shipment> shipments) {
        tableModel.setRowCount(0); // Clear existing data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Shipment s : shipments) {
            tableModel.addRow(new Object[]{
                    s.getShipmentId(),
                    s.getTrackingNumber(),
                    s.getSenderName(),
                    s.getSenderAddress(),
                    s.getSenderContact(),
                    s.getReceiverName(),
                    s.getReceiverAddress(),
                    s.getReceiverContact(),
                    s.getPackageContents(),
                    s.getWeight(),
                    s.getDimensions(),
                    s.getDeliveryStatus(),
                    s.getCurrentLocation(),
                    s.getEstimatedDeliveryTime() != null ? s.getEstimatedDeliveryTime().format(formatter) : "N/A",
                    s.getActualDeliveryTime() != null ? s.getActualDeliveryTime().format(formatter) : "N/A",
                    s.getSpecialInstructions(),
                    s.getCreationDate() != null ? s.getCreationDate().format(formatter) : "N/A"
            });
        }
    }

    // --- Methods to get/set input field values (called by Controller) ---
    public Shipment getShipmentFromFields() { // Renamed from getShipmentInput for clarity
        String trackingNumber = trackingNumberField.getText().trim();
        String senderName = senderNameField.getText().trim();
        String senderAddress = senderAddressField.getText().trim();
        String senderContact = senderContactField.getText().trim();
        String receiverName = receiverNameField.getText().trim();
        String receiverAddress = receiverAddressField.getText().trim();
        String receiverContact = receiverContactField.getText().trim();
        String packageContents = packageContentsField.getText().trim();
        String deliveryStatus = (String) deliveryStatusComboBox.getSelectedItem();
        String currentLocation = currentLocationField.getText().trim();
        String specialInstructions = specialInstructionsArea.getText().trim();

        double weight = 0.0;
        try {
            weight = Double.parseDouble(weightField.getText().trim());
        } catch (NumberFormatException e) {
            showMessage("Invalid number format for Weight. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        String dimensions = dimensionsField.getText().trim();

        LocalDateTime estimatedDeliveryTime = null;
        String estTimeStr = estimatedDeliveryTimeField.getText().trim();
        if (!estTimeStr.isEmpty()) {
            try {
                estimatedDeliveryTime = LocalDateTime.parse(estTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e) {
                showMessage("Invalid date/time format for Estimated Delivery Time. Please use YYYY-MM-DD HH:MM:SS.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }

        LocalDateTime actualDeliveryTime = null;
        String actTimeStr = actualDeliveryTimeField.getText().trim();
        if (!actTimeStr.isEmpty()) {
            try {
                actualDeliveryTime = LocalDateTime.parse(actTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e) {
                showMessage("Invalid date/time format for Actual Delivery Time. Please use YYYY-MM-DD HH:MM:SS.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }

        // Basic validation for required fields
        if (trackingNumber.isEmpty() || senderName.isEmpty() || receiverName.isEmpty() || deliveryStatus.isEmpty()) {
            showMessage("Tracking Number, Sender Name, Receiver Name, and Delivery Status are required fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }


        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(trackingNumber);
        shipment.setSenderName(senderName);
        shipment.setSenderAddress(senderAddress);
        shipment.setSenderContact(senderContact);
        shipment.setReceiverName(receiverName);
        shipment.setReceiverAddress(receiverAddress);
        shipment.setReceiverContact(receiverContact);
        shipment.setPackageContents(packageContents);
        shipment.setWeight(weight);
        shipment.setDimensions(dimensions);
        shipment.setDeliveryStatus(deliveryStatus);
        shipment.setCurrentLocation(currentLocation);
        shipment.setEstimatedDeliveryTime(estimatedDeliveryTime);
        shipment.setActualDeliveryTime(actualDeliveryTime);
        shipment.setSpecialInstructions(specialInstructions);

        return shipment;
    }

    public void setShipmentFields(Shipment shipment) { // Renamed from setShipmentInput for clarity
        if (shipment == null) {
            clearFields();
            return;
        }
        trackingNumberField.setText(shipment.getTrackingNumber() != null ? shipment.getTrackingNumber() : "");
        senderNameField.setText(shipment.getSenderName() != null ? shipment.getSenderName() : "");
        senderAddressField.setText(shipment.getSenderAddress() != null ? shipment.getSenderAddress() : "");
        senderContactField.setText(shipment.getSenderContact() != null ? shipment.getSenderContact() : "");
        receiverNameField.setText(shipment.getReceiverName() != null ? shipment.getReceiverName() : "");
        receiverAddressField.setText(shipment.getReceiverAddress() != null ? shipment.getReceiverAddress() : "");
        receiverContactField.setText(shipment.getReceiverContact() != null ? shipment.getReceiverContact() : "");
        packageContentsField.setText(shipment.getPackageContents() != null ? shipment.getPackageContents() : "");
        weightField.setText(String.valueOf(shipment.getWeight()));
        dimensionsField.setText(shipment.getDimensions() != null ? shipment.getDimensions() : "");
        deliveryStatusComboBox.setSelectedItem(shipment.getDeliveryStatus() != null ? shipment.getDeliveryStatus() : "Pending");
        currentLocationField.setText(shipment.getCurrentLocation() != null ? shipment.getCurrentLocation() : "");
        estimatedDeliveryTimeField.setText(shipment.getEstimatedDeliveryTime() != null ?
                shipment.getEstimatedDeliveryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
        actualDeliveryTimeField.setText(shipment.getActualDeliveryTime() != null ?
                shipment.getActualDeliveryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
        specialInstructionsArea.setText(shipment.getSpecialInstructions() != null ? shipment.getSpecialInstructions() : "");
    }

    public void clearFields() { // Renamed from clearShipmentInput for clarity
        trackingNumberField.setText("");
        senderNameField.setText("");
        senderAddressField.setText("");
        senderContactField.setText("");
        receiverNameField.setText("");
        receiverAddressField.setText("");
        receiverContactField.setText("");
        packageContentsField.setText("");
        weightField.setText("0.0"); // Reset to default numeric value
        dimensionsField.setText("");
        deliveryStatusComboBox.setSelectedItem("Pending");
        currentLocationField.setText("");
        estimatedDeliveryTimeField.setText("");
        actualDeliveryTimeField.setText("");
        specialInstructionsArea.setText("");
        shipmentsTable.clearSelection();
    }

    public int getSelectedShipmentId() {
        int selectedRow = shipmentsTable.getSelectedRow();
        if (selectedRow != -1) {
            Object idValue = tableModel.getValueAt(selectedRow, 0);
            if (idValue instanceof Integer) {
                return (int) idValue;
            } else if (idValue instanceof String) {
                try {
                    return Integer.parseInt((String) idValue);
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return -1;
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
    }
}