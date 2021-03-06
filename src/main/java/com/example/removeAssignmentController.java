package com.example;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.Arrays;

import static com.example.ControllerCalendar.dateScore;
import static com.example.addAssignmentController.Date_To_Days;
import static com.example.addAssignmentController.deleteYear;

public class removeAssignmentController {

    // May 1
    LocalDate CurrentDate = LocalDate.parse("2021-05-01");

    // success or error
    public Text error;
    public Text noAssignmentsIn;
    public Text removeSuccess;
    public ComboBox<String> selectedAssignment;

    // Text fields for information
    public Label nameImport;
    public Label weightingImport;
    public Label dueDateImport;
    public Label assignmentScoreImport;
    public Label assignedImport;
    public Label dateScoreImport;
    public Label totalHoursImport;
    public Label dailyHoursImport;

    // PRESENTING THE ONE. THE ONLY. THE LIIIIIIIIIIIIIINE!!!!!!!!!!
    public Line massiveLineThing;


    /*
     Pre: None
     Post: Assigns the values of all assignment names to a comboBox (drop-down menu)
      */
    public void comboBoxAssign() throws GeneralSecurityException, IOException {
        selectedAssignment.setValue(null);
        String[][] assignmentInfo = SheetsAPI.PullAssignments();

        ObservableList<String> data = FXCollections.observableArrayList();

        for (int i = 1; i < assignmentInfo.length; i++) {
            data.add(assignmentInfo[i][0]);
        }
        selectedAssignment.setItems(data);
    }

    /*
    Pre: None
    Post: Selects a assignment, and removed it from all related arrays
     */
    public void removeAssignment() throws GeneralSecurityException, IOException {
        removeSuccess.setVisible(false);
        error.setVisible(false);
        noAssignmentsIn.setVisible(false);
        String choice;
        int id = 0;
        choice = selectedAssignment.getValue();
        String[][] assignmentInfo = SheetsAPI.PullAssignments();


        //Getting the assignment array id
        for (int i = 1; i < assignmentInfo.length; i++) {
            if (assignmentInfo[i][0].equals(choice)) {
                id = i;
                break;
            }
        }
        // Creating info of array to delete
        String[] delete = new String[6];
        delete[0] = assignmentInfo[id][0];
        delete[1] = assignmentInfo[id][1];
        delete[2] = assignmentInfo[id][2];
        delete[3] = assignmentInfo[id][3];
        delete[4] = assignmentInfo[id][4];
        delete[5] = assignmentInfo[id][5];

        // Confirmation that user has removed the selected assignment
        removeSuccess.setVisible(true);

        // Updating date score
        for (int i = ControllerCalendar.isolateDays(assignmentInfo[id][4]);
             i <= ControllerCalendar.isolateDays(String.valueOf(assignmentInfo[id][2])); i++) {
            dateScore[i - 1] -= Integer.parseInt(assignmentInfo[id][3]);
        }
        System.out.println(Arrays.toString(dateScore));


        // Resetting assignments.

        selectedAssignment.setValue(null);
        nameImport.setVisible(false);
        weightingImport.setVisible(false);
        dueDateImport.setVisible(false);
        assignmentScoreImport.setVisible(false);
        assignedImport.setVisible(false);
        dateScoreImport.setVisible(false);
        totalHoursImport.setVisible(false);
        dailyHoursImport.setVisible(false);

        massiveLineThing.setVisible(false);

        removeSuccess.setVisible(true);

        String assignmentToDelete = SheetsAPI.DeleteAssignment(delete);

    }

    /*
    Pre: None
    Post: Checks to see if user entered correct information
     */
    public void textCheck() throws GeneralSecurityException, IOException {
        removeSuccess.setVisible(false);
        error.setVisible(false);
        noAssignmentsIn.setVisible(false);
        String choice = selectedAssignment.getValue();


        // Checking if assignment was entered correctly
        if (choice == null) {
            error.setVisible(true);
        } else {
            removeAssignment();
        }
    }

    /*
       Pre: None
       Post: Finds what assignment user selected, and shows the assignment details
        */
    public void setTextDetails() throws IOException, GeneralSecurityException {

        ControllerCalendar.updateDateAndDailyHoursScore();

        String[][] assignmentInfo = SheetsAPI.PullAssignments();
        int id = 0;

                // Setting the choice of assigment
                String choice = selectedAssignment.getValue();
                removeSuccess.setVisible(false);
                error.setVisible(false);
                noAssignmentsIn.setVisible(false);

                // Determines the id of the assignment selected
                for (int i = 1; i < assignmentInfo.length; i++) {
                    if (assignmentInfo[i][0].equals(choice)) {
                        id = i;
                        break;
                    }
                }

                // Turning date selected into a number that can be identified to datescore
                int dateAndHoursScoreIndex = Date_To_Days(deleteYear(assignmentInfo[id][2])) - 120;

                // variables
                String nameRetrieved = assignmentInfo[id][0];
                String weightingRetrieved = assignmentInfo[id][1];
                LocalDate dueDateRetrieved = LocalDate.parse(assignmentInfo[id][2]);
                String scoreRetrieved = assignmentInfo[id][3];
                String dateAssignedRetrieved = assignmentInfo[id][4];
                int totalHoursRetrieved = Integer.parseInt(assignmentInfo[id][5]);
                //Converts data into readable numbers.
                int dateScore = ControllerCalendar.dateScore[dateAndHoursScoreIndex - 1];

                // getting daily hours
                double currentDate = Date_To_Days(deleteYear(String.valueOf((CurrentDate))));
                double assignmentDueDate = Date_To_Days(deleteYear(String.valueOf(assignmentInfo[id][2])));
                double daily_Hours = Integer.parseInt(assignmentInfo[id][5]) / (assignmentDueDate - currentDate);
                double roundedDailyHours = Math.round(daily_Hours * 100.0) / 100.0;


                // Assigning an assignment's information to the labels.
                nameImport.setText("Name: " + nameRetrieved);
                weightingImport.setText("Weighting: " + weightingRetrieved);
                dueDateImport.setText("Due Date: " + dueDateRetrieved + " (Due in " + Math.abs(CurrentDate
                        .getDayOfMonth() - dueDateRetrieved.getDayOfMonth()) + " day(s))");
                assignmentScoreImport.setText("Score: " + scoreRetrieved);
                assignedImport.setText("Assigned: " + dateAssignedRetrieved);
                dateScoreImport.setText("Date Score on Due Date: " + dateScore);
                totalHoursImport.setText("Total Hours to Complete: " + totalHoursRetrieved);
                dailyHoursImport.setText("Suggested Hours per Day: " + roundedDailyHours);


                // Setting the information labels to be invisible (they become visible when "more info" is clicked).
                nameImport.setVisible(true);
                weightingImport.setVisible(true);
                dueDateImport.setVisible(true);
                assignmentScoreImport.setVisible(true);
                assignedImport.setVisible(true);
                dateScoreImport.setVisible(true);
                totalHoursImport.setVisible(true);
                dailyHoursImport.setVisible(true);

                massiveLineThing.setVisible(true);


            }

    }

