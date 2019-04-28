package com.mobiquityinc.packer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class PackagingTaskSolver {

    private PackagingTaskSolver() {
        //No need to instantiate this class
    }

    static PackageLayout findBestLayout(PackagingTask packagingTask) {
        //Use empty package as a seed for determining the best layout
        PackageLayout bestLayout = new PackageLayout(Collections.emptyList());
        //Number of possible layouts is 2 in power of number of things. Here we use binary shift to calculate it.
        int numberOfPossibleLayouts = 1 << (packagingTask.getThingsToPackage().size());
        for (int layoutNumber = 0; layoutNumber < numberOfPossibleLayouts; layoutNumber++) {
            PackageLayout generatedLayout = generateLayout(layoutNumber, packagingTask.getThingsToPackage());
            if (!generatedLayout.isOverWeighted(packagingTask.getWeightLimit())
                && generatedLayout.isThisLayoutBetter(bestLayout)) {
                bestLayout = generatedLayout;
            }
        }
        return bestLayout;
    }

    //This method composes package layout from things provided. In the layoutNumber each bit with value 1 means
    //that we should take a thing which index is equal to the bit position.
    private static PackageLayout generateLayout(int layoutNumber, List<Thing> things) {
        List<Thing> layoutThings = new ArrayList<>();
        for (int i = 0; i < things.size(); i++) {
            if ((layoutNumber >> i & 1) != 0) {
                layoutThings.add(things.get(i));
            }
        }
        return new PackageLayout(layoutThings);
    }

}
