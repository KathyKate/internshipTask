package mk.com.unpacklong.util;

public class DecodeParameter {
    //The first effective SYNC_BYTE in transport stream
    private int position;
    //No.num of SYNC_BYTE in transport stream as the start of package
    private int num;
    private int point;

    public DecodeParameter() {
    }

    public void initParameterAdd(int position) {
        point = position + 1;
        num = 0;
    }

    public void initParameter(int position) {
        point = position;
        num = 0;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
