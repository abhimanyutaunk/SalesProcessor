package com.sales.process;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;

/**
 * Test cases for Sales Processor
 * Created by Prashant on 01-04-2017.
 */
public class TestProcessor {

    private static Processor processor;

    @BeforeClass
    public static void setUp(){
        processor = new Processor();
        processor.recordSale("Apple", new BigDecimal(40));
        processor.recordMultiSale("Apple", 10, new BigDecimal(80));

        processor.recordSale("Lemon", new BigDecimal(50));
        processor.recordMultiSale("Lemon", 14, new BigDecimal(25));

        processor.recordAdjustments("Apple", "ADD", new BigDecimal(10));

        processor.recordAdjustments("Lemon", "MULTIPLY", new BigDecimal(1.5));
        processor.recordAdjustments("Lemon", "SUBTRACT", new BigDecimal(5));

        System.out.println(processor.getTotalValues());
    }

    @Test
    public void testTotalSales(){
        assertEquals("Apple total sales should be 11", processor.getTotalSales().get("Apple"), Integer.valueOf(11));
        assertEquals("Lemon total sales should be 15", processor.getTotalSales().get("Lemon"), Integer.valueOf(15));
    }

    @Test
    public void testTotalValue(){
        assertEquals("Apple total sale value should be 950.0", processor.getTotalValues().get("Apple"), Double.valueOf(950.0));
        assertEquals("Lemon total sale value should be 525.0", processor.getTotalValues().get("Lemon"), Double.valueOf(525.0));
    }

    @Test
    public void testProductSummary(){
        String report = "\n******Product Summary Report********" +
                "\n[Type \t| Quantity \t| Value \t\t]" +
                "\n[Apple\t| 11\t\t| 950.0000\t\t]" +
                "\n[Lemon\t| 15\t\t| 525.0000\t\t]";

        assertEquals("Product Report does not match", processor.generateProductSummary(), report);
    }

    @Test
    public void testAdjustmentSummary(){
        String report = "\n*********Adjustment Summary Report*********" +
                "\n\tApple -> ADD by 10" +
                "\n\tLemon -> MULTIPLY by 1.5" +
                "\n\tLemon -> SUBTRACT by 5";

        assertEquals("Adjustment Report does not match", processor.generateAdjustmentSummary(), report);
    }
}
