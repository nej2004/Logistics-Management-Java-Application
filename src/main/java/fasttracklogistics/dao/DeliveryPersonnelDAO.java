package fasttracklogistics.dao;

import fasttracklogistics.model.DeliveryPersonnel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeliveryPersonnelDAO {

    /**
     * Adds a new delivery personnel record to the database.
     * The personnel object's ID will be updated with the auto-generated key from the database.
     *
     * @param personnel The DeliveryPersonnel object to add.
     * @throws SQLException If a database access error occurs.
     */
    public void addPersonnel(DeliveryPersonnel personnel) throws SQLException {
        String sql = "INSERT INTO delivery_personnel (name, contact_info, personnel_type, license_number, vehicle_details, availability_status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, personnel.getName());
            stmt.setString(2, personnel.getContactInfo());
            stmt.setString(3, personnel.getPersonnelType());
            stmt.setString(4, personnel.getLicenseNumber());
            stmt.setString(5, personnel.getVehicleDetails());
            stmt.setString(6, personnel.getAvailabilityStatus());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    personnel.setPersonnelId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Retrieves a delivery personnel record from the database by its ID.
     *
     * @param personnelId The ID of the personnel to retrieve.
     * @return The DeliveryPersonnel object if found, otherwise null.
     * @throws SQLException If a database access error occurs.
     */
    public DeliveryPersonnel getPersonnelById(int personnelId) throws SQLException {
        String sql = "SELECT * FROM delivery_personnel WHERE personnel_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personnelId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPersonnel(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all delivery personnel records from the database.
     *
     * @return A list of all DeliveryPersonnel objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<DeliveryPersonnel> getAllPersonnel() throws SQLException {
        List<DeliveryPersonnel> personnelList = new ArrayList<>();
        String sql = "SELECT * FROM delivery_personnel";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                personnelList.add(mapResultSetToPersonnel(rs));
            }
        }
        return personnelList;
    }

    /**
     * Updates an existing delivery personnel record in the database.
     *
     * @param personnel The DeliveryPersonnel object with updated information.
     * @throws SQLException If a database access error occurs.
     */
    public void updatePersonnel(DeliveryPersonnel personnel) throws SQLException {
        String sql = "UPDATE delivery_personnel SET name=?, contact_info=?, personnel_type=?, license_number=?, vehicle_details=?, availability_status=? WHERE personnel_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personnel.getName());
            stmt.setString(2, personnel.getContactInfo());
            stmt.setString(3, personnel.getPersonnelType());
            stmt.setString(4, personnel.getLicenseNumber());
            stmt.setString(5, personnel.getVehicleDetails());
            stmt.setString(6, personnel.getAvailabilityStatus());
            stmt.setInt(7, personnel.getPersonnelId());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a delivery personnel record from the database by its ID.
     *
     * @param personnelId The ID of the personnel to delete.
     * @throws SQLException If a database access error occurs.
     */
    public void deletePersonnel(int personnelId) throws SQLException {
        String sql = "DELETE FROM delivery_personnel WHERE personnel_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personnelId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps a ResultSet row to a DeliveryPersonnel object.
     *
     * @param rs The ResultSet containing personnel data.
     * @return A populated DeliveryPersonnel object.
     * @throws SQLException If a database access error occurs.
     */
    private DeliveryPersonnel mapResultSetToPersonnel(ResultSet rs) throws SQLException {
        DeliveryPersonnel personnel = new DeliveryPersonnel();
        personnel.setPersonnelId(rs.getInt("personnel_id"));
        personnel.setName(rs.getString("name"));
        personnel.setContactInfo(rs.getString("contact_info"));
        personnel.setPersonnelType(rs.getString("personnel_type"));
        personnel.setLicenseNumber(rs.getString("license_number"));
        personnel.setVehicleDetails(rs.getString("vehicle_details"));
        personnel.setAvailabilityStatus(rs.getString("availability_status"));
        return personnel;
    }
}