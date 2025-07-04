// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: storage_record.proto
// Protobuf Java Version: 4.30.2

package org.example.libs;

public final class StorageRecordOuterClass {
  private StorageRecordOuterClass() {}
  static {
    com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
      com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
      /* major= */ 4,
      /* minor= */ 30,
      /* patch= */ 2,
      /* suffix= */ "",
      StorageRecordOuterClass.class.getName());
  }
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_StorageRecord_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_StorageRecord_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensAccelerometerData_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensAccelerometerData_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensAinAinValue_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensAinAinValue_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensBufferData_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensBufferData_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensButtonPressCounter_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensButtonPressCounter_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensCanLogData_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensCanLogData_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensCanLogTmpDataExt_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensCanLogTmpDataExt_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensCounterCount_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensCounterCount_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensCrashData_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensCrashData_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensDinsFlags_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensDinsFlags_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensDoutsFlags_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensDoutsFlags_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensFmeterFrequency_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensFmeterFrequency_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensFuelLevel_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensFuelLevel_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensGsmCellMonitoring_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensGsmCellMonitoring_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensNdNavData_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensNdNavData_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensStorInfo_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensStorInfo_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensTermoData_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensTermoData_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensTestEraTests_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensTestEraTests_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SensTrackerInfo_descriptor;
  static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_SensTrackerInfo_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\024storage_record.proto\"\372\014\n\rStorageRecord" +
      "\022\025\n\rrecord_number\030\001 \001(\r\022\022\n\ntime_stamp\030\002 " +
      "\001(\007\022,\n\rrecord_reason\030\003 \003(\0162\025.StorageReco" +
      "rd.Reason\022\024\n\014status_flags\030\004 \001(\007\0227\n\027sens_" +
      "accelerometer_data\030\022 \003(\0132\026.SensAccelerom" +
      "eterData\022,\n\022sens_ain_ain_value\030\t \003(\0132\020.S" +
      "ensAinAinValue\022)\n\020sens_buffer_data\030\024 \003(\013" +
      "2\017.SensBufferData\022:\n\031sens_button_press_c" +
      "ounter\030\016 \003(\0132\027.SensButtonPressCounter\022*\n" +
      "\021sens_can_log_data\030\020 \003(\0132\017.SensCanLogDat" +
      "a\0228\n\031sens_can_log_tmp_data_ext\030\021 \003(\0132\025.S" +
      "ensCanLogTmpDataExt\022-\n\022sens_counter_coun" +
      "t\030\013 \003(\0132\021.SensCounterCount\022\'\n\017sens_crash" +
      "_data\030\030 \003(\0132\016.SensCrashData\022\'\n\017sens_dins" +
      "_flags\030\n \003(\0132\016.SensDinsFlags\022)\n\020sens_dou" +
      "ts_flags\030\023 \003(\0132\017.SensDoutsFlags\0223\n\025sens_" +
      "fmeter_frequency\030\014 \003(\0132\024.SensFmeterFrequ" +
      "ency\022\'\n\017sens_fuel_level\030\010 \003(\0132\016.SensFuel" +
      "Level\0228\n\030sens_gsm_cell_monitoring\030\r \003(\0132" +
      "\026.SensGsmCellMonitoring\022(\n\020sens_nd_nav_d" +
      "ata\030\007 \003(\0132\016.SensNdNavData\022%\n\016sens_stor_i" +
      "nfo\030\025 \003(\0132\r.SensStorInfo\022\'\n\017sens_termo_d" +
      "ata\030\017 \003(\0132\016.SensTermoData\022.\n\023sens_test_e" +
      "ra_tests\030\027 \003(\0132\021.SensTestEraTests\022+\n\021sen" +
      "s_tracker_info\030\026 \003(\0132\020.SensTrackerInfo\"\252" +
      "\005\n\006Reason\022\020\n\014DEVICE_RESET\020\000\022\022\n\016PROFILE_C" +
      "HANGE\020\001\022\017\n\013IGNITION_ON\020\002\022\020\n\014IGNITION_OFF" +
      "\020\003\022\016\n\nTRIP_BEGIN\020\004\022\014\n\010TRIP_END\020\005\022\010\n\004STOP" +
      "\020\006\022\010\n\004MOVE\020\007\022\020\n\014TOWING_BEGIN\020\010\022\016\n\nTOWING" +
      "_END\020\t\022\016\n\nTIMER_MOVE\020\n\022\016\n\nTIMER_STOP\020\013\022\t" +
      "\n\005ANGLE\020\014\022\014\n\010DISTANCE\020\r\022\016\n\nSOS_BUTTON\020\016\022" +
      "\022\n\016SERVICE_BUTTON\020\017\022\n\n\006TAMPER\020\020\022\022\n\016ANTEN" +
      "NA_SWITCH\020\021\022\014\n\010GSM_LOST\020\022\022\021\n\rGSM_RECONNE" +
      "CT\020\023\022\r\n\tGNSS_LOST\020\024\022\025\n\021GNSS_REAQUISITION" +
      "\020\025\022\016\n\nJAMMER_GSM\020\026\022\017\n\013JAMMER_GNSS\020\027\022\016\n\nO" +
      "VER_SPEED\020\030\022\014\n\010OVER_RPM\020\031\022\024\n\020OVER_TEMPER" +
      "ATURE\020\032\022\025\n\021DANGEROUS_DRIVING\020\033\022\014\n\010ACCIDE" +
      "NT\020\034\022\r\n\tOVERTHROW\020\035\022\016\n\nON_BATTERY\020\036\022\025\n\021B" +
      "ATTERY_DISCHARGE\020\037\022\021\n\rRADIO_TAG_REG\020 \022\023\n" +
      "\017RADIO_TAG_UNREG\020!\022\032\n\026MOVE_WITHOUT_RADIO" +
      "_TAG\020\"\022\022\n\016ECU_ERROR_CODE\020#\022\024\n\020EXTERNAL_R" +
      "EQUEST\020$\022\017\n\013DEVICE_TEST\020%\022\020\n\014OTHER_REASO" +
      "N\020c\"\203\001\n\025SensAccelerometerData\022\020\n\010sens_nu" +
      "m\030\001 \001(\r\022\013\n\003buf\030\002 \001(\014\022\013\n\003atm\030\003 \001(\r\022\021\n\tfre" +
      "quency\030\004 \001(\r\022\r\n\005range\030\005 \001(\r\022\016\n\006format\030\006 " +
      "\001(\r\022\014\n\004zlib\030\007 \001(\r\"/\n\017SensAinAinValue\022\020\n\010" +
      "sens_num\030\001 \001(\r\022\n\n\002mv\030\002 \001(\r\"C\n\016SensBuffer" +
      "Data\022\020\n\010sens_num\030\001 \001(\r\022\014\n\004data\030\002 \001(\014\022\021\n\t" +
      "is_packed\030\003 \001(\010\"9\n\026SensButtonPressCounte" +
      "r\022\020\n\010sens_num\030\001 \001(\r\022\r\n\005state\030\002 \001(\010\"\364\002\n\016S" +
      "ensCanLogData\022\020\n\010sens_num\030\001 \001(\r\022\033\n\023flag_" +
      "security_state\030\002 \001(\r\022\022\n\nflag_alarm\030\003 \001(\r" +
      "\022\027\n\017engine_time_all\030\004 \001(\r\022\031\n\021engine_turn" +
      "_speed\030\005 \001(\r\022\032\n\022engine_temperature\030\006 \001(\021" +
      "\022\034\n\024fuel_consumption_all\030\007 \001(\r\022\022\n\nfuel_l" +
      "evel\030\010 \001(\r\022\021\n\ttrack_all\030\t \001(\r\022\r\n\005speed\030\n" +
      " \001(\r\022\027\n\017pressure_axis_1\030\013 \001(\r\022\027\n\017pressur" +
      "e_axis_2\030\014 \001(\r\022\027\n\017pressure_axis_3\030\r \001(\r\022" +
      "\027\n\017pressure_axis_4\030\016 \001(\r\022\027\n\017pressure_axi" +
      "s_5\030\017 \001(\r\"O\n\024SensCanLogTmpDataExt\022\020\n\010sen" +
      "s_num\030\001 \001(\r\022\022\n\nflags_high\030\002 \001(\r\022\021\n\tflags" +
      "_low\030\003 \001(\r\"3\n\020SensCounterCount\022\020\n\010sens_n" +
      "um\030\001 \001(\r\022\r\n\005value\030\002 \001(\r\"H\n\rSensCrashData" +
      "\022\020\n\010sens_num\030\001 \001(\r\022\016\n\006energy\030\002 \001(\r\022\025\n\ris" +
      "_overturned\030\003 \001(\010\"C\n\rSensDinsFlags\022\020\n\010se" +
      "ns_num\030\001 \001(\r\022\016\n\006device\030\002 \001(\r\022\020\n\010external" +
      "\030\003 \001(\r\"D\n\016SensDoutsFlags\022\020\n\010sens_num\030\001 \001" +
      "(\r\022\016\n\006device\030\002 \001(\r\022\020\n\010external\030\003 \001(\r\"6\n\023" +
      "SensFmeterFrequency\022\020\n\010sens_num\030\001 \001(\r\022\r\n" +
      "\005value\030\002 \001(\r\"j\n\rSensFuelLevel\022\020\n\010sens_nu" +
      "m\030\001 \001(\r\022\r\n\005value\030\002 \001(\002\022\017\n\007parrots\030\003 \001(\r\022" +
      "\014\n\004unit\030\004 \001(\r\022\t\n\001t\030\005 \001(\r\022\016\n\006status\030\006 \001(\r" +
      "\"}\n\025SensGsmCellMonitoring\022\020\n\010sens_num\030\001 " +
      "\001(\r\022\013\n\003lac\030\004 \001(\014\022\013\n\003cid\030\005 \001(\014\022\014\n\004rssi\030\006 " +
      "\001(\014\022\020\n\010time_adv\030\007 \001(\014\022\013\n\003mcc\030\002 \001(\r\022\013\n\003mn" +
      "c\030\003 \001(\r\"\271\001\n\rSensNdNavData\022\020\n\010sens_num\030\001 " +
      "\001(\r\022\021\n\tlongitude\030\002 \001(\017\022\020\n\010latitude\030\003 \001(\017" +
      "\022\020\n\010altitude\030\004 \001(\r\022\r\n\005speed\030\005 \001(\r\022\016\n\006cou" +
      "rse\030\006 \001(\r\022\021\n\tsat_count\030\007 \001(\r\022\014\n\004pdop\030\010 \001" +
      "(\r\022\r\n\005track\030\t \001(\r\022\020\n\010odometer\030\n \001(\r\"\231\001\n\014" +
      "SensStorInfo\022\020\n\010sens_num\030\001 \001(\r\022\016\n\006id_max" +
      "\030\002 \001(\r\022\016\n\006id_min\030\003 \001(\r\022\021\n\ttm_oldest\030\004 \001(" +
      "\r\022\027\n\017tm_oldest_unack\030\005 \001(\r\022\021\n\tcnt_unack\030" +
      "\006 \001(\r\022\030\n\020cnt_unack_losted\030\007 \001(\r\"F\n\rSensT" +
      "ermoData\022\020\n\010sens_num\030\001 \001(\r\022\016\n\006status\030\002 \001" +
      "(\r\022\023\n\013temperature\030\003 \001(\021\"\234\002\n\020SensTestEraT" +
      "ests\022\020\n\010sens_num\030\001 \001(\r\022\027\n\017mic_con_failur" +
      "e\030\002 \001(\010\022\023\n\013mic_failure\030\003 \001(\010\022\030\n\020ignition" +
      "_failure\030\004 \001(\010\022\023\n\013uim_failure\030\005 \001(\010\022\023\n\013b" +
      "at_failure\030\006 \001(\010\022\024\n\014bat_volt_low\030\007 \001(\010\022\032" +
      "\n\022crash_sens_failure\030\010 \001(\010\022\024\n\014raim_probl" +
      "em\030\t \001(\010\022\034\n\024gnss_antenna_failure\030\n \001(\010\022\036" +
      "\n\026events_memory_overflow\030\013 \001(\010\"v\n\017SensTr" +
      "ackerInfo\022\020\n\010sens_num\030\001 \001(\r\022\017\n\007cnt_ack\030\002" +
      " \001(\r\022\030\n\020cnt_ack_realtime\030\003 \001(\r\022\021\n\tcnt_no" +
      "ack\030\004 \001(\r\022\023\n\013cnt_connect\030\005 \001(\rB\024\n\020org.ex" +
      "ample.libsP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_StorageRecord_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_StorageRecord_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_StorageRecord_descriptor,
        new java.lang.String[] { "RecordNumber", "TimeStamp", "RecordReason", "StatusFlags", "SensAccelerometerData", "SensAinAinValue", "SensBufferData", "SensButtonPressCounter", "SensCanLogData", "SensCanLogTmpDataExt", "SensCounterCount", "SensCrashData", "SensDinsFlags", "SensDoutsFlags", "SensFmeterFrequency", "SensFuelLevel", "SensGsmCellMonitoring", "SensNdNavData", "SensStorInfo", "SensTermoData", "SensTestEraTests", "SensTrackerInfo", });
    internal_static_SensAccelerometerData_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_SensAccelerometerData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensAccelerometerData_descriptor,
        new java.lang.String[] { "SensNum", "Buf", "Atm", "Frequency", "Range", "Format", "Zlib", });
    internal_static_SensAinAinValue_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_SensAinAinValue_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensAinAinValue_descriptor,
        new java.lang.String[] { "SensNum", "Mv", });
    internal_static_SensBufferData_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_SensBufferData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensBufferData_descriptor,
        new java.lang.String[] { "SensNum", "Data", "IsPacked", });
    internal_static_SensButtonPressCounter_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_SensButtonPressCounter_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensButtonPressCounter_descriptor,
        new java.lang.String[] { "SensNum", "State", });
    internal_static_SensCanLogData_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_SensCanLogData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensCanLogData_descriptor,
        new java.lang.String[] { "SensNum", "FlagSecurityState", "FlagAlarm", "EngineTimeAll", "EngineTurnSpeed", "EngineTemperature", "FuelConsumptionAll", "FuelLevel", "TrackAll", "Speed", "PressureAxis1", "PressureAxis2", "PressureAxis3", "PressureAxis4", "PressureAxis5", });
    internal_static_SensCanLogTmpDataExt_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_SensCanLogTmpDataExt_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensCanLogTmpDataExt_descriptor,
        new java.lang.String[] { "SensNum", "FlagsHigh", "FlagsLow", });
    internal_static_SensCounterCount_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_SensCounterCount_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensCounterCount_descriptor,
        new java.lang.String[] { "SensNum", "Value", });
    internal_static_SensCrashData_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_SensCrashData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensCrashData_descriptor,
        new java.lang.String[] { "SensNum", "Energy", "IsOverturned", });
    internal_static_SensDinsFlags_descriptor =
      getDescriptor().getMessageTypes().get(9);
    internal_static_SensDinsFlags_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensDinsFlags_descriptor,
        new java.lang.String[] { "SensNum", "Device", "External", });
    internal_static_SensDoutsFlags_descriptor =
      getDescriptor().getMessageTypes().get(10);
    internal_static_SensDoutsFlags_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensDoutsFlags_descriptor,
        new java.lang.String[] { "SensNum", "Device", "External", });
    internal_static_SensFmeterFrequency_descriptor =
      getDescriptor().getMessageTypes().get(11);
    internal_static_SensFmeterFrequency_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensFmeterFrequency_descriptor,
        new java.lang.String[] { "SensNum", "Value", });
    internal_static_SensFuelLevel_descriptor =
      getDescriptor().getMessageTypes().get(12);
    internal_static_SensFuelLevel_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensFuelLevel_descriptor,
        new java.lang.String[] { "SensNum", "Value", "Parrots", "Unit", "T", "Status", });
    internal_static_SensGsmCellMonitoring_descriptor =
      getDescriptor().getMessageTypes().get(13);
    internal_static_SensGsmCellMonitoring_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensGsmCellMonitoring_descriptor,
        new java.lang.String[] { "SensNum", "Lac", "Cid", "Rssi", "TimeAdv", "Mcc", "Mnc", });
    internal_static_SensNdNavData_descriptor =
      getDescriptor().getMessageTypes().get(14);
    internal_static_SensNdNavData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensNdNavData_descriptor,
        new java.lang.String[] { "SensNum", "Longitude", "Latitude", "Altitude", "Speed", "Course", "SatCount", "Pdop", "Track", "Odometer", });
    internal_static_SensStorInfo_descriptor =
      getDescriptor().getMessageTypes().get(15);
    internal_static_SensStorInfo_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensStorInfo_descriptor,
        new java.lang.String[] { "SensNum", "IdMax", "IdMin", "TmOldest", "TmOldestUnack", "CntUnack", "CntUnackLosted", });
    internal_static_SensTermoData_descriptor =
      getDescriptor().getMessageTypes().get(16);
    internal_static_SensTermoData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensTermoData_descriptor,
        new java.lang.String[] { "SensNum", "Status", "Temperature", });
    internal_static_SensTestEraTests_descriptor =
      getDescriptor().getMessageTypes().get(17);
    internal_static_SensTestEraTests_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensTestEraTests_descriptor,
        new java.lang.String[] { "SensNum", "MicConFailure", "MicFailure", "IgnitionFailure", "UimFailure", "BatFailure", "BatVoltLow", "CrashSensFailure", "RaimProblem", "GnssAntennaFailure", "EventsMemoryOverflow", });
    internal_static_SensTrackerInfo_descriptor =
      getDescriptor().getMessageTypes().get(18);
    internal_static_SensTrackerInfo_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_SensTrackerInfo_descriptor,
        new java.lang.String[] { "SensNum", "CntAck", "CntAckRealtime", "CntNoack", "CntConnect", });
    descriptor.resolveAllFeaturesImmutable();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
