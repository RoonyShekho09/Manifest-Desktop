/* This example shows how to generate document type string.
 */

import Pr22.DocumentReaderDevice;
import Pr22.Engine;
import Pr22.Events.*;
import Pr22.Processing.*;
import PrIns.Exceptions.EntryNotFound;
import PrIns.Exceptions.General;
import java.io.IOException;
import java.util.List;

public class Pr05_doctype {

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

        addScanEvents();

        Pr22.DocScanner scanner = pr.getScanner();
        Engine ocrEngine = pr.getEngine();

        System.out.println("Scanning some images to read from.");
        Pr22.Task.DocScannerTask scanTask = new Pr22.Task.DocScannerTask();
        //For OCR (MRZ) reading purposes, IR (infrared) image is recommended.
        scanTask.add(Pr22.Imaging.Light.White).add(Pr22.Imaging.Light.Infra);
        Page docPage = scanner.scan(scanTask, Pr22.Imaging.PagePosition.First);
        System.out.println();

        System.out.println("Reading all the field data.");
        Pr22.Task.EngineTask ReadingTask = new Pr22.Task.EngineTask();
        //Specify the fields we would like to receive
        ReadingTask.add(FieldSource.Viz, FieldId.All);

        Document ocrDoc = ocrEngine.analyze(docPage, ReadingTask);

        System.out.println();
        System.out.println("Document code: " + ocrDoc.toVariant().toInt());
        System.out.println("Document type: " + getDocType(ocrDoc));
        System.out.println("Status: " + ocrDoc.getStatus().toString());

        pr.close();
        return 0;
    }
    //--------------------------------------------------------------------------

    static String getFieldValue(Pr22.Processing.Document doc, Pr22.Processing.FieldId id) {
        try {
            FieldReference filter = new FieldReference(FieldSource.All, id);
            List<FieldReference> fields = doc.getFields(filter);
            for (FieldReference fieldRef : fields) {
                try {
                    String value = doc.getField(fieldRef).getBestStringValue();
                    if (!value.isEmpty()) {
                        return value;
                    }
                } catch (EntryNotFound ex) {
                }
            }
        } catch (Exception e) {
        }
        return "";
    }
    //--------------------------------------------------------------------------

    static String getDocType(Document OcrDoc) throws General {
        String documentTypeName;

        int documentCode = OcrDoc.toVariant().toInt();
        documentTypeName = Pr22.Extension.DocumentType.getDocumentName(documentCode);

        if (documentTypeName.isEmpty()) {
            String issue_country = getFieldValue(OcrDoc, FieldId.IssueCountry);
            String issue_state = getFieldValue(OcrDoc, FieldId.IssueState);
            String doc_type = getFieldValue(OcrDoc, FieldId.DocType);
            String doc_page = getFieldValue(OcrDoc, FieldId.DocPage);
            String doc_subtype = getFieldValue(OcrDoc, FieldId.DocTypeDisc);

            String tmpval = Pr22.Extension.CountryCode.getName(issue_country);
            if (!tmpval.isEmpty()) {
                issue_country = tmpval;
            }

            documentTypeName = new StrCon().add(issue_country).add(issue_state).
                    add(Pr22.Extension.DocumentType.getDocTypeName(doc_type)).
                    addCon("-").add(Pr22.Extension.DocumentType.getPageName(doc_page)).
                    addCon(",").add(doc_subtype).toString();

        }
        return documentTypeName;
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
            Pr05_doctype prog = new Pr05_doctype();
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

    /**
     * This class makes string concatenation with spaces and prefixes.
     */
    public static class StrCon {

        String fstr = "";
        String cstr = "";

        public StrCon() {
        }

        public StrCon add(String str) {
            if (!str.isEmpty()) {
                str = cstr + str;
            }
            if (!fstr.isEmpty() && !str.isEmpty() && str.charAt(0) != ',') {
                fstr += " ";
            }
            fstr += str;
            cstr = "";
            return this;
        }

        public StrCon addCon(String str) {
            cstr = str + " ";
            return this;
        }

        @Override
        public String toString() {
            return fstr;
        }
    }
}
