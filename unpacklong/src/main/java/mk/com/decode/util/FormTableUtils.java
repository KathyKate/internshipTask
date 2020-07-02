package mk.com.decode.util;

import java.util.Arrays;

import mk.com.decode.entity.Package;
import mk.com.decode.entity.Section;
import mk.com.decode.entity.TransportStream;
import mk.com.decode.parameter.GetSectionParameters;
import mk.com.decode.parameter.InitSectionParameters;
import mk.com.decode.parameter.InitSectionReturn;

/**
 * @ClassName: Form
 * @Description: form the table
 * @Author: xiaolan
 * @Date: 2020/6/11 16:55
 */
public class FormTableUtils {
    private static int CRC_LEN = 16;

    /**
     * @method formTable
     * @description the entrance method of form table
     * @date 2020/7/2 16:34
     * @param file
     * @param targetPid
     * @param targetTable_id
     * @return void
     */
    public static void formTable(String file, short targetPid, byte targetTable_id) throws Exception {
        byte[] data = FileUtils.readFile(file);
        TransportStream ts = DecodeUtils.analysisTransportStream(data);
        getTable(ts, targetPid, targetTable_id);
    }

    /**
     * @method getTable
     * @description get all sections in the table
     * @date 2020/7/2 16:34
     * @param ts
     * @param targetPid
     * @param targetTable_id
     * @return void
     */
    public static void getTable(TransportStream ts, short targetPid, byte targetTable_id) {
        int lastSectionNumber;
        int sectionCount = 0;
        boolean[] sectionNumberRecord = new boolean[256];
        Section section = new Section();
        GetSectionParameters parameters;
        int from = ts.getPosition();
        int to = ts.getPosition() + ts.getPackageLen();

        /**transport stream invalid*/
        if (ts.getTsData() == null) {
            return;
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
    }

    /**
     * @method getSection
     * @description according to targetTable_id and targetPid assemble a complete section
     * @date 2020/7/2 16:33
     * @param parameters
     * @return int
     */
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

                    initSectionParameters = new InitSectionParameters(parameters.getTs(), parameters.getSection(), packet, sectionPosition, parameters.getSectionNumberRecord());
                    initSecReturn = initSection(initSectionParameters);
                    sectionPosition = initSecReturn.getSectionPosition();
                    if (initSecReturn.isSectionEnd()) {
                        parameters.getSectionNumberRecord()[parameters.getSection().getSection_number()] = true;
                        parameters.setSectionCount(parameters.getSectionCount() + 1);
                        break;
                    }
                } else if (sectionFlag && packet.getContinuity_counter() == continuity_counter) {
                    continuity_counter = (continuity_counter + 1) % Package.CONTINUITY_COUNTER_MAX;

                    initSectionParameters = new InitSectionParameters(parameters.getTs(), parameters.getSection(), packet, sectionPosition, parameters.getSectionNumberRecord());
                    initSecReturn = initSection(initSectionParameters);
                    sectionPosition = initSecReturn.getSectionPosition();
                    if (initSecReturn.isSectionEnd()) {
                        parameters.getSectionNumberRecord()[parameters.getSection().getSection_number()] = true;
                        parameters.setSectionCount(parameters.getSectionCount() + 1);
                        break;
                    }
                }
            }
        }
        return to;
    }

    /**
     * @method initSection
     * @description fill the section'container with package's payload
     * @date 2020/7/2 16:33
     * @param parameters
     * @return mk.com.decode.parameter.InitSectionReturn
     */
    public static InitSectionReturn initSection(InitSectionParameters parameters) {
        boolean sectionEnd = false;
        int size = 0;
        int sectionStart = getSectionStart(parameters.getPacket());
        if (parameters.getTs().getPackageLen() == TransportStream.LEN_204) {
            size = -CRC_LEN;
        }
        size += parameters.getTs().getPackageLen() - sectionStart;

        /**the last package in section*/
        if ((parameters.getTs().getPackageLen() - sectionStart) > (parameters.getSection().getSection_length() + 3 - parameters.getSectionPosition())) {
            size = parameters.getSection().getSection_length() + 3 - parameters.getSectionPosition();
            sectionEnd = true;
        }

        /**data input */
        for (int i = sectionStart; i < sectionStart + size; i++) {
            parameters.getSection().getContainer()[parameters.getSectionPosition()] = parameters.getPacket().getData()[i];
            parameters.setSectionPosition(parameters.getSectionPosition() + 1);
        }
        return new InitSectionReturn(sectionEnd, parameters.getSectionPosition());
    }

    /**
     * @method getSectionStart
     * @description mark where to start form section in a package
     * @date 2020/7/2 16:32
     * @param packet
     * @return int
     */
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

}

