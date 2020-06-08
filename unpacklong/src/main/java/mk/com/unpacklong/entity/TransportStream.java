package mk.com.unpacklong.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mk.com.unpacklong.util.DecodeParameter;

//@Getter
//@Setter
public class TransportStream {
    public static final int LEN_188 = 188;
    public static final int LEN_204 = 204;
    public static final byte SYNC_BYTE = 0x47;
    private String file;
    private int position;
    private int packageLen;
    private byte[] tsData;
    private int size;
    private List<Package> packages = new ArrayList<>();

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public byte[] getTsData() {
        return tsData;
    }

    public void setTsData(byte[] tsData) {
        this.tsData = tsData;
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
