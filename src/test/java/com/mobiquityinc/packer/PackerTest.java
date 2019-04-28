package com.mobiquityinc.packer;

import com.mobiquityinc.exception.APIException;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "integration")
public class PackerTest {

    private static final String NON_EXISTING_PATH = "some_never_existing.file_";
    private static final String NEW_LINE = System.lineSeparator();
    private static final Path DATASETS_PATH = Paths.get("src", "test", "resources", "datasets");

    @Test(expectedExceptions = APIException.class)
    public void throwsExceptionWhenCannotReadFile() throws APIException {
        Packer.pack(NON_EXISTING_PATH);
    }

    @Test(dependsOnMethods = "throwsExceptionWhenCannotReadFile")
    public void putsUnreadableFileNameInException() {
        try {
            Packer.pack(NON_EXISTING_PATH);
        } catch (APIException e) {
            assertTrue(e.getMessage().contains(NON_EXISTING_PATH));
        }
    }

    @Test(expectedExceptions = APIException.class)
    public void throwsExceptionWhenFileInvalid() throws APIException {
        Packer.pack(DATASETS_PATH.resolve("invalid-dataset.txt").toString());
    }

    @Test(dependsOnMethods = "throwsExceptionWhenFileInvalid")
    public void putsInvalidLineNumbersInException() {
        try {
            Packer.pack(DATASETS_PATH.resolve("invalid-dataset.txt").toString());
        } catch (APIException e) {
            assertTrue(e.getMessage().contains("line 2:"));
            assertTrue(e.getMessage().contains("line 4:"));
        }
    }

    public void findsSolution() throws APIException {
        Path packDatasetPath = DATASETS_PATH.resolve("pack-dataset.txt");
        assertEquals(Packer.pack(packDatasetPath.toString()), String.join(NEW_LINE, "4", "-", "2,7", "8,9") + NEW_LINE);
    }

}
