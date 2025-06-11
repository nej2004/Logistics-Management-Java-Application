package fasttracklogistics.model;

import java.time.LocalDateTime;

public class Shipment {
    private int shipmentId;
    private String trackingNumber;
    private String senderName;
    private String senderAddress;
    private String senderContact;
    private String receiverName;
    private String receiverAddress;
    private String receiverContact;
    private String packageContents;
    private double weight;
    private String dimensions;
    private String deliveryStatus;
    private String currentLocation;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private String specialInstructions;
    private LocalDateTime creationDate;

    // Default Constructor (important for DAOs and when building from ResultSet)
    public Shipment() {
    }

    // Full Constructor (e.g., when retrieving from DB)
    public Shipment(int shipmentId, String trackingNumber, String senderName, String senderAddress, String senderContact,
                    String receiverName, String receiverAddress, String receiverContact, String packageContents,
                    double weight, String dimensions, String deliveryStatus, String currentLocation,
                    LocalDateTime estimatedDeliveryTime, LocalDateTime actualDeliveryTime, String specialInstructions,
                    LocalDateTime creationDate) {
        this.shipmentId = shipmentId;
        this.trackingNumber = trackingNumber;
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.senderContact = senderContact;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.receiverContact = receiverContact;
        this.packageContents = packageContents;
        this.weight = weight;
        this.dimensions = dimensions;
        this.deliveryStatus = deliveryStatus;
        this.currentLocation = currentLocation;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.actualDeliveryTime = actualDeliveryTime;
        this.specialInstructions = specialInstructions;
        this.creationDate = creationDate;
    }

    // Constructor for creating new shipments (e.g., from UI input)
    public Shipment(String trackingNumber, String senderName, String senderAddress, String senderContact,
                    String receiverName, String receiverAddress, String receiverContact, String packageContents,
                    double weight, String dimensions, String deliveryStatus, String currentLocation,
                    LocalDateTime estimatedDeliveryTime, String specialInstructions) {
        this.trackingNumber = trackingNumber;
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.senderContact = senderContact;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.receiverContact = receiverContact;
        this.packageContents = packageContents;
        this.weight = weight;
        this.dimensions = dimensions;
        this.deliveryStatus = deliveryStatus;
        this.currentLocation = currentLocation;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.specialInstructions = specialInstructions;
        this.creationDate = LocalDateTime.now(); // Set creation date automatically
    }


    // --- Getters and Setters ---

    public int getShipmentId() { return shipmentId; }
    public void setShipmentId(int shipmentId) { this.shipmentId = shipmentId; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAddress() { return senderAddress; }
    public void setSenderAddress(String senderAddress) { this.senderAddress = senderAddress; }

    public String getSenderContact() { return senderContact; }
    public void setSenderContact(String senderContact) { this.senderContact = senderContact; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverAddress() { return receiverAddress; }
    public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }

    public String getReceiverContact() { return receiverContact; }
    public void setReceiverContact(String receiverContact) { this.receiverContact = receiverContact; }

    public String getPackageContents() { return packageContents; }
    public void setPackageContents(String packageContents) { this.packageContents = packageContents; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public LocalDateTime getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }

    public LocalDateTime getActualDeliveryTime() { return actualDeliveryTime; }
    public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) { this.actualDeliveryTime = actualDeliveryTime; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    @Override
    public String toString() {
        return "Shipment{" +
                "shipmentId=" + shipmentId +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", senderName='" + senderName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                '}';
    }
}