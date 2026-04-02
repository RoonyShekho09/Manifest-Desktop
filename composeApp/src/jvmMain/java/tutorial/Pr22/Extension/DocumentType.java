package Pr22.Extension;

/**
 * Document identifier name converter.
 */
public class DocumentType {

    /**
     * Returns the name of a general document.
     * @param id The document code.
     * @return The name of a general document.
     */
    public static String getDocumentName(int id) {
        String[] values = {
            "Unknown document",
            "ICAO standard Passport (MRP)",
            "ICAO standard 2 row Travel Document (TD-2)",
            "ICAO standard 3 row Travel Document (TD-1)",
            "ICAO standard visa (MRV-A) (MRV-B)",
            "French ID card",
            "Pre ICAO standard 3 row Travel Document",
            "Slovak ID card",
            "AAMVA standard driving license",
            "Belgian ID",
            "Swiss driving license",
            "ID of Cote d'Ivoire",
            "Financial Transaction Card",
            "IATA boarding pass",
            "ICAO Travel Document (TD-1, front page, named)",
            "ICAO Travel Document (TD-1, front page, typed)",
            "ISO standard driving license",
            "Mail item",
            "ICAO standard electronic document (Passport/ID)",
            "EID",
            "ESign",
            "NFC",
            "European standard driving license",
            "Portuguese ID",
            "Ecuadorian ID",
            "ID card with MRZ",
            "USA military ID",
            "Vehicle Registration Document"
        };
        return id < 0 || id >= values.length ? "" : values[id];
    }

    /**
     * Returns the document type name.
     * @param doc_type Document type identifier string.
     * @return The name of the document type.
     */
    public static String getDocTypeName(String doc_type) {
        if (doc_type.startsWith("DL")) {
            if (doc_type.equals("DLL")) {
                return "driving license for learner";
            } else {
                return "driving license";
            }
        } else if (doc_type.startsWith("ID")) {
            if (doc_type.equals("IDF")) {
                return "ID card for foreigner";
            } else if (doc_type.equals("IDC")) {
                return "ID card for children";
            } else {
                return "ID card";
            }
        } else if (doc_type.startsWith("PP")) {
            if (doc_type.equals("PPD")) {
                return "diplomatic passport";
            } else if (doc_type.equals("PPS")) {
                return "service passport";
            } else if (doc_type.equals("PPE")) {
                return "emergency passport";
            } else if (doc_type.equals("PPC")) {
                return "passport for children";
            } else {
                return "passport";
            }
        } else if (doc_type.startsWith("TD")) {
            return "travel document";
        } else if (doc_type.startsWith("RP")) {
            return "residence permit";
        } else if (doc_type.startsWith("VS")) {
            return "visa";
        } else if (doc_type.startsWith("WP")) {
            return "work permit";
        } else if (doc_type.startsWith("SI")) {
            return "social insurance document";
        } else {
            return "document";
        }
    }

    /**
     * Returns the page name.
     * @param doc_page Document page identifier.
     * @return Document page name.
     */
    public static String getPageName(String doc_page) {
        if (doc_page.equals("F")) {
            return "front";
        } else if (doc_page.equals("B")) {
            return "back";
        }
        return "";
    }
}
