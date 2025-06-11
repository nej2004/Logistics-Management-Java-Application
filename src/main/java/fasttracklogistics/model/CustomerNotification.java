package fasttracklogistics.model;

import java.time.LocalDateTime;

/**
 * Represents a Notification specifically intended for a Customer.
 * This model is designed to work with the 'notifications' table where recipient_type is 'Customer'.
 */
public class CustomerNotification {
    private int notificationId;
    private int customerShipmentId; // Represents the shipment ID associated with the customer recipient (maps to recipient_id in DB)
    private int relatedShipmentId;  // The ID of the primary shipment this notification is about (maps to related_shipment_id in DB)
    private String message;
    private String notificationType; // e.g., "Status Update", "Delay"
    private LocalDateTime timestamp;
    private boolean isRead;
    private String contactMethod; // e.g., "App", "Email"

    /**
     * Full constructor for loading CustomerNotification objects from the database.
     * Used when retrieving data and mapping it to this specific model.
     */
    public CustomerNotification(int notificationId, int customerShipmentId, int relatedShipmentId,
                                String message, String notificationType, LocalDateTime timestamp,
                                boolean isRead, String contactMethod) {
        this.notificationId = notificationId;
        this.customerShipmentId = customerShipmentId;
        this.relatedShipmentId = relatedShipmentId;
        this.message = message;
        this.notificationType = notificationType;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.contactMethod = contactMethod;
    }

    /**
     * Constructor for creating NEW CustomerNotification objects (ID will be auto-generated).
     * Timestamp will be set to the current time, and isRead will be set to false.
     * Used when sending a new notification to a customer.
     */
    public CustomerNotification(int customerShipmentId, int relatedShipmentId,
                                String message, String notificationType, String contactMethod) {
        this.customerShipmentId = customerShipmentId;
        this.relatedShipmentId = relatedShipmentId;
        this.message = message;
        this.notificationType = notificationType;
        this.contactMethod = contactMethod;
        this.timestamp = LocalDateTime.now(); // Automatically set current timestamp
        this.isRead = false; // New notifications are unread by default
    }

    // --- Getters and Setters ---
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getCustomerShipmentId() { return customerShipmentId; }
    public void setCustomerShipmentId(int customerShipmentId) { this.customerShipmentId = customerShipmentId; }

    public int getRelatedShipmentId() { return relatedShipmentId; }
    public void setRelatedShipmentId(int relatedShipmentId) { this.relatedShipmentId = relatedShipmentId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getContactMethod() { return contactMethod; }
    public void setContactMethod(String contactMethod) { this.contactMethod = contactMethod; }

    @Override
    public String toString() {
        return "CustomerNotification{" +
                "notificationId=" + notificationId +
                ", customerShipmentId=" + customerShipmentId +
                ", relatedShipmentId=" + relatedShipmentId +
                ", message='" + message + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                ", contactMethod='" + contactMethod + '\'' +
                '}';
    }
}
