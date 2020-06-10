package mk.com.decode.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Section
 * @Description: transport stream description table
 * @Author: 小懒
 * @Date: 2020/6/9 8:54
 */

public class Section {
    byte table_id ;
    byte section_syntax_indicator;
    byte zero_flag;
    byte reserved_1;
    short section_length;
    int reserved_2;
    byte version_number;
    byte current_next_indicator;
    byte section_number;
    byte last_section_number;
    List<Package> list = new ArrayList<>();
    int CRC_32;
}