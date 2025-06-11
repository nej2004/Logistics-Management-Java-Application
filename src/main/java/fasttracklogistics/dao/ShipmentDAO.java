package fasttracklogistics.dao;

import fasttracklogistics.model.Shipment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShipmentDAO {

    /**
     * Adds a new shipment record to the database.
     * The shipment object's ID will be updated with the auto-generated key from the database.
     *
     * @param shipment The Shipment object to add.
     * @throws SQLException If a database access error occurs.
     */
    public void addShipment(Shipment shipment) throws SQLException {
        String sql = "INSERT INTO shipments (tracking_number, sender_name, sender_address, sender_contact, " +
                "receiver_name, receiver_address, receiver_contact, package_contents, weight, dimensions, " +
                "delivery_status, current_location, estimated_delivery_time, special_instructions) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, shipment.getTrackingNumber());
            stmt.setString(2, shipment.getSenderName());
            stmt.setString(3, shipment.getSenderAddress());
            stmt.setString(4, shipment.getSenderContact());
            stmt.setString(5, shipment.getReceiverName());
            stmt.setString(6, shipment.getReceiverAddress());
            stmt.setString(7, shipment.getReceiverContact());
            stmt.setString(8, shipment.getPackageContents());
            stmt.setDouble(9, shipment.getWeight());
            stmt.setString(10, shipment.getDimensions());
            stmt.setString(11, shipment.getDeliveryStatus());
            stmt.setString(12, shipment.getCurrentLocation());
            stmt.setTimestamp(13, shipment.getEstimatedDeliveryTime() != null ? Timestamp.valueOf(shipment.getEstimatedDeliveryTime()) : null);
            stmt.setString(14, shipment.getSpecialInstructions());

            stmt.executeUpdate();

            // Retrieve the auto-generated shipment_id
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    shipment.setShipmentId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Retrieves a shipment record from the database by its ID.
     *
     * @param shipmentId The ID of the shipment to retrieve.
     * @return The Shipment object if found, otherwise null.
     * @throws SQLException If a database access error occurs.
     */
    public Shipment getShipmentById(int shipmentId) throws SQLException {
        String sql = "SELECT * FROM shipments WHERE shipment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shipmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToShipment(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a shipment record from the database by its tracking number.
     *
     * @param trackingNumber The tracking number of the shipment to retrieve.
     * @return The Shipment object if found, otherwise null.
     * @throws SQLException If a database access error occurs.
     */
    public Shipment getShipmentByTrackingNumber(String trackingNumber) throws SQLException {
        String sql = "SELECT * FROM shipments WHERE tracking_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trackingNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToShipment(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all shipment records from the database.
     *
     * @return A list of all Shipment objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Shipment> getAllShipments() throws SQLException {
        List<Shipment> shipments = new ArrayList<>();
        String sql = "SELECT * FROM shipments";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                shipments.add(mapResultSetToShipment(rs));
            }
        }
        return shipments;
    }

    /**
     * Updates an existing shipment record in the database.
     *
     * @param shipment The Shipment object with updated information.
     * @throws SQLException If a database access error occurs.
     */
    public void updateShipment(Shipment shipment) throws SQLException {
        String sql = "UPDATE shipments SET tracking_number=?, sender_name=?, sender_address=?, sender_contact=?, " +
                "receiver_name=?, receiver_address=?, receiver_contact=?, package_contents=?, weight=?, dimensions=?, " +
                "delivery_status=?, current_location=?, estimated_delivery_time=?, actual_delivery_time=?, special_instructions=? " +
                "WHERE shipment_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, shipment.getTrackingNumber());
            stmt.setString(2, shipment.getSenderName());
            stmt.setString(3, shipment.getSenderAddress());
            stmt.setString(4, shipment.getSenderContact());
            stmt.setString(5, shipment.getReceiverName());
            stmt.setString(6, shipment.getReceiverAddress());
            stmt.setString(7, shipment.getReceiverContact());
            stmt.setString(8, shipment.getPackageContents());
            stmt.setDouble(9, shipment.getWeight());
            stmt.setString(10, shipment.getDimensions());
            stmt.setString(11, shipment.getDeliveryStatus());
            stmt.setString(12, shipment.getCurrentLocation());
            stmt.setTimestamp(13, shipment.getEstimatedDeliveryTime() != null ? Timestamp.valueOf(shipment.getEstimatedDeliveryTime()) : null);
            stmt.setTimestamp(14, shipment.getActualDeliveryTime() != null ? Timestamp.valueOf(shipment.getActualDeliveryTime()) : null);
            stmt.setString(15, shipment.getSpecialInstructions());
            stmt.setInt(16, shipment.getShipmentId());

            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a shipment record from the database by its ID.
     *
     * @param shipmentId The ID of the shipment to delete.
     * @throws SQLException If a database access error occurs.
     */
    public void deleteShipment(int shipmentId) throws SQLException {
        String sql = "DELETE FROM shipments WHERE shipment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shipmentId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps a ResultSet row to a Shipment object.
     * This is a private helper method used by all read operations.
     *
     * @param rs The ResultSet containing shipment data.
     * @return A populated Shipment object.
     * @throws SQLException If a database access error occurs.
     */
    private Shipment mapResultSetToShipment(ResultSet rs) throws SQLException {
        Shipment shipment = new Shipment();
        shipment.setShipmentId(rs.getInt("shipment_id"));
        shipment.setTrackingNumber(rs.getString("tracking_number"));
        shipment.setSenderName(rs.getString("sender_name"));
        shipment.setSenderAddress(rs.getString("sender_address"));
        shipment.setSenderContact(rs.getString("sender_contact"));
        shipment.setReceiverName(rs.getString("receiver_name"));
        shipment.setReceiverAddress(rs.getString("receiver_address"));
        shipment.setReceiverContact(rs.getString("receiver_contact"));
        shipment.setPackageContents(rs.getString("package_contents"));
        shipment.setWeight(rs.getDouble("weight"));
        shipment.setDimensions(rs.getString("dimensions"));
        shipment.setDeliveryStatus(rs.getString("delivery_status"));
        shipment.setCurrentLocation(rs.getString("current_location"));

        Timestamp estTime = rs.getTimestamp("estimated_delivery_time");
        if (estTime != null) {
            shipment.setEstimatedDeliveryTime(estTime.toLocalDateTime());
        }
        Timestamp actTime = rs.getTimestamp("actual_delivery_time");
        if (actTime != null) {
            shipment.setActualDeliveryTime(actTime.toLocalDateTime());
        }
        shipment.setSpecialInstructions(rs.getString("special_instructions"));

        Timestamp creationTs = rs.getTimestamp("creation_date");
        if (creationTs != null) {
            shipment.setCreationDate(creationTs.toLocalDateTime());
        }

        return shipment;
    }
}