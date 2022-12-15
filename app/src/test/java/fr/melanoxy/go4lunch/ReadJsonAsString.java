package fr.melanoxy.go4lunch;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadJsonAsString {

    public static String readFileAsString(String file)throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(file)));
    }
}
