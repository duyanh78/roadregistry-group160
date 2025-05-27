package roadregistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class AddDemeritPointsTest {

    private Person person;

    @BeforeEach
    public void setUp() {
        person = new Person("78!@#%_zAB", "John", "Doe","32|Highland Street|Melbourne|Victoria|Australia", "15-11-1990");
        person.addPerson();
    }

    /**
     * Test Case 1: All data valid
     * Expectation: Dmerit Points should be added successfully.
     */
    @Test
    public void testValidDemeritPointsAddition() {
        String result = person.addDemeritPoints("01-01-2024", 3);
        assertEquals("Success", result);
    }


    /**
     * Test Case 2: Invalid Date Format
     * Expectation: Dmerit Points should not be added.
     */
    @Test
    public void testInvalidDateFormat() {
        String result = person.addDemeritPoints("2024-01-01", 3);
        assertEquals("Failed", result);
    }

    /**
     * Test Case 3: Number of Points too high, greater than 6
     * Expectation: Dmerit Points should not be added.
     */
    @Test
    public void testInvalidPointsTooHigh() {
        String result = person.addDemeritPoints("01-01-2024", 7);
        assertEquals("Failed", result);
    }

     /**
     * Test Case 4: Number of Points too Low, less than 1
     * Expectation: Dmerit Points should not be added.
     */
    @Test
    public void testInvalidPointsTooLow() {
        String result = person.addDemeritPoints("01-01-2024", 0);
        assertEquals("Failed", result);
    }

     /**
     * Test Case 5: Add Demerit points to a person that does not exist in the file.
     * Expectation: Dmerit Points should not be added.
     */
    @Test
    public void testAddDemeritToNonExistingPerson() {
        Person newPerson = new Person("24!!$$abcZZ", "Clint", "Ferreira", "321|Vasai|Mumbai|Maharashtra|India", "10-07-2000");
        String result = newPerson.addDemeritPoints("01-01-2024", 3);
        assertEquals("Failed", result);
    }
}
