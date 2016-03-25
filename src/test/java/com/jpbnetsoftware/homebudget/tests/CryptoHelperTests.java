package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.domain.CryptoHelper;
import com.jpbnetsoftware.homebudget.domain.InvalidKeyException;
import com.jpbnetsoftware.homebudget.domain.impl.DefaultCryptoHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by pburzynski on 22/03/2016.
 */
public class CryptoHelperTests {

    @Test
    public void base64DecodeTest() {
        CryptoHelper helper = createCryptoHelper();
        Assert.assertEquals("ala ma kota", helper.decodeBase64("YWxhIG1hIGtvdGE="));
    }

    @Test
    public void base64EncodeTest() {
        CryptoHelper helper = createCryptoHelper();
        Assert.assertEquals("YWxhIG1hIGtvdGE=", helper.encodeBase64("ala ma kota"));
    }

    @Test
    public void hashTest() {
        CryptoHelper helper = createCryptoHelper();
        Assert.assertEquals(
                "awuvcNV9JyzXtz5nROT178sFxiNy6UZVWgdj1k0uZ238+JAR8YqcdLj9h2OQpIv4xsBq3tLgGxOmN310S/C5pg==",
                helper.hash("ala ma kota"));
    }

    @Test
    public void encryptionTest() {
        CryptoHelper helper = createCryptoHelper();

        String message = "ala ma kota";
        String key = "kasia";

        String cypher = helper.encrypt(message, key);
        Assert.assertNotEquals(cypher, message);
        String decrypted = helper.decrypt(cypher, key);
        Assert.assertEquals(decrypted, message);
    }

    @Test(expected = InvalidKeyException.class)
    public void badKeyTest() {
        CryptoHelper helper = createCryptoHelper();

        String message = "ala ma kota";
        String key = "kasia";

        String cypher = helper.encrypt(message, key);
        Assert.assertNotEquals(cypher, message);
        helper.decrypt(cypher, "invalid key");
    }

    private CryptoHelper createCryptoHelper() {
        return new DefaultCryptoHelper();
    }
}
