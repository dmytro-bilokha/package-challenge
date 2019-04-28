package com.mobiquityinc.packer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class PackagingTask {

    private final BigDecimal weightLimit;
    private final List<Thing> thingsToPackage;

    PackagingTask(BigDecimal weightLimit, Collection<Thing> thingsToPackage) {
        this.weightLimit = weightLimit;
        this.thingsToPackage = new ArrayList<>(thingsToPackage);
    }

    BigDecimal getWeightLimit() {
        return weightLimit;
    }

    List<Thing> getThingsToPackage() {
        return Collections.unmodifiableList(thingsToPackage);
    }

    @Override
    public String toString() {
        return "PackagingTask{"
                + "weightLimit=" + weightLimit
                + ", thingsToPackage=" + thingsToPackage
                + '}';
    }

}
