//package mk.com.decode;
//
//import java.sql.SQLOutput;
//import java.util.Arrays;
//
//import mk.com.decode.entity.Package;
//import mk.com.decode.entity.Section;
//import mk.com.decode.entity.TransportStream;
//import mk.com.decode.util.StringUtils;
//
///**
// * @ClassName: FormSection
// * @Description: according the syntax from section
// * @Author: xiaolan
// * @Date: 2020/6/9 10:27
// */
//class FormSection {
//    public FormSection(TransportStream ts) {
//        this.ts = ts;
//        this.from = ts.getPosition();
//        this.to = from + ts.getPackageLen();
//    }
//    public FormSection() {
//    }
//    static final int SECTION_HEAD_LEN = 8;
//    static final int PACKET_HEAD = 4;
//    static final int POINTER_FIELD_LEN = 1;
//    TransportStream ts;
//    Package packet;
//    //一个包长中读的section的有效数据
//    byte[] container;
//    //from to针对一段码流而言的 指针
//    int from;
//    int to;
//    //本次构建的section
//    Section section = null;
//    //pointer_field 也就是5 目前的理解是这样子的
//    int sectionStart;
//    //首包符合table_id的要求，标记现在在组section
//    boolean table_id_flag = false;
//    //标记找到了pid的section
//    boolean PID_flag = false;
//    //组建的section数据部分 section_length之后的字节长度
//    short len = 0;
//
//
//
//    public static int getSectionStart(Package packet) {
//        int sectionStart = 0;
//        switch (packet.getAdaptation_field_control()) {
//            case 0:
//                break;
//            case 1:
//                sectionStart = 4;
//                break;
//            case 2:
//                break;
//            case 3:
//                sectionStart = 5 + packet.getData()[0];
//        }
//        if (packet.getPayload_unit_start_indicator() == 1) {
//            sectionStart += sectionStart + 1;
//        }
//        return sectionStart;
//    }
//
//    public void initParameters(boolean move){
//        len = 0;
//        table_id_flag = false;
//        PID_flag = false;
////        section.getData().clear();
//        section = null;
//        if(move) {
//            pointMove();
//        }
//    }
//    public void pointMove(){
//        from = to;
//        to = from + ts.getPackageLen();
//    }
//
//
//
//    public Section form( byte table_id, short PID) {
//        while (to < ts.getTsData().length) {
//
//            container = Arrays.copyOfRange(ts.getTsData(), from, to);
//            packet = new Package(container);
////            if(table_id_flag && !(packet.getPayload_unit_start_indicator() == 0)){
////                initParameters();
////                continue;
////            }
//
//            /**already in forming*/
//            if (table_id_flag) {
//                if(section.getSection_length() - len + 3 >= to - from - PACKET_HEAD) {
//                    section.getData().add(packet.getData());
//                    len += packet.getData().length;
//                }else{
//                    section.getData().add(Arrays.copyOfRange(packet.getData(),PACKET_HEAD,PACKET_HEAD + section.getSection_length() - len + 3));
//                    len += section.getSection_length() - len + 3;
//                }
//            }
//            /**this package is the first package of section*/
//            if (!table_id_flag && packet.getPayload_unit_start_indicator() == 1 && (packet.getData()[0] & 0xFF) == 0) {
//                sectionStart = PACKET_HEAD + POINTER_FIELD_LEN;
//                section = new Section(container, sectionStart);
//                /**the table_id incompatible*/
//                if (!(section.getTable_id() == table_id)) {
////                    initParameters();
//                    continue;
//                }
//                //装入剩余数据部分
//                /**load the section data*/
//                if (section.getSection_length() - len + 3 >= to - from - sectionStart) {
//                    /**section remaining length enough*/
//                    byte[] bytes = Arrays.copyOfRange(container, sectionStart, container.length);
//                    section.getData().add(bytes);
//                    len += container.length - sectionStart;
//                } else if (section.getSection_length() - len + 3 > 0){
//                    System.out.println("sectionStart: "+sectionStart);
//                    System.out.println("end: "+(section.getSection_length() + 3 - len + sectionStart));
//                    System.out.println("end: "+(len - section.getSection_length() - 3 + sectionStart));
//                    byte[] bytes = Arrays.copyOfRange(container, sectionStart, section.getSection_length() + 3 - len + sectionStart);
//                    section.getData().add(bytes);
//                    len += section.getSection_length() + 3;
//                }
//            }
//            /**mark this section including the target package */
//            if (table_id_flag && packet.getPID() == PID) {
//                PID_flag = true;
//            }
//            pointMove();
//
//
//            //组建完成
//            /**form a section over*/
//            if (table_id_flag && len >= section.getSection_length()) {
//                if (PID_flag) {
//                    /**this section meets all demand*/
//                    break;
//                } else {
//                    /**restart form the section*/
//                    initParameters(true);
//                    continue;
//                }
//            }
//        }
//        if (table_id_flag && PID_flag) {
//            return section;
//        } else {
//            return null;
//        }
//    }
//
//    public static void read(byte[] data) {
//        for (byte b : data) {
//            System.out.print(StringUtils.byteToHexString(b) + " ");
//        }
//        System.out.println();
//    }
//
//}