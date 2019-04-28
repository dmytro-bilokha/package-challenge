package com.mobiquityinc.packer;

import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit")
public class PackageLayoutTest {

    public void returnsDashIndexIfEmpty() {
        PackageLayout layout = new PackageLayout(Collections.emptyList());
        assertEquals(layout.getIndexesString(), "-");
    }

    public void returnsCommaSeparatedThingsIndexes() {
        PackageLayout layout = new PackageLayout(Arrays.asList(
                new Thing(11, new BigDecimal("12.2"), new BigDecimal("0.3"))
                , new Thing(42, new BigDecimal("2.5"), new BigDecimal("1.3"))
        ));
        assertEquals(layout.getIndexesString(), "11,42");
    }

    @Test(dependsOnMethods = "returnsCommaSeparatedThingsIndexes")
    public void returnsThingIndexesOrdered() {
        PackageLayout layout = new PackageLayout(Arrays.asList(
                new Thing(42, new BigDecimal("2.5"), new BigDecimal("1.3"))
                , new Thing(11, new BigDecimal("12.2"), new BigDecimal("0.3"))
        ));
        assertEquals(layout.getIndexesString(), "11,42");
    }

    public void detectsOverWeight() {
        PackageLayout layout = new PackageLayout(Arrays.asList(
                new Thing(1, new BigDecimal("12.0009"), new BigDecimal("0.3"))
        ));
        assertTrue(layout.isOverWeighted(new BigDecimal("12")));
    }

    public void detectsUnderWeight() {
        PackageLayout layout = new PackageLayout(Arrays.asList(
                new Thing(1, new BigDecimal("12.0009"), new BigDecimal("0.3"))
        ));
        assertFalse(layout.isOverWeighted(new BigDecimal("12.1")));
    }

    public void allowsMaximumWeightEqualToLimit() {
        PackageLayout layout = new PackageLayout(Arrays.asList(
                new Thing(1, new BigDecimal("12.0009"), new BigDecimal("0.3"))
        ));
        assertFalse(layout.isOverWeighted(new BigDecimal("12.0009")));
    }

    public void emptyWeightIsZero() {
        PackageLayout layout = new PackageLayout(Collections.emptyList());
        assertFalse(layout.isOverWeighted(BigDecimal.ZERO));
    }

    public void determinesBetterByCost() {
        PackageLayout cheapLayout = new PackageLayout(Arrays.asList(
                new Thing(1, new BigDecimal("12.0009"), new BigDecimal("0.3"))
        ));
        PackageLayout expensiveLayout = new PackageLayout(Arrays.asList(
                new Thing(2, new BigDecimal("12.0009"), new BigDecimal("0.6"))
        ));
        assertTrue(expensiveLayout.isThisLayoutBetter(cheapLayout));
        assertFalse(cheapLayout.isThisLayoutBetter(expensiveLayout));
    }

    public void determinesBetterByWeightForEqualCost() {
        PackageLayout lightLayout = new PackageLayout(Arrays.asList(
                new Thing(1, new BigDecimal("12.0009"), new BigDecimal("0.333"))
        ));
        PackageLayout heavyLayout = new PackageLayout(Arrays.asList(
                new Thing(2, new BigDecimal("2.0009"), new BigDecimal("0.333"))
        ));
        assertTrue(heavyLayout.isThisLayoutBetter(lightLayout));
        assertFalse(lightLayout.isThisLayoutBetter(heavyLayout));
    }

    public void sameIsNotBetter() {
        PackageLayout layout = new PackageLayout(Arrays.asList(
                new Thing(1, new BigDecimal("12.0009"), new BigDecimal("0.333"))
        ));
        assertFalse(layout.isThisLayoutBetter(layout));
    }

    public void emptyLayoutSameAsZero() {
        PackageLayout emptyLayout = new PackageLayout(Collections.emptyList());
        PackageLayout zeroLayout = new PackageLayout(Arrays.asList(
                new Thing(1, BigDecimal.ZERO, BigDecimal.ZERO)
        ));
        assertFalse(emptyLayout.isThisLayoutBetter(zeroLayout));
        assertFalse(zeroLayout.isThisLayoutBetter(emptyLayout));
    }

    public void sumsThingsWeight() {
        //Total weight 12.2 + 2.51 = 14.71
        PackageLayout layout = new PackageLayout(Arrays.asList(
                new Thing(11, new BigDecimal("12.2"), new BigDecimal("0.3"))
                , new Thing(42, new BigDecimal("2.51"), new BigDecimal("1.3"))
        ));
        assertFalse(layout.isOverWeighted(new BigDecimal("14.71")));
        assertTrue(layout.isOverWeighted(new BigDecimal("14.7")));
    }

    public void sumsThingsCost() {
        PackageLayout layoutCheap = new PackageLayout(generateCostOnlyThings(2));
        PackageLayout layoutMedium = new PackageLayout(generateCostOnlyThings(3));
        PackageLayout layoutExpensive = new PackageLayout(generateCostOnlyThings(4));
        assertTrue(layoutMedium.isThisLayoutBetter(layoutCheap));
        assertTrue(layoutExpensive.isThisLayoutBetter(layoutCheap));
        assertTrue(layoutExpensive.isThisLayoutBetter(layoutMedium));
    }

    private List<Thing> generateCostOnlyThings(int number) {
        return IntStream.rangeClosed(1, number)
                .mapToObj(id -> new Thing(id, BigDecimal.ZERO, new BigDecimal("0.1")))
                .collect(Collectors.toList());
    }

}
