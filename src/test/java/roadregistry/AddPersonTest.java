
package roadregistry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

/**
 * Unit tests for addPerson() method in Person class.
 * Each test validates different conditions as per assignment specification.
 */
public class AddPersonTest {

    @BeforeEach
    void resetDataFile() {
        File file = new File("data/people.txt");
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Test Case 1: All data valid
     * Expectation: Person should be added successfully.
     */
    @Test
    public void testValidPerson_AllCorrect() {
        Person p = new Person("78!@#%_zAB", "John", "Doe",
                "32|Highland Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(p.addPerson());
    }

    /**
     * Test Case 2: Invalid personID format (starts with digits not in 2–9 range)
     * Expectation: Person should NOT be added.
     */
    @Test
    public void testInvalidPersonID_FormatWrong() {
        Person p = new Person("12abcXYZaa", "Jane", "Smith",
                "32|Highland Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertFalse(p.addPerson());
    }

    /**
     * Test Case 3: Address with wrong state (not Victoria)
     * Expectation: Person should NOT be added.
     */
    @Test
    public void testInvalidAddress_WrongState() {
        Person p = new Person("89##@@__GH", "Alice", "Lee",
                "12|George Street|Sydney|NSW|Australia", "10-05-1995");
        assertFalse(p.addPerson());
    }

    /**
     * Test Case 4: Birthdate with invalid format (should be DD-MM-YYYY)
     * Expectation: Person should NOT be added.
     */
    @Test
    public void testInvalidBirthdateFormat() {
        Person p = new Person("77&*^^!!KL", "Bob", "Chan",
                "22|King Street|Melbourne|Victoria|Australia", "1990/11/15");
        assertFalse(p.addPerson());
    }

    /**
     * Test Case 5: Duplicate personID
     * Expectation: First add succeeds, second add with same ID should fail.
     */
    @Test
    public void testDuplicatePersonID() {
        String personID = "99$$__++ZZ";
        Person p1 = new Person(personID, "Test", "User",
                "33|Main Road|Melbourne|Victoria|Australia", "15-11-1990");
        Person p2 = new Person(personID, "Another", "User",
                "33|Main Road|Melbourne|Victoria|Australia", "15-11-1990");

        assertTrue(p1.addPerson());     // First insert: should succeed
        assertFalse(p2.addPerson());    // Second insert: should fail (duplicate ID)
    }
}
