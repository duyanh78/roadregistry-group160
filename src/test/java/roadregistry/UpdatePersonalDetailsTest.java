package roadregistry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for updatePersonalDetails(String oldPersonID)
 * - Tests under-18 address change restriction
 * - Tests birthdate-only changes
 * - Tests ID change logic (even-start not allowed)
 * - Tests valid update when all rules are respected
 */
public class UpdatePersonalDetailsTest {

    private Person createPerson(String id, String fname, String lname, String address, String birthdate) {
        Person p = new Person(id, fname, lname, address, birthdate);
        assertTrue(p.addPerson());  // Ensure person is saved before update
        return p;
    }

    @BeforeAll
    public static void clearPeopleFileOnceBeforeAllTests() {
        File file = new File("data/people.txt");
        if (file.exists()) {
            file.delete();
        }
    }

    // Rule 1: Under 18 cannot change address
    @Test
    public void testUnder18CannotChangeAddress() {
        Person p = createPerson("56!@abCDXY", "Alice", "Nguyen",
                "12|Old St|Melbourne|Victoria|Australia", "15-06-2010");

        // Attempt to change address
        p.setAddress("45|New St|Melbourne|Victoria|Australia");
        boolean result = p.updatePersonalDetails("56!@abCDXY");
        assertFalse(result); // Should be rejected due to age
    }

    // Rule 2: Only birthdate changes, other fields must stay the same
    @Test
    public void testChangeBirthdateOnlyIsAllowed() {
        Person p = createPerson("57##xYQZAB", "John", "Smith",
                "88|Main Rd|Melbourne|Victoria|Australia", "01-01-1995");

        // Only change birthdate
        p.setBirthdate("02-02-1996");
        boolean result = p.updatePersonalDetails("57##xYQZAB");
        assertTrue(result); // Allowed if only birthdate changes
    }

    // Rule 2 violation: birthdate change + name change = invalid
    @Test
    public void testChangeBirthdateAndOtherFieldFails() {
        Person p = createPerson("58%%cCWLAB", "Tom", "Lee",
                "99|Alpha St|Melbourne|Victoria|Australia", "01-01-1990");

        // Change birthdate AND name
        p.setBirthdate("02-02-1991");
        p.setFirstName("Tim");
        boolean result = p.updatePersonalDetails("58%%cCWLAB");
        assertFalse(result); // Should fail due to other fields changing
    }

    // Rule 3: ID starting with even number → cannot change ID
    @Test
    public void testEvenStartIDCannotBeChanged() {
        Person p = createPerson("24@#BBccXY", "David", "Tran",
                "77|Sunset Blvd|Melbourne|Victoria|Australia", "01-01-1980");

        // Try to change ID to something else
        p.setPersonID("99!!XXwwWW"); // Valid new ID
        boolean result = p.updatePersonalDetails("24@#BBccXY");
        assertFalse(result); // Rejected due to even-start ID
    }

    // Rule 3 valid: ID starts with odd number → can change ID if available
    @Test
    public void testOddStartIDCanBeChanged() {
        Person p = createPerson("35!!QQzZAB", "Mary", "Vo",
                "11|Park Ave|Melbourne|Victoria|Australia", "01-01-1990");

        p.setPersonID("77@@GGHHJK");
        p.setFirstName("Maria");
        p.setAddress("22|Lake St|Melbourne|Victoria|Australia");

        boolean result = p.updatePersonalDetails("35!!QQzZAB");
        assertTrue(result); // Allowed because rules passed

        assertEquals("77@@GGHHJK", p.getPersonID());
        assertEquals("Maria", p.getFirstName());
        assertEquals("Vo", p.getLastName());
        assertEquals("22|Lake St|Melbourne|Victoria|Australia", p.getAddress());
    }

    // Additional check: trying to change ID to an existing one → fail
    @Test
    public void testChangeToExistingIDFails() {
        createPerson("22!!QQWWER", "PreExisting", "User",
                "55|Somewhere|Melbourne|Victoria|Australia", "01-01-1980");

        Person p = createPerson("39!!ZZTTAB", "Tim", "Nguyen",
                "66|Other Place|Melbourne|Victoria|Australia", "01-01-1995");

        // Try to change to an already existing ID
        p.setPersonID("22!!QQWWER");
        boolean result = p.updatePersonalDetails("39!!ZZTTAB");
        assertFalse(result);
    }
}
