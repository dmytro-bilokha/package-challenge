package com.mobiquityinc.packer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class PackageLayout {

    private final BigDecimal weight;
    private final BigDecimal cost;
    private final List<Integer> thingIndexes;

    PackageLayout(Collection<Thing> things) {
        BigDecimal totalThingsWeight = BigDecimal.ZERO;
        BigDecimal totalThingsCost = BigDecimal.ZERO;
        thingIndexes = new ArrayList<>();
        for (Thing thing : things) {
            totalThingsWeight = totalThingsWeight.add(thing.getWeight());
            totalThingsCost = totalThingsCost.add(thing.getCost());
            thingIndexes.add(thing.getIndex());
        }
        this.weight = totalThingsWeight;
        this.cost = totalThingsCost;
        //Sort thing indexes to have a deterministic output
        Collections.sort(thingIndexes);
    }

    boolean isOverWeighted(BigDecimal maximumWeight) {
        return weight.compareTo(maximumWeight) > 0;
    }

    String getIndexesString() {
        if (thingIndexes.isEmpty()) {
            return "-";
        }
        return thingIndexes.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    boolean isThisLayoutBetter(PackageLayout other) {
        int costComparisonResult = this.cost.compareTo(other.cost);
        if (costComparisonResult != 0) {
            return costComparisonResult > 0;
        }
        return this.weight.compareTo(other.weight) < 0;
    }

    @Override
    public String toString() {
        return "PackageLayout{"
                + "weight=" + weight
                + ", cost=" + cost
                + ", thingIndexes=" + thingIndexes
                + '}';
    }

}
