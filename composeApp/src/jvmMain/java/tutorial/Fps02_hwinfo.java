/* This example shows how to get general information about the device capabilities.
 */

import Pr22.Events.Connection;
import Pr22.Events.ConnectionEventArgs;
import Pr22.Events.DeviceUpdate;
import Pr22.Events.UpdateEventArgs;
import Pr22.FingerprintScannerDevice;
import PrIns.Exceptions.General;
import java.io.IOException;
import java.util.List;

public class Fps02_hwinfo {

    FingerprintScannerDevice fps = null;

    //--------------------------------------------------------------------------
    /**
     * Opens the first document reader device.
     * @return
     * @throws General
     */
    public int open() throws General {

        System.out.println("Opening a device");
        System.out.println();
        fps = new Pr22.FingerprintScannerDevice();

        addDeviceEvents();

        try {
            fps.useDevice(0);
        } catch (PrIns.Exceptions.NoSuchDevice e) {
            System.out.println("No device found!");
            return 1;
        }

        System.out.println("The device " + fps.getDeviceName() + " is opened.");
        System.out.println();
        return 0;
    }
    //--------------------------------------------------------------------------

    public int program() throws General {

        //Devices can be manipulated only after opening.
        if (open() != 0) {
            return 1;
        }

        System.out.println("SDK versions:");
        System.out.println("\tInterface: " + fps.getVersion('A'));
        System.out.println("\tSystem: " + fps.getVersion('S'));
        System.out.println();

        Pr22.FingerScanner.Information ScannerInfo = fps.getScanner().getInfo();

        //Devices provide proper image quality only if they are calibrated.
        //Devices are calibrated by default. If you receive the message "not calibrated"
        //then please contact your hardware supplier.
        System.out.println("Calibration state of the device:");
        if (ScannerInfo.isCalibrated()) {
            System.out.println("\tcalibrated");
        } else {
            System.out.println("\tnot calibrated");
        }
        System.out.println();

        java.awt.Rectangle frame = ScannerInfo.getSize(0);
        System.out.println("Window size: " + frame.width / 1000 + " x " + frame.height / 1000 + " mm");
        System.out.println();

        System.out.println("Scanner component versions:");
        System.out.println("\tFirmware: " + ScannerInfo.getVersion('F'));
        System.out.println("\tHardware: " + ScannerInfo.getVersion('H'));
        System.out.println("\tSoftware: " + ScannerInfo.getVersion('S'));
        System.out.println();

        System.out.println("Available status LEDs:");
        List<Pr22.Control.StatusLed> leds = fps.getPeripherals().getStatusLeds();
        for (int i = 0; i < leds.size(); ++i) {
            System.out.println("\t" + i + ": color " + leds.get(i).getLight());
        }
        System.out.println();

        System.out.println("Closing the device.");
        fps.close();
        return 0;
    }
    //--------------------------------------------------------------------------
    // Event handlers
    //--------------------------------------------------------------------------

    void addDeviceEvents() throws General {

        //----------------------------------------------------------------------

        fps.addEventListener(new Connection() {

            public void onConnection(ConnectionEventArgs e) {
                System.out.println("Connection event. Device number: " + e.deviceNumber);
            }
        });
        //----------------------------------------------------------------------

        fps.addEventListener(new DeviceUpdate() {

            public void onDeviceUpdate(UpdateEventArgs e) {
                System.out.println("Update event.");
                switch (e.part) {
                    case 1:
                        System.out.println("  Reading calibration file from device.");
                        break;
                    case 2:
                        System.out.println("  Scanner firmware update.");
                        break;
                    case 4:
                        System.out.println("  RFID reader firmware update.");
                        break;
                    case 5:
                        System.out.println("  License update.");
                        break;
                }
            }
        });
    }
    //--------------------------------------------------------------------------

    public static void main(String[] args) {
        try {
            Fps02_hwinfo prog = new Fps02_hwinfo();
            prog.program();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        System.out.println("Press enter to exit!");
        try {
            System.in.read();
        } catch (IOException e) {
        }
    }
    //--------------------------------------------------------------------------
}
