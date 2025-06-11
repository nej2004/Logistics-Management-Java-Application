package fasttracklogistics.controller;

import fasttracklogistics.model.Shipment;
import fasttracklogistics.dao.ShipmentDAO;
import fasttracklogistics.view.ManageShipmentsPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ShipmentController {
    private ManageShipmentsPanel shipmentView;
    private ShipmentDAO shipmentDAO;

    public ShipmentController(ManageShipmentsPanel shipmentView, ShipmentDAO shipmentDAO) {
        this.shipmentView = shipmentView;
        this.shipmentDAO = shipmentDAO;

        initListeners();
        loadShipments(); // Load initial data when the panel is displayed
    }

    private void initListeners() {
        shipmentView.addAddButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addShipment();
            }
        });

        shipmentView.addUpdateButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateShipment();
            }
        });

        shipmentView.addRemoveButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteShipment();
            }
        });

        shipmentView.addClearButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shipmentView.clearFields();
            }
        });

        shipmentView.addTableSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Ensures action only on final selection change
                displaySelectedShipment();
            }
        });
    }

    public void loadShipments() {
        try {
            List<Shipment> shipments = shipmentDAO.getAllShipments();
            shipmentView.displayShipments(shipments);
        } catch (SQLException e) {
            shipmentView.showMessage("Error loading shipments: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addShipment() {
        Shipment shipment = shipmentView.getShipmentFromFields();
        if (shipment == null) { // Validation failed in view
            return;
        }

        try {
            shipmentDAO.addShipment(shipment);
            shipmentView.showMessage("Shipment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadShipments(); // Refresh table
            shipmentView.clearFields();
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("tracking_number")) {
                shipmentView.showMessage("Error: Tracking number already exists. Please use a unique tracking number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                shipmentView.showMessage("Error adding shipment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        }
    }

    private void updateShipment() {
        int selectedId = shipmentView.getSelectedShipmentId();
        if (selectedId == -1) {
            shipmentView.showMessage("Please select a shipment to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Shipment shipment = shipmentView.getShipmentFromFields();
        if (shipment == null) { // Validation failed in view
            return;
        }
        shipment.setShipmentId(selectedId); // Set the ID of the selected shipment

        try {
            shipmentDAO.updateShipment(shipment);
            shipmentView.showMessage("Shipment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadShipments(); // Refresh table
            shipmentView.clearFields();
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("tracking_number")) {
                shipmentView.showMessage("Error: Tracking number already exists. Please use a unique tracking number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                shipmentView.showMessage("Error updating shipment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        }
    }

    private void deleteShipment() {
        int selectedId = shipmentView.getSelectedShipmentId();
        if (selectedId == -1) {
            shipmentView.showMessage("Please select a shipment to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = shipmentView.showConfirmDialog("Are you sure you want to delete this shipment?", "Confirm Deletion");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                shipmentDAO.deleteShipment(selectedId);
                shipmentView.showMessage("Shipment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadShipments(); // Refresh table
                shipmentView.clearFields();
            } catch (SQLException e) {
                shipmentView.showMessage("Error deleting shipment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void displaySelectedShipment() {
        int selectedId = shipmentView.getSelectedShipmentId();
        if (selectedId != -1) {
            try {
                Shipment shipment = shipmentDAO.getShipmentById(selectedId);
                if (shipment != null) {
                    shipmentView.setShipmentFields(shipment);
                }
            } catch (SQLException e) {
                shipmentView.showMessage("Error fetching shipment details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            shipmentView.clearFields();
        }
    }
}