package com.mobiquityinc.packer;

import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit")
public class PackagingTaskParserTest {

    @Test(expectedExceptions = InvalidTaskStringException.class)
    public void bansLineWithoutSeparator() throws InvalidTaskStringException {
        PackagingTaskParser.parse("81 (1,53.38,€13)");
    }

    @Test(expectedExceptions = InvalidTaskStringException.class)
    public void bansLineWithTwoSeparators() throws InvalidTaskStringException {
        PackagingTaskParser.parse("81 : 15 : (1,53.38,€13)");
    }

    @Test(expectedExceptions = InvalidTaskStringException.class)
    public void bansInvalidWeightLimit() throws InvalidTaskStringException {
        PackagingTaskParser.parse("81X : (1,53.38,€13)");
    }

    @Test(dependsOnMethods = "bansInvalidWeightLimit")
    public void includesInvalidWeightDataInExceptionMessage() {
        try {
            PackagingTaskParser.parse("81X : (1,53.38,€13)");
        } catch (InvalidTaskStringException e) {
            assertTrue(e.getMessage().contains("81X"));
        }
    }

    public void parsesSimpleTaskWeight() throws InvalidTaskStringException {
        PackagingTask task = PackagingTaskParser.parse("81 : (1,53.38,€13)");
        assertEquals(task.getWeightLimit(), new BigDecimal("81"));
    }

    public void parsesSimpleTaskThing() throws InvalidTaskStringException {
        PackagingTask task = PackagingTaskParser.parse("81 : (1,53.38,€13)");
        Thing thing = task.getThingsToPackage().get(0);
        assertThingProperties(thing, 1, "53.38", "13");
    }

    @Test(dependsOnMethods = {"parsesSimpleTaskWeight", "parsesSimpleTaskThing"})
    public void parsesTaskWithAllFractional() throws InvalidTaskStringException {
        PackagingTask task = PackagingTaskParser.parse("8.01 : (1,0.88,€13.99)");
        assertEquals(task.getWeightLimit(), new BigDecimal("8.01"));
        assertThingProperties(task.getThingsToPackage().get(0), 1, "0.88", "13.99");
    }

    private void assertThingProperties(Thing thing, int index, String weight, String cost) {
        assertEquals(thing.getIndex(), index);
        assertEquals(thing.getWeight(), new BigDecimal(weight));
        assertEquals(thing.getCost(), new BigDecimal(cost));
    }

    @Test(dependsOnMethods = {"parsesTaskWithAllFractional"})
    public void parsesThingsList() throws InvalidTaskStringException {
        PackagingTask task = PackagingTaskParser.parse("81 : (1,3.38,€1.01) (2,0.8,€13.2) (3,1.111,€0.16)");
        assertThingProperties(task.getThingsToPackage().get(0), 1, "3.38", "1.01");
        assertThingProperties(task.getThingsToPackage().get(1), 2, "0.8", "13.2");
        assertThingProperties(task.getThingsToPackage().get(2), 3, "1.111", "0.16");
    }

    @Test(expectedExceptions = InvalidTaskStringException.class)
    public void detectsUnbalancedBraces1() throws InvalidTaskStringException {
        PackagingTaskParser.parse("81 : (1,53.38,€13) 2,0.8,€12)");
    }

    @Test(dependsOnMethods = "detectsUnbalancedBraces1")
    public void includesThingsPartInExceptionMessage() {
        try {
            PackagingTaskParser.parse("81 : (1,53.38,€13) 2,0.8,€12)");
        } catch (InvalidTaskStringException e) {
            assertTrue(e.getMessage().contains("(1,53.38,€13) 2,0.8,€12)"));
        }
    }

    @Test(expectedExceptions = InvalidTaskStringException.class)
    public void detectsUnbalancedBraces2() throws InvalidTaskStringException {
        PackagingTaskParser.parse("81 : (1,53.38,€13) (2,0.8,€12");
    }

    @Test(expectedExceptions = InvalidTaskStringException.class)
    public void detectsUnbalancedBraces3() throws InvalidTaskStringException {
        PackagingTaskParser.parse("81 : (1,53.38,€13) (2,0.8,€12) )");
    }

    @Test(expectedExceptions = InvalidTaskStringException.class)
    public void bansTaskWithoutThingDefinition() throws InvalidTaskStringException {
        PackagingTaskParser.parse("81 : ");
    }

    @Test(expectedExceptions = InvalidTaskStringException.class)
    public void bansTaskWithEmptyThingDefinition() throws InvalidTaskStringException {
        PackagingTaskParser.parse("81 : (1,53.38,€13) ()");
    }

    @Test(expectedExceptions = InvalidTaskStringException.class)
    public void bansTaskWithInvalidThingDefinition() throws InvalidTaskStringException {
        PackagingTaskParser.parse("81 : (1,53.3e3,€13)");
    }

    @Test(expectedExceptions = InvalidTaskStringException.class)
    public void bansWeightLimitMoreThanLimit() throws InvalidTaskStringException {
        PackagingTaskParser.parse("101 : (1,53.3,€13)");
    }

    @Test(expectedExceptions = InvalidTaskStringException.class)
    public void bansWeightMoreThanLimit() throws InvalidTaskStringException {
        PackagingTaskParser.parse("81 : (1,100.3,€13)");
    }

    @Test(expectedExceptions = InvalidTaskStringException.class)
    public void bansCostMoreThanLimit() throws InvalidTaskStringException {
        PackagingTaskParser.parse("81 : (1,1.3,€101)");
    }

    @Test(expectedExceptions = InvalidTaskStringException.class)
    public void bansThingsMoreThanLimit() throws InvalidTaskStringException {
        PackagingTaskParser.parse("81 :"
            + " (1,1.3,€10) (2,1.3,€10) (3,1.3,€10) (4,1.3,€10) (5,1.3,€10)"
            + " (6,1.3,€10) (7,1.3,€10) (8,1.3,€10) (9,1.3,€10) (10,1.3,€10)"
            + " (11,1.3,€10) (12,1.3,€10) (13,1.3,€10) (14,1.3,€10) (15,1.3,€10)"
            + " (16,1.3,€10)"
        );
    }

}
