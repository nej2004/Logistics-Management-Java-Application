package fasttracklogistics.controller;

import fasttracklogistics.model.Shipment;
import fasttracklogistics.dao.ShipmentDAO;
import fasttracklogistics.view.TrackShipmentPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class TrackShipmentController {
    private ShipmentDAO shipmentDAO;
    private TrackShipmentPanel trackShipmentView;

    public TrackShipmentController(TrackShipmentPanel trackShipmentView, ShipmentDAO shipmentDAO) {
        this.trackShipmentView = trackShipmentView;
        this.shipmentDAO = shipmentDAO;

        initListeners();
    }

    private void initListeners() {
        trackShipmentView.addTrackButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trackShipment();
            }
        });
    }

    private void trackShipment() {
        String trackingNumber = trackShipmentView.getTrackingNumberInput();
        if (trackingNumber.isEmpty()) {
            trackShipmentView.showMessage("Please enter a tracking number.", "Input Required", JOptionPane.WARNING_MESSAGE);
            trackShipmentView.clearDisplay();
            return;
        }

        try {
            Shipment shipment = shipmentDAO.getShipmentByTrackingNumber(trackingNumber);

            if (shipment == null) {
                trackShipmentView.showMessage("Shipment with tracking number '" + trackingNumber + "' not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                trackShipmentView.clearDisplay();
            } else {
                trackShipmentView.displayShipmentDetails(shipment);
            }
        } catch (SQLException e) {
            trackShipmentView.showMessage("Error tracking shipment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            trackShipmentView.clearDisplay();
        }
    }
}
