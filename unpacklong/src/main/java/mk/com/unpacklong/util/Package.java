package mk.com.unpacklong.util;


public class Package {

    //包头
    private byte[] head;
//    //标识符
//    private byte sync_byte;
//    //传输差错指示
//    private bit transport_error_Indicator;

    //净荷
    private byte[] data;

    public Package() {
    }
    public Package(byte[] container) {
        //根据传进来的自己数组初始化包的属性

    }

    public byte[] getHead() {
        return head;
    }

    public void setHead(byte[] head) {
        this.head = head;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
