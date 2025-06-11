package fasttracklogistics.model;

import java.time.LocalDateTime;

/**
 * Represents a Notification specifically intended for Delivery Personnel.
 * This model is designed to work with the 'notifications' table where recipient_type is 'Personnel'.
 */
public class PersonnelNotification {
    private int notificationId;
    private int personnelId; // The ID of the personnel recipient (maps to recipient_id in DB)
    private int relatedShipmentId; // The ID of a related shipment (e.g., assignment) (maps to related_shipment_id in DB)
    private int relatedPersonnelId; // The ID of other related personnel (e.g., dispatcher) (maps to related_personnel_id in DB)
    private String message;
    private String notificationType; // e.g., "Assignment", "Route Change", "Urgent"
    private LocalDateTime timestamp;
    private boolean isRead;
    private String contactMethod; // e.g., "App", "SMS"

    /**
     * Full constructor for loading PersonnelNotification objects from the database.
     * Used when retrieving data and mapping it to this specific model.
     */
    public PersonnelNotification(int notificationId, int personnelId, int relatedShipmentId, int relatedPersonnelId,
                                 String message, String notificationType, LocalDateTime timestamp,
                                 boolean isRead, String contactMethod) {
        this.notificationId = notificationId;
        this.personnelId = personnelId;
        this.relatedShipmentId = relatedShipmentId;
        this.relatedPersonnelId = relatedPersonnelId;
        this.message = message;
        this.notificationType = notificationType;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.contactMethod = contactMethod;
    }

    /**
     * Constructor for creating NEW PersonnelNotification objects (ID will be auto-generated).
     * Timestamp will be set to the current time, and isRead will be set to false.
     * Used when sending a new notification to personnel.
     */
    public PersonnelNotification(int personnelId, int relatedShipmentId, int relatedPersonnelId,
                                 String message, String notificationType, String contactMethod) {
        this.personnelId = personnelId;
        this.relatedShipmentId = relatedShipmentId;
        this.relatedPersonnelId = relatedPersonnelId;
        this.message = message;
        this.notificationType = notificationType;
        this.contactMethod = contactMethod;
        this.timestamp = LocalDateTime.now(); // Automatically set current timestamp
        this.isRead = false; // New notifications are unread by default
    }

    // --- Getters and Setters ---
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getPersonnelId() { return personnelId; }
    public void setPersonnelId(int personnelId) { this.personnelId = personnelId; }

    public int getRelatedShipmentId() { return relatedShipmentId; }
    public void setRelatedShipmentId(int relatedShipmentId) { this.relatedShipmentId = relatedShipmentId; }

    public int getRelatedPersonnelId() { return relatedPersonnelId; }
    public void setRelatedPersonnelId(int relatedPersonnelId) { this.relatedPersonnelId = relatedPersonnelId; }

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
        return "PersonnelNotification{" +
                "notificationId=" + notificationId +
                ", personnelId=" + personnelId +
                ", relatedShipmentId=" + relatedShipmentId +
                ", relatedPersonnelId=" + relatedPersonnelId +
                ", message='" + message + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                ", contactMethod='" + contactMethod + '\'' +
                '}';
    }
}
