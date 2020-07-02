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
    GetSectionParameters parameters;
    Package packet;
    int sectionPosition;

    public InitSectionParameters() {
    }
    public InitSectionParameters(GetSectionParameters parameters,Package packet,int sectionPosition) {
        this.parameters = parameters;
        this.packet = packet;
        this.sectionPosition = sectionPosition;
    }

    public Package getPacket() {
        return packet;
    }

    public GetSectionParameters getParameters() {
        return parameters;
    }

    public void setParameters(GetSectionParameters parameters) {
        this.parameters = parameters;
    }

    public int getSectionPosition() {
        return sectionPosition;
    }

    public void setSectionPosition(int sectionPosition) {
        this.sectionPosition = sectionPosition;
    }
}