/* This example shows the main capabilities of the image processing analyzer function.
 */
package tutorial;

import Pr22.DocumentReaderDevice;
import Pr22.Engine;
import Pr22.Events.*;
import Pr22.Imaging.RawImage;
import Pr22.Processing.*;
import PrIns.Exceptions.General;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Pr04_analyze {

    DocumentReaderDevice pr = null;

    //--------------------------------------------------------------------------
    /**
     * Opens the first document reader device.
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

        // Devices can be manipulated only after opening.
        if (open() != 0) {
            return 1;
        }

        addScanEvents();

        Pr22.DocScanner scanner = pr.getScanner();
        Engine ocrEngine = pr.getEngine();

        System.out.println("Scanning some images to read from.");
        Pr22.Task.DocScannerTask scanTask = new Pr22.Task.DocScannerTask();
        //For OCR (MRZ) reading purposes, IR (infrared) image is recommended.
        scanTask.add(Pr22.Imaging.Light.White).add(Pr22.Imaging.Light.Infra);
        Page docPage = scanner.scan(scanTask, Pr22.Imaging.PagePosition.First);
        System.out.println();

        System.out.println("Reading all the field data of the Machine Readable Zone.");
        Pr22.Task.EngineTask mrzReadingTask = new Pr22.Task.EngineTask();
        //Specify the fields we would like to receive.
        mrzReadingTask.add(FieldSource.Mrz, FieldId.All);
        Document mrzDoc = ocrEngine.analyze(docPage, mrzReadingTask);

        System.out.println();
        printDocFields(mrzDoc);
        //Returned fields by the Analyze function can be saved to an XML file:
        try {
            mrzDoc.save(Document.FileFormat.Xml).save("MRZ.xml");
        } catch (IOException e) {
        }

        System.out.println("Scanning more images for VIZ reading and image authentication.");
        //Reading from VIZ -except face photo is available in special OCR engines only.
        scanTask.add(Pr22.Imaging.Light.All);
        docPage = scanner.scan(scanTask, Pr22.Imaging.PagePosition.Current);
        System.out.println();

        System.out.println("Reading all the textual and graphical field data as well as "
                + "authentication result from the Visual Inspection Zone.");
        Pr22.Task.EngineTask vizReadingTask = new Pr22.Task.EngineTask();
        vizReadingTask.add(FieldSource.Viz, FieldId.All);
        Document vizDoc = ocrEngine.analyze(docPage, vizReadingTask);

        System.out.println();
        printDocFields(vizDoc);
        try {
            vizDoc.save(Document.FileFormat.Xml).save("VIZ.xml");
        } catch (IOException e) {
        }

        System.out.println("Reading barcodes.");
        Pr22.Task.EngineTask bcReadingTask = new Pr22.Task.EngineTask();
        bcReadingTask.add(FieldSource.Barcode, FieldId.All);
        Document bcrDoc = ocrEngine.analyze(docPage, bcReadingTask);

        System.out.println();
        printDocFields(bcrDoc);
        try {
            bcrDoc.save(Document.FileFormat.Xml).save("BCR.xml");
        } catch (IOException e) {
        }
        pr.close();
        return 0;
    }
    //--------------------------------------------------------------------------

    /**
     * Prints a hexa dump line from a part of an array into a string.
     *
     * @param arr The whole array.
     * @param pos Position of the first item to print.
     * @param sz Number of items to print.
     */
    static String printBinary(byte[] arr, int pos, int sz) {

        int p0;
        String str = "", str2 = "";
        for (p0 = pos; p0 < arr.length && p0 < pos + sz; p0++) {
            str += String.format("%02X", arr[p0]) + " ";
            str2 += arr[p0] < 0x21 || arr[p0] > 0x7e ? '.' : (char) arr[p0];
        }
        for (; p0 < pos + sz; p0++) {
            str += "   ";
            str2 += " ";
        }
        return str + str2;
    }
    //--------------------------------------------------------------------------

    /**
     * Prints out all fields of a document structure to console.
     *
     * Values are printed in three different forms: raw, formatted and standardized.
     * Status (checksum result) is printed together with fieldname and raw value.
     * At the end, images of all fields are saved into png format.
     * @param doc
     */
    static void printDocFields(Document doc) {

        List<FieldReference> fields = doc.getFields();

        System.out.printf("  %1$-20s%2$-17s%3$s%n", "FieldId", "Status", "Value");
        System.out.printf("  %1$-20s%2$-17s%3$s%n", "-------", "------", "-----");
        System.out.println();

        for (FieldReference currentFieldRef : fields) {
            try {
                Field currentField = doc.getField(currentFieldRef);
                String value = "", formattedValue = "", standardizedValue = "";
                byte[] binValue = null;
                try {
                    value = currentField.getRawStringValue();
                } catch (PrIns.Exceptions.EntryNotFound e) {
                } catch (PrIns.Exceptions.InvalidParameter e) {
                    binValue = currentField.getBinaryValue();
                }
                try {
                    formattedValue = currentField.getFormattedStringValue();
                } catch (PrIns.Exceptions.EntryNotFound e) {
                }
                try {
                    standardizedValue = currentField.getStandardizedStringValue();
                } catch (PrIns.Exceptions.EntryNotFound e) {
                }
                Status status = currentField.getStatus();
                String fieldName = currentFieldRef.toString();
                if (binValue != null) {
                    System.out.printf("  %1$-20s%2$-17sBinary%n", fieldName, status);
                    for (int cnt = 0; cnt < binValue.length; cnt += 16) {
                        System.out.println(printBinary(binValue, cnt, 16));
                    }
                } else {
                    System.out.printf("  %1$-20s%2$-17s[%3$s]%n", fieldName, status, value);
                    System.out.printf("\t%2$-31s[%1$s]%n", formattedValue, "   - Formatted");
                    System.out.printf("\t%2$-31s[%1$s]%n", standardizedValue, "   - Standardized");
                }

                ArrayList<Checking> lst = currentField.getDetailedStatus();
                for (Checking chk : lst) {
                    System.out.println(chk);
                }

                try {
                    currentField.getImage().save(RawImage.FileFormat.Png).save(fieldName + ".png");
                } catch (Exception e) {
                }
            } catch (Exception e) {
            }
        }
        System.out.println();

        for (FieldCompare comp : doc.getFieldCompareList()) {
            System.out.println("Comparing " + comp.field1 + " vs. "
                    + comp.field2 + " results " + comp.confidence);
        }
        System.out.println();
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
    }
    //--------------------------------------------------------------------------

    public static void main(String[] args) {
        try {
            Pr04_analyze prog = new Pr04_analyze();
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
