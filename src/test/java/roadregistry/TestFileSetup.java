package roadregistry;

import org.junit.jupiter.api.BeforeAll;

import java.io.File;

public class TestFileSetup {
    @BeforeAll
    public static void clearFilesOnce() {
        File people = new File("data/people.txt");
        File demerits = new File("data/demerit_points.txt");
        if (people.exists()) people.delete();
        if (demerits.exists()) demerits.delete();
    }
}
