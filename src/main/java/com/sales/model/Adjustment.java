package com.sales.model;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * This class represents adjustment made to product
 * Created by Prashant on 01-04-2017.
 */

public class Adjustment {
    private String productType;
    private Operation operation;
    private BigDecimal adjust;

    public Adjustment(String productType, String operation, BigDecimal adjust) {
        this.productType = productType;
        this.operation = Operation.valueOf(operation);
        this.adjust = adjust;
    }

    public String getProductType() {
        return productType;
    }

    public Operation getOperation() {
        return operation;
    }

    public BigDecimal getAdjust() {
        return adjust;
    }

    @Override
    public String toString() {
        return productType + " -> "+ operation.toString() + " by " + adjust.round(MathContext.DECIMAL64);
    }
}
