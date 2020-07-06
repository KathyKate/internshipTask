# ts码流解析
组建section

啊啊啊啊啊啊啊啊啊啊啊啊...好不容易组出来了section，好几周了吧，其中一周是回学校了，之后就是放空状态了，感觉要是认真思考的话，应该不难的吧......

组section的历程：

1. 首先，需要清楚ts码流里的层次关系：
   ts码流是由很多的包组成的，即ts码流是包的集合，而section是ts包的集合；
   在码流中会解析出各种table，这些table是实际业务中需要用到的东西，而table是section的集合；
2. 其次，要清楚section的组成，以下是section头部字段：
       public Section(byte[] container, int sectionStart){
           this.table_id = container[sectionStart];
           this.section_syntax_indicator = (byte) ((container[sectionStart + 1] >> 7) & 0x1);
           this.zero_flag= (byte) ((container[sectionStart+1]>>6)&0x1);
           this.reserved_1= (byte) (container[sectionStart+1]&0x11);
           this.section_length = (short) (((container[sectionStart + 1] & 0xf) << 8) | container[sectionStart + 2]);
           this.ts_id= (short) (container[sectionStart+3]<<8|container[sectionStart+4]);
           this.reserved_2= (byte) ((container[sectionStart+5]>>6)&0x11);
           this.version_number = (byte) ((container[sectionStart + 5] >> 1) & 0x1f);
           this.current_next_indicator = (byte) (container[sectionStart + 5] & 0x1);
           this.section_number = container[sectionStart + 6];
           this.last_section_number = container[sectionStart + 7];
       }
3. 接下来就是思考section组成的流程：
   一个个包的获取，首先判断包是不是要找的包(PID)，其次这个包能否作为section的包，如果是首包，判断table_id是不是要寻找的。确定了这个包的payload可以作为section的数据部分，将数据部分装入section的container中。
   

核心方法：

- formTable() 作为解目标table中所有section的入口方法，给定ts文件的路径，将其解析为一个TransportStream对象，以及入参的简单处理(这里的入参不需要处理...)

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Section> formTable(String file, short targetPid, byte targetTable_id) throws Exception {
        byte[] data = FileUtils.readFile(file);
        TransportStream ts = DecodeUtils.analysisTransportStream(data);
        return getTable(ts, targetPid, targetTable_id);
    }

- getTable() 获取给定table_id的所有section。通过调用下一层方法getSection()一次获取一个section，而每一个section都有section_number来对其进行编号，那么想要获取所有section就需要用一个字段来标记哪些section是被获取过的(因为码流中的数据很多都是重复的，对于一些section在解析过程中，可能会存在重复解析的情况)。用sectionCount字段来标记目前拿到了多少个section，从而判断section是不是都拿完了。
  table的解出来的所有section都存入了List<Section>中，并重写了list的sort()，根据section的section_number字段来进行排序。

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Section> getTable(TransportStream ts, short targetPid, byte targetTable_id) {
        Integer a =8;
        Integer b=9;
        a.compareTo(b);
        int lastSectionNumber;
        int sectionCount = 0;
        boolean[] sectionNumberRecord = new boolean[256];
        Section section = new Section();
        GetSectionParameters parameters;
        List<Section> sections = new ArrayList<>();
        sections.sort(new Comparator<Section>() {
            @Override
            public int compare(Section s1, Section s2) {
                return (s1.getSection_number() < s2.getSection_number()) ? -1 : ((s1.getSection_number() == s1.getSection_number()) ? 0 : 1);
            }
        });
        int from = ts.getPosition();
        int to = ts.getPosition() + ts.getPackageLen();
    
        /**transport stream invalid*/
        if (ts.getTsData() == null) {
            return null;
        }
    
        while (to < ts.getTsData().length) {
            parameters = new GetSectionParameters(ts, section, targetTable_id, targetPid, sectionCount, sectionNumberRecord, from);
            to = getSection(parameters);
            sectionNumberRecord = parameters.getSectionNumberRecord();
            sectionCount = parameters.getSectionCount();
            lastSectionNumber = section.getContainer()[7];
            /**whether has been form all section*/
            if (sectionCount > lastSectionNumber) {
                break;
            }
            from = to;
            to = from + ts.getPackageLen();
        }
        return sections;
    }
    



