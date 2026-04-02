/* This example shows how to parametrize the rolled scanning process.
 */

import Pr22.Events.*;
import Pr22.FingerprintScannerDevice;
import Pr22.Imaging.FingerPosition;
import Pr22.Imaging.ImpressionType;
import Pr22.Imaging.RawImage.FileFormat;
import Pr22.Task.FingerTask;
import Pr22.Task.TaskControl;
import PrIns.Exceptions.General;
import java.io.IOException;

public class Fps04_rolledscan {

    FingerprintScannerDevice fps = null;
    //progress status values
    int resolution = 0;
    int scanState = 0;
    int progress = 0;
    String message = "";

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

    /** Sleep with exception handling.
     *
     * @param ms
     */
    static void Sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
        }
    }
    //--------------------------------------------------------------------------

    public int program() throws General {

        //Devices can be manipulated only after opening.
        if (open() != 0) {
            return 1;
        }

        addScanEvents();

        Pr22.FingerScanner scanner = fps.getScanner();

        System.out.println("Roll your left index finger on the scanner.");
        System.out.println();

        //starting rolling scan finger collection
        FingerTask rollTask = FingerTask.rolledScan(FingerPosition.LeftIndex);
        TaskControl scanTask = scanner.startTask(rollTask);

        while (progress < 100) {
            progress = scanTask.getState();
            Sleep(100);
        }
        scanTask.Wait();
        message = "Done";
        printState();

        System.out.println();
        System.out.println();

        //saving images
        try {
            scanner.getFinger(FingerPosition.LeftIndex, ImpressionType.Rolled).
                    getImage().save(FileFormat.Bmp).save("index.bmp");
        } catch (IOException ex) {
        }

        //removing the finger data before a possible additional scan process
        scanner.cleanUpData();

        System.out.println("Scanning processes are finished.");
        fps.close();
        return 0;
    }
    //--------------------------------------------------------------------------

    void printState() {
        String states = "|/-\\";
        String prog = "##########";

        System.out.printf(" %c %3d DPI [%-10s] [%-21s]\r",
                states.charAt(scanState % 4), resolution, prog.substring(10 - progress / 10), message);
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

    void addScanEvents() throws General {

        //----------------------------------------------------------------------

        fps.addEventListener(new PreviewCaptured() {

            public void onPreviewCaptured(PreviewEventArgs previewEventArgs) {
                try {
                    resolution = (int) (fps.getScanner().getLiveImage().getHRes() / 39.37);
                    scanState++;
                    printState();
                } catch (General ex) {
                }
            }
        });
        //----------------------------------------------------------------------

        fps.addEventListener(new FingerImageUpdated() {

            public void onImageUpdated(FingerUpdateEventArgs e) {

                try {
                    fps.getScanner().getFinger(e.position, ImpressionType.Rolled).getImage().
                            save(FileFormat.Bmp).save(String.format("%02d.bmp", scanState));
                } catch (General ex) {
                } catch (IOException ex) {
                }
                printState();
            }
        });
    }
    //--------------------------------------------------------------------------

    public static void main(String[] args) {
        try {
            Fps04_rolledscan prog = new Fps04_rolledscan();
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
