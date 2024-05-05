package com.risknarrative.springexercise;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class TestUtil {

  public static String getStringFromFile(String fileName) throws IOException, URISyntaxException {
    return new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(fileName).toURI())),
                  StandardCharsets.UTF_8);
  }
}
