package com.productiveedge.content_mgmt_automation.entity.tag;

import org.junit.Test;

public class TagUtilTest {

    @Test
    public void validateData() {
        System.out.println(new BaseTag().validateData("\"Hello world\""));
        System.out.println(new BaseTag().validateData("\"Hello' 'world\""));

    }
}