package roadregistry;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {

    // Test 1: All details valid — should return true
    @Test
    public void testValidPersonShouldBeAdded() {
        Person p = new Person();
        boolean result = p.addPerson("56s_d%&fAB", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(result);  // valid input
    }

    // Test 2: Person ID is too short — should fail
    @Test
    public void testShortPersonIDShouldFail() {
        Person p = new Person();
        boolean result = p.addPerson("56s_fAB", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertFalse(result);  // ID is not 10 characters
    }

    // Test 3: Wrong address (not Victoria) — should fail
    @Test
    public void testAddressWrongStateShouldFail() {
        Person p = new Person();
        boolean result = p.addPerson("56s_d%&fAB", "32|Main Street|Melbourne|NSW|Australia", "15-11-1990");
        assertFalse(result);  // State must be Victoria
    }

    // Test 4: Wrong date format — should fail
    @Test
    public void testInvalidBirthdateFormatShouldFail() {
        Person p = new Person();
        boolean result = p.addPerson("56s_d%&fAB", "32|Main Street|Melbourne|Victoria|Australia", "1990/11/15");
        assertFalse(result);  // Wrong format
    }

    // Test 5: Person ID ends with lowercase — should fail
    @Test
    public void testPersonIDEndsWithLowerCaseShouldFail() {
        Person p = new Person();
        boolean result = p.addPerson("56s_d%&fab", "32|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertFalse(result);  // Last 2 chars must be uppercase
    }
}
