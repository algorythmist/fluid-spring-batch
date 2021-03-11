package com.tecacet.fluidbatch;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ClassPathOrFileResourceSupplier {

    public Resource getResource(String resourceName) {
        Resource resource = new FileSystemResource(resourceName);
        if (resource.exists()) {
            return resource;
        }
        resource = new ClassPathResource(resourceName);
        if (!resource.exists()) {
            throw new UncheckedIOException(
                    new IOException("Resource Not Found: " + resourceName));
        }
        return resource;
    }
}
