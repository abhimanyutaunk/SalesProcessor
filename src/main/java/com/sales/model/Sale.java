package com.sales.model;

import java.math.BigDecimal;

/**
 * This class represents a sale of a product
 * Created by Prashant on 01-04-2017.
 */
public class Sale {
    private String productType;
    private int quantity;
    private BigDecimal price;

    public Sale(String productType, int quantity, BigDecimal price) {
        this.productType = productType;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductType() {
        return productType;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "productType='" + productType + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
