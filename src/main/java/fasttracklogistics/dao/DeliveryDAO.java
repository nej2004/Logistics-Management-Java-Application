package fasttracklogistics.dao;

import fasttracklogistics.model.Delivery;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DeliveryDAO {

    /**
     * Adds a new delivery record to the database.
     * The delivery object's ID will be updated with the auto-generated key from the database.
     *
     * @param delivery The Delivery object to add.
     * @throws SQLException If a database access error occurs.
     */
    public void addDelivery(Delivery delivery) throws SQLException {
        String sql = "INSERT INTO deliveries (shipment_id, personnel_id, scheduled_pickup_time, " +
                "actual_pickup_time, scheduled_delivery_time, actual_delivery_time, " +
                "delivery_status, route_details, delivery_notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, delivery.getShipmentId());
            // personnel_id can be null in DB, so handle that
            if (delivery.getPersonnelId() != 0) { // Assuming 0 or -1 means not assigned
                stmt.setInt(2, delivery.getPersonnelId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setTimestamp(3, delivery.getScheduledPickupTime() != null ? Timestamp.valueOf(delivery.getScheduledPickupTime()) : null);
            stmt.setTimestamp(4, delivery.getActualPickupTime() != null ? Timestamp.valueOf(delivery.getActualPickupTime()) : null);
            stmt.setTimestamp(5, delivery.getScheduledDeliveryTime() != null ? Timestamp.valueOf(delivery.getScheduledDeliveryTime()) : null);
            stmt.setTimestamp(6, delivery.getActualDeliveryTime() != null ? Timestamp.valueOf(delivery.getActualDeliveryTime()) : null);
            stmt.setString(7, delivery.getDeliveryStatus());
            stmt.setString(8, delivery.getRouteDetails());
            stmt.setString(9, delivery.getDeliveryNotes());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    delivery.setDeliveryId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Retrieves a delivery record from the database by its ID.
     *
     * @param deliveryId The ID of the delivery to retrieve.
     * @return The Delivery object if found, otherwise null.
     * @throws SQLException If a database access error occurs.
     */
    public Delivery getDeliveryById(int deliveryId) throws SQLException {
        String sql = "SELECT * FROM deliveries WHERE delivery_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deliveryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDelivery(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all delivery records from the database.
     *
     * @return A list of all Delivery objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Delivery> getAllDeliveries() throws SQLException {
        List<Delivery> deliveries = new ArrayList<>();
        String sql = "SELECT * FROM deliveries";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                deliveries.add(mapResultSetToDelivery(rs));
            }
        }
        return deliveries;
    }

    /**
     * Retrieves delivery records associated with a specific shipment.
     *
     * @param shipmentId The ID of the shipment.
     * @return A list of Delivery objects for the given shipment.
     * @throws SQLException If a database access error occurs.
     */
    public List<Delivery> getDeliveriesByShipmentId(int shipmentId) throws SQLException {
        List<Delivery> deliveries = new ArrayList<>();
        String sql = "SELECT * FROM deliveries WHERE shipment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shipmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    deliveries.add(mapResultSetToDelivery(rs));
                }
            }
        }
        return deliveries;
    }

    /**
     * Retrieves delivery records assigned to a specific personnel.
     *
     * @param personnelId The ID of the personnel.
     * @return A list of Delivery objects for the given personnel.
     * @throws SQLException If a database access error occurs.
     */
    public List<Delivery> getDeliveriesByPersonnelId(int personnelId) throws SQLException {
        List<Delivery> deliveries = new ArrayList<>();
        String sql = "SELECT * FROM deliveries WHERE personnel_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personnelId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    deliveries.add(mapResultSetToDelivery(rs));
                }
            }
        }
        return deliveries;
    }

    /**
     * Updates an existing delivery record in the database.
     *
     * @param delivery The Delivery object with updated information.
     * @throws SQLException If a database access error occurs.
     */
    public void updateDelivery(Delivery delivery) throws SQLException {
        String sql = "UPDATE deliveries SET shipment_id=?, personnel_id=?, scheduled_pickup_time=?, " +
                "actual_pickup_time=?, scheduled_delivery_time=?, actual_delivery_time=?, " +
                "delivery_status=?, route_details=?, delivery_notes=? WHERE delivery_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, delivery.getShipmentId());
            if (delivery.getPersonnelId() != 0) {
                stmt.setInt(2, delivery.getPersonnelId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setTimestamp(3, delivery.getScheduledPickupTime() != null ? Timestamp.valueOf(delivery.getScheduledPickupTime()) : null);
            stmt.setTimestamp(4, delivery.getActualPickupTime() != null ? Timestamp.valueOf(delivery.getActualPickupTime()) : null);
            stmt.setTimestamp(5, delivery.getScheduledDeliveryTime() != null ? Timestamp.valueOf(delivery.getScheduledDeliveryTime()) : null);
            stmt.setTimestamp(6, delivery.getActualDeliveryTime() != null ? Timestamp.valueOf(delivery.getActualDeliveryTime()) : null);
            stmt.setString(7, delivery.getDeliveryStatus());
            stmt.setString(8, delivery.getRouteDetails());
            stmt.setString(9, delivery.getDeliveryNotes());
            stmt.setInt(10, delivery.getDeliveryId());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a delivery record from the database by its ID.
     *
     * @param deliveryId The ID of the delivery to delete.
     * @throws SQLException If a database access error occurs.
     */
    public void deleteDelivery(int deliveryId) throws SQLException {
        String sql = "DELETE FROM deliveries WHERE delivery_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deliveryId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps a ResultSet row to a Delivery object.
     *
     * @param rs The ResultSet containing delivery data.
     * @return A populated Delivery object.
     * @throws SQLException If a database access error occurs.
     */
    private Delivery mapResultSetToDelivery(ResultSet rs) throws SQLException {
        Delivery delivery = new Delivery();
        delivery.setDeliveryId(rs.getInt("delivery_id"));
        delivery.setShipmentId(rs.getInt("shipment_id"));
        // Check for NULL personnel_id from DB
        int personnelId = rs.getInt("personnel_id");
        if (rs.wasNull()) {
            delivery.setPersonnelId(0); // Or some other indicator for unassigned
        } else {
            delivery.setPersonnelId(personnelId);
        }
        delivery.setScheduledPickupTime(rs.getTimestamp("scheduled_pickup_time") != null ? rs.getTimestamp("scheduled_pickup_time").toLocalDateTime() : null);
        delivery.setActualPickupTime(rs.getTimestamp("actual_pickup_time") != null ? rs.getTimestamp("actual_pickup_time").toLocalDateTime() : null);
        delivery.setScheduledDeliveryTime(rs.getTimestamp("scheduled_delivery_time") != null ? rs.getTimestamp("scheduled_delivery_time").toLocalDateTime() : null);
        delivery.setActualDeliveryTime(rs.getTimestamp("actual_delivery_time") != null ? rs.getTimestamp("actual_delivery_time").toLocalDateTime() : null);
        delivery.setDeliveryStatus(rs.getString("delivery_status"));
        delivery.setRouteDetails(rs.getString("route_details"));
        delivery.setDeliveryNotes(rs.getString("delivery_notes"));
        return delivery;
    }
}