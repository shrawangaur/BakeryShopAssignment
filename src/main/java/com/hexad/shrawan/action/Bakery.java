package com.hexad.shrawan.action;

import com.hexad.shrawan.common.Constants;
import com.hexad.shrawan.io.impl.ConsoleWriter;
import com.hexad.shrawan.io.impl.UserInputReader;
import com.hexad.shrawan.io.Reader;

public class Bakery {
    private Reader reader = UserInputReader.getInstance();
    private OrderProcessor orderProcessor;
    private boolean open;

    public void open() {
        this.open = true;
        orderProcessor = new OrderProcessor(this);

        while (open) {
            ConsoleWriter.write(Constants.ORDER_TEXT);
            ConsoleWriter.write(orderProcessor.process(reader.readValue()));
            ConsoleWriter.write(Constants.LINE);
        }
    }

    public void close() {
        this.open = false;
    }
}
