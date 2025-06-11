package fasttracklogistics.controller;

import fasttracklogistics.view.NotificationPanel;
import fasttracklogistics.dao.NotificationDAO;
import fasttracklogistics.dao.ShipmentDAO;
import fasttracklogistics.dao.DeliveryPersonnelDAO;
import fasttracklogistics.model.Notification; // Generic Notification model (used internally by DAO)
import fasttracklogistics.model.CustomerNotification; // New specific model
import fasttracklogistics.model.PersonnelNotification; // New specific model
import fasttracklogistics.model.Shipment;
import fasttracklogistics.model.DeliveryPersonnel;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors; // Needed for stream operations

public class NotificationController {
    private NotificationPanel notificationPanel;
    private NotificationDAO notificationDAO;
    private ShipmentDAO shipmentDAO;
    private DeliveryPersonnelDAO personnelDAO;

    private static final Logger LOGGER = Logger.getLogger(NotificationController.class.getName());

    public NotificationController(NotificationPanel notificationPanel,
                                  NotificationDAO notificationDAO,
                                  ShipmentDAO shipmentDAO,
                                  DeliveryPersonnelDAO personnelDAO) {
        this.notificationPanel = notificationPanel;
        this.notificationDAO = notificationDAO;
        this.shipmentDAO = shipmentDAO;
        this.personnelDAO = personnelDAO;

        initListeners();
        loadInitialData();
    }

