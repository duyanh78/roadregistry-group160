package roadregistry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
/**
 * JUnit tests for addPerson().
 * These cover all rules listed in the assignment:
 *  - PersonID must be 10 chars: first two digits (2–9), at least two special chars in positions 3–8, last two uppercase letters
 *  - firstName and lastName cannot be blank
 *  - address must have exactly 5 parts (Number|Street|City|State|Country) and State must be "Victoria"
 *  - birthdate must be in DD-MM-YYYY format and not in the future
 *  - duplicate PersonID should be rejected
 */
public class AddPersonTest {

    private static final String PEOPLE_FILE = "data/people.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    // test 1: Everything is valid -> should return true
    @Test
    public void testValidPersonShouldBeAdded() {
        Person p = new Person(
            "56s_d%&fAB", 
            "Anna", 
            "Smith",
            "32|Main Street|Melbourne|Victoria|Australia", 
            "15-11-1990"
        );
        assertTrue(p.addPerson());
    }

    // 2. test2: ID is too short (not 10 chars) → should return false
    @Test
    public void testPersonIDTooShort() {
        Person p = new Person(
            "56s_fAB",   // only 7 characters
            "Anna", 
            "Smith",
            "32|Main Street|Melbourne|Victoria|Australia", 
            "15-11-1990"
        );
        assertFalse(p.addPerson());
    }

    // test3:  First character of ID is '1', which is outside 2–9 - should return false
    @Test
    public void testPersonIDStartsWithInvalidDigit() {
        // Second char is '2' so only the first digit is wrong
        Person p = new Person(
            "12s_d%&fAB", 
            "Tom", 
            "Lee",
            "45|Test Street|Melbourne|Victoria|Australia", 
            "01-01-1995"
        );
        assertFalse(p.addPerson());
    }

    // test4: Second character of ID is not a digit - should return false
    @Test
    public void testPersonIDSecondCharNotDigit() {
        
        Person p = new Person(
            "2as_d%&fAB", 
            "Tom", 
            "Lee",
            "45|Test Street|Melbourne|Victoria|Australia", 
            "01-01-1995"
        );
        assertFalse(p.addPerson());
    }

    // test 5. ID has no special chars in positions 3–8 - should return false
    @Test
    public void testPersonIDNotEnoughSpecialCharacters() {
       
        Person p = new Person(
            "56abcdefAB", 
            "May", 
            "Nguyen",
            "22|High Street|Melbourne|Victoria|Australia", 
            "12-12-1990"
        );
        assertFalse(p.addPerson());
    }

    // test 6. ID ends with lowercase instead of uppercase - should return false
    @Test
    public void testPersonIDEndsWithLowercase() {
        Person p = new Person(
            "56s_d%&fab", 
            "Huy", 
            "Tran",
            "10|Low Street|Melbourne|Victoria|Australia", 
            "10-10-1992"
        );
        assertFalse(p.addPerson());
    }

    // test 7 firstName is empty -  should return false
    @Test
    public void testFirstNameIsEmpty() {
        Person p = new Person(
            "67$@_&*zXY", 
            "",      // empty first name
            "Nguyen",
            "10|Test St|Melbourne|Victoria|Australia", 
            "10-01-1991"
        );
        assertFalse(p.addPerson());
    }

    // test 8. lastName is all whitespace → should return false
    @Test
    public void testLastNameIsWhitespace() {
        Person p = new Person(
            "68$@_&*zXY", 
            "Linh", 
            "   ",   // whitespace only
            "10|Test St|Melbourne|Victoria|Australia", 
            "10-01-1991"
        );
        assertFalse(p.addPerson());
    }

    // test 9. Address is missing the country part (only 4 segments) → should return false
    @Test
    public void testAddressMissingCountry() {
        Person p = new Person(
            "69$@_&*zXY", 
            "Minh", 
            "Phan",
            "10|Test St|Melbourne|Victoria", // missing country
            "10-01-1991"
        );
        assertFalse(p.addPerson());
    }

    // test 10. Address number is not numeric → should return false
    @Test
    public void testAddressNumberNotNumeric() {
        Person p = new Person(
            "70$@_&*zXY", 
            "Duy", 
            "Pham",
            "abc|Street|City|Victoria|Australia", // "abc" is not a number
            "10-01-1991"
        );
        assertFalse(p.addPerson());
    }

    // test 11. Address number is zero (must be > 0) → should return false
    @Test
    public void testAddressNumberZero() {
        Person p = new Person(
            "71$@_&*zXY", 
            "Tuan", 
            "Do",
            "0|Street|City|Victoria|Australia", // number = 0, invalid
            "10-01-1991"
        );
        assertFalse(p.addPerson());
    }

    // test 12. State is not "Victoria" → should return false
    @Test
    public void testStateIsNotVictoria() {
        Person p = new Person(
            "72$@_&*zXY", 
            "Vy", 
            "Le",
            "10|Street|City|NSW|Australia", // state is NSW, not Victoria
            "10-01-1991"
        );
        assertFalse(p.addPerson());
    }

    // test 13. City in address is empty → should return false
    @Test
    public void testAddressHasEmptyCity() {
        Person p = new Person(
            "73$@_&*zXY", 
            "Nam", 
            "Bui",
            "10|Street||Victoria|Australia", // city is empty
            "10-01-1991"
        );
        assertFalse(p.addPerson());
    }

    // test 14. Birthdate uses wrong format → should return false
    @Test
    public void testInvalidBirthdateFormat() {
        Person p = new Person(
            "74$@_&*zXY", 
            "Hanh", 
            "Pham",
            "10|Street|City|Victoria|Australia", 
            "1990/01/15" // wrong format YYYY/MM/DD
        );
        assertFalse(p.addPerson());
    }

    // test 15. Birthdate is a future date → should return false
    @Test
    public void testBirthdateInFuture() {
        String futureDate = LocalDate.now().plusDays(1).format(FORMATTER);
        Person p = new Person(
            "75$@_&*zXY", 
            "Trang", 
            "Vo",
            "10|Street|City|Victoria|Australia", 
            futureDate // tomorrow's date
        );
        assertFalse(p.addPerson());
    }

    // test 16. Duplicate PersonID: second addition should fail
    @Test
    public void testDuplicatePersonID() {
        Person p1 = new Person(
            "76$@_&*zXY", 
            "Lan", 
            "Ho",
            "10|Street|City|Victoria|Australia", 
            "01-01-1990"
        );
        assertTrue(p1.addPerson());

        Person p2 = new Person(
            "76$@_&*zXY", 
            "Hieu", 
            "Ngo",
            "20|Street|City|Victoria|Australia", 
            "02-02-1991"
        );
        assertFalse(p2.addPerson());
    }
}
