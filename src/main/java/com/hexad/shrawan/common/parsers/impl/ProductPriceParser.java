package com.hexad.shrawan.common.parsers.impl;

import com.hexad.shrawan.models.ProductPrice;
import com.hexad.shrawan.common.parsers.Parser;

import java.util.List;
import java.util.Map;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.groupingBy;

public class ProductPriceParser implements Parser<List<ProductPrice>> {

    @Override
    public Map<String, List<ProductPrice>> parseList(List<String> lines) {
        return lines.stream()
                .map(this::getProductQuantityPrice)
                .collect(groupingBy(ProductPrice::getProductCode));
    }

    private ProductPrice getProductQuantityPrice(String rowValue) {
        String[] values = rowValue.split(CSV_SEPARATOR);
        return new ProductPrice(values[0].trim(), parseInt(values[1].trim()), parseFloat(values[2].trim()));
    }
}
