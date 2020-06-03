package mk.com.unpacklong.util;

public class DecodeParameter{
    //第一个SYNC_BYTE的位置
    private int position;
    //第几个SYNC_BYTE位 （第几个包的起始）
    private int n;
    //指针
    private int p;
    public DecodeParameter(){}
    public void initParameterAdd(int position){
        p = position+1;
        n = 0;
    }
    public void initParameter(int position){
        p = position;
        n = 0;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }
}
