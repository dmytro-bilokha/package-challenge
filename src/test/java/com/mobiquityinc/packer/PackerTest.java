package com.mobiquityinc.packer;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = "packer.unit")
public class PackerTest {

    public void returnsFilePath() {
        String path = "/some/path";
        assertEquals(Packer.pack(path), path);
    }

}
