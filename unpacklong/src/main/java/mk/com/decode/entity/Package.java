package mk.com.decode.entity;


import java.util.Arrays;

public class Package {
    public static final int CONTINUITY_COUNTER_MAX = 15 ;

    private byte sync_byte;
    private byte transport_error_indicator;
    private byte payload_unit_start_indicator;
    private byte transport_priority;
    private short PID;
    private byte transport_scrambling_control;
    private byte adaptation_field_control;
    private byte continuity_counter;
    private byte[] data;

    public Package() {
    }

    public Package(byte[] container) {
        initPackageHead(container);
    }
    public boolean initPackageHead(byte[] container){
        //according in parameters initial package attributes
        this.sync_byte = container[0];
        if(sync_byte != TransportStream.SYNC_BYTE)
            return false;
        this.transport_error_indicator = (byte) (container[1] >> 7);
        this.payload_unit_start_indicator = (byte) (container[1] >> 6 & 0x01);
        this.transport_priority = (byte) (container[1] >> 5 & 0x01);
        this.PID = (short) ((container[1] & 0x1F) << 8 | (container[2]&0x0FF));
        this.transport_scrambling_control = (byte) (container[3] >> 6);
        this.adaptation_field_control = (byte) (container[3] >> 4 & 0x03);
        this.continuity_counter = (byte) (container[3] & 0x0F);

//        /**0x01:No adaptation_field, payload only
//         * 0x11:Adaptation_field followed by payload*/
//        if(adaptation_field_control == 0x10 || adaptation_field_control == 0x11){
////            Adaptation_field adaptation_field = new Adaptation_field(Arrays.copyOfRange(container,4,container.length));
//        }
        this.data = Arrays.copyOf(container,container.length);
        return true;
    }


    public short getPID() {
        return PID;
    }

    public byte getPayload_unit_start_indicator() {
        return payload_unit_start_indicator;
    }

    public byte getAdaptation_field_control() {
        return adaptation_field_control;
    }

    public byte getContinuity_counter() {
        return continuity_counter;
    }

    public byte[] getData() {
        return data;
    }

}