- getSection()获取一个完整的section，ts码流以ts包的形式一个个读取出来，并判断这个包是不是section的一部分。首先分析这个包是能否作为要找section的首包（payload_unit_start_indicator=1）；如果不是首包，继续读取下一个进行判断。如果是要组建section的首包，需要判断section从包的何处开始组建。找到首包后，继续找section剩余的包，根据 tsread 软件中显示的section数据，可以发现，section所有的包的continuity_counter字段是自增的，文档中有提到说section中的package是连续的，或许是这个意思，根据continuity_counter字段找section剩余的包。

    public static int getSection(GetSectionParameters parameters) {
        boolean sectionFlag = false;
        int sectionStart;
        int sectionPosition = 0;
        Package packet;
        int from = parameters.getPosition();
        int to = from + parameters.getTs().getPackageLen();
        int continuity_counter = 0;
        InitSectionReturn initSecReturn;
        InitSectionParameters initSectionParameters;
        boolean tag = false;
    
        while (to < parameters.getTs().getTsData().length) {
            packet = new Package(Arrays.copyOfRange(parameters.getTs().getTsData(), from, to));
            from = to;
            to = from + parameters.getTs().getPackageLen();
            if (packet.getPID() == parameters.getTargetPid()) {
                if (packet.getPayload_unit_start_indicator() == 1) {
                    sectionStart = getSectionStart(packet);
                    if ((sectionStart > parameters.getTs().getPackageLen()) || (sectionStart == 0))
                        continue;
                    parameters.getSection().initSectionHead(packet.getData(), sectionStart);
                    if (parameters.getSection().getTable_id() != parameters.getTargetTable_id())
                        continue;
                    if (parameters.getSectionNumberRecord()[parameters.getSection().getSection_number()]) {
                        continue;
                    }
                    sectionFlag = true;
                    continuity_counter = (packet.getContinuity_counter() + 1) % Package.CONTINUITY_COUNTER_MAX;
                    tag = true;
                } else if (sectionFlag && packet.getContinuity_counter() == continuity_counter) {
                    continuity_counter = (continuity_counter + 1) % Package.CONTINUITY_COUNTER_MAX;
                    tag = true;
    
                }
                if (tag) {
                    tag = false;
                    initSectionParameters = new InitSectionParameters(parameters, packet, sectionPosition);
                    initSecReturn = initSection(initSectionParameters);
                    if (initSecReturn.isSectionEnd()) {
                        break;
                    }
                }
            }
        }
        return to;
    }
    



- initSection()将符合条件package的payload部分装入section的container中。装入的时候需要注意，这个package能装入的数据有哪些。并且204的包和188的包相比多的是后面16位的校验码，不需要装入section。在这个方法中还对本次获取的section进行判断，这个section是否已经把数据填充完毕。

    public static InitSectionReturn initSection(InitSectionParameters parameters) {
        boolean sectionEnd = false;
        int size = 0;
        int sectionStart = getSectionStart(parameters.getPacket());
        if (parameters.getParameters().getTs().getPackageLen() == TransportStream.LEN_204) {
            size = -CRC_LEN;
        }
        size += parameters.getParameters().getTs().getPackageLen() - sectionStart;
    
        /**the last package in section*/
        if ((parameters.getParameters().getTs().getPackageLen() - sectionStart) > (parameters.getParameters().getSection().getSection_length() + 3 - parameters.getSectionPosition())) {
            size = parameters.getParameters().getSection().getSection_length() + 3 - parameters.getSectionPosition();
            sectionEnd = true;
        }
    
        /**data input */
        for (int i = sectionStart; i < sectionStart + size; i++) {
            parameters.getParameters().getSection().getContainer()[parameters.getSectionPosition()] = parameters.getPacket().getData()[i];
            parameters.setSectionPosition(parameters.getSectionPosition() + 1);
        }
        if (sectionEnd) {
            parameters.getParameters().getSectionNumberRecord()[parameters.getParameters().getSection().getSection_number()] = true;
                    parameters.getParameters().setSectionCount(parameters.getParameters().getSectionCount() + 1);
        }
        return new InitSectionReturn(sectionEnd, parameters.getSectionPosition());
    }
    



- getSectionStart()标记package中payload的开始位置。package的adaption_field_control判断包的包头后的数据情况，以此来判断payload的实际开始位置。
  00：保留以后使用，（00的数据包在解码的时候应该丢弃）
  01：没有adaption_field， 只有payload
  10：只有adaption，没有payload
  11：adaption_field后面是payload

    public static int getSectionStart(Package packet) {
        int sectionStart = 0;
        switch (packet.getAdaptation_field_control()) {
            case 0:
            case 2:
                break;
            case 1:
                sectionStart = 4;
                break;
            case 3:
                sectionStart = 5 + packet.getData()[4];
        }
        if (packet.getPayload_unit_start_indicator() == 1) {
            sectionStart += packet.getData()[sectionStart] + 1;
        }
        return sectionStart;
    }

感想:

对于入门新手，组建section看似是最难的，也是最关键的，其实理解了section的组成，再结合tsread软件把section拆分，逆推他是怎么组成的，会发现所有的问题都是小cese啦。
其次在查看资料时，对于section的很多细节都还没注意到，可以对其进行大胆猜想说不定就是你推测的那个意思呢！

如果理解上有什么偏差忘指正...


