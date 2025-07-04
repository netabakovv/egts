// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: storage_record.proto
// Protobuf Java Version: 4.30.2

package org.example.libs;

public interface SensGsmCellMonitoringOrBuilder extends
    // @@protoc_insertion_point(interface_extends:SensGsmCellMonitoring)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Номер датчика
   * </pre>
   *
   * <code>uint32 sens_num = 1;</code>
   * @return The sensNum.
   */
  int getSensNum();

  /**
   * <pre>
   * Код локальной зоны
   * </pre>
   *
   * <code>bytes lac = 4;</code>
   * @return The lac.
   */
  com.google.protobuf.ByteString getLac();

  /**
   * <pre>
   * Идентификатор соты
   * </pre>
   *
   * <code>bytes cid = 5;</code>
   * @return The cid.
   */
  com.google.protobuf.ByteString getCid();

  /**
   * <pre>
   * Уровень принимаемого по данному каналу радиосигнала на входе в приёмник телефона
   * </pre>
   *
   * <code>bytes rssi = 6;</code>
   * @return The rssi.
   */
  com.google.protobuf.ByteString getRssi();

  /**
   * <pre>
   * Параметр компенсации времени прохождения сигнала от телефона до БС
   * </pre>
   *
   * <code>bytes time_adv = 7;</code>
   * @return The timeAdv.
   */
  com.google.protobuf.ByteString getTimeAdv();

  /**
   * <pre>
   * Код страны
   * </pre>
   *
   * <code>uint32 mcc = 2;</code>
   * @return The mcc.
   */
  int getMcc();

  /**
   * <pre>
   * Код сотовой сети
   * </pre>
   *
   * <code>uint32 mnc = 3;</code>
   * @return The mnc.
   */
  int getMnc();
}
