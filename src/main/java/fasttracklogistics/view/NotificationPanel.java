package fasttracklogistics.view;

import fasttracklogistics.model.Shipment;
import fasttracklogistics.model.DeliveryPersonnel;
import fasttracklogistics.model.Notification; // Keep this import for the generic type in displayNotifications
import fasttracklogistics.model.CustomerNotification; // **NEW: Import CustomerNotification model**
import fasttracklogistics.model.PersonnelNotification; // **NEW: Import PersonnelNotification model**

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Needed for stream operations in displayNotifications

/**
 * Main JPanel for the Notification management feature.
 * It contains a panel for sending new notifications and a tabbed pane
 * to view Customer and Personnel notifications separately using dedicated sub-panels.
 */
public class NotificationPanel extends JPanel {
    // Send Notification Panel Components
    private JComboBox<String> recipientTypeComboBox;
    private JComboBox<String> recipientComboBox;
    private JComboBox<String> relatedShipmentComboBox;
    private JComboBox<String> relatedPersonnelComboBox;
    private JTextArea messageTextArea;
    private JTextField contactMethodField;
    private JComboBox<String> notificationTypeComboBox;
    private JButton sendNotificationButton;

    // View Notifications Panel Components (now uses custom tab panels)
    private JTabbedPane notificationTabbedPane;
    private CustomerNotificationsTabPanel customerNotificationsTabPanel; // Dedicated panel for customer notifications
    private PersonnelNotificationsTabPanel personnelNotificationsTabPanel; // Dedicated panel for personnel notifications
    private JButton refreshButton;

    // Maps for ComboBox population and reverse lookup (data caches managed here and passed to sub-panels)
    private Map<String, Integer> customerContactToShipmentIdMap;
    private Map<String, Integer> personnelNameToIdMap;
    private Map<String, Integer> trackingNumberToShipmentIdMap;
    private Map<Integer, String> shipmentIdToTrackingNumberMap; // For displaying related shipment info
    private Map<Integer, String> personnelIdToNameMap;           // For displaying personnel names

    public NotificationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Send Notification Panel ---
        JPanel sendPanel = new JPanel(new GridBagLayout());
        sendPanel.setBorder(BorderFactory.createTitledBorder("Send New Notification"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Recipient Type
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        sendPanel.add(new JLabel("Recipient Type:"), gbc);
        recipientTypeComboBox = new JComboBox<>(new String[]{"Customer", "Personnel"});
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
        sendPanel.add(recipientTypeComboBox, gbc);

        // Recipient (dynamically populated)
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        sendPanel.add(new JLabel("Recipient:"), gbc);
        recipientComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = row++;
        sendPanel.add(recipientComboBox, gbc);

        // Related Shipment (Optional)
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        sendPanel.add(new JLabel("Related Shipment (Optional):"), gbc);
        relatedShipmentComboBox = new JComboBox<>();
        relatedShipmentComboBox.addItem("N/A"); // Default option
        gbc.gridx = 1; gbc.gridy = row++;
        sendPanel.add(relatedShipmentComboBox, gbc);

        // Related Personnel (Optional)
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        sendPanel.add(new JLabel("Related Personnel (Optional):"), gbc);
        relatedPersonnelComboBox = new JComboBox<>();
        relatedPersonnelComboBox.addItem("N/A"); // Default option
        gbc.gridx = 1; gbc.gridy = row++;
        sendPanel.add(relatedPersonnelComboBox, gbc);

        // Notification Type
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        sendPanel.add(new JLabel("Notification Type:"), gbc);
        notificationTypeComboBox = new JComboBox<>(new String[]{"Status Update", "Assignment", "Urgent", "General", "Delay"});
        gbc.gridx = 1; gbc.gridy = row++;
        sendPanel.add(notificationTypeComboBox, gbc);

        // Message
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        sendPanel.add(new JLabel("Message:"), gbc);
        messageTextArea = new JTextArea(5, 30);
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageTextArea);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        sendPanel.add(messageScrollPane, gbc);

        // Contact Method
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        sendPanel.add(new JLabel("Contact Method:"), gbc);
        contactMethodField = new JTextField(20);
        contactMethodField.setText("App"); // Default
        gbc.gridx = 1; gbc.gridy = row++;
        sendPanel.add(contactMethodField, gbc);

        // Send Button
        sendNotificationButton = new JButton("Send Notification");
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        sendPanel.add(sendNotificationButton, gbc);

        add(sendPanel, BorderLayout.NORTH);

        // --- View Notifications Panel (with JTabbedPane) ---
        JPanel viewPanel = new JPanel(new BorderLayout(5, 5));
        viewPanel.setBorder(BorderFactory.createTitledBorder("Sent Notifications"));

        notificationTabbedPane = new JTabbedPane();

        // Instantiate the new tab panels
        customerNotificationsTabPanel = new CustomerNotificationsTabPanel();
        personnelNotificationsTabPanel = new PersonnelNotificationsTabPanel();

        // Add the new panels as tabs
        notificationTabbedPane.addTab("Customer Notifications", customerNotificationsTabPanel);
        notificationTabbedPane.addTab("Personnel Notifications", personnelNotificationsTabPanel);

        viewPanel.add(notificationTabbedPane, BorderLayout.CENTER);

        refreshButton = new JButton("Refresh Notifications");
        JPanel refreshButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        refreshButtonPanel.add(refreshButton);
        viewPanel.add(refreshButtonPanel, BorderLayout.SOUTH);

        add(viewPanel, BorderLayout.CENTER);
    }

