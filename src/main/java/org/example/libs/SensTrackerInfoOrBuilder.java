// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: storage_record.proto
// Protobuf Java Version: 4.30.2

package org.example.libs;

public interface SensTrackerInfoOrBuilder extends
    // @@protoc_insertion_point(interface_extends:SensTrackerInfo)
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
   * Количество подтвержденных отметок
   * </pre>
   *
   * <code>uint32 cnt_ack = 2;</code>
   * @return The cntAck.
   */
  int getCntAck();

  /**
   * <pre>
   * Количество подтвержденных отметок в качестве realtime
   * </pre>
   *
   * <code>uint32 cnt_ack_realtime = 3;</code>
   * @return The cntAckRealtime.
   */
  int getCntAckRealtime();

  /**
   * <pre>
   * Количество отметок, которых не получилось отправить
   * </pre>
   *
   * <code>uint32 cnt_noack = 4;</code>
   * @return The cntNoack.
   */
  int getCntNoack();

  /**
   * <pre>
   * Количество соединений с сервером
   * </pre>
   *
   * <code>uint32 cnt_connect = 5;</code>
   * @return The cntConnect.
   */
  int getCntConnect();
}
