package fasttracklogistics.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map; // For generic report data

public class ReportsPanel extends JPanel {
    private JComboBox<String> reportTypeComboBox;
    private JTextField startDateField, endDateField;
    private JButton generateReportButton;
    private JTable reportTable;
    private DefaultTableModel reportTableModel;
    private JTextArea summaryArea; // For non-tabular reports or summaries

    public ReportsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Controls Panel ---
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Report Controls"));

        controlsPanel.add(new JLabel("Report Type:"));
        reportTypeComboBox = new JComboBox<>(new String[]{
                "Monthly Shipment Volume",
                "Delivery Performance",
                "Personnel Availability",
                "Shipment Status Overview"
        });
        controlsPanel.add(reportTypeComboBox);

        controlsPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        startDateField = new JTextField(12);
        controlsPanel.add(startDateField);

        controlsPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        endDateField = new JTextField(12);
        controlsPanel.add(endDateField);

        generateReportButton = new JButton("Generate Report");
        controlsPanel.add(generateReportButton);

        add(controlsPanel, BorderLayout.NORTH);

        // --- Report Display Panel ---
        JPanel reportDisplayPanel = new JPanel(new BorderLayout(5, 5));
        reportDisplayPanel.setBorder(BorderFactory.createTitledBorder("Report Output"));

        summaryArea = new JTextArea(5, 50);
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        reportDisplayPanel.add(new JScrollPane(summaryArea), BorderLayout.NORTH);

        reportTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        reportTable = new JTable(reportTableModel);
        reportDisplayPanel.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        add(reportDisplayPanel, BorderLayout.CENTER);
    }

    public void addGenerateReportButtonListener(ActionListener listener) {
        generateReportButton.addActionListener(listener);
    }

    public String getSelectedReportType() {
        return (String) reportTypeComboBox.getSelectedItem();
    }

    public LocalDate getStartDate() {
        return parseDate(startDateField.getText());
    }

    public LocalDate getEndDate() {
        return parseDate(endDateField.getText());
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            showMessage("Invalid date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // Method to display tabular reports (e.g., shipment lists)
    public void displayTabularReport(List<String[]> data, String[] columnNames) {
        reportTableModel.setColumnIdentifiers(columnNames);
        reportTableModel.setRowCount(0); // Clear existing data
        for (String[] row : data) {
            reportTableModel.addRow(row);
        }
        summaryArea.setText(""); // Clear summary
    }

    // Method to display summary reports (e.g., overall stats)
    public void displaySummaryReport(String summary) {
        summaryArea.setText(summary);
        reportTableModel.setRowCount(0); // Clear table
        reportTableModel.setColumnCount(0); // Clear columns
    }

    public void clearReportDisplay() {
        summaryArea.setText("");
        reportTableModel.setRowCount(0);
        reportTableModel.setColumnCount(0);
        startDateField.setText("");
        endDateField.setText("");
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
