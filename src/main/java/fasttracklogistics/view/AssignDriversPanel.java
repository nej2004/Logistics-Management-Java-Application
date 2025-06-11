package fasttracklogistics.view;

import fasttracklogistics.model.Shipment;
import fasttracklogistics.model.DeliveryPersonnel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssignDriversPanel extends JPanel {
    private JTable unassignedShipmentsTable;
    private JTable availablePersonnelTable;
    private DefaultTableModel unassignedShipmentsTableModel;
    private DefaultTableModel availablePersonnelTableModel;
    private JButton assignButton;
    private JButton refreshButton;

    // Maps to store IDs for easy lookup (populated by controller)
    private Map<Integer, Shipment> allShipmentsMap;
    private Map<Integer, DeliveryPersonnel> allPersonnelMap;

    public AssignDriversPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Panel for Tables ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5); // Divide space equally

        // Unassigned Shipments Table
        JPanel unassignedShipmentsPanel = new JPanel(new BorderLayout());
        unassignedShipmentsPanel.setBorder(BorderFactory.createTitledBorder("Unassigned Shipments (Status: Pending)"));
        unassignedShipmentsTableModel = new DefaultTableModel(new Object[]{
                "Shipment ID", "Tracking No.", "Receiver Name", "Receiver Contact", "Contents", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        unassignedShipmentsTable = new JTable(unassignedShipmentsTableModel);
        unassignedShipmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        unassignedShipmentsPanel.add(new JScrollPane(unassignedShipmentsTable), BorderLayout.CENTER);
        splitPane.setLeftComponent(unassignedShipmentsPanel);

        // Available Personnel Table
        JPanel availablePersonnelPanel = new JPanel(new BorderLayout());
        availablePersonnelPanel.setBorder(BorderFactory.createTitledBorder("Available Delivery Personnel"));
        availablePersonnelTableModel = new DefaultTableModel(new Object[]{
                "Personnel ID", "Name", "Contact Info", "Vehicle Type", "Availability"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        availablePersonnelTable = new JTable(availablePersonnelTableModel);
        availablePersonnelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availablePersonnelPanel.add(new JScrollPane(availablePersonnelTable), BorderLayout.CENTER);
        splitPane.setRightComponent(availablePersonnelPanel);

        add(splitPane, BorderLayout.CENTER);

        // --- Bottom Panel for Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        assignButton = new JButton("Assign Selected Shipment to Selected Driver");
        refreshButton = new JButton("Refresh Data");

        buttonPanel.add(assignButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // --- Methods to attach listeners ---
    public void addAssignButtonListener(ActionListener listener) {
        assignButton.addActionListener(listener);
    }

    public void addRefreshButtonListener(ActionListener listener) {
        refreshButton.addActionListener(listener);
    }

    // --- Methods to update the View ---
    public void displayUnassignedShipments(List<Shipment> shipments) {
        unassignedShipmentsTableModel.setRowCount(0);
        allShipmentsMap = shipments.stream()
                .collect(Collectors.toMap(Shipment::getShipmentId, s -> s));
        for (Shipment s : shipments) {
            unassignedShipmentsTableModel.addRow(new Object[]{
                    s.getShipmentId(),
                    s.getTrackingNumber(),
                    s.getReceiverName(),
                    s.getReceiverContact(),
                    s.getPackageContents(),
                    s.getDeliveryStatus()
            });
        }
    }

    public void displayAvailablePersonnel(List<DeliveryPersonnel> personnelList) {
        availablePersonnelTableModel.setRowCount(0);
        allPersonnelMap = personnelList.stream()
                .collect(Collectors.toMap(DeliveryPersonnel::getPersonnelId, p -> p));
        for (DeliveryPersonnel p : personnelList) {
            availablePersonnelTableModel.addRow(new Object[]{
                    p.getPersonnelId(),
                    p.getName(),
                    p.getContactInfo(),
                    p.getVehicleDetails(),
                    p.getAvailabilityStatus()
            });
        }
    }

    // --- Methods to get selected items ---
    public int getSelectedShipmentId() {
        int selectedRow = unassignedShipmentsTable.getSelectedRow();
        if (selectedRow != -1) {
            Object idValue = unassignedShipmentsTableModel.getValueAt(selectedRow, 0);
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

    public int getSelectedPersonnelId() {
        int selectedRow = availablePersonnelTable.getSelectedRow();
        if (selectedRow != -1) {
            Object idValue = availablePersonnelTableModel.getValueAt(selectedRow, 0);
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
}