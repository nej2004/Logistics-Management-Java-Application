package fasttracklogistics.view;

import fasttracklogistics.model.Delivery;
import fasttracklogistics.model.Shipment;
import fasttracklogistics.model.DeliveryPersonnel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Vector; // Used by JComboBox, but not strictly needed for Map-based population

public class ScheduleDeliveryPanel extends JPanel {
    private JTable deliveriesTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> shipmentComboBox, personnelComboBox;
    private JTextField scheduledPickupTimeField, actualPickupTimeField,
            scheduledDeliveryTimeField, actualDeliveryTimeField,
            routeDetailsField, deliveryNotesField;
    private JComboBox<String> deliveryStatusComboBox;
    private JButton addButton, updateButton, removeButton, clearButton;

    // Maps to store IDs corresponding to display names in ComboBoxes
    private Map<String, Integer> shipmentDisplayNameToIdMap;
    private Map<String, Integer> personnelDisplayNameToIdMap;
    private Map<Integer, String> shipmentIdToDisplayNameMap; // For reverse lookup for table display
    private Map<Integer, String> personnelIdToDisplayNameMap; // For reverse lookup for table display



    public ScheduleDeliveryPanel() {
        setLayout(new BorderLayout(10, 10));

        // --- Table for displaying deliveries ---
        tableModel = new DefaultTableModel(new Object[]{
                "Delivery ID", "Shipment", "Personnel", "Scheduled Pickup",
                "Actual Pickup", "Scheduled Delivery", "Actual Delivery",
                "Status", "Route Details", "Notes"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        deliveriesTable = new JTable(tableModel);
        deliveriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(deliveriesTable), BorderLayout.CENTER);

        // --- Input form ---
        JPanel inputFormPanel = new JPanel(new GridBagLayout());
        inputFormPanel.setBorder(BorderFactory.createTitledBorder("Delivery Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        inputFormPanel.add(new JLabel("Shipment:"), gbc(0, row));
        shipmentComboBox = new JComboBox<>();
        inputFormPanel.add(shipmentComboBox, gbc(1, row++));

        inputFormPanel.add(new JLabel("Personnel:"), gbc(0, row));
        personnelComboBox = new JComboBox<>();
        inputFormPanel.add(personnelComboBox, gbc(1, row++));

        inputFormPanel.add(new JLabel("Scheduled Pickup (YYYY-MM-DD HH:MM:SS):"), gbc(0, row));
        scheduledPickupTimeField = new JTextField(25);
        inputFormPanel.add(scheduledPickupTimeField, gbc(1, row++));

        inputFormPanel.add(new JLabel("Actual Pickup (YYYY-MM-DD HH:MM:SS):"), gbc(0, row));
        actualPickupTimeField = new JTextField(25);
        // actualPickupTimeField.setEditable(false); // Typically updated by system/driver
        inputFormPanel.add(actualPickupTimeField, gbc(1, row++));

        inputFormPanel.add(new JLabel("Scheduled Delivery (YYYY-MM-DD HH:MM:SS):"), gbc(0, row));
        scheduledDeliveryTimeField = new JTextField(25);
        inputFormPanel.add(scheduledDeliveryTimeField, gbc(1, row++));

        inputFormPanel.add(new JLabel("Actual Delivery (YYYY-MM-DD HH:MM:SS):"), gbc(0, row));
        actualDeliveryTimeField = new JTextField(25);
        // actualDeliveryTimeField.setEditable(false); // Typically updated by system/driver
        inputFormPanel.add(actualDeliveryTimeField, gbc(1, row++));

        inputFormPanel.add(new JLabel("Delivery Status:"), gbc(0, row));
        String[] statuses = {"Scheduled", "Picked Up", "In Transit", "Out for Delivery", "Delivered", "Failed"};
        deliveryStatusComboBox = new JComboBox<>(statuses);
        inputFormPanel.add(deliveryStatusComboBox, gbc(1, row++));

        inputFormPanel.add(new JLabel("Route Details:"), gbc(0, row));
        routeDetailsField = new JTextField(25);
        inputFormPanel.add(routeDetailsField, gbc(1, row++));

        inputFormPanel.add(new JLabel("Delivery Notes:"), gbc(0, row));
        deliveryNotesField = new JTextField(25);
        inputFormPanel.add(deliveryNotesField, gbc(1, row++));

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Schedule Delivery");
        updateButton = new JButton("Update Delivery");
        removeButton = new JButton("Remove Delivery");
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

    // --- Methods to attach listeners ---
    public void addAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void addUpdateButtonListener(ActionListener listener) {
        updateButton.addActionListener(listener);
    }

    public void addRemoveButtonListener(ActionListener listener) {
        removeButton.addActionListener(listener);
    }

    public void addClearButtonListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }

    public void addTableSelectionListener(ListSelectionListener listener) {
        deliveriesTable.getSelectionModel().addListSelectionListener(listener);
    }

    // --- Methods to populate combo boxes ---
    public void populateShipmentComboBox(List<Shipment> shipments) {
        shipmentComboBox.removeAllItems();
        shipmentDisplayNameToIdMap = new java.util.HashMap<>();
        shipmentIdToDisplayNameMap = new java.util.HashMap<>();
        for (Shipment s : shipments) {
            String displayName = s.getTrackingNumber() + " - " + s.getReceiverName();
            shipmentComboBox.addItem(displayName);
            shipmentDisplayNameToIdMap.put(displayName, s.getShipmentId());
            shipmentIdToDisplayNameMap.put(s.getShipmentId(), displayName);
        }
    }

    public void populatePersonnelComboBox(List<DeliveryPersonnel> personnelList) {
        personnelComboBox.removeAllItems();
        personnelComboBox.addItem("Unassigned"); // Option for no personnel
        personnelDisplayNameToIdMap = new java.util.HashMap<>();
        personnelIdToDisplayNameMap = new java.util.HashMap<>();
        personnelDisplayNameToIdMap.put("Unassigned", 0); // Map "Unassigned" to ID 0 or -1
        personnelIdToDisplayNameMap.put(0, "Unassigned");

        for (DeliveryPersonnel p : personnelList) {
            String displayName = p.getName() + " (" + p.getAvailabilityStatus() + ")";
            personnelComboBox.addItem(displayName);
            personnelDisplayNameToIdMap.put(displayName, p.getPersonnelId());
            personnelIdToDisplayNameMap.put(p.getPersonnelId(), displayName);
        }
    }

    // --- Methods to update the View ---
    public void displayDeliveries(List<Delivery> deliveries) {
        tableModel.setRowCount(0); // Clear existing data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Delivery d : deliveries) {
            String shipmentName = shipmentIdToDisplayNameMap.getOrDefault(d.getShipmentId(), "Unknown Shipment ID: " + d.getShipmentId());
            String personnelName = personnelIdToDisplayNameMap.getOrDefault(d.getPersonnelId(), "Unassigned");

            tableModel.addRow(new Object[]{
                    d.getDeliveryId(),
                    shipmentName,
                    personnelName,
                    d.getScheduledPickupTime() != null ? d.getScheduledPickupTime().format(formatter) : "N/A",
                    d.getActualPickupTime() != null ? d.getActualPickupTime().format(formatter) : "N/A",
                    d.getScheduledDeliveryTime() != null ? d.getScheduledDeliveryTime().format(formatter) : "N/A",
                    d.getActualDeliveryTime() != null ? d.getActualDeliveryTime().format(formatter) : "N/A",
                    d.getDeliveryStatus(),
                    d.getRouteDetails(),
                    d.getDeliveryNotes()
            });
        }
    }

    // --- Methods to get/set input field values ---
    public Delivery getDeliveryFromFields() { // Renamed for clarity
        Integer shipmentId = shipmentDisplayNameToIdMap.get((String) shipmentComboBox.getSelectedItem());
        Integer personnelId = personnelDisplayNameToIdMap.get((String) personnelComboBox.getSelectedItem());

        if (shipmentId == null || shipmentId == 0) { // Shipment is mandatory
            showMessage("Please select a Shipment.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        LocalDateTime scheduledPickupTime = parseDateTime(scheduledPickupTimeField.getText());
        LocalDateTime actualPickupTime = parseDateTime(actualPickupTimeField.getText());
        LocalDateTime scheduledDeliveryTime = parseDateTime(scheduledDeliveryTimeField.getText());
        LocalDateTime actualDeliveryTime = parseDateTime(actualDeliveryTimeField.getText());

        String deliveryStatus = (String) deliveryStatusComboBox.getSelectedItem();
        String routeDetails = routeDetailsField.getText().trim();
        String deliveryNotes = deliveryNotesField.getText().trim();

        if (scheduledPickupTime == null || scheduledDeliveryTime == null) {
            showMessage("Scheduled Pickup and Scheduled Delivery times are required and must be in YYYY-MM-DD HH:MM:SS format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (deliveryStatus == null || deliveryStatus.isEmpty()) {
            showMessage("Delivery Status is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Delivery delivery = new Delivery(
                shipmentId,
                personnelId != null ? personnelId : 0, // Pass 0 if unassigned
                scheduledPickupTime,
                scheduledDeliveryTime,
                deliveryStatus,
                routeDetails,
                deliveryNotes
        );
        delivery.setActualPickupTime(actualPickupTime);
        delivery.setActualDeliveryTime(actualDeliveryTime);
        return delivery;
    }

    public void setDeliveryFields(Delivery delivery) { // Renamed for clarity
        if (delivery == null) {
            clearFields();
            return;
        }

        String shipmentDisplayName = shipmentIdToDisplayNameMap.getOrDefault(delivery.getShipmentId(), "Unknown Shipment ID: " + delivery.getShipmentId());
        shipmentComboBox.setSelectedItem(shipmentDisplayName);

        String personnelDisplayName = personnelIdToDisplayNameMap.getOrDefault(delivery.getPersonnelId(), "Unassigned");
        personnelComboBox.setSelectedItem(personnelDisplayName);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        scheduledPickupTimeField.setText(delivery.getScheduledPickupTime() != null ? delivery.getScheduledPickupTime().format(formatter) : "");
        actualPickupTimeField.setText(delivery.getActualPickupTime() != null ? delivery.getActualPickupTime().format(formatter) : "");
        scheduledDeliveryTimeField.setText(delivery.getScheduledDeliveryTime() != null ? delivery.getScheduledDeliveryTime().format(formatter) : "");
        actualDeliveryTimeField.setText(delivery.getActualDeliveryTime() != null ? delivery.getActualDeliveryTime().format(formatter) : "");
        deliveryStatusComboBox.setSelectedItem(delivery.getDeliveryStatus() != null ? delivery.getDeliveryStatus() : "Scheduled");
        routeDetailsField.setText(delivery.getRouteDetails() != null ? delivery.getRouteDetails() : "");
        deliveryNotesField.setText(delivery.getDeliveryNotes() != null ? delivery.getDeliveryNotes() : "");
    }

    public void clearFields() { // Renamed for clarity
        shipmentComboBox.setSelectedIndex(-1);
        personnelComboBox.setSelectedItem("Unassigned");
        scheduledPickupTimeField.setText("");
        actualPickupTimeField.setText("");
        scheduledDeliveryTimeField.setText("");
        actualDeliveryTimeField.setText("");
        deliveryStatusComboBox.setSelectedItem("Scheduled");
        routeDetailsField.setText("");
        deliveryNotesField.setText("");
        deliveriesTable.clearSelection();
    }

    public int getSelectedDeliveryId() {
        int selectedRow = deliveriesTable.getSelectedRow();
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

    // Helper to parse date-time string
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            // Error message handled in getDeliveryFromFields
            return null;
        }
    }
    public JComboBox<String> getShipmentComboBox() {
        return shipmentComboBox;
    }

    // Public getter for personnelComboBox
    public JComboBox<String> getPersonnelComboBox() {
        return personnelComboBox;
    }
}