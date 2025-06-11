package fasttracklogistics.controller;

import fasttracklogistics.model.DeliveryPersonnel;
import fasttracklogistics.dao.DeliveryPersonnelDAO;
import fasttracklogistics.view.ManagePersonnelPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class PersonnelController {
    private DeliveryPersonnelDAO personnelDAO;
    private ManagePersonnelPanel managePersonnelView;

    public PersonnelController(ManagePersonnelPanel managePersonnelView, DeliveryPersonnelDAO personnelDAO) {
        this.managePersonnelView = managePersonnelView;
        this.personnelDAO = personnelDAO;

        initListeners();
        loadPersonnel(); // Initial load
    }

    private void initListeners() {
        managePersonnelView.addAddButtonListener(e -> addPersonnel());
        managePersonnelView.addUpdateButtonListener(e -> updatePersonnel());
        managePersonnelView.addRemoveButtonListener(e -> removePersonnel());
        managePersonnelView.addClearButtonListener(e -> managePersonnelView.clearFields());
        managePersonnelView.addTableSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    displaySelectedPersonnelDetails();
                }
            }
        });
    }

    public void loadPersonnel() {
        try {
            List<DeliveryPersonnel> personnelList = personnelDAO.getAllPersonnel();
            managePersonnelView.displayPersonnel(personnelList);
        } catch (SQLException e) {
            managePersonnelView.showMessage("Error loading personnel: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addPersonnel() {
        DeliveryPersonnel newPersonnel = managePersonnelView.getPersonnelFromFields();
        if (newPersonnel == null) { // Validation failed in view
            return;
        }
        try {
            personnelDAO.addPersonnel(newPersonnel);
            managePersonnelView.showMessage("Personnel added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            managePersonnelView.clearFields();
            loadPersonnel();
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("license_number")) {
                managePersonnelView.showMessage("Error: License number already exists. Please use a unique license number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                managePersonnelView.showMessage("Error adding personnel: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        }
    }

    private void updatePersonnel() {
        int selectedId = managePersonnelView.getSelectedPersonnelId();
        if (selectedId == -1) {
            managePersonnelView.showMessage("Please select personnel to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DeliveryPersonnel updatedPersonnel = managePersonnelView.getPersonnelFromFields();
        if (updatedPersonnel == null) { // Validation failed in view
            return;
        }
        updatedPersonnel.setPersonnelId(selectedId);

        try {
            personnelDAO.updatePersonnel(updatedPersonnel);
            managePersonnelView.showMessage("Personnel updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            managePersonnelView.clearFields();
            loadPersonnel();
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("license_number")) {
                managePersonnelView.showMessage("Error: License number already exists. Please use a unique license number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                managePersonnelView.showMessage("Error updating personnel: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        }
    }

    private void removePersonnel() {
        int selectedId = managePersonnelView.getSelectedPersonnelId();
        if (selectedId == -1) {
            managePersonnelView.showMessage("Please select personnel to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = managePersonnelView.showConfirmDialog("Are you sure you want to remove this personnel?", "Confirm Removal");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                personnelDAO.deletePersonnel(selectedId);
                managePersonnelView.showMessage("Personnel removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                managePersonnelView.clearFields();
                loadPersonnel();
            } catch (SQLException e) {
                managePersonnelView.showMessage("Error removing personnel: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void displaySelectedPersonnelDetails() {
        int selectedId = managePersonnelView.getSelectedPersonnelId();
        if (selectedId != -1) {
            try {
                DeliveryPersonnel selectedPersonnel = personnelDAO.getPersonnelById(selectedId);
                if (selectedPersonnel != null) {
                    managePersonnelView.setPersonnelFields(selectedPersonnel);
                }
            } catch (SQLException e) {
                managePersonnelView.showMessage("Error retrieving personnel details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            managePersonnelView.clearFields();
        }
    }
}