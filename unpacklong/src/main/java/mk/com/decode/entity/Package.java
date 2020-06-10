package mk.com.decode.entity;


import java.util.Arrays;

public class Package {

    //包头
    private byte[] head;
    //标识符
    private byte sync_byte;
    //传输差错指示
    private byte transport_error_indicator;
    //负载单元开始标志
    private byte payload_unit_start_indicator;
    //传输优先级标志
    private byte transport_priority;
    //Package的ID号码
    private short PID;
    //加密标志
    private byte transport_scrambling_control;
    //附加区域控制
    private byte adaption_field_control;
    //包递增计数器
    private byte continuity_counter;


    //净荷
    private byte[] data;

    public Package() {
    }

    public Package(byte[] container) {
        //according in parameters initial package attributes
        this.sync_byte = container[0];
        this.transport_error_indicator = (byte) (container[1] >> 7);
        this.payload_unit_start_indicator = (byte) (container[1] >> 6 & 0x01);
        this.transport_priority = (byte) (container[1] >> 5 & 0x01);
        this.PID = (short) ((container[1] & 0x1F) << 8 | container[2]);
        this.transport_scrambling_control = (byte) (container[3] >> 6);
        this.adaption_field_control = (byte) (container[3] >> 4 & 0x03);
        this.continuity_counter = (byte) (container[3] & 0x0F);
        this.head= Arrays.copyOfRange(container,0,3);
        this.data=Arrays.copyOfRange(container,4,container.length+1);
    }

    public short getPID() {
        return PID;
    }

    public byte getPayload_unit_start_indicator() {
        return payload_unit_start_indicator;
    }

    public void setPayload_unit_start_indicator(byte payload_unit_start_indicator) {
        this.payload_unit_start_indicator = payload_unit_start_indicator;
    }

    public byte getAdaption_field_control() {
        return adaption_field_control;
    }

    public void setAdaption_field_control(byte adaption_field_control) {
        this.adaption_field_control = adaption_field_control;
    }

    public void setPID(short PID) {
        this.PID = PID;
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
