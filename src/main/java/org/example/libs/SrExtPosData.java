package org.example.libs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.ByteBuffer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SrExtPosData implements BinaryData {

    private String vdopFieldExists = "0";             // бит 0
    private String hdopFieldExists = "0";             // бит 1
    private String pdopFieldExists = "0";             // бит 2
    private String satellitesFieldExists = "0";       // бит 3
    private String navigationSystemFieldExists = "0"; // бит 4

    private int vdop;              // VDOP
    private int hdop;              // HDOP
    private int pdop;              // PDOP
    private byte satellites;       // SAT
    private int navigationSystem;  // NS

    @Override
    public void decode(byte[] content) throws IOException {
        if (content == null || content.length < 1) {
            throw new IllegalArgumentException("Недостаточно данных для декодирования SrExtPosData");
        }

        ByteBuffer buf = ByteBuffer.wrap(content).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        byte flags = buf.get();

        // порядок битов: от младшего к старшему (бит 0 — VFE, бит 4 — NSFE)
        this.vdopFieldExists = ((flags >> 0) & 1) == 1 ? "1" : "0";
        this.hdopFieldExists = ((flags >> 1) & 1) == 1 ? "1" : "0";
        this.pdopFieldExists = ((flags >> 2) & 1) == 1 ? "1" : "0";
        this.satellitesFieldExists = ((flags >> 3) & 1) == 1 ? "1" : "0";
        this.navigationSystemFieldExists = ((flags >> 4) & 1) == 1 ? "1" : "0";

        if ("1".equals(vdopFieldExists)) {
            if (buf.remaining() < 2) throw new IllegalArgumentException("Недостаточно данных для VDOP");
            this.vdop = Short.toUnsignedInt(buf.getShort());
        }

        if ("1".equals(hdopFieldExists)) {
            if (buf.remaining() < 2) throw new IllegalArgumentException("Недостаточно данных для HDOP");
            this.hdop = Short.toUnsignedInt(buf.getShort());
        }

        if ("1".equals(pdopFieldExists)) {
            if (buf.remaining() < 2) throw new IllegalArgumentException("Недостаточно данных для PDOP");
            this.pdop = Short.toUnsignedInt(buf.getShort());
        }

        if ("1".equals(satellitesFieldExists)) {
            if (buf.remaining() < 1) throw new IllegalArgumentException("Недостаточно данных для SAT");
            this.satellites = buf.get();
        }

        if ("1".equals(navigationSystemFieldExists)) {
            if (buf.remaining() < 2) throw new IllegalArgumentException("Недостаточно данных для NS");
            this.navigationSystem = Short.toUnsignedInt(buf.getShort());
        }
    }

    @Override
    public byte[] encode() throws IOException {
        // собрать флаг в виде одного байта (от бита 0 к 7)
        int flags = 0;
        flags |= "1".equals(vdopFieldExists) ? (1 << 0) : 0;
        flags |= "1".equals(hdopFieldExists) ? (1 << 1) : 0;
        flags |= "1".equals(pdopFieldExists) ? (1 << 2) : 0;
        flags |= "1".equals(satellitesFieldExists) ? (1 << 3) : 0;
        flags |= "1".equals(navigationSystemFieldExists) ? (1 << 4) : 0;

        int size = 1 + // flags
                ("1".equals(vdopFieldExists) ? 2 : 0) +
                ("1".equals(hdopFieldExists) ? 2 : 0) +
                ("1".equals(pdopFieldExists) ? 2 : 0) +
                ("1".equals(satellitesFieldExists) ? 1 : 0) +
                ("1".equals(navigationSystemFieldExists) ? 2 : 0);

        ByteBuffer buf = ByteBuffer.allocate(size).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) flags);

        if ("1".equals(vdopFieldExists)) {
            buf.putShort((short) vdop);
        }

        if ("1".equals(hdopFieldExists)) {
            buf.putShort((short) hdop);
        }

        if ("1".equals(pdopFieldExists)) {
            buf.putShort((short) pdop);
        }

        if ("1".equals(satellitesFieldExists)) {
            buf.put(satellites);
        }

        if ("1".equals(navigationSystemFieldExists)) {
            buf.putShort((short) navigationSystem);
        }

        return buf.array();
    }

    @Override
    public int length() {
        return 1 +
                ("1".equals(vdopFieldExists) ? 2 : 0) +
                ("1".equals(hdopFieldExists) ? 2 : 0) +
                ("1".equals(pdopFieldExists) ? 2 : 0) +
                ("1".equals(satellitesFieldExists) ? 1 : 0) +
                ("1".equals(navigationSystemFieldExists) ? 2 : 0);
    }
}
