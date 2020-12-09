package utilities.general;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author FiruzzZ
 */
public final class ClipboardHandler {

    public static Transferable getClipboard() {
        return Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
    }

    /**
     * Gets plain text with Unicode encoding {@link DataFlavor#getTextPlainUnicodeFlavor()}
     *
     * @return String or <code>null</code> if clipboard is empty
     * @throws UnsupportedFlavorException
     * @throws IOException
     */
    public static String getText() throws UnsupportedFlavorException, IOException {
        Transferable t = getClipboard();
        if (t != null) {
            return (String) t.getTransferData(DataFlavor.getTextPlainUnicodeFlavor());
        }
        return null;
    }

    /**
     *
     * @return List of files or <code>null</code> if clipboard is empty
     * @throws UnsupportedFlavorException
     * @throws IOException
     */
    public static List<File> getFiles() throws UnsupportedFlavorException, IOException {
        Transferable t = getClipboard();
        if (t != null) {
            return (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Get an image of the system clipboard.
     *
     * @return Returns an Image or <code>null</code> if clipboard is empty
     * @throws java.awt.datatransfer.UnsupportedFlavorException
     * @throws java.io.IOException
     */
    public static Image getImage() throws UnsupportedFlavorException, IOException {
        Transferable t = getClipboard();
        if (t != null) {
            return (Image) t.getTransferData(DataFlavor.imageFlavor);
        }
        return null;
    }

    private ClipboardHandler() {
    }

}
