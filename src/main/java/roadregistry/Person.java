package roadregistry;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.Period;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Person class for RoadRegistry platform
 * Handles person management including adding persons, updating personal details,
 * and managing demerit points with suspension logic.
 * 
 * @author Group 160
 * @version 1.0
 */
public class Person {
    
    // Person attributes
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private HashMap<Date, Integer> demeritPoints; // Maps offense date to demerit points
    private boolean isSuspended;
    
    // File paths for data storage
    private static final String PERSON_FILE = "data/people.txt";
    private static final String DEMERIT_FILE = "data/demerit_points.txt";
    
    // Date formatter for consistent date handling
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    /**
     * Default constructor
     */
    public Person() {
        this.demeritPoints = new HashMap<>();
        this.isSuspended = false;
        createDataDirectoryIfNotExists();
    }
    
    /**
     * Parameterized constructor
     */
    public Person(String personID, String firstName, String lastName, String address, String birthdate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
        this.demeritPoints = new HashMap<>();
        this.isSuspended = false;
        createDataDirectoryIfNotExists();
    }
    
    /**
     * Creates data directory if it doesn't exist
     */
    private void createDataDirectoryIfNotExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
    
    /**
     * Adds a person to the system with comprehensive validation
     * 
     * Validation Rules:
     * 1. PersonID: exactly 10 characters, first 2 are numbers (2-9), 
     *    at least 2 special characters in positions 3-8, last 2 are uppercase letters
     * 2. Address: format "Number|Street|City|State|Country" where State must be "Victoria"
     * 3. Birthdate: format "DD-MM-YYYY"
     * 
     * @return true if person is successfully added, false otherwise
     */
    public boolean addPerson() {
        try {
            // Validate all person data
            if (!isValidPersonID(this.personID)) {
                System.out.println("Invalid PersonID format");
                return false;
            }
            
            if (!isValidAddress(this.address)) {
                System.out.println("Invalid address format");
                return false;
            }
            
            if (!isValidBirthdate(this.birthdate)) {
                System.out.println("Invalid birthdate format");
                return false;
            }
            
            // Check if person already exists
            if (personExists(this.personID)) {
                System.out.println("Person with this ID already exists");
                return false;
            }
            
            // Save person to file if all validations pass
            return savePersonToFile();
            
        } catch (Exception e) {
            System.out.println("Error adding person: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates personal details of an existing person with additional business rules
     * 
     * Additional Rules:
     * 1. If person is under 18, their address cannot be changed
     * 2. If birthday is being changed, no other personal detail can be changed
     * 3. If first digit of personID is even, the ID cannot be changed
     * 
     * @return true if successfully updated, false otherwise
     */
    public boolean updatePersonalDetails() {
        try {
            // First validate the updated data formats
            if (!isValidPersonID(this.personID)) {
                System.out.println("Invalid PersonID format");
                return false;
            }
            
            if (!isValidAddress(this.address)) {
                System.out.println("Invalid address format");
                return false;
            }
            
            if (!isValidBirthdate(this.birthdate)) {
                System.out.println("Invalid birthdate format");
                return false;
            }
            
            // Get existing person data
            Person existingPerson = getPersonFromFile(this.personID);
            if (existingPerson == null) {
                System.out.println("Person not found");
                return false;
            }
            
            // Check business rules
            
            // Rule 1: If person is under 18, address cannot be changed
            int age = calculateAge(existingPerson.birthdate);
            if (age < 18 && !existingPerson.address.equals(this.address)) {
                System.out.println("Cannot change address for person under 18");
                return false;
            }
            
            // Rule 2: If birthday is changing, no other details can change
            if (!existingPerson.birthdate.equals(this.birthdate)) {
                if (!existingPerson.personID.equals(this.personID) ||
                    !existingPerson.firstName.equals(this.firstName) ||
                    !existingPerson.lastName.equals(this.lastName) ||
                    !existingPerson.address.equals(this.address)) {
                    System.out.println("When changing birthday, no other personal details can be changed");
                    return false;
                }
            }
            
            // Rule 3: If first digit is even, ID cannot be changed
            if (!existingPerson.personID.equals(this.personID)) {
                char firstDigit = existingPerson.personID.charAt(0);
                if (Character.isDigit(firstDigit) && (firstDigit - '0') % 2 == 0) {
                    System.out.println("Cannot change ID when first digit is even");
                    return false;
                }
            }
            
            // Update the person in file
            return updatePersonInFile(existingPerson.personID);
            
        } catch (Exception e) {
            System.out.println("Error updating personal details: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Adds demerit points for a person with suspension logic
     * 
     * Rules:
     * 1. Date format must be DD-MM-YYYY
     * 2. Demerit points must be 1-6 (whole number)
     * 3. Suspension logic:
     *    - Under 21: suspended if total points in 2 years > 6
     *    - Over 21: suspended if total points in 2 years > 12
     * 
     * @param offenseDate the date of the offense in DD-MM-YYYY format
     * @param points the demerit points (1-6)
     * @return "Success" if points added successfully, "Failed" otherwise
     */
    public String addDemeritPoints(String offenseDate, int points) {
        try {
            // Validate offense date format
            if (!isValidDateFormat(offenseDate)) {
                System.out.println("Invalid offense date format. Use DD-MM-YYYY");
                return "Failed";
            }
            
            // Validate demerit points range
            if (points < 1 || points > 6) {
                System.out.println("Demerit points must be between 1 and 6");
                return "Failed";
            }
            
            // Check if person exists
            Person existingPerson = getPersonFromFile(this.personID);
            if (existingPerson == null) {
                System.out.println("Person not found");
                return "Failed";
            }
            
            // Add demerit points
            LocalDate offense = LocalDate.parse(offenseDate, DATE_FORMATTER);
            Date offenseJavaDate = java.sql.Date.valueOf(offense);
            
            // Load existing demerit points for this person
            loadDemeritPointsForPerson(this.personID);
            
            // Add new demerit points
            this.demeritPoints.put(offenseJavaDate, points);
            
            // Calculate suspension status based on age and total points in last 2 years
            updateSuspensionStatus(existingPerson.birthdate, offense);
            
            // Save demerit points to file
            if (saveDemeritPointsToFile(offenseDate, points)) {
                return "Success";
            } else {
                return "Failed";
            }
            
        } catch (Exception e) {
            System.out.println("Error adding demerit points: " + e.getMessage());
            return "Failed";
        }
    }
    
    /**
     * Validates PersonID format according to specification
     * Must be exactly 10 characters: first 2 numbers (2-9), at least 2 special chars in positions 3-8, last 2 uppercase
     */
    private boolean isValidPersonID(String personID) {
        if (personID == null || personID.length() != 10) {
            return false;
        }
        
        // Check first two characters are numbers between 2-9
        for (int i = 0; i < 2; i++) {
            char c = personID.charAt(i);
            if (!Character.isDigit(c) || c < '2' || c > '9') {
                return false;
            }
        }
        
        // Check at least 2 special characters in positions 3-8 (indices 2-7)
        int specialCharCount = 0;
        for (int i = 2; i < 8; i++) {
            char c = personID.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                specialCharCount++;
            }
        }
        if (specialCharCount < 2) {
            return false;
        }
        
        // Check last two characters are uppercase letters
        for (int i = 8; i < 10; i++) {
            char c = personID.charAt(i);
            if (!Character.isUpperCase(c)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validates address format: "Number|Street|City|State|Country" where State = "Victoria"
     */
    private boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        
        String[] parts = address.split("\\|");
        if (parts.length != 5) {
            return false;
        }
        
        // Check that all parts are not empty
        for (String part : parts) {
            if (part.trim().isEmpty()) {
                return false;
            }
        }
        
        // Check that state is "Victoria"
        if (!"Victoria".equals(parts[3].trim())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates birthdate format: DD-MM-YYYY
     */
    private boolean isValidBirthdate(String birthdate) {
        return isValidDateFormat(birthdate);
    }
    
    /**
     * Validates date format: DD-MM-YYYY
     */
    private boolean isValidDateFormat(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }
        
        try {
            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Calculates age from birthdate string
     */
    private int calculateAge(String birthdate) {
        try {
            LocalDate birth = LocalDate.parse(birthdate, DATE_FORMATTER);
            return Period.between(birth, LocalDate.now()).getYears();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Updates suspension status based on age and demerit points in last 2 years
     */
    private void updateSuspensionStatus(String birthdate, LocalDate offenseDate) {
        int age = calculateAge(birthdate);
        LocalDate twoYearsAgo = offenseDate.minusYears(2);
        
        int totalPoints = 0;
        for (Map.Entry<Date, Integer> entry : this.demeritPoints.entrySet()) {
            LocalDate pointDate = entry.getKey().toLocalDate();
            if (!pointDate.isBefore(twoYearsAgo)) {
                totalPoints += entry.getValue();
            }
        }
        
        // Apply suspension logic
        if (age < 21) {
            this.isSuspended = totalPoints > 6;
        } else {
            this.isSuspended = totalPoints > 12;
        }
    }
    
    /**
     * Checks if person exists in file
     */
    private boolean personExists(String personID) {
        return getPersonFromFile(personID) != null;
    }
    
    /**
     * Saves person data to file
     */
    private boolean savePersonToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PERSON_FILE, true))) {
            String personData = String.format("%s|%s|%s|%s|%s|%s%n", 
                this.personID, this.firstName, this.lastName, this.address, this.birthdate, this.isSuspended);
            writer.write(personData);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving person to file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets person data from file
     */
    private Person getPersonFromFile(String personID) {
        File file = new File(PERSON_FILE);
        if (!file.exists()) {
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5 && parts[0].equals(personID)) {
                    Person person = new Person();
                    person.personID = parts[0];
                    person.firstName = parts[1];
                    person.lastName = parts[2];
                    person.address = parts[3];
                    person.birthdate = parts[4];
                    if (parts.length > 5) {
                        person.isSuspended = Boolean.parseBoolean(parts[5]);
                    }
                    return person;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading person file: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Updates person data in file
     */
    private boolean updatePersonInFile(String oldPersonID) {
        File file = new File(PERSON_FILE);
        if (!file.exists()) {
            return false;
        }
        
        List<String> lines = new ArrayList<>();
        boolean updated = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5 && parts[0].equals(oldPersonID)) {
                    // Replace with updated data
                    String updatedLine = String.format("%s|%s|%s|%s|%s|%s", 
                        this.personID, this.firstName, this.lastName, this.address, this.birthdate, this.isSuspended);
                    lines.add(updatedLine);
                    updated = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file for update: " + e.getMessage());
            return false;
        }
        
        if (!updated) {
            return false;
        }
        
        // Write back to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line + System.lineSeparator());
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error writing updated file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Loads existing demerit points for a person
     */
    private void loadDemeritPointsForPerson(String personID) {
        File file = new File(DEMERIT_FILE);
        if (!file.exists()) {
            return;
        }
        
        this.demeritPoints.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3 && parts[0].equals(personID)) {
                    LocalDate date = LocalDate.parse(parts[1], DATE_FORMATTER);
                    int points = Integer.parseInt(parts[2]);
                    this.demeritPoints.put(java.sql.Date.valueOf(date), points);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading demerit points: " + e.getMessage());
        }
    }
    
    /**
     * Saves demerit points to file
     */
    private boolean saveDemeritPointsToFile(String offenseDate, int points) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DEMERIT_FILE, true))) {
            String demeritData = String.format("%s|%s|%d|%s%n", 
                this.personID, offenseDate, points, this.isSuspended);
            writer.write(demeritData);
            
            // Also update person file with new suspension status
            updatePersonSuspensionStatus();
            
            return true;
        } catch (IOException e) {
            System.out.println("Error saving demerit points to file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates suspension status in person file
     */
    private void updatePersonSuspensionStatus() {
        updatePersonInFile(this.personID);
    }
    
    // Getter and Setter methods
    public String getPersonID() { return personID; }
    public void setPersonID(String personID) { this.personID = personID; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }
    
    public HashMap<Date, Integer> getDemeritPoints() { return demeritPoints; }
    public void setDemeritPoints(HashMap<Date, Integer> demeritPoints) { this.demeritPoints = demeritPoints; }
    
    public boolean getIsSuspended() { return isSuspended; }
    public void setIsSuspended(boolean isSuspended) { this.isSuspended = isSuspended; }
}