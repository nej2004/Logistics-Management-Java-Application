package fasttracklogistics.view;

import fasttracklogistics.model.Shipment;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

public class TrackShipmentPanel extends JPanel {
    private JTextField trackingNumberField;
    private JButton trackButton;
    private JTextArea shipmentDetailsArea;
    private JLabel statusLabel, currentLocationLabel, estimatedDeliveryLabel, actualDeliveryLabel; // Added actualDeliveryLabel

    public TrackShipmentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Search Panel ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Track Shipment by Tracking Number"));

        searchPanel.add(new JLabel("Tracking Number:"));
        trackingNumberField = new JTextField(25);
        searchPanel.add(trackingNumberField);

        trackButton = new JButton("Track Shipment");
        searchPanel.add(trackButton);

        add(searchPanel, BorderLayout.NORTH);

        // --- Display Panel ---
        JPanel displayPanel = new JPanel(new GridBagLayout());
        displayPanel.setBorder(BorderFactory.createTitledBorder("Shipment Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        displayPanel.add(new JLabel("Current Status:"), gbc);
        statusLabel = new JLabel("N/A");
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
        displayPanel.add(statusLabel, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        displayPanel.add(new JLabel("Current Location:"), gbc);
        currentLocationLabel = new JLabel("N/A");
        gbc.gridx = 1; gbc.gridy = row++;
        displayPanel.add(currentLocationLabel, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        displayPanel.add(new JLabel("Estimated Delivery:"), gbc);
        estimatedDeliveryLabel = new JLabel("N/A");
        gbc.gridx = 1; gbc.gridy = row++;
        displayPanel.add(estimatedDeliveryLabel, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        displayPanel.add(new JLabel("Actual Delivery:"), gbc); // New label
        actualDeliveryLabel = new JLabel("N/A"); // New label
        gbc.gridx = 1; gbc.gridy = row++;
        displayPanel.add(actualDeliveryLabel, gbc);


        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0; // Allow text area to expand vertically
        shipmentDetailsArea = new JTextArea(10, 40);
        shipmentDetailsArea.setEditable(false);
        shipmentDetailsArea.setLineWrap(true);
        shipmentDetailsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(shipmentDetailsArea);
        displayPanel.add(scrollPane, gbc);

        add(displayPanel, BorderLayout.CENTER);
    }

    public void addTrackButtonListener(ActionListener listener) {
        trackButton.addActionListener(listener);
    }

    public String getTrackingNumberInput() {
        return trackingNumberField.getText().trim();
    }

    public void displayShipmentDetails(Shipment shipment) {
        if (shipment == null) {
            statusLabel.setText("Shipment Not Found");
            currentLocationLabel.setText("N/A");
            estimatedDeliveryLabel.setText("N/A");
            actualDeliveryLabel.setText("N/A");
            shipmentDetailsArea.setText("No shipment found with that tracking number.");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            statusLabel.setText(shipment.getDeliveryStatus() != null ? shipment.getDeliveryStatus() : "N/A");
            currentLocationLabel.setText(shipment.getCurrentLocation() != null ? shipment.getCurrentLocation() : "N/A");
            estimatedDeliveryLabel.setText(
                    shipment.getEstimatedDeliveryTime() != null ? shipment.getEstimatedDeliveryTime().format(formatter) : "N/A"
            );
            actualDeliveryLabel.setText( // Display actual delivery time
                    shipment.getActualDeliveryTime() != null ? shipment.getActualDeliveryTime().format(formatter) : "N/A"
            );

            StringBuilder details = new StringBuilder();
            details.append("Tracking Number: ").append(shipment.getTrackingNumber()).append("\n");
            details.append("Sender: ").append(shipment.getSenderName()).append(" (").append(shipment.getSenderContact()).append(")\n");
            details.append("Sender Address: ").append(shipment.getSenderAddress()).append("\n");
            details.append("Receiver: ").append(shipment.getReceiverName()).append(" (").append(shipment.getReceiverContact()).append(")\n");
            details.append("Receiver Address: ").append(shipment.getReceiverAddress()).append("\n");
            details.append("Contents: ").append(shipment.getPackageContents()).append("\n");
            details.append("Weight: ").append(shipment.getWeight()).append(" kg\n");
            details.append("Dimensions: ").append(shipment.getDimensions()).append("\n");
            details.append("Special Instructions: ").append(shipment.getSpecialInstructions()).append("\n");
            details.append("Created On: ").append(shipment.getCreationDate() != null ? shipment.getCreationDate().format(formatter) : "N/A").append("\n");

            shipmentDetailsArea.setText(details.toString());
        }
    }

    public void clearDisplay() {
        trackingNumberField.setText("");
        statusLabel.setText("N/A");
        currentLocationLabel.setText("N/A");
        estimatedDeliveryLabel.setText("N/A");
        actualDeliveryLabel.setText("N/A");
        shipmentDetailsArea.setText("");
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}