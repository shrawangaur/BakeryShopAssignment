package com.hexad.shrawan.common.utils;

import com.hexad.shrawan.common.Constants;
import com.hexad.shrawan.common.exceptions.ReaderException;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class FileUtilsTest {
    @Test
    public void testReadFileValid() throws Exception {
        List<String> strings = FileUtils.readFileText(Constants.PRODUCT_CSV_FILE);
        Assert.assertNotNull(strings);
        Assert.assertEquals(3, strings.size());
    }

    @Test(expected = ReaderException.class)
    public void testReadFileInvalid() throws Exception {
        FileUtils.readFileText(TestData.INVALID_CSV_FILE);
    }
}
