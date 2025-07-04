// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: storage_record.proto
// Protobuf Java Version: 4.30.2

package org.example.libs;

/**
 * <pre>
 * Уровень топлива
 * </pre>
 *
 * Protobuf type {@code SensFuelLevel}
 */
public final class SensFuelLevel extends
    com.google.protobuf.GeneratedMessage implements
    // @@protoc_insertion_point(message_implements:SensFuelLevel)
    SensFuelLevelOrBuilder {
private static final long serialVersionUID = 0L;
  static {
    com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
      com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
      /* major= */ 4,
      /* minor= */ 30,
      /* patch= */ 2,
      /* suffix= */ "",
      SensFuelLevel.class.getName());
  }
  // Use SensFuelLevel.newBuilder() to construct.
  private SensFuelLevel(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
    super(builder);
  }
  private SensFuelLevel() {
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.example.libs.StorageRecordOuterClass.internal_static_SensFuelLevel_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.example.libs.StorageRecordOuterClass.internal_static_SensFuelLevel_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.example.libs.SensFuelLevel.class, org.example.libs.SensFuelLevel.Builder.class);
  }

  public static final int SENS_NUM_FIELD_NUMBER = 1;
  private int sensNum_ = 0;
  /**
   * <pre>
   * Номер датчика
   * </pre>
   *
   * <code>uint32 sens_num = 1;</code>
   * @return The sensNum.
   */
  @java.lang.Override
  public int getSensNum() {
    return sensNum_;
  }

  public static final int VALUE_FIELD_NUMBER = 2;
  private float value_ = 0F;
  /**
   * <pre>
   * Значение датчика
   * </pre>
   *
   * <code>float value = 2;</code>
   * @return The value.
   */
  @java.lang.Override
  public float getValue() {
    return value_;
  }

  public static final int PARROTS_FIELD_NUMBER = 3;
  private int parrots_ = 0;
  /**
   * <pre>
   * Значение датчика в условных единицах
   * </pre>
   *
   * <code>uint32 parrots = 3;</code>
   * @return The parrots.
   */
  @java.lang.Override
  public int getParrots() {
    return parrots_;
  }

  public static final int UNIT_FIELD_NUMBER = 4;
  private int unit_ = 0;
  /**
   * <pre>
   * Единица измерения для поля value (1 - литры, 2 - миллилитры)
   * </pre>
   *
   * <code>uint32 unit = 4;</code>
   * @return The unit.
   */
  @java.lang.Override
  public int getUnit() {
    return unit_;
  }

  public static final int T_FIELD_NUMBER = 5;
  private int t_ = 0;
  /**
   * <pre>
   * Температура датчика
   * </pre>
   *
   * <code>uint32 t = 5;</code>
   * @return The t.
   */
  @java.lang.Override
  public int getT() {
    return t_;
  }

  public static final int STATUS_FIELD_NUMBER = 6;
  private int status_ = 0;
  /**
   * <pre>
   * Статус датчика
   * </pre>
   *
   * <code>uint32 status = 6;</code>
   * @return The status.
   */
  @java.lang.Override
  public int getStatus() {
    return status_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (sensNum_ != 0) {
      output.writeUInt32(1, sensNum_);
    }
    if (java.lang.Float.floatToRawIntBits(value_) != 0) {
      output.writeFloat(2, value_);
    }
    if (parrots_ != 0) {
      output.writeUInt32(3, parrots_);
    }
    if (unit_ != 0) {
      output.writeUInt32(4, unit_);
    }
    if (t_ != 0) {
      output.writeUInt32(5, t_);
    }
    if (status_ != 0) {
      output.writeUInt32(6, status_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (sensNum_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(1, sensNum_);
    }
    if (java.lang.Float.floatToRawIntBits(value_) != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeFloatSize(2, value_);
    }
    if (parrots_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(3, parrots_);
    }
    if (unit_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(4, unit_);
    }
    if (t_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(5, t_);
    }
    if (status_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt32Size(6, status_);
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof org.example.libs.SensFuelLevel)) {
      return super.equals(obj);
    }
    org.example.libs.SensFuelLevel other = (org.example.libs.SensFuelLevel) obj;

    if (getSensNum()
        != other.getSensNum()) return false;
    if (java.lang.Float.floatToIntBits(getValue())
        != java.lang.Float.floatToIntBits(
            other.getValue())) return false;
    if (getParrots()
        != other.getParrots()) return false;
    if (getUnit()
        != other.getUnit()) return false;
    if (getT()
        != other.getT()) return false;
    if (getStatus()
        != other.getStatus()) return false;
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + SENS_NUM_FIELD_NUMBER;
    hash = (53 * hash) + getSensNum();
    hash = (37 * hash) + VALUE_FIELD_NUMBER;
    hash = (53 * hash) + java.lang.Float.floatToIntBits(
        getValue());
    hash = (37 * hash) + PARROTS_FIELD_NUMBER;
    hash = (53 * hash) + getParrots();
    hash = (37 * hash) + UNIT_FIELD_NUMBER;
    hash = (53 * hash) + getUnit();
    hash = (37 * hash) + T_FIELD_NUMBER;
    hash = (53 * hash) + getT();
    hash = (37 * hash) + STATUS_FIELD_NUMBER;
    hash = (53 * hash) + getStatus();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.example.libs.SensFuelLevel parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.example.libs.SensFuelLevel parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.example.libs.SensFuelLevel parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.example.libs.SensFuelLevel parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.example.libs.SensFuelLevel parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.example.libs.SensFuelLevel parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.example.libs.SensFuelLevel parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessage
        .parseWithIOException(PARSER, input);
  }
  public static org.example.libs.SensFuelLevel parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessage
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static org.example.libs.SensFuelLevel parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessage
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static org.example.libs.SensFuelLevel parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessage
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.example.libs.SensFuelLevel parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessage
        .parseWithIOException(PARSER, input);
  }
  public static org.example.libs.SensFuelLevel parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessage
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(org.example.libs.SensFuelLevel prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessage.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * <pre>
   * Уровень топлива
   * </pre>
   *
   * Protobuf type {@code SensFuelLevel}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessage.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:SensFuelLevel)
      org.example.libs.SensFuelLevelOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.example.libs.StorageRecordOuterClass.internal_static_SensFuelLevel_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.example.libs.StorageRecordOuterClass.internal_static_SensFuelLevel_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.example.libs.SensFuelLevel.class, org.example.libs.SensFuelLevel.Builder.class);
    }

    // Construct using org.example.libs.SensFuelLevel.newBuilder()
    private Builder() {

    }

    private Builder(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      super(parent);

    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      sensNum_ = 0;
      value_ = 0F;
      parrots_ = 0;
      unit_ = 0;
      t_ = 0;
      status_ = 0;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.example.libs.StorageRecordOuterClass.internal_static_SensFuelLevel_descriptor;
    }

    @java.lang.Override
    public org.example.libs.SensFuelLevel getDefaultInstanceForType() {
      return org.example.libs.SensFuelLevel.getDefaultInstance();
    }

    @java.lang.Override
    public org.example.libs.SensFuelLevel build() {
      org.example.libs.SensFuelLevel result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.example.libs.SensFuelLevel buildPartial() {
      org.example.libs.SensFuelLevel result = new org.example.libs.SensFuelLevel(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(org.example.libs.SensFuelLevel result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.sensNum_ = sensNum_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.value_ = value_;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.parrots_ = parrots_;
      }
      if (((from_bitField0_ & 0x00000008) != 0)) {
        result.unit_ = unit_;
      }
      if (((from_bitField0_ & 0x00000010) != 0)) {
        result.t_ = t_;
      }
      if (((from_bitField0_ & 0x00000020) != 0)) {
        result.status_ = status_;
      }
    }

    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof org.example.libs.SensFuelLevel) {
        return mergeFrom((org.example.libs.SensFuelLevel)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.example.libs.SensFuelLevel other) {
      if (other == org.example.libs.SensFuelLevel.getDefaultInstance()) return this;
      if (other.getSensNum() != 0) {
        setSensNum(other.getSensNum());
      }
      if (java.lang.Float.floatToRawIntBits(other.getValue()) != 0) {
        setValue(other.getValue());
      }
      if (other.getParrots() != 0) {
        setParrots(other.getParrots());
      }
      if (other.getUnit() != 0) {
        setUnit(other.getUnit());
      }
      if (other.getT() != 0) {
        setT(other.getT());
      }
      if (other.getStatus() != 0) {
        setStatus(other.getStatus());
      }
      this.mergeUnknownFields(other.getUnknownFields());
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {
              sensNum_ = input.readUInt32();
              bitField0_ |= 0x00000001;
              break;
            } // case 8
            case 21: {
              value_ = input.readFloat();
              bitField0_ |= 0x00000002;
              break;
            } // case 21
            case 24: {
              parrots_ = input.readUInt32();
              bitField0_ |= 0x00000004;
              break;
            } // case 24
            case 32: {
              unit_ = input.readUInt32();
              bitField0_ |= 0x00000008;
              break;
            } // case 32
            case 40: {
              t_ = input.readUInt32();
              bitField0_ |= 0x00000010;
              break;
            } // case 40
            case 48: {
              status_ = input.readUInt32();
              bitField0_ |= 0x00000020;
              break;
            } // case 48
            default: {
              if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                done = true; // was an endgroup tag
              }
              break;
            } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }
    private int bitField0_;

    private int sensNum_ ;
    /**
     * <pre>
     * Номер датчика
     * </pre>
     *
     * <code>uint32 sens_num = 1;</code>
     * @return The sensNum.
     */
    @java.lang.Override
    public int getSensNum() {
      return sensNum_;
    }
    /**
     * <pre>
     * Номер датчика
     * </pre>
     *
     * <code>uint32 sens_num = 1;</code>
     * @param value The sensNum to set.
     * @return This builder for chaining.
     */
    public Builder setSensNum(int value) {

      sensNum_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Номер датчика
     * </pre>
     *
     * <code>uint32 sens_num = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearSensNum() {
      bitField0_ = (bitField0_ & ~0x00000001);
      sensNum_ = 0;
      onChanged();
      return this;
    }

    private float value_ ;
    /**
     * <pre>
     * Значение датчика
     * </pre>
     *
     * <code>float value = 2;</code>
     * @return The value.
     */
    @java.lang.Override
    public float getValue() {
      return value_;
    }
    /**
     * <pre>
     * Значение датчика
     * </pre>
     *
     * <code>float value = 2;</code>
     * @param value The value to set.
     * @return This builder for chaining.
     */
    public Builder setValue(float value) {

      value_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Значение датчика
     * </pre>
     *
     * <code>float value = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearValue() {
      bitField0_ = (bitField0_ & ~0x00000002);
      value_ = 0F;
      onChanged();
      return this;
    }

    private int parrots_ ;
    /**
     * <pre>
     * Значение датчика в условных единицах
     * </pre>
     *
     * <code>uint32 parrots = 3;</code>
     * @return The parrots.
     */
    @java.lang.Override
    public int getParrots() {
      return parrots_;
    }
    /**
     * <pre>
     * Значение датчика в условных единицах
     * </pre>
     *
     * <code>uint32 parrots = 3;</code>
     * @param value The parrots to set.
     * @return This builder for chaining.
     */
    public Builder setParrots(int value) {

      parrots_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Значение датчика в условных единицах
     * </pre>
     *
     * <code>uint32 parrots = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearParrots() {
      bitField0_ = (bitField0_ & ~0x00000004);
      parrots_ = 0;
      onChanged();
      return this;
    }

    private int unit_ ;
    /**
     * <pre>
     * Единица измерения для поля value (1 - литры, 2 - миллилитры)
     * </pre>
     *
     * <code>uint32 unit = 4;</code>
     * @return The unit.
     */
    @java.lang.Override
    public int getUnit() {
      return unit_;
    }
    /**
     * <pre>
     * Единица измерения для поля value (1 - литры, 2 - миллилитры)
     * </pre>
     *
     * <code>uint32 unit = 4;</code>
     * @param value The unit to set.
     * @return This builder for chaining.
     */
    public Builder setUnit(int value) {

      unit_ = value;
      bitField0_ |= 0x00000008;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Единица измерения для поля value (1 - литры, 2 - миллилитры)
     * </pre>
     *
     * <code>uint32 unit = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearUnit() {
      bitField0_ = (bitField0_ & ~0x00000008);
      unit_ = 0;
      onChanged();
      return this;
    }

    private int t_ ;
    /**
     * <pre>
     * Температура датчика
     * </pre>
     *
     * <code>uint32 t = 5;</code>
     * @return The t.
     */
    @java.lang.Override
    public int getT() {
      return t_;
    }
    /**
     * <pre>
     * Температура датчика
     * </pre>
     *
     * <code>uint32 t = 5;</code>
     * @param value The t to set.
     * @return This builder for chaining.
     */
    public Builder setT(int value) {

      t_ = value;
      bitField0_ |= 0x00000010;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Температура датчика
     * </pre>
     *
     * <code>uint32 t = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearT() {
      bitField0_ = (bitField0_ & ~0x00000010);
      t_ = 0;
      onChanged();
      return this;
    }

    private int status_ ;
    /**
     * <pre>
     * Статус датчика
     * </pre>
     *
     * <code>uint32 status = 6;</code>
     * @return The status.
     */
    @java.lang.Override
    public int getStatus() {
      return status_;
    }
    /**
     * <pre>
     * Статус датчика
     * </pre>
     *
     * <code>uint32 status = 6;</code>
     * @param value The status to set.
     * @return This builder for chaining.
     */
    public Builder setStatus(int value) {

      status_ = value;
      bitField0_ |= 0x00000020;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * Статус датчика
     * </pre>
     *
     * <code>uint32 status = 6;</code>
     * @return This builder for chaining.
     */
    public Builder clearStatus() {
      bitField0_ = (bitField0_ & ~0x00000020);
      status_ = 0;
      onChanged();
      return this;
    }

    // @@protoc_insertion_point(builder_scope:SensFuelLevel)
  }

  // @@protoc_insertion_point(class_scope:SensFuelLevel)
  private static final org.example.libs.SensFuelLevel DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.example.libs.SensFuelLevel();
  }

  public static org.example.libs.SensFuelLevel getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<SensFuelLevel>
      PARSER = new com.google.protobuf.AbstractParser<SensFuelLevel>() {
    @java.lang.Override
    public SensFuelLevel parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      Builder builder = newBuilder();
      try {
        builder.mergeFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(builder.buildPartial());
      } catch (com.google.protobuf.UninitializedMessageException e) {
        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e)
            .setUnfinishedMessage(builder.buildPartial());
      }
      return builder.buildPartial();
    }
  };

  public static com.google.protobuf.Parser<SensFuelLevel> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<SensFuelLevel> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.example.libs.SensFuelLevel getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

