package fasttracklogistics;

import fasttracklogistics.dao.DeliveryDAO;
import fasttracklogistics.dao.DeliveryPersonnelDAO;
import fasttracklogistics.dao.NotificationDAO;
import fasttracklogistics.dao.ShipmentDAO;

import fasttracklogistics.view.ManageShipmentsPanel;
import fasttracklogistics.view.ManagePersonnelPanel;
import fasttracklogistics.view.ScheduleDeliveryPanel;
import fasttracklogistics.view.TrackShipmentPanel;
import fasttracklogistics.view.AssignDriversPanel;
import fasttracklogistics.view.ReportsPanel;
import fasttracklogistics.view.NotificationPanel;

import fasttracklogistics.controller.ShipmentController;
import fasttracklogistics.controller.PersonnelController;
import fasttracklogistics.controller.DeliveryController;
import fasttracklogistics.controller.TrackShipmentController;
import fasttracklogistics.controller.AssignDriversController;
import fasttracklogistics.controller.ReportController;
import fasttracklogistics.controller.NotificationController;


import javax.swing.*;
import java.awt.*;
import java.sql.SQLException; // Only needed if you catch SQLException here, but DAOs handle it

public class FastTrackLogisticsApp {

    public static void main(String[] args) {
        // Ensure Swing operations are on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Instantiate DAOs (Models) - these are responsible for database interaction
            // No try-catch here, as DAOs will handle their own connection errors
            ShipmentDAO shipmentDAO = new ShipmentDAO();
            DeliveryPersonnelDAO personnelDAO = new DeliveryPersonnelDAO();
            DeliveryDAO deliveryDAO = new DeliveryDAO();
            NotificationDAO notificationDAO = new NotificationDAO();

            // Create the main application frame
            JFrame frame = new JFrame("FastTrack Logistics Management System");
            frame.setSize(1200, 800); // Increased size for better layout
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null); // Center the window

            // Create the tabbed pane
            JTabbedPane tabbedPane = new JTabbedPane();

            // Instantiate Views (UI Panels)
            ManageShipmentsPanel manageShipmentsPanel = new ManageShipmentsPanel();
            ManagePersonnelPanel managePersonnelPanel = new ManagePersonnelPanel();
            ScheduleDeliveryPanel scheduleDeliveryPanel = new ScheduleDeliveryPanel();
            TrackShipmentPanel trackShipmentPanel = new TrackShipmentPanel();
            AssignDriversPanel assignDriversPanel = new AssignDriversPanel();
            ReportsPanel reportsPanel = new ReportsPanel();
            NotificationPanel notificationPanel = new NotificationPanel();


            // Instantiate Controllers, wiring Views to DAOs
            new ShipmentController(manageShipmentsPanel, shipmentDAO);
            new PersonnelController(managePersonnelPanel, personnelDAO);
            new DeliveryController(scheduleDeliveryPanel, deliveryDAO, shipmentDAO, personnelDAO);
            new TrackShipmentController(trackShipmentPanel, shipmentDAO);
            new AssignDriversController(assignDriversPanel, shipmentDAO, personnelDAO, deliveryDAO);
            new ReportController(reportsPanel, shipmentDAO, deliveryDAO, personnelDAO);
            new NotificationController(notificationPanel, notificationDAO, shipmentDAO, personnelDAO);


            // Add panels to the tabbed pane
            tabbedPane.addTab("Manage Shipments", manageShipmentsPanel);
            tabbedPane.addTab("Manage Personnel", managePersonnelPanel);
            tabbedPane.addTab("Schedule Delivery", scheduleDeliveryPanel);
            tabbedPane.addTab("Track Shipment", trackShipmentPanel);
            tabbedPane.addTab("Assign Drivers", assignDriversPanel);
            tabbedPane.addTab("Reports", reportsPanel);
            tabbedPane.addTab("Notifications", notificationPanel);


            frame.add(tabbedPane, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}
