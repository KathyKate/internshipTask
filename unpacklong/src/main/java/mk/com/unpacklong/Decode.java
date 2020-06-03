package mk.com.unpacklong;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mk.com.unpacklong.util.DecodeParameter;
import mk.com.unpacklong.util.TransportStream;
import mk.com.unpacklong.util.StringUtils;
import mk.com.unpacklong.util.Package;


public class Decode {
    static final int FLAG = 20;
    //判断包长
    public static void judgePackageLength(byte[] bytes , int bytesRead, TransportStream ts){
        //用于切换包长
        int tag =TransportStream.LEN_188;
        DecodeParameter parameter = new DecodeParameter();
        while (parameter.getN()<FLAG && parameter.getP()<bytesRead){
            //找SYN_BYTE位
            if(bytes[parameter.getP()]==TransportStream.SYN_BYTE){
                //首次找到SYN_BYTE
                if(parameter.getN()==0) {
                    //标记这段流中的第一个SYN_BYTE
                    parameter.setPosition(parameter.getP());
                    //可以用位运算实现
                    tag = TransportStream.LEN_188+TransportStream.LEN_204-tag;
                }
                parameter.setN(parameter.getN()+1);
                parameter.setP(parameter.getP()+tag);
            }else{
                //找到的不是SYN_BYTE位
                if(parameter.getN()==0){
                    //还没找到第一个0x47
                    parameter.setP(parameter.getP()+1);
                }else {//重新找 参数重置
                    //这里好像写死了
                    if(tag == TransportStream.LEN_188){
                        parameter.initParameter(parameter.getPosition());
                    }else{
                        //结束188 和 204 一整个轮回
                        parameter.initParameterAdd(parameter.getPosition());
                    }
//                    tag==LEN_188?parameter.initParameter(parameter.getPosition()):parameter.initParameterAdd(parameter.getPosition());
                    continue;
                }
            }
        }

        //判断了20次退出循环 初始化这段码流中的第一个0x47所在位置
        if (parameter.getN()==FLAG ){
            //初始化该段码流中首个包的0x47
            ts.setPosition(parameter.getPosition());
        } else{
            //码流中解析不到包长
            tag=-1;
        }
        ts.setPackageLen(tag);
    }

    //解包
    public static byte[] decodePackage(byte[] bytes , int bytesRead,TransportStream ts,byte[] remians) throws Exception {
        if(ts.getPackageLen()==0){
            //码流中的包无效
            throw new Exception("该码流无效");
        }
        byte[] container = new byte[ts.getPackageLen()];
        int from =0 ;
        int to =0 ;
        int p=0;
        //解析本次读取数据中的第一个包
        if(ts.getPackages().size()==0){
            //ts流中的第一个包
            from = ts.getPosition();
            to = ts.getPosition()+ts.getPackageLen();
            container = Arrays.copyOfRange(bytes,from,to);
        } else {
            //
            for(int i =0;i<container.length;i++){
                container[i++]=i<remians.length?remians[i]:bytes[p++];
            }
            from= p;
            to = p+ts.getPackageLen();
        }
        ts.getPackages().add(new Package(container));

        while(to<bytesRead){
            container = Arrays.copyOfRange(bytes,from,to);
            from = to;
            to += ts.getPackageLen();
            ts.getPackages().add(new Package(container));
        }
        remians = Arrays.copyOfRange(bytes,from,bytes.length-1);
        return remians;
    }

    //读取文件
    public static void readTsFile(TransportStream ts,File destFile) throws Exception {
        InputStream is = null ;
        Writer writer =null;
        byte[] remains = null;
        try {
            is = new BufferedInputStream(new FileInputStream(ts.getFile()));
//            os = new BufferedOutputStream(new FileOutputStream(destFile));
            if(destFile!=null) {
                writer = new BufferedWriter(new FileWriter(destFile));
            }
            byte[] bytes = new byte[1024*20];
            int bytesRead = 0;
            bytesRead = is.read(bytes);
            judgePackageLength(bytes,bytesRead,ts);
            remains=decodePackage(bytes,bytesRead,ts,remains);
            while((bytesRead = is.read(bytes))!=-1){
                if(ts.getPackageLen()!=0){
                    remains=decodePackage(bytes,bytesRead,ts,remains);
                }
                //以字符的形式写入到文件中
                if (destFile!=null){
                    writer.write(StringUtils.bytesToHexString(bytes));
                    writer.flush();
                }
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            if(writer != null){
                writer.close();
            }
            if(is != null) {
                is.close();
            }
        }
    }

    //解析码流文件
    public static void analysisTransportStream(String srcName,String destName) throws Exception {
        TransportStream ts = new TransportStream();
        ts.setFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+srcName));
        File dest = null;
        if(destName!=null && !destName.equals("")) {
            dest= new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + destName);
        }
        readTsFile(ts,dest);
        System.out.println(ts.getPackageLen());
    }

    //测试本次解码
    public static void test() throws Exception {
        String[] path = new String[2];
        int i =0;
        int size = 2;
        List<String[]> list = new ArrayList<>();
        while(i<size){
            path[0]=String.format("%03d",i)+".ts";
            path[1]=String.format("%03d",i)+".txt";
            list.add(path);
            i++;
        }
        for (String[] filePath : list){
            analysisTransportStream(filePath[0],filePath[1]);
        }
    }

}