    // --- Listeners ---
    public void addSendButtonListener(ActionListener listener) {
        sendNotificationButton.addActionListener(listener);
    }

    public void addRefreshButtonListener(ActionListener listener) {
        refreshButton.addActionListener(listener);
    }

    public void addRecipientTypeComboBoxListener(ActionListener listener) {
        recipientTypeComboBox.addActionListener(listener);
    }

    // Getter for the JTabbedPane to add change listeners in the controller
    public JTabbedPane getNotificationTabbedPane() {
        return notificationTabbedPane;
    }

    // Public method to set the selected recipient type in the combo box
    public void setSelectedRecipientType(String type) {
        recipientTypeComboBox.setSelectedItem(type);
    }

    // --- Populators for ComboBoxes (also pass maps to sub-panels) ---
    public void populatePersonnelComboBox(List<DeliveryPersonnel> personnelList) {
        recipientComboBox.removeAllItems(); // Clear previous items in the main recipient combo box
        relatedPersonnelComboBox.removeAllItems(); // Clear related personnel combo box

        personnelNameToIdMap = new java.util.HashMap<>();
        personnelIdToNameMap = new java.util.HashMap<>();

        // Add "N/A" option for related personnel
        relatedPersonnelComboBox.addItem("N/A");
        personnelNameToIdMap.put("N/A", 0); // Map N/A to 0 for DB NULL handling
        personnelIdToNameMap.put(0, "N/A"); // Reverse map for N/A

        for (DeliveryPersonnel p : personnelList) {
            String displayName = p.getName() + " (" + p.getPersonnelId() + ")"; // Use ID for unique display
            recipientComboBox.addItem(displayName); // For main recipient selection
            relatedPersonnelComboBox.addItem(displayName); // For related personnel selection
            personnelNameToIdMap.put(displayName, p.getPersonnelId());
            personnelIdToNameMap.put(p.getPersonnelId(), displayName);
        }
        // Also update the personnel tab panel with the map
        personnelNotificationsTabPanel.setPersonnelIdToNameMap(personnelIdToNameMap);
    }

    public void populateCustomerComboBox(List<Shipment> shipments) {
        recipientComboBox.removeAllItems(); // Clear previous items in the main recipient combo box
        customerContactToShipmentIdMap = new java.util.HashMap<>();

        for (Shipment s : shipments) {
            String displayName = s.getReceiverName() + " (Shipment: " + s.getTrackingNumber() + ")";
            if (!customerContactToShipmentIdMap.containsKey(displayName)) {
                recipientComboBox.addItem(displayName);
                customerContactToShipmentIdMap.put(displayName, s.getShipmentId()); // Use shipment ID as proxy for customer ID
            }
        }
        // Also update the customer tab panel with the map
        // **FIXED TYPO HERE: customerContactToShipmentToIdMap -> customerContactToShipmentIdMap**
        customerNotificationsTabPanel.setCustomerContactToShipmentIdMap(customerContactToShipmentIdMap);
    }

    public void populateRelatedShipmentComboBox(List<Shipment> shipments) {
        relatedShipmentComboBox.removeAllItems();
        relatedShipmentComboBox.addItem("N/A"); // Option for no related shipment
        trackingNumberToShipmentIdMap = new java.util.HashMap<>();
        shipmentIdToTrackingNumberMap = new java.util.HashMap<>();

        trackingNumberToShipmentIdMap.put("N/A", 0); // Map N/A to 0 for DB NULL handling
        shipmentIdToTrackingNumberMap.put(0, "N/A");

        for (Shipment s : shipments) {
            String displayName = s.getTrackingNumber() + " (" + s.getReceiverName() + ")";
            relatedShipmentComboBox.addItem(displayName);
            trackingNumberToShipmentIdMap.put(displayName, s.getShipmentId());
            shipmentIdToTrackingNumberMap.put(s.getShipmentId(), displayName);
        }
        // Also update both tab panels with the map
        customerNotificationsTabPanel.setShipmentIdToTrackingNumberMap(shipmentIdToTrackingNumberMap);
        personnelNotificationsTabPanel.setShipmentIdToTrackingNumberMap(shipmentIdToTrackingNumberMap);
    }

