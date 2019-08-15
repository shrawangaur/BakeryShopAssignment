package com.hexad.shrawan.action;

import com.hexad.shrawan.common.Constants;
import com.hexad.shrawan.common.exceptions.InputException;
import com.hexad.shrawan.common.parsers.Parser;
import com.hexad.shrawan.common.parsers.impl.UserInputParser;
import com.hexad.shrawan.models.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

public class OrderProcessor {
    private BakeryProductStore bakeryProductStore = BakeryProductStore.getInstance();
    private Parser<Integer> userInputParser = new UserInputParser();
    private Bakery bakery;

    public OrderProcessor(Bakery bakery) {
        this.bakery = bakery;
    }

    public String process(String inputString) {
        if(!Constants.EXIT_COMMANDS.contains(inputString.trim())) {
            try {
                Map<String, Integer> userInput = userInputParser.parseList(singletonList(inputString));
                return userInput.entrySet().stream()
                        .map(this::generateOrderBill)
                        .collect(Collectors.joining(Constants.NEWLINE));
            } catch (InputException ie) {
                return ie.getMessage();
            }
        } else {
            bakery.close();
            return Constants.BAKERY_CLOSED_TEXT;
        }
    }

    private String generateOrderBill(Entry<String, Integer> userInputEntry) {
        final Product product = bakeryProductStore.findProduct(userInputEntry.getKey());

        if(nonNull(product)) {
            final Integer quantity = userInputEntry.getValue();
            return printBill(calculateBill(product, quantity), product, quantity);
        } else {
            throw new InputException(Constants.INVALID_PRODUCT_CODE);
        }
    }

    private Map<Integer, Integer> calculateBill(Product product, Integer quantity) {
        Map<Integer, Integer> output = new HashMap<>();

        List<Integer> packageSizeList = product.getSortedSupportedPackageList();
        int orderQuantity = quantity;
        int startIndex = 0;
        int packageSize = 0;

        while(orderQuantity > 0  && startIndex < packageSizeList.size()){
            if(packageSize > 0) {
                if (packageSizeList.indexOf(packageSize) + 1 == packageSizeList.size()) {
                    packageSize = packageSizeList.get(0);
                }

                if (output.containsKey(packageSize)) {
                    orderQuantity = orderQuantity + packageSize;
                    if (output.get(packageSize) > 1) {
                        output.put(packageSize, output.get(packageSize) - 1);
                    } else {
                        output.remove(packageSize);
                    }
                    startIndex = packageSizeList.indexOf(packageSize) + 1;
                }
            }
            for (int index=startIndex; index<packageSizeList.size(); index++) {
                if (orderQuantity/packageSizeList.get(index) > 0) {
                    packageSize = packageSizeList.get(index);
                    output.put(packageSize, orderQuantity/packageSize);
                    orderQuantity = orderQuantity % packageSize;
                }
            }

            startIndex++;
        }

        if(orderQuantity > 0) {
            output.clear();
        }

        return output;
    }

    private String printBill(Map<Integer, Integer> output, Product product, Integer quantity) {
        if(output.isEmpty()) {
            return Constants.INVALID_INPUT_PRODUCT_COUNT;
        } else {
            StringBuffer outputBuffer = new StringBuffer();
            float totalOrderValue = 0f;

            for(Integer packSize :  output.keySet()) {
                totalOrderValue += output.get(packSize) * product.getPrice(packSize);

                outputBuffer.append(Constants.NEWLINE + Constants.TABSPACE + output.get(packSize) + Constants.MUL + packSize + Constants.CURRENCY
                        + product.getPrice(packSize));
            }

            return quantity + Constants.SPACE + product + Constants.SPACE + Constants.CURRENCY + totalOrderValue + outputBuffer.toString();
        }
    }
}
