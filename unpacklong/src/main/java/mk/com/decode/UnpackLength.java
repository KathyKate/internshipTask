package mk.com.decode;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

import mk.com.decode.entity.TransportStream;
import mk.com.decode.util.DecodeUtils;
import mk.com.decode.util.FileUtils;


public class UnpackLength {
    /**
     * @return void
     * @method test
     * @description test this decode ,set the in parameter
     * @date 2020/6/8 19:47
     */
    @SuppressLint("DefaultLocale")
    public static void test() throws Exception {
        String[] path = new String[2];
        int i = 0;
        int size = 2;
        List<String[]> list = new ArrayList<>();
        while (i < size) {
            path[0] = String.format("%03d", i) + ".ts";
            path[1] = String.format("%03d", i) + ".txt";
            list.add(path);
            i++;
        }
        for (String[] filePath : list) {
            TransportStream ts = DecodeUtils.analysisTransportStream(FileUtils.readFile(filePath[0]));
            DecodeUtils.writeTransportStreamHexContent(ts,filePath[1]);
        }
    }

}


