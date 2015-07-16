package com.yahoo.mobile.itern.guagua.Util;

import com.parse.ParseObject;

/**
 * Created by cmwang on 7/16/15.
 */
public class ParseUtils {
    static public void testParse() {
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }
}
