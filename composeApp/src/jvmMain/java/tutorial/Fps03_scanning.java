/* This example shows how to parametrize the image scanning process.
 */

import Pr22.Events.*;
import Pr22.FingerprintScannerDevice;
import Pr22.Imaging.FingerPosition;
import Pr22.Imaging.ImpressionType;
import Pr22.Imaging.RawImage.FileFormat;
import Pr22.Processing.FingerCollection;
import Pr22.Task.FingerTask;
import Pr22.Task.TaskControl;
import Pr22.Util.PresenceState;
import Pr22.Util.Variant;
import PrIns.Exceptions.General;
import java.io.IOException;

public class Fps03_scanning {

    FingerprintScannerDevice fps = null;
    //progress status values
    int resolution = 0;
    int scanState = 0;
    int progress = 0;
    int[] quality = new int[4];
    String message = "";
    PresenceState detect = PresenceState.NoMove;

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

        System.out.println("Put your left hand to the scanner.");
        System.out.println();

        //starting detection
        TaskControl liveTask = scanner.startTask(FingerTask.detection());

        int timeout = 100;  // in 100 milliseconds
        int i;

        for (i = 0; i < timeout && detect != PresenceState.Present; ++i) {
            Sleep(100);
        }

        //starting plain scan finger collection
        FingerTask flatTask = FingerTask.plainScan(800, 10000);
        flatTask.add(FingerPosition.PlainLeft4Fingers);
        TaskControl scanTask = scanner.startTask(flatTask);

        while (progress < 100) {
            progress = scanTask.getState();
            Sleep(100);
        }
        scanTask.Wait();
        message = "Done";
        printState();

        for (i = 0; i < timeout && detect == PresenceState.Present; ++i) {
            Sleep(100);
        }

        liveTask.Stop();

        printState();
        System.out.println();
        System.out.println();

        //saving images
        try {
            scanner.getFinger(FingerPosition.LeftIndex, ImpressionType.Plain).
                    getImage().save(FileFormat.Bmp).save("index.bmp");

            Variant nistprop = buildNistProps();

            //excludes the middle finger from NIST saving
            flatTask.del(FingerPosition.LeftMiddle);

            scanner.getFingerCollection(flatTask).
                    save(FingerCollection.FileFormat.Nist, nistprop).save("mynist.nist");
        } catch (IOException ex) {
        }

        //removing the finger data before a possible additional scan process
        scanner.cleanUpData();

        System.out.println("Scanning processes are finished.");
        fps.close();
        return 0;
    }
    //--------------------------------------------------------------------------

    Variant buildNistProps() throws General {

        Variant nistprop = new Variant(0, Variant.ListT.List);

        //list of additional type-1 record data
        Variant type1 = new Variant(1, Variant.ListT.List);
        nistprop.addListItem(type1);

        //set 1.004 to "ATP"
        type1.addListItem(new Variant(4, "ATP"));

        //list of additional type-2 record data
        Variant type2 = new Variant(2, Variant.ListT.List);
        nistprop.addListItem(type2);

        //set 2.045 to "Test message"
        type2.addListItem(new Variant(45, "Test message"));

        return nistprop;
    }
    //--------------------------------------------------------------------------

    void printState() {
        String states = "|/-\\";
        String prog = "##########";

        System.out.printf(" %c %3d DPI [%-10s] [%-21s] Q1:%4d Q2:%4d Q3:%4d Q4:%4d\r",
                states.charAt(scanState % 4), resolution, prog.substring(10 - progress / 10),
                message, quality[0], quality[1], quality[2], quality[3]);
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

                int[] pos = {0, 2, 0, 1, 2, 3, 1, 3, 2, 1, 0};
                int ix = e.position.id > 10 ? 0 : e.position.id;
                try {
                    quality[pos[ix]] = fps.getScanner().getFinger(e.position, ImpressionType.Plain).getQuality();
                } catch (General ex) {
                }
                printState();
            }
        });
        //----------------------------------------------------------------------

        fps.addEventListener(new FingerPositioning() {

            public void onFingerPositioning(FingerEventArgs fingerEventArgs) {
                message = fingerEventArgs.fingerFailureMask.toString();
                printState();
            }
        });
        //----------------------------------------------------------------------

        fps.addEventListener(new PresenceStateChanged() {

            public void onStateChanged(DetectionEventArgs e) {
                message = e.state.toString();
                printState();
                detect = e.state;
            }
        });
    }
    //--------------------------------------------------------------------------

    public static void main(String[] args) {
        try {
            Fps03_scanning prog = new Fps03_scanning();
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
