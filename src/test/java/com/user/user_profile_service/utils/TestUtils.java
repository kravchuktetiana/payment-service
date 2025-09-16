package com.user.user_profile_service.utils;

import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {
    public static String readSql(String location) throws Exception {
        ClassPathResource resource = new ClassPathResource(location);
        Path path = resource.getFile().toPath();
        return Files.readString(path, StandardCharsets.UTF_8);
    }
}
