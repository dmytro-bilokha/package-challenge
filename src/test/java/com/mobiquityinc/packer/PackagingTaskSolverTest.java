package com.mobiquityinc.packer;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.assertEquals;

@Test(groups = "integration")
public class PackagingTaskSolverTest {

    @Test(groups = "solver.simple")
    public void findsEmptyPackagingBetterThanOverweight1() {
        PackagingTask task = new PackagingTask(BigDecimal.TEN, Collections.singletonList(
                new Thing(42, BigDecimal.valueOf(55L), new BigDecimal("99.99"))
        ));
        assertEquals(PackagingTaskSolver.findBestLayout(task).getIndexesString(), "-");
    }

    @Test(groups = "solver.simple")
    public void findsEmptyPackagingBetterThanOverweight2() {
        PackagingTask task = new PackagingTask(BigDecimal.TEN, Arrays.asList(
                new Thing(42, BigDecimal.valueOf(55L), new BigDecimal("99.99"))
                , new Thing(15, BigDecimal.valueOf(11L), new BigDecimal("0.99"))
        ));
        assertEquals(PackagingTaskSolver.findBestLayout(task).getIndexesString(), "-");
    }

    @Test(groups = "solver.simple")
    public void findsObviousSolution() {
        PackagingTask task = new PackagingTask(BigDecimal.TEN, Collections.singletonList(
                new Thing(36, BigDecimal.valueOf(5L), new BigDecimal("99.99"))
        ));
        assertEquals(PackagingTaskSolver.findBestLayout(task).getIndexesString(), "36");
    }

    @Test(groups = "solver.simple")
    public void findsMoreCostlyThingFromTwo() {
        PackagingTask task = new PackagingTask(BigDecimal.TEN, Arrays.asList(
                new Thing(100, BigDecimal.valueOf(6L), new BigDecimal("99.99"))
                , new Thing(15, BigDecimal.valueOf(6L), new BigDecimal("0.99"))
        ));
        assertEquals(PackagingTaskSolver.findBestLayout(task).getIndexesString(), "100");
    }

    @Test(groups = "solver.simple")
    public void picksTwoThingsFromThree() {
        PackagingTask task = new PackagingTask(BigDecimal.TEN, Arrays.asList(
                new Thing(3, BigDecimal.valueOf(6L), new BigDecimal("70.99"))
                , new Thing(1, BigDecimal.valueOf(4L), new BigDecimal("51.99"))
                , new Thing(2, BigDecimal.valueOf(7L), new BigDecimal("100.99"))
        ));
        assertEquals(PackagingTaskSolver.findBestLayout(task).getIndexesString(), "1,3");
    }

    @DataProvider(name = "packaging-tasks")
    public Object[][] getPackagingTasks() {
        return new Object[][]{
            {new PackagingTask(new BigDecimal("12.1"), Arrays.asList(
                    new Thing(1, new BigDecimal("6.6"), new BigDecimal("0.333"))
                    , new Thing(2, new BigDecimal("1.5"), new BigDecimal("0.111"))
                    , new Thing(3, new BigDecimal("5.6"), new BigDecimal("0.222"))
                    , new Thing(4, new BigDecimal("3.6"), new BigDecimal("0.444"))
                )), "1,2,4"}
            , {new PackagingTask(new BigDecimal("67"), Arrays.asList(
                    new Thing(1, new BigDecimal("23"), new BigDecimal("50.5"))
                    , new Thing(2, new BigDecimal("26"), new BigDecimal("35.2"))
                    , new Thing(3, new BigDecimal("20"), new BigDecimal("45.8"))
                    , new Thing(4, new BigDecimal("18"), new BigDecimal("22.0"))
                    , new Thing(5, new BigDecimal("32"), new BigDecimal("35.4"))
                    , new Thing(6, new BigDecimal("27"), new BigDecimal("41.4"))
                    , new Thing(7, new BigDecimal("29"), new BigDecimal("49.8"))
                    , new Thing(8, new BigDecimal("26"), new BigDecimal("54.5"))
                    , new Thing(9, new BigDecimal("30"), new BigDecimal("47.3"))
                    , new Thing(10, new BigDecimal("27"), new BigDecimal("54.3"))
                )), "1,4,8"}
            , {new PackagingTask(new BigDecimal("5"), Arrays.asList(
                    new Thing(1, new BigDecimal("2"), new BigDecimal("3"))
                    , new Thing(2, new BigDecimal("3"), new BigDecimal("4"))
                    , new Thing(3, new BigDecimal("4"), new BigDecimal("5"))
                    , new Thing(4, new BigDecimal("5"), new BigDecimal("6"))
                )), "1,2"}
        };
    }

    @Test(groups = "solver.complex", dependsOnGroups = "solver.simple", dataProvider = "packaging-tasks")
    public void findsBestLayout(PackagingTask task, String answer) {
        assertEquals(PackagingTaskSolver.findBestLayout(task).getIndexesString(), answer);
    }

}
