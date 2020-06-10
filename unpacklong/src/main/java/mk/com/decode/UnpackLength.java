package mk.com.decode;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mk.com.decode.util.DecodeParameter;
import mk.com.decode.entity.TransportStream;
import mk.com.decode.util.DecodeUtils;
import mk.com.decode.util.FileUtils;
import mk.com.decode.entity.Package;


public class UnpackLength {
    static final int JUDGE_NUM = 20;

    /**
     * @param tsData
     * @return void
     * @method judgePackageLength
     * @description Used to get the length of the package
     * @date 2020/6/4 10:39
     */
    public static TransportStream judgePackageLength(byte[] tsData) {

        int switchPackage = TransportStream.LEN_188;
        DecodeParameter parameter = new DecodeParameter();
        while (parameter.getNum() < JUDGE_NUM && parameter.getPoint() < tsData.length) {
            // seek out SYNC_BYTE
            if (tsData[parameter.getPoint()] == TransportStream.SYNC_BYTE) {
                if (parameter.getNum() == 0) {
                    parameter.setPosition(parameter.getPoint());
                    switchPackage = TransportStream.LEN_188 + TransportStream.LEN_204 - switchPackage;
                }
                parameter.setNum(parameter.getNum() + 1);
                parameter.setPoint(parameter.getPoint() + switchPackage);
            } else {
                if (parameter.getNum() == 0) {
                    parameter.setPoint(parameter.getPoint() + 1);
                } else {
                    if (switchPackage == TransportStream.LEN_188) {
                        parameter.initParameter(parameter.getPosition());
                    } else {
                        //finish a cycle of LEN_188 and LEN_204
                        parameter.initParameterAdd(parameter.getPosition());
                    }
                }
            }
        }

        TransportStream ts = new TransportStream();
        //obtained package length then initialized SYNC_BYTE position
        if (parameter.getNum() == JUDGE_NUM) {
            ts.setPosition(parameter.getPosition());
        } else {
            switchPackage = -1;
        }
        ts.setPackageLen(switchPackage);
        return ts;
    }


    public static Package getPackageByPID(TransportStream ts,short target_PID) throws Exception {
        if (ts.getTsData() == null) {
            throw new Exception("该码流还未初始化");
        }
        if (ts.getPackageLen() == 0) {
            throw new Exception("该码流无效");
        }
        byte[] container ;
        int from = ts.getPosition();
        int to = ts.getPosition() + ts.getPackageLen();
        while (to < ts.getTsData().length) {
            container = Arrays.copyOfRange(ts.getTsData(), from, to);
            from = to;
            to += ts.getPackageLen();
            Package packet=new Package(container);
            if(packet.getPID()==target_PID){
                return packet;
            }
        }
        return null;
    }






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
            TransportStream ts = DecodeUtils.analysisTransportStream(filePath[0]);
            DecodeUtils.writeTransportStreamHexContent(ts,filePath[1]);
        }
    }

}


