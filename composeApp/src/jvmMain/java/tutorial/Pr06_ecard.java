/* This example shows how to read and process data from ECards.
 * After ECard selection before reading some authentication process have to
 * be called for accessing the data files.
 */

import Pr22.DocumentReaderDevice;
import Pr22.ECard;
import Pr22.ECardHandling.AuthProcess;
import Pr22.ECardHandling.File;
import Pr22.ECardHandling.FileId;
import Pr22.ECardReader;
import Pr22.Events.*;
import Pr22.Imaging.RawImage;
import Pr22.Imaging.Light;
import Pr22.Imaging.PagePosition;
import Pr22.Processing.*;
import Pr22.Util.CompatEnum;
import PrIns.Exceptions.General;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Pr06_ecard {

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

    /** Match filename to file filter
     *
     * @param entry
     * @param mask
     * @return
     */
    public boolean match(String entry, String mask) {
        if (entry.equals(mask)) {
            return true;
        }
        int s = 0, m = 0;
        while (s < entry.length() && m < mask.length()) {
            if (entry.charAt(s) == mask.charAt(m) || mask.charAt(m) == '?') {
                // Continue on character match or on ? wildcard
                s++;
                m++;
            } else if (mask.charAt(m) == '*') {
                // Check for * wildcard, recursively for each possible matching
                while (m < mask.length() && mask.charAt(m) == '*') {
                    m++;
                }
                if (m == mask.length()) {
                    return true;
                }
                while (s < entry.length() && entry.charAt(s) != mask.charAt(m)) {
                    s++;
                }
                if (s == entry.length()) {
                    return false;
                }
                if (match(entry.substring(s), mask.substring(m))) {
                    return true;
                }
                s++;
                m--;
            } else {
                return false;
            }
        }
        while (m < mask.length() && mask.charAt(m) == '*') {
            m++;
        }
        return s == entry.length() && m == mask.length();
    }
    //--------------------------------------------------------------------------

    /**
     * Returns a list of files in a directory.
     * @param dirname
     * @param mask
     * @return
     */
    public List<String> fileList(String dirname, String mask) {
        List<String> list = new ArrayList<String>();
        try {
            java.io.File dir = new java.io.File(dirname);

            for (java.io.File entry : dir.listFiles()) {
                String newItem = dirname + java.io.File.separator + entry.getName();
                if (entry.isDirectory()) {
                    list.addAll(fileList(newItem, mask));
                } else if (entry.isFile()) {
                    if (match(entry.getName(), mask)) {
                        list.add(newItem);
                    }
                }
            }
        } catch (RuntimeException e) {
        }
        return list;
    }
    //----------------------------------------------------------------------

    /**
     * Loads certificates from a directory.
     * @param dir
     */
    public void loadCertificates(String dir) {
        String[] exts = {"*.cer", "*.crt", "*.der", "*.pem", "*.crl", "*.cvcert", "*.ldif", "*.ml"};
        int cnt = 0;

        for (String ex : exts) {
            List<String> list = fileList(dir, ex);
            for (String file : list) {
                try {
                    BinData fd = new BinData().load(file);
                    String pk = null;
                    if (ex.equals("*.cvcert")) {
                        //Searching for private key
                        pk = file.substring(0, file.lastIndexOf('.') + 1) + "pkcs8";
                        if (!new java.io.File(pk).isFile()) {
                            pk = null;
                        }
                    }
                    if (pk == null) {
                        pr.getCertificateManager().load(fd);
                        System.out.println("Certificate " + file + " is loaded.");
                    } else {
                        pr.getCertificateManager().load(fd, new BinData().load(pk));
                        System.out.println("Certificate " + file + " is loaded with private key.");
                    }
                    ++cnt;
                } catch (Exception e) {
                    System.out.println("Loading certificate " + file + " is failed!");
                }
            }
        }
        if (cnt == 0) {
            System.out.println("No certificates loaded from " + dir);
        }
        System.out.println();
    }
    //--------------------------------------------------------------------------

    /** Does an authentication after collecting the necessary information.
     *
     * @param selectedCard
     * @param currentAuth
     * @return
     * @throws General
     */
    public boolean authenticate(ECard selectedCard, CompatEnum<AuthProcess> currentAuth) throws General {

        BinData additionalAuthData = null;
        int selector = 0;
        switch (currentAuth.value) {
            case BAC:
            case PACE:
            case BAP:
                //Read MRZ (necessary for BAC, PACE and BAP)
                Pr22.Task.DocScannerTask scanTask = new Pr22.Task.DocScannerTask();
                scanTask.add(Light.Infra);
                Page FirstPage = pr.getScanner().scan(scanTask, PagePosition.First);

                Pr22.Task.EngineTask MrzReadingTask = new Pr22.Task.EngineTask();
                MrzReadingTask.add(FieldSource.Mrz, FieldId.All);
                Document MrzDoc = pr.getEngine().analyze(FirstPage, MrzReadingTask);

                additionalAuthData = new BinData().setString(MrzDoc.getField(FieldSource.Mrz, FieldId.All).getRawStringValue());
                selector = 1;
                break;

            case Passive:
            case Terminal:

                //Load the certificates if not done yet
                break;

            case SelectApp:
                if (!selectedCard.getApplications().isEmpty()) {
                    selector = selectedCard.getApplications().get(0).getNumId();
                }
                break;
        }
        try {
            selectedCard.authenticate(currentAuth, additionalAuthData, selector);
            System.out.println("- " + currentAuth + " authentication succeeded");
            return true;
        } catch (General e) {
            System.out.println("- " + currentAuth + " authentication failed: " + e.getMessage());
            return false;
        }
    }
    //--------------------------------------------------------------------------

    public int program() throws General {

        //Devices can be manipulated only after opening.
        if (open() != 0) {
            return 1;
        }

        //Please set the appropriate path
        loadCertificates(pr.getProperty("rwdata_dir") + "\\certs");

        ArrayList<ECardReader> cardReaders = pr.getReaders();

        //connecting to the 1st card of any reader
        ECard selectedCard = null;
        int CardCount = 0;
        System.out.println("Detected readers and cards:");
        for (ECardReader reader : cardReaders) {

            System.out.println("\tReader: " + reader.getInfo().getHwType());
            ArrayList<String> cards = reader.getCards();
            if (selectedCard == null && !cards.isEmpty()) {
                selectedCard = reader.connectCard(0);
            }
            for (String card : cards) {
                System.out.println("\t\t(" + CardCount++ + ")card: " + card);
            }
            System.out.println();
        }
        if (selectedCard == null) {
            System.out.println("No card selected!");
            return 1;
        }

        System.out.println("Executing authentications:");
        CompatEnum<AuthProcess> currentAuth = selectedCard.getNextAuthentication(false);
        boolean PassiveAuthImplemented = false;

        while (currentAuth.value != AuthProcess.None) {
            if (currentAuth.value == AuthProcess.Passive) {
                PassiveAuthImplemented = true;
            }
            boolean authOk = authenticate(selectedCard, currentAuth);
            currentAuth = selectedCard.getNextAuthentication(!authOk);
        }
        System.out.println();

        System.out.println("Reading data:");
        ArrayList<File> filesOnSelectedCard = selectedCard.getFiles();
        if (PassiveAuthImplemented) {
            filesOnSelectedCard.add(new File(FileId.CertDS));
            filesOnSelectedCard.add(new File(FileId.CertCSCA));
        }
        for (File file : filesOnSelectedCard) {
            try {
                System.out.print("File: " + file + ".");
                BinData RawFileData = selectedCard.getFile(file);
                try {
                    RawFileData.save(file + ".dat");
                } catch (IOException ex) {
                }
                Document fileData = pr.getEngine().analyze(RawFileData);
                try {
                    fileData.save(Document.FileFormat.Xml).save(file + ".xml");
                } catch (IOException ex) {
                }

                //Executing mandatory data integrity check for Passive Authentication
                if (PassiveAuthImplemented) {
                    File f = file;
                    if (f.id >= FileId.GeneralData.id) {
                        f = selectedCard.convertFileId(f);
                    }
                    if (f.id >= 1 && f.id <= 16) {
                        System.out.print(" hash check...");
                        System.out.print(selectedCard.checkHash(f) ? "OK" : "failed");
                    }
                }
                System.out.println();
                printDocFields(fileData);
            } catch (General e) {
                System.out.println(" Reading failed : " + e.getMessage());
            }
            System.out.println();
        }

        System.out.println("Authentications:");
        Document authData = selectedCard.getAuthResult();
        try {
            authData.save(Document.FileFormat.Xml).save("AuthResult.xml");
        } catch (IOException ex) {
        }
        printDocFields(authData);
        System.out.println();

        selectedCard.disconnect();

        pr.close();
        return 0;
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
//                    for (int cnt = 0; cnt < binValue.length; cnt += 16) {
//                        System.out.println(printBinary(binValue, cnt, 16));
//                    }
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

    public static void main(String[] args) {

        try {
            Pr06_ecard prog = new Pr06_ecard();
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
