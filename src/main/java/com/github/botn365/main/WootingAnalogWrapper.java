package com.github.botn365.main;

import java.io.*;

import static com.github.botn365.main.NataiveLoader.hasUnpackedFile;
import static com.github.botn365.main.NataiveLoader.unpackNativeLib;

public class WootingAnalogWrapper {
    public enum WootingAnalogResult {
        WootingAnalogResult_Ok(1),
        WootingAnalogResult_UnInitialized(-2000),
        WootingAnalogResult_NoDevices(-1999),
        WootingAnalogResult_DeviceDisconnected(-1998),
        WootingAnalogResult_Failure(-1997),
        WootingAnalogResult_InvalidArgument(-1996),
        WootingAnalogResult_NoPlugins(-1995),
        WootingAnalogResult_FunctionNotFound(-1994),
        WootingAnalogResult_NoMapping(-1993),
        WootingAnalogResult_NotAvailable(-1992),
        WootingAnalogResult_IncompatibleVersion(-1991),
        WootingAnalogResult_DLLNotFound(-1990);
        public final int value;
        WootingAnalogResult(int value) {
            this.value = value;
        }

        public boolean equals(WootingAnalogResult result) {
            return this.value == result.value;
        }
        /*
         * Translates A int to the Enum with the same value
         *  @ Param value Value of the enum
         *
         * @Return WootingAnalogResult or NULL if not a valid value
         */
        public WootingAnalogResult fromInt(int value) {
            switch (value) {
                case 1:
                    return WootingAnalogResult_Ok;
                case -2000:
                    return WootingAnalogResult_UnInitialized;
                case -1999:
                    return WootingAnalogResult_NoDevices;
                case -1998:
                    return WootingAnalogResult_DeviceDisconnected;
                case -1997:
                    return WootingAnalogResult_Failure;
                case -1996:
                    return WootingAnalogResult_InvalidArgument;
                case -1995:
                    return WootingAnalogResult_NoPlugins;
                case -1994:
                    return WootingAnalogResult_FunctionNotFound;
                case -1993:
                    return WootingAnalogResult_NoMapping;
                case -1992:
                    return WootingAnalogResult_NotAvailable;
                case -1991:
                    return WootingAnalogResult_IncompatibleVersion;
                case -1990:
                    return WootingAnalogResult_DLLNotFound;
            }
            return null;
        }
    }

    public enum WootingAnalogDeviceEventType {
        WootingAnalog_DeviceEventType_Connected(1),
        WootingAnalog_DeviceEventType_Disconnected(2);
        public final int value;
        WootingAnalogDeviceEventType(int value) {
            this.value = value;
        }
        public boolean equals(WootingAnalogDeviceEventType result) {
            return this.value == result.value;
        }
    }

    public enum WootingAnalogDeviceType {
        WootingAnalog_DeviceType_Keyboard(1),
        WootingAnalog_DeviceType_Keypad(2),
        WootingAnalog_DeviceType_Other(3);
        public final int value;
        WootingAnalogDeviceType(int value) {
            this.value = value;
        }

        public boolean equals(WootingAnalogDeviceType result) {
            return this.value == result.value;
        }
    }

    static public class WootingAnalogDeviceInfoFFI {
        public WootingAnalogDeviceInfoFFI() {}

        public WootingAnalogDeviceInfoFFI(int vendorId, int productId, String manufacturerName, String deviceName, long deviceId, WootingAnalogDeviceType deviceType) {
            this.vendorId = vendorId;
            this.productId = productId;
            this.manufacturerName = manufacturerName;
            this.deviceName = deviceName;
            this.deviceId = deviceId;
            this.deviceType = deviceType;
        }
        public int vendorId;
        public int productId;
        public String manufacturerName;
        public String deviceName;
        public long deviceId;
        public WootingAnalogDeviceType deviceType;
    }

    public interface DeviceEventCallback {
        void event(WootingAnalogDeviceEventType eventType,WootingAnalogDeviceInfoFFI device);
    }
    static public native int wootingAnalogInitialise();
    static public native boolean wootingAnalogIsInitialised();
    static public native WootingAnalogResult wootingAnalogUnisialise();
    static public native WootingAnalogResult wootingAnalogSetKeycode(int mode);
    static public native float wootingAnalogReadAnalog(int code);
    static public native float wootingAnalogReadAnalogDevice(int code, long deviceId);
    static public native int wootingAnalogGetConnectedDevicesInfo(WootingAnalogDeviceInfoFFI[] devices);
    static public native int wootingAnalogReadFullBuffer(short[] codes,float[] values);
    static public native int wootingAnalogReadFullBufferDevice(short[] codes,float[] values,long deviceId);
    static public native WootingAnalogResult wootingAnalogSetDeviceEventCb(DeviceEventCallback callback);
    static public native WootingAnalogResult wootingAnalogClearDeviceEventCb();

    public WootingAnalogDeviceInfoFFI t(int vendorId, int productId, String manufacturerName, String deviceName, long deviceId, byte deviceType) {
        return new WootingAnalogDeviceInfoFFI();
    }

    static {
        String resourceNameLib;
        String wootingDll ;
        final String os = System.getProperty("os.name").toLowerCase();
        final String cpu = System.getProperty("os.arch").toLowerCase();
        if (os.contains("windows")) {
            if (cpu.contains("amd64")) {
                resourceNameLib = "native/windows/x86_64/wooting-analog-sdk-java-glue.dll";
                String libPthread = "native/windows/x86_64/libwinpthread-1.dll";
                if (hasUnpackedFile(libPthread)) {
                    File lib = unpackNativeLib(libPthread);
                    System.load(lib.getAbsolutePath());
                }
                wootingDll = "native/windows/x86_64/wooting_analog_wrapper.dll";
            } else {
                throw new RuntimeException("cpu arch "+cpu+" not suported on "+os);
            }
        } else if (os.contains("linux")) {
            if (cpu.contains("amd64")) {
                resourceNameLib = "native/linux/x86_64/libwooting-analog-sdk-java-glue.so";
                wootingDll = "native/linux/x86_64/libwooting_analog_wrapper.so";
            } else {
                throw new RuntimeException("cpu arch "+cpu+" not suported on "+os);
            }
        } else if (os.contains("mac")) {
            if (cpu.contains("amd64")) {
                wootingDll = "native/apple/x86_64/libwooting-analog-sdk-java-glue.dylib";
                resourceNameLib = "native/apple/x86_64/libwooting_analog_wrapper.dylib";
            } else if (cpu.contains("arm") || cpu.contains("aarch")) {
                wootingDll = "native/apple/arm64/libwooting-analog-sdk-java-glue.dylib";
                resourceNameLib = "native/apple/arm64/libwooting_analog_wrapper.dylib";
            } else {
                throw new RuntimeException("cpu arch "+cpu+" not suported on "+os);
            }
        } else {
            throw new RuntimeException("unsuported OS "+os);
        }

        if (!hasUnpackedFile(resourceNameLib)) {
            throw new RuntimeException("native lib for "+os+" was not found in jar");
        }
        File wraper =  unpackNativeLib(wootingDll);
        File resource = unpackNativeLib(resourceNameLib);
        System.load(wraper.getAbsolutePath());
        System.load(resource.getAbsolutePath());
    }
}
