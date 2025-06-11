package fasttracklogistics.controller;

import fasttracklogistics.dao.DeliveryDAO;
import fasttracklogistics.dao.ShipmentDAO;
import fasttracklogistics.dao.DeliveryPersonnelDAO;
import fasttracklogistics.model.Delivery;
import fasttracklogistics.model.DeliveryPersonnel;
import fasttracklogistics.model.Shipment;
import fasttracklogistics.view.ReportsPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

public class ReportController {
    private ReportsPanel reportsView;
    private ShipmentDAO shipmentDAO;
    private DeliveryDAO deliveryDAO;
    private DeliveryPersonnelDAO personnelDAO;

    public ReportController(ReportsPanel reportsView, ShipmentDAO shipmentDAO, DeliveryDAO deliveryDAO, DeliveryPersonnelDAO personnelDAO) {
        this.reportsView = reportsView;
        this.shipmentDAO = shipmentDAO;
        this.deliveryDAO = deliveryDAO;
        this.personnelDAO = personnelDAO;

        initListeners();
    }

    private void initListeners() {
        reportsView.addGenerateReportButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
    }

    private void generateReport() {
        String reportType = reportsView.getSelectedReportType();
        LocalDate startDate = reportsView.getStartDate();
        LocalDate endDate = reportsView.getEndDate();

        if (startDate == null || endDate == null) {
            reportsView.showMessage("Please enter valid Start and End Dates (YYYY-MM-DD).", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (startDate.isAfter(endDate)) {
            reportsView.showMessage("Start Date cannot be after End Date.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            switch (reportType) {
                case "Monthly Shipment Volume":
                    generateMonthlyShipmentVolumeReport(startDate, endDate);
                    break;
                case "Delivery Performance":
                    generateDeliveryPerformanceReport(startDate, endDate);
                    break;
                case "Personnel Availability":
                    generatePersonnelAvailabilityReport(); // Dates might not be as relevant for current status
                    break;
                case "Shipment Status Overview":
                    generateShipmentStatusOverviewReport();
                    break;
                default:
                    reportsView.showMessage("Unknown report type selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            reportsView.showMessage("Error generating report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void generateMonthlyShipmentVolumeReport(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Shipment> shipments = shipmentDAO.getAllShipments();
        Map<String, Long> monthlyCounts = shipments.stream()
                .filter(s -> s.getCreationDate() != null && // Filter by creation date for volume
                        !s.getCreationDate().toLocalDate().isBefore(startDate) &&
                        !s.getCreationDate().toLocalDate().isAfter(endDate))
                .collect(Collectors.groupingBy(
                        s -> s.getCreationDate().toLocalDate().withDayOfMonth(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()
                ));

        List<String[]> data = new ArrayList<>();
        String[] columnNames = {"Month", "Shipment Count"};
        monthlyCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Sort by month
                .forEach(entry -> data.add(new String[]{entry.getKey(), String.valueOf(entry.getValue())}));

        reportsView.displayTabularReport(data, columnNames);
        reportsView.displaySummaryReport("Total shipments created in period (" + startDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + " to " + endDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "): " + data.stream().mapToLong(row -> Long.parseLong(row[1])).sum());
    }

    private void generateDeliveryPerformanceReport(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Delivery> deliveries = deliveryDAO.getAllDeliveries();
        List<String[]> data = new ArrayList<>();
        String[] columnNames = {"Delivery ID", "Shipment Tracking No.", "Personnel Name", "Status", "On-Time", "Delivery Duration (min)"};

        Map<Integer, Shipment> allShipmentsMap = shipmentDAO.getAllShipments().stream()
                .collect(Collectors.toMap(Shipment::getShipmentId, s -> s));
        Map<Integer, String> personnelIdToName = personnelDAO.getAllPersonnel().stream()
                .collect(Collectors.toMap(DeliveryPersonnel::getPersonnelId, DeliveryPersonnel::getName));

        long totalOnTime = 0;
        long totalDeliveriesConsidered = 0;

        for (Delivery d : deliveries) {
            // Only consider deliveries that have an actual delivery time within the report period
            if (d.getActualDeliveryTime() == null) continue;

            LocalDate actualDeliveryDate = d.getActualDeliveryTime().toLocalDate();
            if (actualDeliveryDate.isBefore(startDate) || actualDeliveryDate.isAfter(endDate)) continue;

            Shipment associatedShipment = allShipmentsMap.get(d.getShipmentId());

            String onTime = "N/A";
            long durationMinutes = -1; // -1 indicates not calculable

            if (associatedShipment != null && associatedShipment.getEstimatedDeliveryTime() != null) {
                // Simple on-time logic: actual delivery within 30 mins of estimated delivery
                if (d.getActualDeliveryTime().isBefore(associatedShipment.getEstimatedDeliveryTime().plusMinutes(30))) {
                    onTime = "Yes";
                    totalOnTime++;
                } else {
                    onTime = "No";
                }
                totalDeliveriesConsidered++;
            }

            // Calculate delivery duration (from actual pickup to actual delivery)
            if (d.getActualPickupTime() != null && d.getActualDeliveryTime() != null) {
                durationMinutes = ChronoUnit.MINUTES.between(d.getActualPickupTime(), d.getActualDeliveryTime());
            }


            data.add(new String[]{
                    String.valueOf(d.getDeliveryId()),
                    associatedShipment != null ? associatedShipment.getTrackingNumber() : "ID:" + d.getShipmentId(),
                    personnelIdToName.getOrDefault(d.getPersonnelId(), "ID:" + d.getPersonnelId()),
                    d.getDeliveryStatus(),
                    onTime,
                    durationMinutes != -1 ? String.valueOf(durationMinutes) : "N/A"
            });
        }
        reportsView.displayTabularReport(data, columnNames);

        String summary = String.format("Number of deliveries in period (%s to %s): %d\n",
                startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                endDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                totalDeliveriesConsidered);
        if (totalDeliveriesConsidered > 0) {
            double onTimePercentage = (double) totalOnTime / totalDeliveriesConsidered * 100;
            summary += String.format("On-Time Delivery Rate: %.2f%% (%d out of %d)", onTimePercentage, totalOnTime, totalDeliveriesConsidered);
        } else {
            summary += "No completed deliveries in this period to calculate on-time rate.";
        }
        reportsView.displaySummaryReport(summary);
    }

    private void generatePersonnelAvailabilityReport() throws SQLException {
        List<DeliveryPersonnel> personnelList = personnelDAO.getAllPersonnel();
        List<String[]> data = new ArrayList<>();
        String[] columnNames = {"Personnel ID", "Name", "Contact Info", "Vehicle Details", "Current Availability"};

        for (DeliveryPersonnel p : personnelList) {
            data.add(new String[]{
                    String.valueOf(p.getPersonnelId()),
                    p.getName(),
                    p.getContactInfo(),
                    p.getVehicleDetails(),
                    p.getAvailabilityStatus()
            });
        }
        reportsView.displayTabularReport(data, columnNames);

        long availableCount = personnelList.stream().filter(p -> p.getAvailabilityStatus().equals("Available")).count();
        long onDutyCount = personnelList.stream().filter(p -> p.getAvailabilityStatus().equals("On Duty")).count();
        long onLeaveCount = personnelList.stream().filter(p -> p.getAvailabilityStatus().equals("On Leave")).count();
        long unavailableCount = personnelList.stream().filter(p -> p.getAvailabilityStatus().equals("Unavailable")).count();

        String summary = String.format("Total Personnel: %d\nAvailable: %d\nOn Duty: %d\nOn Leave: %d\nUnavailable: %d",
                personnelList.size(), availableCount, onDutyCount, onLeaveCount, unavailableCount);
        reportsView.displaySummaryReport(summary);
    }

    private void generateShipmentStatusOverviewReport() throws SQLException {
        List<Shipment> shipments = shipmentDAO.getAllShipments();
        Map<String, Long> statusCounts = shipments.stream()
                .collect(Collectors.groupingBy(Shipment::getDeliveryStatus, Collectors.counting()));

        List<String[]> data = new ArrayList<>();
        String[] columnNames = {"Status", "Count"};
        statusCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> data.add(new String[]{entry.getKey(), String.valueOf(entry.getValue())}));

        reportsView.displayTabularReport(data, columnNames);
        reportsView.displaySummaryReport("Total Shipments: " + shipments.size());
    }
}