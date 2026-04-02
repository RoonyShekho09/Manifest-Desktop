/* This example shows how to get general information about the device capabilities.
 */

import Pr22.DocumentReaderDevice;
import Pr22.Events.Connection;
import Pr22.Events.ConnectionEventArgs;
import Pr22.Events.DeviceUpdate;
import Pr22.Events.UpdateEventArgs;
import Pr22.Util.CompatEnum;
import PrIns.Exceptions.General;
import java.io.IOException;
import java.util.List;

public class Pr02_hwinfo {

    DocumentReaderDevice pr = null;

    //--------------------------------------------------------------------------
    /**
     * Opens the first document reader device.
     * @return
     * @throws General
     */
    public int open() throws General {

        System.out.println("Opening a device");
        System.out.println();
        pr = new DocumentReaderDevice();

        addDeviceEvents();

        try {
            pr.useDevice(0);
        } catch (PrIns.Exceptions.NoSuchDevice e) {
            System.out.println("No device found!");
            return 1;
        }

        System.out.println("The device " + pr.getDeviceName() + " is opened.");
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
        System.out.println("\tInterface: " + pr.getVersion('A'));
        System.out.println("\tSystem: " + pr.getVersion('S'));
        System.out.println();

        Pr22.DocScanner.Information scannerInfo = pr.getScanner().getInfo();

        //Devices provide proper image quality only if they are calibrated.
        //Devices are calibrated by default. If you receive the message "not calibrated"
        //then please contact your hardware supplier.
        System.out.println("Calibration state of the device:");
        if (scannerInfo.isCalibrated()) {
            System.out.println("\tcalibrated");
        } else {
            System.out.println("\tnot calibrated");
        }
        System.out.println();

        System.out.println("Available lights for image scanning:");
        List<CompatEnum<Pr22.Imaging.Light>> lights = scannerInfo.getLights();
        for (CompatEnum<Pr22.Imaging.Light> light : lights) {
            System.out.println("\t" + light.toString());
        }
        System.out.println();

        System.out.println("Available object windows for image scanning:");
        for (int i = 0; i < scannerInfo.getWindowCount(); ++i) {
            java.awt.Rectangle frame = scannerInfo.getSize(i);
            System.out.println("\t" + i + ": " + frame.width / 1000 + " x " + frame.height / 1000 + " mm");
        }
        System.out.println();

        System.out.println("Scanner component versions:");
        System.out.println("\tFirmware: " + scannerInfo.getVersion('F'));
        System.out.println("\tHardware: " + scannerInfo.getVersion('H'));
        System.out.println("\tSoftware: " + scannerInfo.getVersion('S'));
        System.out.println();

        System.out.println("Available card readers:");
        List<Pr22.ECardReader> readers = pr.getReaders();
        for (int i = 0; i < readers.size(); ++i) {
            System.out.println("\t" + i + ": " + readers.get(i).getInfo().getHwType());
            System.out.println("\t\tFirmware: " + readers.get(i).getInfo().getVersion('F'));
            System.out.println("\t\tHardware: " + readers.get(i).getInfo().getVersion('H'));
            System.out.println("\t\tSoftware: " + readers.get(i).getInfo().getVersion('S'));
        }
        System.out.println();

        System.out.println("Available status LEDs:");
        List<Pr22.Control.StatusLed> leds = pr.getPeripherals().getStatusLeds();
        for (int i = 0; i < leds.size(); ++i) {
            System.out.println("\t" + i + ": color " + leds.get(i).getLight());
        }
        System.out.println();

        Pr22.Engine.Information engineInfo = pr.getEngine().getInfo();

        System.out.println("Engine version: " + engineInfo.getVersion('E'));
        String[] licok = {"no presence info", "not available", "present", "expired"};
        String lictxt = engineInfo.getRequiredLicense().toString();
        if (engineInfo.getRequiredLicense() == Pr22.Processing.EngineLicense.MrzOcrBarcodeReading) {
            lictxt = "MrzOcrBarcodeReadingL or MrzOcrBarcodeReadingF";
        }
        System.out.println("Required license: " + lictxt + " - " + licok[testLicense(engineInfo)]);
        System.out.println("Engine release date: " + engineInfo.getRequiredLicenseDate());
        System.out.println();

        System.out.println("Available licenses:");
        List<Pr22.Processing.EngineLicense> licenses = engineInfo.getAvailableLicenses();
        for (Pr22.Processing.EngineLicense lic : licenses) {
            System.out.println("\t" + lic + " (" + engineInfo.getLicenseDate(lic) + ")");
        }
        System.out.println();

        System.out.println("Closing the device.");
        pr.close();
        return 0;
    }
    //--------------------------------------------------------------------------

    /**
     * Tests if the required OCR license is present.
     */
    static int testLicense(Pr22.Engine.Information info) throws General {
        if (info.getRequiredLicense() == Pr22.Processing.EngineLicense.Unknown) {
            return 0;
        }
        String availdate = info.getLicenseDate(info.getRequiredLicense());
        if (availdate.equals("-")) {
            return 1;
        }
        if (info.getRequiredLicenseDate().equals("-")) {
            return 2;
        }
        if (availdate.charAt(0) != 'X' && availdate.compareTo(info.getRequiredLicenseDate()) > 0) {
            return 2;
        }
        return 3;
    }
    //--------------------------------------------------------------------------
    // Event handlers
    //--------------------------------------------------------------------------

    void addDeviceEvents() throws General {

        //----------------------------------------------------------------------

        pr.addEventListener(new Connection() {

            public void onConnection(ConnectionEventArgs e) {
                System.out.println("Connection event. Device number: " + e.deviceNumber);
            }
        });
        //----------------------------------------------------------------------

        pr.addEventListener(new DeviceUpdate() {

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
            Pr02_hwinfo prog = new Pr02_hwinfo();
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
