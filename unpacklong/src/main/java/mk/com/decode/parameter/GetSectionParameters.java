package mk.com.decode.parameter;

import mk.com.decode.entity.Section;
import mk.com.decode.entity.TransportStream;

/**
 * @ClassName: GetSectionParameters
 * @Description: description
 * @Author: xiaolan
 * @Date: 2020/7/2 11:40
 */
public class GetSectionParameters{
    TransportStream ts;
    Section section;
    byte targetTable_id;
    short targetPid;
    int sectionCount;
    boolean[] sectionNumberRecord;
    int position;

    public GetSectionParameters() {
    }
    public GetSectionParameters(TransportStream ts,Section section,byte targetTable_id,short targetPid,int sectionCount,boolean[] sectionNumberRecord,int position) {
        this.ts = ts;
        this.section = section;
        this.targetTable_id = targetTable_id;
        this.targetPid = targetPid;
        this.sectionCount = sectionCount;
        this.sectionNumberRecord = sectionNumberRecord;
        this.position = position;
    }

    public TransportStream getTs() {
        return ts;
    }

    public Section getSection() {
        return section;
    }

    public byte getTargetTable_id() {
        return targetTable_id;
    }

    public short getTargetPid() {
        return targetPid;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    public boolean[] getSectionNumberRecord() {
        return sectionNumberRecord;
    }

    public int getPosition() {
        return position;
    }

    public void setSectionCount(int sectionCount) {
        this.sectionCount = sectionCount;
    }
}