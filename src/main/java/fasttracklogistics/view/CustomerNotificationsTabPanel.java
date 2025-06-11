package fasttracklogistics.view;

import fasttracklogistics.model.CustomerNotification; // Import the specific customer notification model
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * JPanel to display a table of notifications specifically for Customers.
 * This panel is designed to work with CustomerNotification model objects.
 */
public class CustomerNotificationsTabPanel extends JPanel {
    private JTable notificationsTable;
    private DefaultTableModel tableModel;
    private Map<Integer, String> shipmentIdToTrackingNumberMap; // To show readable shipment info
    private Map<String, Integer> customerContactToShipmentIdMap; // For reverse lookup (customer display name to shipment ID)

    public CustomerNotificationsTabPanel() {
        setLayout(new BorderLayout());

        // Define columns for customer notifications table
        String[] columnNames = {"ID", "Recipient (Shipment ID)", "Related Shipment", "Message", "Type", "Timestamp", "Read", "Method"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells non-editable
            }
        };
        notificationsTable = new JTable(tableModel);
        notificationsTable.setFillsViewportHeight(true);
        add(new JScrollPane(notificationsTable), BorderLayout.CENTER);

        // Initialize maps. These will be populated by the controller via setter methods.
        this.shipmentIdToTrackingNumberMap = new java.util.HashMap<>();
        this.customerContactToShipmentIdMap = new java.util.HashMap<>();
    }

    /**
     * Populates the table with customer notifications.
     *
     * @param notifications List of CustomerNotification objects to display.
     */
    public void displayNotifications(List<CustomerNotification> notifications) {
        tableModel.setRowCount(0); // Clear existing rows
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (CustomerNotification n : notifications) {
            String recipientDisplayName = getCustomerDisplayName(n.getCustomerShipmentId()); // Get readable customer name
            String shipmentDisplayName = shipmentIdToTrackingNumberMap.getOrDefault(n.getRelatedShipmentId(), "N/A"); // Get readable related shipment info

            tableModel.addRow(new Object[]{
                    n.getNotificationId(),
                    recipientDisplayName,
                    shipmentDisplayName,
                    n.getMessage(),
                    n.getNotificationType(),
                    n.getTimestamp() != null ? n.getTimestamp().format(formatter) : "N/A",
                    n.isRead() ? "Yes" : "No",
                    n.getContactMethod()
            });
        }
    }

    // --- Helper methods for display names (called by Controller to set data) ---

    /**
     * Sets the map for resolving shipment IDs to readable tracking numbers/receiver names.
     * This map is crucial for correctly displaying related shipment information in the table.
     * @param map A map where keys are Shipment IDs (Integer) and values are display strings (e.g., "TRK123 (Receiver Name)").
     */
    public void setShipmentIdToTrackingNumberMap(Map<Integer, String> map) {
        this.shipmentIdToTrackingNumberMap.clear();
        this.shipmentIdToTrackingNumberMap.putAll(map);
    }

    /**
     * Sets the map for resolving recipient IDs (which are effectively Shipment IDs for customers)
     * to a customer-friendly display name. This map helps in displaying the actual customer associated
     * with the notification based on their linked shipment.
     * @param map A map where keys are customer display strings (e.g., "John Doe (Shipment: TRK456)")
     * and values are the associated Shipment IDs (Integer).
     */
    public void setCustomerContactToShipmentIdMap(Map<String, Integer> map) {
        this.customerContactToShipmentIdMap.clear();
        this.customerContactToShipmentIdMap.putAll(map);
    }

    /**
     * Helper method to get the customer's display name from the cached map.
     * @param customerShipmentId The shipment ID acting as the customer's proxy ID.
     * @return A formatted display string for the customer, or "N/A" / fallback if not found.
     */
    private String getCustomerDisplayName(int customerShipmentId) {
        if (customerShipmentId == 0) return "N/A";
        // Iterate through the map to find the display name associated with the customerShipmentId
        for (Map.Entry<String, Integer> entry : customerContactToShipmentIdMap.entrySet()) {
            if (entry.getValue().equals(customerShipmentId)) {
                return entry.getKey();
            }
        }
        return "Customer (Shipment ID: " + customerShipmentId + ")"; // Fallback if not found
    }
}
