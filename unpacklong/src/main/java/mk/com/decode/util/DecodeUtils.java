package mk.com.decode.util;

import android.os.Environment;

import java.io.File;
import java.util.Arrays;
import mk.com.decode.entity.Package;
import mk.com.decode.entity.TransportStream;

/**
 * @ClassName: DocedeUtils
 * @Description: description
 * @Author: xiaolan
 * @Date: 2020/6/10 9:37
 */
public class DecodeUtils {


    /**
     * @param ts
     * @return void
     * @method decodePackage
     * @description decode transport stream into packet
     * @date 2020/6/8 19:48
     */
    public static void decodePackage(TransportStream ts) throws Exception {
        if (ts.getTsData() == null) {
            throw new Exception("该码流还未初始化");
        }
        if (ts.getPackageLen() == 0) {
            throw new Exception("该码流无效");
        }
        byte[] container;
        int from = ts.getPosition();
        int to = ts.getPosition() + ts.getPackageLen();
        while (to < ts.getTsData().length) {
            container = Arrays.copyOfRange(ts.getTsData(), from, to);
            from = to;
            to += ts.getPackageLen();
            ts.getPackages().add(new Package(container));
        }
    }

    /**
     * @method analysisTransportStream
     * @description give a transport stream file name,initial transport stream object
     * @date 2020/6/10 9:45
     * @param tsData
     * @return mk.com.decode.entity.TransportStream
     */
    public static TransportStream analysisTransportStream(byte[] tsData) {
        TransportStream ts = new TransportStream();
        ts.setTsData(tsData);
        TransportStream t = PackageUtils.judgePackageLength(tsData);
        ts.setPackageLen(t.getPackageLen());
        ts.setPosition(t.getPosition());
        return ts;
    }


    /**
     * @method writeTransportStreamHexContent
     * @description give a transport stream object and destination file name ,will write in the file by hex content
     * @date 2020/6/10 9:47
     * @param ts
     * @param destName
     * @return void
     */
    public static void writeTransportStreamHexContent(TransportStream ts, String destName) throws Exception {
        if (!(destName == null) && !destName.equals("")) {
            destName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + destName;
        }
        FileUtils.writeFileHexContent(ts.getTsData(), destName);
    }


}