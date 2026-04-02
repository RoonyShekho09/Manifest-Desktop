/* This example shows how to parametrize the image scanning process.
 */
package tutorial;

import Pr22.DocumentReaderDevice;
import Pr22.Events.*;
import Pr22.Imaging.RawImage;
import Pr22.Imaging.RawImage.FileFormat;
import Pr22.Task.DocScannerTask;
import Pr22.Task.FreerunTask;
import Pr22.Task.TaskControl;
import PrIns.Exceptions.General;
import java.io.IOException;


public class Pr03_scanning {

    DocumentReaderDevice pr = null;
    boolean docPresent;

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

        System.out.println("This tutorial guides you through a complex image scanning process.");
        System.out.println("This will demonstrate all possible options of page management.");
        System.out.println("The stages of the scan process will be saved into separate zip files");
        System.out.println("in order to provide the possibility of comparing them to each other.");
        System.out.println();

        //Devices can be manipulated only after opening.
        if (open() != 0) {
            return 1;
        }

        addScanEvents();

        Pr22.DocScanner scanner = pr.getScanner();

        TaskControl liveTask = scanner.startTask(FreerunTask.detection());

        //first page
        {
            DocScannerTask firstTask = new DocScannerTask();

            System.out.println("At first the device scans only a white image...");
            firstTask.add(Pr22.Imaging.Light.White);
            Pr22.Processing.Page page1 = scanner.scan(firstTask, Pr22.Imaging.PagePosition.First);

            System.out.println("And then the program saves it as a PNG file.");
            try {
                page1.select(Pr22.Imaging.Light.White).getImage().save(FileFormat.Png).save("original.png");
            } catch (IOException ex) {
            }

            System.out.println("Saving stage 1.");
            try {
                pr.getEngine().getRootDocument().save(Pr22.Processing.Document.FileFormat.Zipped).save("1stScan.zip");
            } catch (IOException ex) {
            }
            System.out.println();

            System.out.println("If scanning of an additional infra image of the same page is required...");
            System.out.println("We need to scan it into the current page.");
            firstTask.add(Pr22.Imaging.Light.Infra);
            scanner.scan(firstTask, Pr22.Imaging.PagePosition.Current);

            try {
                System.out.println("If a cropped image of the document is available");
                System.out.println(" then the program saves it as a PNG file.");
                scanner.getPage(0).select(Pr22.Imaging.Light.White).docView().getImage().save(FileFormat.Png).save("document.png");
            } catch (IOException ex) {
            } catch (PrIns.Exceptions.ImageProcessingFailed e) {
                System.out.println("Cropped image is not available!");
            }

            System.out.println("Saving stage 2.");
            try {
                pr.getEngine().getRootDocument().save(Pr22.Processing.Document.FileFormat.Zipped).save("2ndScan.zip");
            } catch (IOException ex) {
            }
            System.out.println();
        }

        //second page
        {
            System.out.println("At this point, if scanning of an additional page of the document is needed");
            System.out.println("with all of the available lights except the infra light.");
            System.out.println("It is recommended to execute in one scan process");
            System.out.println(" - as it is the fastest in such a way.");
            Pr22.Task.DocScannerTask SecondTask = new DocScannerTask();
            SecondTask.add(Pr22.Imaging.Light.All).del(Pr22.Imaging.Light.Infra);
            System.out.println();

            docPresent = false;
            System.out.println("At this point, the user has to change the document on the reader.");
            while (docPresent == false) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }

            System.out.println("Scanning the images.");
            scanner.scan(SecondTask, Pr22.Imaging.PagePosition.Next);

            System.out.println("Saving stage 3.");
            try {
                pr.getEngine().getRootDocument().save(Pr22.Processing.Document.FileFormat.Zipped).save("3rdScan.zip");
            } catch (IOException ex) {
            }
            System.out.println();

            System.out.println("Upon putting incorrect page on the scanner, the scanned page has to be removed.");
            scanner.cleanUpLastPage();

            docPresent = false;
            System.out.println("And the user has to change the document on the reader again.");
            while (docPresent == false) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }

            System.out.println("Scanning...");
            scanner.scan(SecondTask, Pr22.Imaging.PagePosition.Next);

            System.out.println("Saving stage 4.");
            try {
                pr.getEngine().getRootDocument().save(Pr22.Processing.Document.FileFormat.Zipped).save("4thScan.zip");
            } catch (IOException ex) {
            }
            System.out.println();
        }

        liveTask.Stop();

        System.out.println("Scanning processes are finished.");
        pr.close();
        return 0;
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

    void addScanEvents() throws General {

        //----------------------------------------------------------------------

        pr.addEventListener(new ScanStarted() {

            public void onScanStart(PageEventArgs e) {
                System.out.println("Scan started. Page: " + e.page);
            }
        });
        //----------------------------------------------------------------------

        pr.addEventListener(new ImageScanned() {

            public void onImageScanned(ImageEventArgs e) {
                System.out.println("Image scanned. Page: " + e.page + " Light: " + e.light);
                try {
                    RawImage img = pr.getScanner().getPage(e.page).select(e.light).getImage();
                    img.save(FileFormat.Bmp).save("page_" + e.page + "_light_" + e.light + ".bmp");
                } catch (General ex) {
                } catch (IOException ex) {
                }
            }
        });
        //----------------------------------------------------------------------

        pr.addEventListener(new ScanFinished() {

            public void onScanFinished(PageEventArgs e) {
                System.out.println("Page scanned. Page: " + e.page + " Status: " + e.getStatus());
            }
        });
        //----------------------------------------------------------------------

        pr.addEventListener(new DocFrameFound() {

            public void onDocFrameFound(PageEventArgs e) {
                System.out.println("Document frame found. Page: " + e.page);
            }
        });
        //----------------------------------------------------------------------

        pr.addEventListener(new PresenceStateChanged() {

            public void onStateChanged(DetectionEventArgs e) {
                if (e.state == Pr22.Util.PresenceState.Present) {
                    docPresent = true;
                }
            }
        });
    }
    //--------------------------------------------------------------------------

    public static void main(String[] args) {
        try {
            Pr03_scanning prog = new Pr03_scanning();
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
