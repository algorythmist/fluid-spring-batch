package com.tecacet.fluidbatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.io.UncheckedIOException;


class ClassPathOrFileResourceSupplierTest {

    @Test
    void missingResource() {
        try {
            new ClassPathOrFileResourceSupplier().getResource("missing");
            fail();
        } catch (UncheckedIOException uio) {
            assertEquals("java.io.IOException: Resource Not Found: missing", uio.getMessage());
        }

    }

    @Test
    void loadClasspathFile() {
        Resource resource = new ClassPathOrFileResourceSupplier().getResource("classpathfile.txt");
        assertTrue(resource.exists());
    }

    @Test
    void loadFilesystemFile() {
        Resource resource = new ClassPathOrFileResourceSupplier().getResource("filesystemfile.txt");
        assertTrue(resource.exists());
    }
}
