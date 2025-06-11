package fasttracklogistics.controller;

import fasttracklogistics.model.Shipment;
import fasttracklogistics.model.DeliveryPersonnel;
import fasttracklogistics.model.Delivery;
import fasttracklogistics.dao.ShipmentDAO;
import fasttracklogistics.dao.DeliveryPersonnelDAO;
import fasttracklogistics.dao.DeliveryDAO;
import fasttracklogistics.view.AssignDriversPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AssignDriversController {
    private AssignDriversPanel assignDriversView;
    private ShipmentDAO shipmentDAO;
    private DeliveryPersonnelDAO personnelDAO;
    private DeliveryDAO deliveryDAO;

    public AssignDriversController(AssignDriversPanel assignDriversView,
                                   ShipmentDAO shipmentDAO,
                                   DeliveryPersonnelDAO personnelDAO,
                                   DeliveryDAO deliveryDAO) {
        this.assignDriversView = assignDriversView;
        this.shipmentDAO = shipmentDAO;
        this.personnelDAO = personnelDAO;
        this.deliveryDAO = deliveryDAO;

        initListeners();
        loadData(); // Initial load
    }

    private void initListeners() {
        assignDriversView.addAssignButtonListener(e -> assignDriverToShipment());
        assignDriversView.addRefreshButtonListener(e -> loadData());
    }

    public void loadData() {
        try {
            // Load unassigned shipments (e.g., status is 'Pending')
            List<Shipment> allShipments = shipmentDAO.getAllShipments();
            List<Shipment> unassignedShipments = allShipments.stream()
                    .filter(s -> s.getDeliveryStatus().equals("Pending")) // Define "unassigned" status here
                    .collect(Collectors.toList());

            assignDriversView.displayUnassignedShipments(unassignedShipments);

            // Load available personnel (e.g., status is 'Available')
            List<DeliveryPersonnel> availablePersonnel = personnelDAO.getAllPersonnel().stream()
                    .filter(p -> p.getAvailabilityStatus().equals("Available"))
                    .collect(Collectors.toList());

            assignDriversView.displayAvailablePersonnel(availablePersonnel);

        } catch (SQLException e) {
            assignDriversView.showMessage("Error loading data for assignment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void assignDriverToShipment() {
        int selectedShipmentId = assignDriversView.getSelectedShipmentId();
        int selectedPersonnelId = assignDriversView.getSelectedPersonnelId();

        if (selectedShipmentId == -1) {
            assignDriversView.showMessage("Please select a shipment to assign.", "No Shipment Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedPersonnelId == -1) {
            assignDriversView.showMessage("Please select a delivery personnel to assign.", "No Personnel Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Shipment shipmentToAssign = shipmentDAO.getShipmentById(selectedShipmentId);
            DeliveryPersonnel assignedPersonnel = personnelDAO.getPersonnelById(selectedPersonnelId);

            if (shipmentToAssign == null || assignedPersonnel == null) {
                assignDriversView.showMessage("Selected shipment or personnel not found. Please refresh and try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if a delivery already exists for this shipment
            List<Delivery> existingDeliveries = deliveryDAO.getDeliveriesByShipmentId(shipmentToAssign.getShipmentId());
            if (!existingDeliveries.isEmpty()) {
                assignDriversView.showMessage("This shipment is already assigned to a delivery. Please update the existing delivery instead.", "Assignment Error", JOptionPane.WARNING_MESSAGE);
                return;
            }


            // Create a new Delivery entry
            Delivery newDelivery = new Delivery(
                    shipmentToAssign.getShipmentId(),
                    assignedPersonnel.getPersonnelId(),
                    LocalDateTime.now(), // Scheduled pickup can be now or estimated
                    shipmentToAssign.getEstimatedDeliveryTime(), // Use shipment's estimated delivery
                    "Scheduled", // Initial status
                    "Auto-assigned route", // Placeholder for route details
                    "Assigned automatically through system." // Delivery notes
            );

            deliveryDAO.addDelivery(newDelivery);

            // Update shipment status to "Scheduled"
            shipmentToAssign.setDeliveryStatus("Scheduled");
            shipmentDAO.updateShipment(shipmentToAssign);

            // Update personnel status to "On Duty" if they are available
            if (assignedPersonnel.getAvailabilityStatus().equals("Available")) {
                assignedPersonnel.setAvailabilityStatus("On Duty");
                personnelDAO.updatePersonnel(assignedPersonnel);
            }

            assignDriversView.showMessage("Shipment " + shipmentToAssign.getTrackingNumber() +
                            " successfully assigned to " + assignedPersonnel.getName(),
                    "Assignment Successful", JOptionPane.INFORMATION_MESSAGE);
            loadData(); // Refresh tables after assignment
        } catch (SQLException e) {
            assignDriversView.showMessage("Error assigning driver: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