    // --- Getters for Input ---
    public String getSelectedRecipientType() {
        return (String) recipientTypeComboBox.getSelectedItem();
    }

    public int getSelectedRecipientId() {
        String selectedRecipientDisplayName = (String) recipientComboBox.getSelectedItem();
        if (selectedRecipientDisplayName == null) return 0;

        if ("Customer".equals(getSelectedRecipientType())) {
            return customerContactToShipmentIdMap.getOrDefault(selectedRecipientDisplayName, 0);
        } else if ("Personnel".equals(getSelectedRecipientType())) {
            return personnelNameToIdMap.getOrDefault(selectedRecipientDisplayName, 0);
        }
        return 0;
    }

    public int getSelectedRelatedShipmentId() {
        String selectedShipmentDisplayName = (String) relatedShipmentComboBox.getSelectedItem();
        if (selectedShipmentDisplayName == null || "N/A".equals(selectedShipmentDisplayName)) {
            return 0;
        }
        return trackingNumberToShipmentIdMap.getOrDefault(selectedShipmentDisplayName, 0);
    }

    public int getSelectedRelatedPersonnelId() {
        String selectedPersonnelDisplayName = (String) relatedPersonnelComboBox.getSelectedItem();
        if (selectedPersonnelDisplayName == null || "N/A".equals(selectedPersonnelDisplayName)) {
            return 0;
        }
        return personnelNameToIdMap.getOrDefault(selectedPersonnelDisplayName, 0);
    }

    public String getNotificationMessage() {
        return messageTextArea.getText().trim();
    }

    public String getNotificationType() {
        return (String) notificationTypeComboBox.getSelectedItem();
    }

    public String getContactMethod() {
        return contactMethodField.getText().trim();
    }

    // --- Display Methods (Delegated to sub-panels) ---
    /**
     * Delegates notification display to the appropriate sub-panel based on recipient type.
     * It converts generic Notifications to specific models (CustomerNotification, PersonnelNotification)
     * for display in their respective tables.
     *
     * @param notifications The list of Notifications (generic) to be displayed.
     * @param recipientType "Customer" or "Personnel" (determines which tab panel to update).
     */
    public void displayNotifications(List<Notification> notifications, String recipientType) {
        if ("Customer".equals(recipientType)) {
            // Filter and convert generic Notifications to CustomerNotifications for display
            List<CustomerNotification> customerNotifications = notifications.stream()
                    .filter(n -> "Customer".equals(n.getRecipientType()))
                    .map(n -> new CustomerNotification( // **NEW: Use CustomerNotification constructor**
                            n.getNotificationId(), n.getRecipientId(), n.getRelatedShipmentId(),
                            n.getMessage(), n.getNotificationType(), n.getTimestamp(),
                            n.isRead(), n.getContactMethod()
                    ))
                    .collect(Collectors.toList());
            customerNotificationsTabPanel.displayNotifications(customerNotifications);
        } else if ("Personnel".equals(recipientType)) {
            // Filter and convert generic Notifications to PersonnelNotifications for display
            List<PersonnelNotification> personnelNotifications = notifications.stream()
                    .filter(n -> "Personnel".equals(n.getRecipientType()))
                    .map(n -> new PersonnelNotification( // **NEW: Use PersonnelNotification constructor**
                            n.getNotificationId(), n.getRecipientId(), n.getRelatedShipmentId(), n.getRelatedPersonnelId(),
                            n.getMessage(), n.getNotificationType(), n.getTimestamp(),
                            n.isRead(), n.getContactMethod()
                    ))
                    .collect(Collectors.toList());
            personnelNotificationsTabPanel.displayNotifications(personnelNotifications);
        }
    }

    // --- Clear Methods ---
    public void clearSendNotificationFields() {
        messageTextArea.setText("");
        contactMethodField.setText("App");
        notificationTypeComboBox.setSelectedItem("Status Update");
        relatedShipmentComboBox.setSelectedItem("N/A");
        relatedPersonnelComboBox.setSelectedItem("N/A");
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}