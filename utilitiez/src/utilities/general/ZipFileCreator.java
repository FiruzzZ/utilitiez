package utilities.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileCreator {

    public static void createZip(String zipPathDestination, String... sourceFiles) throws FileNotFoundException, IOException {
        byte[] buffer = new byte[1024];
        FileOutputStream fout = new FileOutputStream(zipPathDestination);
        try (ZipOutputStream zout = new ZipOutputStream(fout)) {
            for (String sourceFile : sourceFiles) {
                File file = new File(sourceFile);
                try (final FileInputStream fin = new FileInputStream(file)) {
                    zout.putNextEntry(new ZipEntry(file.getName()));
                    int length;
                    while ((length = fin.read(buffer)) > 0) {
                        zout.write(buffer, 0, length);
                    }
                    zout.closeEntry();
                }
            }
        }
        System.out.println("Zip file has been created:" + zipPathDestination);
    }
}
