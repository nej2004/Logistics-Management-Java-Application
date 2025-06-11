package fasttracklogistics.controller;

import fasttracklogistics.model.Delivery;
import fasttracklogistics.model.Shipment;
import fasttracklogistics.model.DeliveryPersonnel;
import fasttracklogistics.dao.DeliveryDAO;
import fasttracklogistics.dao.ShipmentDAO;
import fasttracklogistics.dao.DeliveryPersonnelDAO;
import fasttracklogistics.view.ScheduleDeliveryPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeliveryController {
    private DeliveryDAO deliveryDAO;
    private ShipmentDAO shipmentDAO;
    private DeliveryPersonnelDAO personnelDAO;
    private ScheduleDeliveryPanel scheduleDeliveryView;

    public DeliveryController(ScheduleDeliveryPanel scheduleDeliveryView,
                              DeliveryDAO deliveryDAO, ShipmentDAO shipmentDAO, DeliveryPersonnelDAO personnelDAO) {
        this.scheduleDeliveryView = scheduleDeliveryView;
        this.deliveryDAO = deliveryDAO;
        this.shipmentDAO = shipmentDAO;
        this.personnelDAO = personnelDAO;

        initListeners();
        loadAllData(); // Initial load of all related data
    }

    private void initListeners() {
        scheduleDeliveryView.addAddButtonListener(e -> addDelivery());
        scheduleDeliveryView.addUpdateButtonListener(e -> updateDelivery());
        scheduleDeliveryView.addRemoveButtonListener(e -> removeDelivery());
        scheduleDeliveryView.addClearButtonListener(e -> scheduleDeliveryView.clearFields());
        scheduleDeliveryView.addTableSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    displaySelectedDeliveryDetails();
                }
            }
        });
        // If you need to add listeners directly to the combo boxes from the controller
        // scheduleDeliveryView.getShipmentComboBox().addActionListener(e -> /* handle shipment selection */);
        // scheduleDeliveryView.getPersonnelComboBox().addActionListener(e -> /* handle personnel selection */);
    }

    public void loadAllData() {
        try {
            List<Shipment> shipments = shipmentDAO.getAllShipments();
            scheduleDeliveryView.populateShipmentComboBox(shipments);

            List<DeliveryPersonnel> personnelList = personnelDAO.getAllPersonnel();
            scheduleDeliveryView.populatePersonnelComboBox(personnelList);

            List<Delivery> deliveries = deliveryDAO.getAllDeliveries();
            scheduleDeliveryView.displayDeliveries(deliveries);

        } catch (SQLException e) {
            scheduleDeliveryView.showMessage("Error loading data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addDelivery() {
        Delivery newDelivery = scheduleDeliveryView.getDeliveryFromFields();
        if (newDelivery == null) { // Validation failed in view
            return;
        }

        try {
            deliveryDAO.addDelivery(newDelivery);
            // Optionally update shipment status to "Scheduled" if it's pending
            Shipment associatedShipment = shipmentDAO.getShipmentById(newDelivery.getShipmentId()); // Corrected variable name
            if (associatedShipment != null && associatedShipment.getDeliveryStatus().equals("Pending")) {
                associatedShipment.setDeliveryStatus("Scheduled");
                shipmentDAO.updateShipment(associatedShipment);
            }

            scheduleDeliveryView.showMessage("Delivery scheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            scheduleDeliveryView.clearFields();
            loadAllData(); // Refresh all data
        } catch (SQLException e) {
            scheduleDeliveryView.showMessage("Error scheduling delivery: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateDelivery() {
        int selectedId = scheduleDeliveryView.getSelectedDeliveryId();
        if (selectedId == -1) {
            scheduleDeliveryView.showMessage("Please select a delivery to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Delivery updatedDelivery = scheduleDeliveryView.getDeliveryFromFields();
        if (updatedDelivery == null) { // Validation failed
            return;
        }
        updatedDelivery.setDeliveryId(selectedId);

        try {
            deliveryDAO.updateDelivery(updatedDelivery);
            // Optionally update shipment status based on delivery status
            Shipment associatedShipment = shipmentDAO.getShipmentById(updatedDelivery.getShipmentId());
            if (associatedShipment != null && !associatedShipment.getDeliveryStatus().equals(updatedDelivery.getDeliveryStatus())) {
                associatedShipment.setDeliveryStatus(updatedDelivery.getDeliveryStatus());
                shipmentDAO.updateShipment(associatedShipment);
            }

            scheduleDeliveryView.showMessage("Delivery updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            scheduleDeliveryView.clearFields();
            loadAllData();
        } catch (SQLException e) {
            scheduleDeliveryView.showMessage("Error updating delivery: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void removeDelivery() {
        int selectedId = scheduleDeliveryView.getSelectedDeliveryId();
        if (selectedId == -1) {
            scheduleDeliveryView.showMessage("Please select a delivery to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = scheduleDeliveryView.showConfirmDialog("Are you sure you want to remove this delivery?", "Confirm Removal");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                deliveryDAO.deleteDelivery(selectedId);
                scheduleDeliveryView.showMessage("Delivery removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                scheduleDeliveryView.clearFields();
                loadAllData();
            } catch (SQLException e) {
                scheduleDeliveryView.showMessage("Error removing delivery: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void displaySelectedDeliveryDetails() {
        int selectedId = scheduleDeliveryView.getSelectedDeliveryId();
        if (selectedId != -1) {
            try {
                Delivery selectedDelivery = deliveryDAO.getDeliveryById(selectedId);
                if (selectedDelivery != null) {
                    // Need to get display names for combo boxes
                    Shipment associatedShipment = shipmentDAO.getShipmentById(selectedDelivery.getShipmentId());
                    DeliveryPersonnel associatedPersonnel = personnelDAO.getPersonnelById(selectedDelivery.getPersonnelId());

                    String shipmentDisplayName = (associatedShipment != null) ?
                            associatedShipment.getTrackingNumber() + " - " + associatedShipment.getReceiverName() : "Unknown Shipment";
                    String personnelDisplayName = (associatedPersonnel != null) ?
                            associatedPersonnel.getName() + " (" + associatedPersonnel.getAvailabilityStatus() + ")" : "Unassigned";

                    scheduleDeliveryView.setDeliveryFields(selectedDelivery); // Pass the delivery object

                    // Corrected: Use getter methods to access and set the selected items in JComboBoxes
                    // These lines assume you have getShipmentComboBox() and getPersonnelComboBox() in ScheduleDeliveryPanel
                    scheduleDeliveryView.getShipmentComboBox().setSelectedItem(shipmentDisplayName);
                    scheduleDeliveryView.getPersonnelComboBox().setSelectedItem(personnelDisplayName);
                }
            } catch (SQLException e) {
                scheduleDeliveryView.showMessage("Error retrieving delivery details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            scheduleDeliveryView.clearFields();
        }
    }
}
