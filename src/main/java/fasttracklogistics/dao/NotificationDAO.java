package fasttracklogistics.dao;

import fasttracklogistics.model.Notification; // Original generic model
import fasttracklogistics.model.CustomerNotification; // New specific model
import fasttracklogistics.model.PersonnelNotification; // New specific model
import fasttracklogistics.dao.DatabaseConnection; // Corrected import based on your file structure
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing Notification records in the database.
 * Provides methods for adding, retrieving, and managing notifications.
 * It now handles specific CustomerNotification and PersonnelNotification models
 * while mapping them to a single 'notifications' database table.
 */
public class NotificationDAO {

    // --- Add methods for new specific models ---

    /**
     * Adds a new customer notification record to the database.
     * The notification object's ID will be updated with the auto-generated key from the database.
     * This maps a CustomerNotification to the generic 'notifications' table.
     *
     * @param customerNotification The CustomerNotification object to add.
     * @throws SQLException If a database access error occurs.
     */
    public void addCustomerNotification(CustomerNotification customerNotification) throws SQLException {
        String sql = "INSERT INTO notifications (" +
                "recipient_type, recipient_id, related_shipment_id, related_personnel_id, " +
                "message, notification_type, timestamp, is_read, contact_method) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, "Customer"); // Hardcode recipient_type
            pstmt.setInt(2, customerNotification.getCustomerShipmentId()); // customerShipmentId maps to recipient_id
            pstmt.setInt(3, customerNotification.getRelatedShipmentId());
            pstmt.setNull(4, Types.INTEGER); // Customer notifications typically don't have related personnel

            pstmt.setString(5, customerNotification.getMessage());
            pstmt.setString(6, customerNotification.getNotificationType());
            pstmt.setTimestamp(7, Timestamp.valueOf(customerNotification.getTimestamp()));
            pstmt.setBoolean(8, customerNotification.isRead());
            pstmt.setString(9, customerNotification.getContactMethod());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating customer notification failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customerNotification.setNotificationId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating customer notification failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Adds a new personnel notification record to the database.
     * The notification object's ID will be updated with the auto-generated key from the database.
     * This maps a PersonnelNotification to the generic 'notifications' table.
     *
     * @param personnelNotification The PersonnelNotification object to add.
     * @throws SQLException If a database access error occurs.
     */
    public void addPersonnelNotification(PersonnelNotification personnelNotification) throws SQLException {
        String sql = "INSERT INTO notifications (" +
                "recipient_type, recipient_id, related_shipment_id, related_personnel_id, " +
                "message, notification_type, timestamp, is_read, contact_method) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, "Personnel"); // Hardcode recipient_type
            pstmt.setInt(2, personnelNotification.getPersonnelId()); // personnelId maps to recipient_id

            if (personnelNotification.getRelatedShipmentId() > 0) {
                pstmt.setInt(3, personnelNotification.getRelatedShipmentId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            if (personnelNotification.getRelatedPersonnelId() > 0) {
                pstmt.setInt(4, personnelNotification.getRelatedPersonnelId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setString(5, personnelNotification.getMessage());
            pstmt.setString(6, personnelNotification.getNotificationType());
            pstmt.setTimestamp(7, Timestamp.valueOf(personnelNotification.getTimestamp()));
            pstmt.setBoolean(8, personnelNotification.isRead());
            pstmt.setString(9, personnelNotification.getContactMethod());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating personnel notification failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    personnelNotification.setNotificationId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating personnel notification failed, no ID obtained.");
                }
            }
        }
    }

    // --- Existing methods (return generic Notification model) ---

    /**
     * Retrieves all notification records from the database.
     *
     * @return A list of all generic Notification objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Notification> getAllNotifications() throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                notifications.add(new Notification(
                        rs.getInt("notification_id"),
                        rs.getString("recipient_type"),
                        rs.getInt("recipient_id"),
                        rs.getInt("related_shipment_id"),
                        rs.getInt("related_personnel_id"),
                        rs.getString("message"),
                        rs.getString("notification_type"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getBoolean("is_read"),
                        rs.getString("contact_method")
                ));
            }
        }
        return notifications;
    }

    /**
     * Retrieves notifications filtered by recipient type.
     *
     * @param recipientType "Customer" or "Personnel"
     * @return List of generic Notification objects matching the recipient type.
     * @throws SQLException If a database access error occurs.
     */
    public List<Notification> getNotificationsByRecipientType(String recipientType) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE recipient_type = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recipientType);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(new Notification(
                            rs.getInt("notification_id"),
                            rs.getString("recipient_type"),
                            rs.getInt("recipient_id"),
                            rs.getInt("related_shipment_id"),
                            rs.getInt("related_personnel_id"),
                            rs.getString("message"),
                            rs.getString("notification_type"),
                            rs.getTimestamp("timestamp").toLocalDateTime(),
                            rs.getBoolean("is_read"),
                            rs.getString("contact_method")
                    ));
                }
            }
        }
        return notifications;
    }

    /**
     * Updates the 'is_read' status of a notification.
     * @param notificationId The ID of the notification to update.
     * @param isRead The new read status (true for read, false for unread).
     * @throws SQLException If a database access error occurs.
     */
    public void updateNotificationReadStatus(int notificationId, boolean isRead) throws SQLException {
        String sql = "UPDATE notifications SET is_read = ? WHERE notification_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isRead);
            pstmt.setInt(2, notificationId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a notification record from the database by its ID.
     * @param notificationId The ID of the notification to delete.
     * @throws SQLException If a database access error occurs.
     */
    public void deleteNotification(int notificationId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE notification_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notificationId);
            pstmt.executeUpdate();
        }
    }
}