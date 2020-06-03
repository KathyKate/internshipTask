package mk.com.unpacklong.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//@Getter
//@Setter
public class TransportStream {
    public static final int LEN_188 = 188;
    public static final int LEN_204 = 204;
    public static final byte SYN_BYTE =0x47;

    private File file;
    private int position;
    private int packageLen;
    private int size;
    private List<Package> packages = new ArrayList<>();

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPackageLen() {
        return packageLen;
    }

    public void setPackageLen(int packageLen) {
        this.packageLen = packageLen;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }
}
