package org.example.libs;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Data;

@Data
public class StorageRecordWrapper implements BinaryData {

    private StorageRecord record;

    public StorageRecordWrapper() {
        this.record = StorageRecord.newBuilder().build();
    }

    public StorageRecordWrapper(StorageRecord record) {
        this.record = record;
    }

    @Override
    public byte[] encode() {
        return record.toByteArray();
    }

    @Override
    public void decode(byte[] data) throws InvalidProtocolBufferException {
        this.record = StorageRecord.parseFrom(data);
    }

    @Override
    public int length() {
        return encode().length;
    }
}