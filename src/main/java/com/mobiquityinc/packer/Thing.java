package com.mobiquityinc.packer;

import java.math.BigDecimal;

final class Thing {

    private final int index;
    private final BigDecimal weight;
    private final BigDecimal cost;

    Thing(int index, BigDecimal weight, BigDecimal cost) {
        this.index = index;
        this.weight = weight;
        this.cost = cost;
    }

    int getIndex() {
        return index;
    }

    BigDecimal getWeight() {
        return weight;
    }

    BigDecimal getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Thing{"
                + "index=" + index
                + ", weight=" + weight
                + ", cost=" + cost
                + '}';
    }

}
