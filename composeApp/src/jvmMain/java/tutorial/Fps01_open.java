/* This example shows the possibilities of opening a device.
 * The following tutorials show the easy way of opening in their open methods.
 */

import Pr22.Events.Connection;
import Pr22.Events.ConnectionEventArgs;
import Pr22.Events.DeviceUpdate;
import Pr22.Events.UpdateEventArgs;
import Pr22.FingerprintScannerDevice;
import PrIns.Exceptions.General;
import java.io.IOException;
import java.util.List;

public class Fps01_open {

    FingerprintScannerDevice fps = null;

    //--------------------------------------------------------------------------
    public int program() throws General {

        /* To open more than one device simultaneously, create more FingerprintScannerDevice objects */
        System.out.println("Opening system");
        System.out.println();
        fps = new FingerprintScannerDevice();

        addDeviceEvents();

        List<String> deviceList = FingerprintScannerDevice.getDeviceList();

        if (deviceList.isEmpty()) {
            System.out.println("No device found!");
            return 0;
        }

        System.out.println(deviceList.size() + " device" + (deviceList.size() > 1 ? "s" : "") + " found.");
        for (String devName : deviceList) {
            System.out.println("  Device: " + devName);
        }
        System.out.println();

        System.out.println("Connecting to the first device by its name: " + deviceList.get(0));
        System.out.println();
        System.out.println("If this is the first usage of this device on this PC,");
        System.out.println("the \"calibration file\" will be downloaded from the device.");
        System.out.println("This can take a while.");
        System.out.println();

        fps.useDevice(deviceList.get(0));

        System.out.println("The device is opened.");

        System.out.println("Closing the device.");
        fps.close();
        System.out.println();


        /* Opening the first device without using any device lists. */

        System.out.println("Connecting to the first device by its ordinal number: 0");

        fps.useDevice(0);

        System.out.println("The device is opened.");

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
            Fps01_open prog = new Fps01_open();
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
