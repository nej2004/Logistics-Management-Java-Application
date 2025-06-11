package fasttracklogistics.view;

import fasttracklogistics.model.DeliveryPersonnel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class ManagePersonnelPanel extends JPanel {
    private JTable personnelTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, contactInfoField, licenseNumberField, vehicleDetailsField;
    private JComboBox<String> personnelTypeComboBox, availabilityStatusComboBox;
    private JButton addButton, updateButton, removeButton, clearButton;

    public ManagePersonnelPanel() {
        setLayout(new BorderLayout(10, 10));

        // --- Table for displaying personnel ---
        tableModel = new DefaultTableModel(new Object[]{
                "ID", "Name", "Contact Info", "Type", "License No.", "Vehicle Details", "Availability"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        personnelTable = new JTable(tableModel);
        personnelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(personnelTable), BorderLayout.CENTER);

        // --- Input form ---
        JPanel inputFormPanel = new JPanel(new GridBagLayout());
        inputFormPanel.setBorder(BorderFactory.createTitledBorder("Personnel Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        inputFormPanel.add(new JLabel("Name:"), gbc(0, row));
        nameField = new JTextField(25);
        inputFormPanel.add(nameField, gbc(1, row++));

        inputFormPanel.add(new JLabel("Contact Info:"), gbc(0, row));
        contactInfoField = new JTextField(25);
        inputFormPanel.add(contactInfoField, gbc(1, row++));

        inputFormPanel.add(new JLabel("Personnel Type:"), gbc(0, row));
        String[] personnelTypes = {"Driver", "Courier", "Administrator"};
        personnelTypeComboBox = new JComboBox<>(personnelTypes);
        inputFormPanel.add(personnelTypeComboBox, gbc(1, row++));

        inputFormPanel.add(new JLabel("License Number:"), gbc(0, row));
        licenseNumberField = new JTextField(25);
        inputFormPanel.add(licenseNumberField, gbc(1, row++));

        inputFormPanel.add(new JLabel("Vehicle Details:"), gbc(0, row));
        vehicleDetailsField = new JTextField(25);
        inputFormPanel.add(vehicleDetailsField, gbc(1, row++));

        inputFormPanel.add(new JLabel("Availability Status:"), gbc(0, row));
        String[] availabilityStatuses = {"Available", "On Duty", "On Leave", "Unavailable"};
        availabilityStatusComboBox = new JComboBox<>(availabilityStatuses);
        inputFormPanel.add(availabilityStatusComboBox, gbc(1, row++));

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Add Personnel");
        updateButton = new JButton("Update Personnel");
        removeButton = new JButton("Remove Personnel");
        clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputFormPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    // Helper for GridBagConstraints
    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.anchor = (x == 0) ? GridBagConstraints.EAST : GridBagConstraints.WEST;
        return gbc;
    }

    // --- Methods to attach listeners ---
    public void addAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void addUpdateButtonListener(ActionListener listener) {
        updateButton.addActionListener(listener);
    }

    public void addRemoveButtonListener(ActionListener listener) {
        removeButton.addActionListener(listener);
    }

    public void addClearButtonListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }

    public void addTableSelectionListener(ListSelectionListener listener) {
        personnelTable.getSelectionModel().addListSelectionListener(listener);
    }

    // --- Methods to update the View ---
    public void displayPersonnel(List<DeliveryPersonnel> personnelList) {
        tableModel.setRowCount(0); // Clear existing data
        for (DeliveryPersonnel p : personnelList) {
            tableModel.addRow(new Object[]{
                    p.getPersonnelId(),
                    p.getName(),
                    p.getContactInfo(),
                    p.getPersonnelType(),
                    p.getLicenseNumber(),
                    p.getVehicleDetails(),
                    p.getAvailabilityStatus()
            });
        }
    }

    // --- Methods to get/set input field values ---
    public DeliveryPersonnel getPersonnelFromFields() { // Renamed for clarity
        String name = nameField.getText().trim();
        String contactInfo = contactInfoField.getText().trim();
        String personnelType = (String) personnelTypeComboBox.getSelectedItem();
        String licenseNumber = licenseNumberField.getText().trim();
        String vehicleDetails = vehicleDetailsField.getText().trim();
        String availabilityStatus = (String) availabilityStatusComboBox.getSelectedItem();

        // Basic validation
        if (name.isEmpty() || contactInfo.isEmpty() || personnelType == null || availabilityStatus == null) {
            showMessage("Name, Contact Info, Personnel Type, and Availability Status are required fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new DeliveryPersonnel(name, contactInfo, personnelType, licenseNumber, vehicleDetails, availabilityStatus);
    }

    public void setPersonnelFields(DeliveryPersonnel personnel) { // Renamed for clarity
        if (personnel == null) {
            clearFields();
            return;
        }
        nameField.setText(personnel.getName() != null ? personnel.getName() : "");
        contactInfoField.setText(personnel.getContactInfo() != null ? personnel.getContactInfo() : "");
        personnelTypeComboBox.setSelectedItem(personnel.getPersonnelType() != null ? personnel.getPersonnelType() : "Driver");
        licenseNumberField.setText(personnel.getLicenseNumber() != null ? personnel.getLicenseNumber() : "");
        vehicleDetailsField.setText(personnel.getVehicleDetails() != null ? personnel.getVehicleDetails() : "");
        availabilityStatusComboBox.setSelectedItem(personnel.getAvailabilityStatus() != null ? personnel.getAvailabilityStatus() : "Available");
    }

    public void clearFields() { // Renamed for clarity
        nameField.setText("");
        contactInfoField.setText("");
        personnelTypeComboBox.setSelectedItem("Driver");
        licenseNumberField.setText("");
        vehicleDetailsField.setText("");
        availabilityStatusComboBox.setSelectedItem("Available");
        personnelTable.clearSelection();
    }

    public int getSelectedPersonnelId() {
        int selectedRow = personnelTable.getSelectedRow();
        if (selectedRow != -1) {
            Object idValue = tableModel.getValueAt(selectedRow, 0);
            if (idValue instanceof Integer) {
                return (int) idValue;
            } else if (idValue instanceof String) {
                try {
                    return Integer.parseInt((String) idValue);
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return -1;
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
    }
}