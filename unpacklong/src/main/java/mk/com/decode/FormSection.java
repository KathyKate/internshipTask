package mk.com.decode;

import mk.com.decode.entity.Package;
import mk.com.decode.entity.Section;
import mk.com.decode.entity.TransportStream;

/**
 * @ClassName: FormSection
 * @Description: according the syntax from section
 * @Author: xiaolan
 * @Date: 2020/6/9 10:27
 */
class FormSection {

    public static int getSectionStart(Package packet){
        int sectionStart = 0;
        switch (packet.getAdaption_field_control()){
            case 0:break;
            case 1:sectionStart=4;
            break;
            case 3:break;
            case 4:sectionStart=5+packet.getData()[0];
        }
        if (packet.getPayload_unit_start_indicator()==1){
            sectionStart+=sectionStart+1;
        }
        return sectionStart;
    }

    public static Section getSectionByTableIdPID(TransportStream ts){

    }



}