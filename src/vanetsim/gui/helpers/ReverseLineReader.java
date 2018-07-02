package vanetsim.gui.helpers;

import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReverseLineReader {

    private RandomAccessFile realReader = null;

    private int aproxBytesPerLine = 0;

    private long lastPosition = -1;

    private boolean fileStartReached = false;

    public ReverseLineReader(String file) throws FileNotFoundException, IOException {
        this(file, 100);
    }

    public ReverseLineReader(String file, long startPosition) throws FileNotFoundException, IOException {
        this(file, 100, startPosition);
    }

    public ReverseLineReader(String file, int aproxBytesPerLine) throws FileNotFoundException, IOException {
        super();
        realReader = new RandomAccessFile(file, "r");
        this.aproxBytesPerLine = aproxBytesPerLine;
        lastPosition = realReader.length();
    }

    public ReverseLineReader(String file, int aproxBytesPerLine, long startPosition) throws FileNotFoundException, IOException {
        super();
        realReader = new RandomAccessFile(file, "r");
        this.aproxBytesPerLine = aproxBytesPerLine;
        lastPosition = startPosition;
    }

    public String readPreviousLine() throws IOException {
        if (fileStartReached)
            return null;
        String ret = null;
        boolean abort = false;
        int count = 0;
        while (!abort) {
            count++;
            int byteReads = aproxBytesPerLine * count;
            while (lastPosition - byteReads < 0) byteReads--;
            realReader.seek(lastPosition - byteReads);
            byte buf[] = new byte[byteReads];
            int read = realReader.read(buf, 0, buf.length);
            String tmp = new String(buf, 0, read);
            int position = -1;
            if (tmp.indexOf("\r\n") != -1) {
                position = tmp.lastIndexOf("\r\n") + 2;
                abort = true;
            } else if (tmp.indexOf("\n") != -1) {
                position = tmp.lastIndexOf("\n") + 1;
                abort = true;
            } else if (lastPosition - read <= 0) {
                ret = tmp;
                abort = true;
                fileStartReached = true;
            }
            if (abort && position != -1) {
                lastPosition = lastPosition - (byteReads - position + 1);
                ret = tmp.substring(position);
            }
        }
        return ret;
    }

    public void reset() throws IOException {
        fileStartReached = false;
        lastPosition = realReader.length();
    }

    public void close() throws IOException {
        realReader.close();
    }
}