    private void initListeners() {
        notificationPanel.addSendButtonListener(e -> sendNotification());
        notificationPanel.addRefreshButtonListener(e -> loadNotificationsForActiveTab());

        notificationPanel.addRecipientTypeComboBoxListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRecipientTypeChange();
            }
        });

        // Listener for JTabbedPane selection changes
        notificationPanel.getNotificationTabbedPane().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                loadNotificationsForActiveTab(); // Load data when tab changes
            }
        });
    }

    // Initial data loading when the controller is created
    public void loadInitialData() {
        try {
            // Populate maps in NotificationPanel and pass to sub-panels
            List<Shipment> shipments = shipmentDAO.getAllShipments();
            notificationPanel.populateRelatedShipmentComboBox(shipments);
            notificationPanel.populateCustomerComboBox(shipments);

            List<DeliveryPersonnel> personnelList = personnelDAO.getAllPersonnel();
            notificationPanel.populatePersonnelComboBox(personnelList);

            notificationPanel.setSelectedRecipientType("Customer");
            handleRecipientTypeChange();

            // Load notifications for the initially selected tab (Customer Notifications)
            loadNotificationsForActiveTab();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error loading initial data for NotificationPanel", ex);
            notificationPanel.showMessage("Error loading initial data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRecipientTypeChange() {
        String selectedType = notificationPanel.getSelectedRecipientType();
        try {
            if ("Customer".equals(selectedType)) {
                List<Shipment> shipments = shipmentDAO.getAllShipments();
                notificationPanel.populateCustomerComboBox(shipments);
            } else if ("Personnel".equals(selectedType)) {
                List<DeliveryPersonnel> personnelList = personnelDAO.getAllPersonnel();
                notificationPanel.populatePersonnelComboBox(personnelList);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error populating recipient combo box", ex);
            notificationPanel.showMessage("Error loading recipients: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendNotification() {
        String recipientType = notificationPanel.getSelectedRecipientType();
        int recipientId = notificationPanel.getSelectedRecipientId(); // Shipment ID for customer, Personnel ID for personnel
        int relatedShipmentId = notificationPanel.getSelectedRelatedShipmentId();
        int relatedPersonnelId = notificationPanel.getSelectedRelatedPersonnelId(); // This will be personnel ID

        String message = notificationPanel.getNotificationMessage();
        String notificationType = notificationPanel.getNotificationType();
        String contactMethod = notificationPanel.getContactMethod();

        if (recipientId == 0 || message.isEmpty()) {
            notificationPanel.showMessage("Recipient and message cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if ("Customer".equals(recipientType)) {
                CustomerNotification newCustomerNotification = new CustomerNotification(
                        recipientId,        // This is the customer's associated shipment ID
                        relatedShipmentId,  // This is the related shipment ID, potentially different from recipientId
                        message,
                        notificationType,
                        contactMethod
                );
                notificationDAO.addCustomerNotification(newCustomerNotification);
            } else if ("Personnel".equals(recipientType)) {
                PersonnelNotification newPersonnelNotification = new PersonnelNotification(
                        recipientId,        // This is the personnel's ID
                        relatedShipmentId,
                        relatedPersonnelId, // Can be 0 if not applicable
                        message,
                        notificationType,
                        contactMethod
                );
                notificationDAO.addPersonnelNotification(newPersonnelNotification);
            }

            notificationPanel.showMessage("Notification sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            notificationPanel.clearSendNotificationFields();

            // Refresh the tab corresponding to the notification that was just sent
            int tabIndex = notificationPanel.getNotificationTabbedPane().indexOfTab(recipientType + " Notifications");
            if(tabIndex != -1) {
                notificationPanel.getNotificationTabbedPane().setSelectedIndex(tabIndex);
            }
            loadNotificationsForActiveTab(); // Refresh the active tab
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error sending notification", ex);
            notificationPanel.showMessage("Error sending notification: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Determines the currently active tab and loads notifications for its type.
     * It fetches generic Notifications from DAO and converts them to specific models for the view.
     */
    private void loadNotificationsForActiveTab() {
        int selectedIndex = notificationPanel.getNotificationTabbedPane().getSelectedIndex();
        String tabTitle = notificationPanel.getNotificationTabbedPane().getTitleAt(selectedIndex);

        String recipientTypeToLoad = null;
        if (tabTitle.equals("Customer Notifications")) {
            recipientTypeToLoad = "Customer";
        } else if (tabTitle.equals("Personnel Notifications")) {
            recipientTypeToLoad = "Personnel";
        }

        if (recipientTypeToLoad != null) {
            try {
                // DAO still returns generic Notification objects
                List<Notification> genericNotifications = notificationDAO.getNotificationsByRecipientType(recipientTypeToLoad);

                // Pass the generic notifications to the panel, which will handle filtering and conversion to specific models for display
                notificationPanel.displayNotifications(genericNotifications, recipientTypeToLoad);

            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error loading " + recipientTypeToLoad + " notifications", ex);
                notificationPanel.showMessage("Error loading " + recipientTypeToLoad + " notifications: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- Automated Notification Methods (UPDATED to use new specific models for adding to DAO) ---

    /**
     * Sends a notification to a customer about their shipment status.
     * @param shipment The shipment object for which to send the notification.
     * @param notificationMessage The specific message for the customer.
     * @param notificationType The type of notification (e.g., "Status Update", "Delay").
     */
    public void sendCustomerShipmentNotification(Shipment shipment, String notificationMessage, String notificationType) {
        if (shipment == null) return;

        int customerId = shipment.getShipmentId(); // Use shipment ID as proxy for customer ID for recipient_id

        try {
            CustomerNotification notification = new CustomerNotification(
                    customerId,             // customerShipmentId (recipient_id in DB)
                    shipment.getShipmentId(), // relatedShipmentId
                    notificationMessage,
                    notificationType,
                    "App" // Default contact method for automated notifications
            );
            notificationDAO.addCustomerNotification(notification); // Call the specific add method
            LOGGER.log(Level.INFO, "Automated customer notification sent for shipment ID: " + shipment.getShipmentId());
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to send automated customer notification for shipment ID: " + shipment.getShipmentId(), ex);
        }
    }

    /**
     * Sends a notification to delivery personnel (driver) about an assignment or route change.
     * @param personnel The DeliveryPersonnel object.
     * @param shipment The related shipment (optional, can be null if not assignment specific).
     * @param notificationMessage The specific message for the personnel.
     * @param notificationType The type of notification (e.g., "Assignment", "Route Change", "Urgent").
     */
    public void sendPersonnelNotification(DeliveryPersonnel personnel, Shipment shipment, String notificationMessage, String notificationType) {
        if (personnel == null) return;

        int relatedShipmentId = (shipment != null) ? shipment.getShipmentId() : 0;

        try {
            PersonnelNotification notification = new PersonnelNotification(
                    personnel.getPersonnelId(), // personnelId (recipient_id in DB)
                    relatedShipmentId,
                    0, // relatedPersonnelId (not applicable for this scenario)
                    notificationMessage,
                    notificationType,
                    "App" // Default contact method
            );
            notificationDAO.addPersonnelNotification(notification); // Call the specific add method
            LOGGER.log(Level.INFO, "Automated personnel notification sent to ID: " + personnel.getPersonnelId());
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to send automated personnel notification to ID: " + personnel.getPersonnelId(), ex);
        }
    }
}