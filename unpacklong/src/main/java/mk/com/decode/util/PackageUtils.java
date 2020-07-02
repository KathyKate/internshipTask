package mk.com.decode.util;

import java.util.Arrays;

import mk.com.decode.entity.Package;
import mk.com.decode.entity.TransportStream;
import mk.com.decode.parameter.DecodeParameter;

/**
 * @ClassName: PackageUtils
 * @Description: description
 * @Author: xiaolan
 * @Date: 2020/6/10 9:52
 */
public class PackageUtils {
    static final int JUDGE_NUM = 20;
    /**
     * @method judgePackageLength
     * @description Used to get the length of the package
     * @date 2020/6/10 9:55
     * @param tsData
     * @return mk.com.decode.entity.TransportStream
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

    /**
     * @method getPackageByPID
     * @description get the package by PID
     * @date 2020/6/10 9:56
     * @param ts
     * @param target_PID
     * @return mk.com.decode.entity.Package
     */
    public static Package getPackageByPID(TransportStream ts, short target_PID) throws Exception {
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

    public static boolean judgePIDInBytes(byte[] datas,short PID) throws Exception {
        TransportStream ts = DecodeUtils.analysisTransportStream(datas);
        return PackageUtils.getPackageByPID(ts,PID)!=null;
    }

    public static boolean judgePackageByTable_id(Package packet, short target_Table_id) {
        return packet.getPID()==target_Table_id;
    }
}