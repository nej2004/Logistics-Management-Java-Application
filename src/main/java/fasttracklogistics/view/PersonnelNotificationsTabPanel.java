package fasttracklogistics.view;

import fasttracklogistics.model.PersonnelNotification; // Import the specific personnel notification model
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * JPanel to display a table of notifications specifically for Delivery Personnel.
 * This panel is designed to work with PersonnelNotification model objects.
 */
public class PersonnelNotificationsTabPanel extends JPanel {
    private JTable notificationsTable;
    private DefaultTableModel tableModel;
    private Map<Integer, String> personnelIdToNameMap;         // To show readable personnel info (ID to Name)
    private Map<Integer, String> shipmentIdToTrackingNumberMap; // To show readable shipment info (ID to Tracking # / Receiver Name)


    public PersonnelNotificationsTabPanel() {
        setLayout(new BorderLayout());

        // Define columns for personnel notifications table
        String[] columnNames = {"ID", "Recipient (Personnel ID)", "Related Shipment", "Related Personnel (if applicable)", "Message", "Type", "Timestamp", "Read", "Method"};
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
        this.personnelIdToNameMap = new java.util.HashMap<>();
        this.shipmentIdToTrackingNumberMap = new java.util.HashMap<>();
    }

    /**
     * Populates the table with personnel notifications.
     *
     * @param notifications List of PersonnelNotification objects to display.
     */
    public void displayNotifications(List<PersonnelNotification> notifications) {
        tableModel.setRowCount(0); // Clear existing rows
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (PersonnelNotification n : notifications) {
            String recipientDisplayName = personnelIdToNameMap.getOrDefault(n.getPersonnelId(), "Personnel ID: " + n.getPersonnelId());
            String relatedShipmentDisplayName = shipmentIdToTrackingNumberMap.getOrDefault(n.getRelatedShipmentId(), "N/A");
            String relatedPersonnelDisplayName = personnelIdToNameMap.getOrDefault(n.getRelatedPersonnelId(), "N/A"); // For other related personnel

            tableModel.addRow(new Object[]{
                    n.getNotificationId(),
                    recipientDisplayName,
                    relatedShipmentDisplayName,
                    relatedPersonnelDisplayName,
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
     * Sets the map for resolving personnel IDs to readable names.
     * This map is crucial for correctly displaying recipient and related personnel names in the table.
     * @param map A map where keys are Personnel IDs (Integer) and values are display names (e.g., "John Doe (123)").
     */
    public void setPersonnelIdToNameMap(Map<Integer, String> map) {
        this.personnelIdToNameMap.clear();
        this.personnelIdToNameMap.putAll(map);
    }

    /**
     * Sets the map for resolving shipment IDs to readable tracking numbers/receiver names.
     * This map is crucial for correctly displaying related shipment information in the table.
     * @param map A map where keys are Shipment IDs (Integer) and values are display strings (e.g., "TRK123 (Receiver Name)").
     */
    public void setShipmentIdToTrackingNumberMap(Map<Integer, String> map) {
        this.shipmentIdToTrackingNumberMap.clear();
        this.shipmentIdToTrackingNumberMap.putAll(map);
    }
}
