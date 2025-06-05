
package roadregistry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {
    private Person person;

    private Person createPerson(String id, String name, String address, String dob) {
        Person p = new Person();
        p.addPerson(id, address, dob);
        String[] parts = name.split(" ");
        p.firstName = parts[0];
        p.lastName = parts.length > 1 ? parts[1] : "";
        return p;
    }

    @BeforeEach
    public void setup() {
        person = createPerson("23@#AA", "John Doe", "123 Main St, Victoria", "15-06-2010"); // under 18
    }

    @Test
    public void testCantChangeAddressIfUnder18() {
        boolean result = person.updatePersonalDetails("23@#AA", "John Doe", "456 New Rd, Victoria", "15-06-2010");
        assertFalse(result);
    }

    @Test
    public void testOnlyBirthdateCanChange() {
        boolean result = person.updatePersonalDetails("23@#AA", "John Doe", "123 Main St, Victoria", "15-06-2000");
        assertTrue(result);
    }

    @Test
    public void testBirthdateChangeWithOtherFieldsNotAllowed() {
        boolean result = person.updatePersonalDetails("24##ZZ", "Jane Doe", "789 Alt St, Victoria", "20-12-2000");
        assertFalse(result);
    }

    @Test
    public void testEvenStartIDMustStaySame() {
        person = createPerson("24@#BB", "Alice Smith", "22 Sunset Blvd, Victoria", "01-01-1995");
        boolean result = person.updatePersonalDetails("88ZZQQ", "Alice Smith", "22 Sunset Blvd, Victoria", "01-01-1995");
        assertFalse(result);
    }

    @Test
    public void testUpdateAllowedForAdultWithOddID() {
        person = createPerson("35$$XY", "Bob Lee", "88 Pine Ave, Victoria", "12-03-1990");
        boolean result = person.updatePersonalDetails("55##WW", "Robert Lee", "99 King Rd, Victoria", "12-03-1990");
        assertTrue(result);
        assertEquals("55##WW", person.getPersonId());
        assertEquals("Robert", person.getFirstName());
        assertEquals("Lee", person.getLastName());
        assertEquals("99 King Rd, Victoria", person.getAddress());
    }
}
