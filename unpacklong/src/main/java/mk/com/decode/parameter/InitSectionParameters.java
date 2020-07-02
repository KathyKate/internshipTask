package mk.com.decode.parameter;

import mk.com.decode.entity.Package;
import mk.com.decode.entity.Section;
import mk.com.decode.entity.TransportStream;

/**
 * @ClassName: InitSectionParameters
 * @Description: description
 * @Author: xiaolan
 * @Date: 2020/7/2 11:40
 */
public class InitSectionParameters{
    TransportStream ts;
    Section section;
    Package packet;
    boolean[] sectionNumberRecord;
    int sectionPosition;
    public InitSectionParameters() {
    }
    public InitSectionParameters(TransportStream ts,Section section,Package packet,int sectionPosition,boolean[] sectionNumberRecord) {
        this.ts = ts;
        this.section = section;
        this.packet = packet;
        this.sectionPosition = sectionPosition;
        this.sectionNumberRecord = sectionNumberRecord;
    }

    public TransportStream getTs() {
        return ts;
    }

    public Section getSection() {
        return section;
    }

    public Package getPacket() {
        return packet;
    }

    public boolean[] getSectionNumberRecord() {
        return sectionNumberRecord;
    }

    public int getSectionPosition() {
        return sectionPosition;
    }

    public void setSectionPosition(int sectionPosition) {
        this.sectionPosition = sectionPosition;
    }

    public void setSectionNumberRecord(boolean[] sectionNumberRecord) {
        this.sectionNumberRecord = sectionNumberRecord;
    }
}