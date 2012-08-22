package net.eisele.example.rewritetricks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import org.ocpsoft.common.util.Streams;
import org.ocpsoft.rewrite.servlet.config.OutputBuffer;

/**
 * GZIP Compression for your application as an OutputBuffer
 * @author eiselem
 */
public class ZipOutputBuffer implements OutputBuffer {

    private final static Logger LOGGER = Logger.getLogger(ZipOutputBuffer.class.getName());

    @Override
    public InputStream execute(InputStream input) {
        String contents = Streams.toString(input);
        LOGGER.log(Level.FINER, "Content {0} Length {1}", new Object[]{contents, contents.getBytes().length});
        byte[] compressed = compress(contents);
        LOGGER.log(Level.FINER, "Length: {0}", compressed.length);
        return new ByteArrayInputStream(compressed);


    }

    public static byte[] compress(String string) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        byte[] compressed = null;
        try {
            try (GZIPOutputStream gos = new GZIPOutputStream(os)) {
                gos.write(string.getBytes());
            } 
            compressed = os.toByteArray();
            os.close();
        } catch (IOException iox) {
            LOGGER.log(Level.SEVERE, "Compression Failed: ", iox);
        }


        return compressed;
    }
}
