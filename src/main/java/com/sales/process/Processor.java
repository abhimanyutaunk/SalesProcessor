package com.sales.process;

import com.sales.model.Adjustment;
import com.sales.model.Operation;
import com.sales.model.Sale;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;
import static java.util.stream.Collectors.summingInt;

/**
 * Sales processor responsible for recording and processing messages. Also generates reports.
 * Created by Prashant on 01-04-2017.
 */
public class Processor {

    private List<Sale> sales = new ArrayList<>();
    private List<Adjustment> adjustments = new ArrayList<>();

    /**
     * Record a single sale
     * @param productType
     * @param price
     */
    public void recordSale(String productType, BigDecimal price){
        sales.add(new Sale(productType, 1, price));
    }

    /**
     * Record multi sale
     * @param productType
     * @param quantity
     * @param price
     */
    public void recordMultiSale(String productType, int quantity, BigDecimal price){
        sales.add(new Sale(productType, quantity, price));
    }

    /**
     * Record adjustments to prices
     * @param productType
     * @param operation
     * @param adjust
     */
    public void recordAdjustments(String productType, String operation, BigDecimal adjust){
        adjustments.add(new Adjustment(productType, operation, adjust));
    }

    /**
     * Generate Product Summary report detailing type, total number of sales and their value
     */
    public String generateProductSummary(){
        StringBuilder  report = new StringBuilder();
        report.append("\n******Product Summary Report********");
        report.append("\n[Type \t| Quantity \t| Value \t\t]");

        Map<String, Double> amounts = getTotalValues();
        getTotalSales().forEach(
                (key, value) -> report.append("\n[" + key + "\t| " + value + "\t\t| " + new BigDecimal(amounts.get(key)).setScale(4, RoundingMode.HALF_UP) + "\t\t]")
        );

        return report.toString();
    }

    /**
     * Get total number of sales for each product type
     * @return
     */
    public Map<String, Integer> getTotalSales(){
        return sales.parallelStream()
                    .collect(groupingBy(Sale::getProductType, summingInt(Sale::getQuantity)));
    }

    /**
     * Get total value (after adjustments) for each product type
     * @return
     */
    public Map<String, Double> getTotalValues(){

        return sales.parallelStream()
                    .collect(groupingBy(Sale::getProductType, summingDouble(sale -> getAdjustedValue(sale.getProductType(), sale.getQuantity(), sale.getPrice()))));
    }

    /**
     * Apply adjustments to each product sequentially
     * @param productType
     * @param originalPrice
     * @return
     */
    private Double getAdjustedValue(String productType, Integer quantity, BigDecimal originalPrice){
        List<Adjustment> applicableAdjustments =
            adjustments
                .stream()
                .filter(x -> x.getProductType().equals(productType))
                .collect(Collectors.toList());

        BigDecimal adjustedPrice = originalPrice;
        for(Adjustment adjustment : applicableAdjustments){
            adjustedPrice = applyAdjustment(adjustedPrice, adjustment.getOperation(), adjustment.getAdjust());
        }

        return adjustedPrice.multiply(new BigDecimal(quantity)).doubleValue();
    }

    /**
     * Apply adjustment as per operation
     * @param originalPrice
     * @param operation
     * @param adjustment
     * @return
     */
    private BigDecimal applyAdjustment(BigDecimal originalPrice, Operation operation, BigDecimal adjustment){
        switch (operation){
            case ADD:
                return originalPrice.add(adjustment);
            case SUBTRACT:
                return originalPrice.subtract(adjustment);
            case MULTIPLY:
                return originalPrice.multiply(adjustment);
            default:
                return originalPrice;
        }
    }

    /**
     * Generate adjustment summary report which details each adjustment made to a product type sequentially
     */
    public String generateAdjustmentSummary(){
        StringBuilder report = new StringBuilder();
        if(adjustments.size() == 0 ){
            report.append("No adjustments");
            return report.toString();
        }
        else{
            report.append("\n*********Adjustment Summary Report*********");
            adjustments
                    .stream()
                    .collect(
                            groupingBy(Adjustment::getProductType)
                    ).forEach(
                    (key, value) -> value.forEach(adjust -> report.append("\n\t"+adjust.toString()))
            );
        }

        return report.toString();
    }

}
