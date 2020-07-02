package mk.com.decode.entity;

/**
 * @ClassName: Section
 * @Description: transport stream description table
 * @Author: 小懒
 * @Date: 2020/6/9 8:54
 */

public class Section {
    byte table_id;
    byte section_syntax_indicator;
    byte zero_flag;
    byte reserved_1;
    short section_length;
    short ts_id;
    byte reserved_2;
    byte version_number;
    byte current_next_indicator;
    byte section_number;
    byte last_section_number;
    int CRC_32;
    byte[] container;

    public Section() {
        this.container = new byte[4096];
    }

    public void initSectionHead(byte[] container, int sectionStart) {
        this.table_id = (byte) (container[sectionStart] & 0x0FF);
        this.section_syntax_indicator = (byte) ((container[sectionStart + 1] >> 7) & 0x1);
        this.zero_flag = (byte) ((container[sectionStart + 1] >> 6) & 0x1);
        this.reserved_1 = (byte) (container[sectionStart + 1] & 0x11);
        this.section_length = (short) (((container[sectionStart + 1] & 0xf) << 8) | (container[sectionStart + 2] & 0x0FF));
        this.ts_id = (short) (container[sectionStart + 3] << 8 | container[sectionStart + 4]);
        this.reserved_2 = (byte) ((container[sectionStart + 5] >> 6) & 0x11);
        this.version_number = (byte) ((container[sectionStart + 5] >> 1) & 0x1f);
        this.current_next_indicator = (byte) (container[sectionStart + 5] & 0x1);
        this.section_number = container[sectionStart + 6];
        this.last_section_number = container[sectionStart + 7];
    }

    public short getSection_length() {
        return section_length;
    }

    public byte[] getContainer() {
        return container;
    }

    public byte getTable_id() {
        return table_id;
    }

    public byte getSection_number() {
        return section_number;
    }

}