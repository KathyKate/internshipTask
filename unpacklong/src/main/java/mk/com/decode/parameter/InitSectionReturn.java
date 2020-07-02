package mk.com.decode.parameter;

/**
 * @ClassName: InitSectionReturn
 * @Description: description
 * @Author: xiaolan
 * @Date: 2020/7/2 11:40
 */
public class InitSectionReturn{
    boolean sectionEnd;
    int sectionPosition;
    public InitSectionReturn() {
    }
    public InitSectionReturn(boolean sectionEnd, int sectionPosition) {
        this.sectionEnd = sectionEnd;
        this.sectionPosition = sectionPosition;
    }

    public boolean isSectionEnd() {
        return sectionEnd;
    }

    public int getSectionPosition() {
        return sectionPosition;
    }
}