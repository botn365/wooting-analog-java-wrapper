package com.github.botn365.main;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

public class NataiveLoader {

    private static void unpackAndLoadNativeLib(String libFileName) {
        loadUnpackedLibrary(unpackNativeLib(libFileName));
    }

    public static File unpackNativeLib(String libFileName) {
        try {
            final File unpackedLibFile = unpackedLibFile(libFileName);
            if (unpackedLibExists(unpackedLibFile)) {
                if (unpackedLibraryHashCheck(packedLibInputStream(libFileName), unpackedLibFile)) {
                    return unpackedLibFile;
                } else {
                    if (!unpackedLibFile.delete())
                        throw new RuntimeException("Failed to delete: " + unpackedLibFile.getAbsolutePath());
                }
            }
            unpackLibrary(packedLibInputStream(libFileName), unpackedLibFile);
            if (!unpackedLibraryHashCheck(packedLibInputStream(libFileName), unpackedLibFile))
                throw new RuntimeException("Failed to unpack: " + unpackedLibFile.getAbsolutePath());
            return unpackedLibFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasUnpackedFile(String libFileName) {
        URL file = NataiveLoader.class.getResource("/"+libFileName);
        return file != null;
    }

    private static InputStream packedLibInputStream(String libFileName) {
        return NataiveLoader.class.getResourceAsStream("/" + libFileName);
    }

    private static File unpackedLibFile(String libFileName) {
        return new File(libFileName);
    }

    private static boolean unpackedLibExists(File unpackedLibrary) {
        return unpackedLibrary.isFile();
    }

    private static boolean unpackedLibraryHashCheck(InputStream packedLibInputStream, File unpackedLibFile) throws IOException {
        return DigestUtils.sha256Hex(packedLibInputStream).equals(
                DigestUtils.sha256Hex(Files.newInputStream(unpackedLibFile.toPath())));
    }

    private static void unpackLibrary(InputStream packedLibInputStream, File unpackedLibFile) throws IOException {
        FileUtils.copyInputStreamToFile(packedLibInputStream, unpackedLibFile);
    }

    private static void loadUnpackedLibrary(File unpackedLibrary) {
        System.load(unpackedLibrary.getAbsolutePath());
    }
}
