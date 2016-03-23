package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.domain.CryptoHelper;
import com.jpbnetsoftware.homebudget.domain.impl.DefaultCryptoHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by pburzynski on 22/03/2016.
 */
public class CryptoHelperTests {

    @Test
    public void base64Test() {
        CryptoHelper helper = createCryptoHelper();
        Assert.assertEquals("ala ma kota", helper.decodeBase64("YWxhIG1hIGtvdGE="));
    }

    @Test
    public void hashTest() {
        CryptoHelper helper = createCryptoHelper();
        Assert.assertEquals(
                "awuvcNV9JyzXtz5nROT178sFxiNy6UZVWgdj1k0uZ238+JAR8YqcdLj9h2OQpIv4xsBq3tLgGxOmN310S/C5pg==",
                helper.hash("ala ma kota"));
    }

    private CryptoHelper createCryptoHelper() {
        return new DefaultCryptoHelper();
    }
}