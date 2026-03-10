
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import Pr22.ECardHandling.AuthLevel;
import Pr22.ECardHandling.FileId;
import Pr22.Events.*;
import Pr22.Processing.BinData;
import Pr22.Processing.FieldId;
import Pr22.Processing.FieldReference;
import Pr22.Processing.FieldSource;
import PrIns.Exceptions.*;

public class ReadCard {

    private JFrame frmReadCard;
    private JTextArea textFieldMrz;
    private Pr22.DocumentReaderDevice pr;
    private boolean deviceIsConnected;
    private Pr22.Task.TaskControl readCtrl;
    private Pr22.ECard card;
    private CheckedListBox checkedListBoxCardReaders;
    private CheckedListBox checkedListBoxFiles;
    private JButton btnConnect;
    private JButton btnDisconnect;
    private JButton btnRead;
    private Color darkGreen;
    private Color darkYellow;
    private Pr22.Processing.Document vizResult;
    private Pr22.Processing.Document faceDoc;
    private DefaultListModel listModelDevices = new DefaultListModel();
    private JList listDevices = new JList(listModelDevices);
    private JLabel lblBAC;
    private JLabel lblCa;
    private JLabel lblPa;
    private JLabel lblMrz;
    private JLabel lblPace;
    private JLabel lblTa;
    private JLabel lblAa;
    private JLabel lblFace;
    private JComboBox comboBoxAuthSel;
    private JTextArea textArea1;
    private JLabel lblSignPicBox1;
    private JLabel lblSignPicBox2;
    private JLabel lblPicBox1;
    private JLabel lblPicBox2;
    private JLabel lblFingerPicBox1;
    private JLabel lblFingerPicBox2;

    class PopUpMenu extends JPopupMenu {

        private static final long serialVersionUID = 1L;
        private JMenuItem item;

        public PopUpMenu() {
            item = new JMenuItem("Save");
            add(item);
        }
    }

    public class CheckedListBox {

        private JTable listBoxTable;
        private JScrollPane listBoxScrollPane;
        private DefaultTableModel listBoxTableModel;
        private Color[] tableRowColors;
        private int tableMaxLength;

        public final void addTableRow(final boolean check, final String text) {
            getTableModel().addRow(new Object[]{check, text});
            try {
                if (tableMaxLength > getRowCount() - 1) {
                    setRowColor(getRowCount() - 1, Color.BLACK);
                }
            } catch (General ex) {
            }
        }

        public final boolean isRowChecked(final int row) {
            return getTableModel().getValueAt(row, 0).equals(true);
        }

        public final String getRowText(final int row) {
            return getTableModel().getValueAt(row, 1).toString();
        }

        public final void removeAllRows() {
            for (int i = getTableModel().getRowCount() - 1; i > -1; i--) {
                getTableModel().removeRow(i);
            }
        }

        public final void removeRow(final int row) {
            getTableModel().removeRow(row);
        }

        public final int getRowCount() {
            return getTableModel().getRowCount();
        }

        private DefaultTableModel getTableModel() {
            return listBoxTableModel;
        }

        private JTable getTable() {
            return listBoxTable;
        }

        public final Color getRowColor(final int row) throws General {
            if (tableMaxLength > row && row > -1) {
                return tableRowColors[row];
            } else {
                throw new DataOutOfRange("Row index out of range");
            }
        }

        public final void setRowColor(final int row, final Color color) throws General {
            if (row < 0) {
                for (int cnt = 0; cnt < getRowCount(); cnt++) {
                    tableRowColors[cnt] = color;
                }
            } else {
                if (tableMaxLength > row) {
                    tableRowColors[row] = color;
                } else {
                    throw new DataOutOfRange("Row index out of range");
                }
            }

            getTableModel().fireTableDataChanged();
        }

        final int findItem(final String str) {
            for (int cnt = 0; cnt < getRowCount(); cnt++) {
                if (getRowText(cnt).equals(str)) {
                    return cnt;
                }
            }
            return -1;
        }

        private class PopClickListener extends MouseAdapter {

            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.getButton() > MouseEvent.BUTTON2) {
                    try {
                        stripMenu(getTable().rowAtPoint(e.getPoint()));
                    } catch (General ex) {
                    }
                }
            }
        }

        // It needs JScrollPane creation and bounds set beforehand. (With the form designer for example)
        public CheckedListBox(final JScrollPane scrollpane, final int maxLength) {
            tableMaxLength = maxLength;

            tableRowColors = new Color[tableMaxLength];

            listBoxScrollPane = scrollpane;

            listBoxTable = new JTable() {

                private static final long serialVersionUID = 1L;

                @Override
                public boolean isCellEditable(final int row, final int column) {
                    return (column == 0);
                }

                @Override
                public Component prepareRenderer(final TableCellRenderer renderer,
                        final int row, final int col) {
                    Component comp = super.prepareRenderer(renderer, row, col);

                    if (tableMaxLength > row) {
                        comp.setForeground(tableRowColors[row]);
                    }

                    return comp;
                }
            };

            listBoxTable.setRowSelectionAllowed(true);
            listBoxTable.setShowGrid(false);
            listBoxTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            listBoxTable.setModel(new DefaultTableModel(new Object[][]{ // {Boolean.FALSE, "text"}
            }, new String[]{"", ""}) {

                private static final long serialVersionUID = 1L;
                private Class[] columnTypes = new Class[]{Boolean.class, Object.class};

                @Override
                public Class getColumnClass(final int columnIndex) {
                    return columnTypes[columnIndex];
                }
            });
            listBoxScrollPane.setColumnHeaderView(listBoxTable);

            listBoxTableModel = (DefaultTableModel) listBoxTable.getModel();

            listBoxScrollPane.setViewportView(listBoxTable);
            listBoxScrollPane.getViewport().setBackground(Color.WHITE);

            listBoxTable.addMouseListener(new PopClickListener());

            listBoxTable.getTableHeader().setReorderingAllowed(false);

            listBoxTable.getColumnModel().getColumn(0).setPreferredWidth(20);
            listBoxTable.getColumnModel().getColumn(1).setPreferredWidth(listBoxScrollPane.getWidth() - 20);
        }

        // It needs JScrollPane creation and bounds set beforehand. (With the form designer for example)
        public CheckedListBox(final JScrollPane scrollpane) {
            this(scrollpane, 0);
        }
    }

    public class OpenFileFilter extends javax.swing.filechooser.FileFilter {

        private String description = "";
        private String fileExt = "";

        public OpenFileFilter(final String extension) {
            fileExt = extension;
        }

        public OpenFileFilter(final String extension, final String typeDescription) {
            fileExt = extension;
            this.description = typeDescription;
        }

        public final boolean accept(final java.io.File f) {
            if (f.isDirectory()) {
                return true;
            }
            return (f.getName().toLowerCase().endsWith("." + fileExt));
        }

        public final String getDescription() {
            return description;
        }

        public final String getExtension() {
            return fileExt;
        }
    }

    /**
     * Launch the application.
     */
    public static void main(final String[] args) {

        EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    ReadCard window = new ReadCard();
                    window.frmReadCard.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ReadCard() throws General {

        initialize();

        try {
            pr = new Pr22.DocumentReaderDevice();
        } catch (UnsatisfiedLinkError ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
        } catch (General ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
        }
    }

    public final void addMainWindowEvents() {
        frmReadCard.addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(final WindowEvent arg0) {

                if (pr == null) {
                    arg0.getWindow().dispose();
                    return;
                }

                try {
                    addDeviceConnection();

                    addAuthBegin();
                    addAuthFinished();
                    addAuthWaitForInput();
                    addReadBegin();
                    addReadFinished();
                    addFileChecked();
                } catch (General ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                }

                for (FileId val : Pr22.ECardHandling.FileId.values()) {
                    checkedListBoxFiles.addTableRow(false, val.toString());
                }

                comboBoxAuthSel.setFont(new Font("Arial", Font.PLAIN, 11));
                for (AuthLevel level : AuthLevel.values()) {
                    comboBoxAuthSel.addItem(level.toString());
                }

                comboBoxAuthSel.setSelectedIndex(1);

                Properties mySettings = new Properties();
                try {
                    mySettings.load(new FileInputStream("ReadCard.config"));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                }

                loadCertificates(mySettings.getProperty("CertDir"));
            }

            @Override
            public void windowClosing(final WindowEvent arg0) {
                try {
                    if (readCtrl != null) {
                        readCtrl.Stop().Wait();
                    }
                    if (deviceIsConnected) {
                        pr.close();
                    }
                } catch (General ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                }
            }
        });
    }

    // <editor-fold desc="Connection">
    //--------------------------------------------------------------------------
    final void addDeviceConnection() throws General {
        pr.addEventListener(new Connection() {

            /*
             * This raises only when no device is used or when the currently used
             * device is disconnected.
             */
            @Override
            public void onConnection(final ConnectionEventArgs e) {

                try {
                    ArrayList<String> devices = Pr22.DocumentReaderDevice.getDeviceList();
                    listModelDevices.clear();
                    for (String str : devices) {
                        listModelDevices.addElement(str);
                    }
                } catch (General ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                }
            }
        });
    }

    final void addConnectCallback() {
        btnConnect.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent arg0) {

                if (listDevices.isSelectionEmpty()) {
                    return;
                }
                btnConnect.setEnabled(false);
                Component glassPane = frmReadCard.getGlassPane();
                glassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                glassPane.setVisible(true);

                try {
                    pr.useDevice(listDevices.getSelectedValue().toString());
                    deviceIsConnected = true;
                    btnDisconnect.setEnabled(true);

                    ArrayList<Pr22.ECardReader> readers = pr.getReaders();
                    for (Pr22.ECardReader reader : readers) {
                        checkedListBoxCardReaders.addTableRow(false, reader.getInfo().getHwType().toString());
                    }
                    btnRead.setEnabled(true);

                } catch (General ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                    disconnect();
                }
                glassPane.setVisible(false);
            }
        });
    }

    final void addDisconnectCallback() {
        btnDisconnect.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent arg0) {
                disconnect();
            }
        });
    }

    private void disconnect() {
        if (deviceIsConnected) {

            try {
                if (readCtrl != null) {
                    readCtrl.Stop().Wait();
                    readCtrl = null;
                }
                if (card != null) {
                    card.disconnect();
                    card = null;
                }
                pr.close();
            } catch (General ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
            }
            deviceIsConnected = false;
        }

        btnConnect.setEnabled(true);
        btnDisconnect.setEnabled(false);
        btnRead.setEnabled(false);

        checkedListBoxCardReaders.removeAllRows();

        textArea1.setText("");
    }

    // </editor-fold>
    // <editor-fold desc="Reading">
    //--------------------------------------------------------------------------
    final void addReadCallback() {
        btnRead.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent arg0) {
                textArea1.setText("");
                clearControls();

                if (readCtrl != null) {

                    try {
                        readCtrl.Wait();
                    } catch (General ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                    }
                    readCtrl = null;
                }
                if (card != null) {
                    try {
                        card.disconnect();
                    } catch (General ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                    }
                    card = null;
                }

                Pr22.ECardReader cardReader = null;
                for (int cnt = 0; cnt < checkedListBoxCardReaders.getRowCount(); cnt++) {
                    if (checkedListBoxCardReaders.isRowChecked(cnt)) {
                        try {
                            Pr22.ECardReader reader = pr.getReaders().get(cnt);

                            ArrayList<String> rgc = reader.getCards();
                            if (rgc.size() > 0) {
                                card = reader.connectCard(0);
                                cardReader = reader;
                                break;
                            }
                        } catch (General ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                        }
                    }
                }

                if (cardReader != null && card != null) {
                    try {
                        startReading(cardReader);
                    } catch (General ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                    }
                }
            }
        });
    }

    protected final void startReading(final Pr22.ECardReader cardReader) throws General {

        // New thread execution for immediate log update on UI
        new Thread() {

            @Override
            public void run() {
                clearControls();
                btnRead.setEnabled(false);

                logText("Scanning");
                Pr22.Task.DocScannerTask scanTask = new Pr22.Task.DocScannerTask();
                scanTask.add(Pr22.Imaging.Light.Infra).add(Pr22.Imaging.Light.White);
                Pr22.Processing.Page page = null;
                try {
                    page = pr.getScanner().scan(scanTask, Pr22.Imaging.PagePosition.First);
                } catch (General e) {
                }

                logText("Analyzing");
                Pr22.Task.EngineTask engineTask = new Pr22.Task.EngineTask();
                engineTask.add(FieldSource.Mrz, FieldId.All);
                engineTask.add(FieldSource.Viz, FieldId.CAN);

                Pr22.Processing.FieldReference faceFieldId = null, signatureFieldId = null;
                faceFieldId = new FieldReference(FieldSource.Viz, FieldId.Face);
                engineTask.add(faceFieldId);
                signatureFieldId = new FieldReference(FieldSource.Viz, FieldId.Signature);
                engineTask.add(signatureFieldId);
                try {
                    vizResult = pr.getEngine().analyze(page, engineTask);
                } catch (General e) {
                }

                faceDoc = null;

                try {
                    drawImage(lblPicBox2, vizResult.getField(faceFieldId).getImage().toImage());
                } catch (General ex) {
                }
                try {
                    drawImage(lblSignPicBox2, vizResult.getField(signatureFieldId).getImage().toImage());
                } catch (General ex) {
                }

                Pr22.Task.ECardTask task = new Pr22.Task.ECardTask();
                task.setAuthLevel(AuthLevel.valueOf(comboBoxAuthSel.getSelectedItem().toString()));

                for (int cnt = 0; cnt < checkedListBoxFiles.getRowCount(); cnt++) {
                    if (checkedListBoxFiles.isRowChecked(cnt)) {
                        String str = checkedListBoxFiles.getRowText(cnt);
                        task.add(Pr22.ECardHandling.FileId.valueOf(str));
                    }
                }

                try {
                    readCtrl = cardReader.startRead(card, task);
                } catch (General ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                }
            }
        }.start();
    }

    final void addAuthBegin() throws General {
        pr.addEventListener(new AuthBegin() {

            @Override
            public void onAuthBegin(final AuthEventArgs e) {
                logText("Auth Begin: " + e.authentication.toString());
                colorAuthLabel(e.authentication, darkYellow);
            }
        });
    }

    final void addAuthFinished() throws General {
        pr.addEventListener(new AuthFinished() {

            @Override
            public void onAuthFinished(final AuthEventArgs e) {
                String errstr = e.getResult().toString();
                if (e.getResult() == ErrorCodes.Unknown) {
                    errstr = String.format("%04X", e.result);
                }
                logText("Auth Done: " + e.authentication.toString() + " status: " + errstr);
                boolean ok = e.getResult() == ErrorCodes.ENOERR;
                colorAuthLabel(e.authentication, ok ? darkGreen : Color.RED);
            }
        });

    }

    final void addAuthWaitForInput() throws General {
        pr.addEventListener(new AuthWaitForInput() {

            @Override
            public void onAuthWaitForInput(final AuthEventArgs e) {

                try {
                    logText("Auth Wait For Input: " + e.authentication.toString());
                    colorAuthLabel(e.authentication, darkYellow);

                    Pr22.Processing.BinData authData = null;
                    int selector = 0;

                    switch (e.authentication.value) {
                        case BAC:
                        case BAP:
                        case PACE:
                            ArrayList<Pr22.Processing.FieldReference> authFields = null;
                            Pr22.Processing.FieldReference fr = null;
                            fr = new FieldReference(FieldSource.Mrz, FieldId.All);
                            authFields = vizResult.getFields(fr);
                            selector = 1;
                            if (authFields.isEmpty()) {
                                fr = new FieldReference(FieldSource.Viz, FieldId.CAN);
                                authFields = vizResult.getFields(fr);
                                selector = 2;
                            }
                            if (authFields.isEmpty()) {
                                break;
                            }

                            authData = new Pr22.Processing.BinData();
                            authData.setString(vizResult.getField(fr).getBestStringValue());
                            break;

                        default:
                            break;
                    }

                    card.authenticate(e.authentication, authData, selector);
                } catch (General ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                }
            }
        });
    }

    final void addReadBegin() throws General {
        pr.addEventListener(new ReadBegin() {

            @Override
            public void onReadBegin(final FileEventArgs e) {
                logText("Read Begin: " + e.fileId.toString());
            }
        });
    }

    final void addReadFinished() throws General {
        pr.addEventListener(new ReadFinished() {

            @Override
            public void onReadFinished(final FileEventArgs e) {
                String errstr = e.getResult().toString();
                if (e.getResult() == ErrorCodes.Unknown) {
                    errstr = String.format("%04X", e.result);
                }

                logText("Read End: " + e.fileId.toString() + " status: " + errstr);

                if (e.fileId.id == Pr22.ECardHandling.FileId.All.id) {
                    processAfterAllRead();
                    btnRead.setEnabled(true);
                } else if (e.getResult() != ErrorCodes.ENOERR) {
                    colorFileName(e.fileId, Color.RED);
                } else {
                    colorFileName(e.fileId, Color.BLUE);
                    processAfterFileRead(e.fileId);
                }
            }
        });
    }

    final void processAfterAllRead() {
        try {
            String mrz = vizResult.getField(FieldSource.Mrz, FieldId.All).getRawStringValue();
            String dg1 = textFieldMrz.getText();
            if (dg1.length() > 40) {
                colorLabel(lblMrz, (mrz.equals(dg1) ? darkGreen : Color.RED));
            }
        } catch (PrIns.Exceptions.EntryNotFound ex) {
        } catch (PrIns.Exceptions.General ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
        }

        try {
            Pr22.Processing.Document facecmp;
            facecmp = Pr22.Processing.Document.merge(vizResult, faceDoc);
            ArrayList<Pr22.Processing.FieldCompare> fcl = facecmp.getFieldCompareList();
            for (Pr22.Processing.FieldCompare fc : fcl) {
                if (fc.field1.getId() == FieldId.Face && fc.field2.getId() == FieldId.Face) {
                    Color col = darkYellow;
                    if (fc.confidence < 300) {
                        col = Color.RED;
                    } else if (fc.confidence > 600) {
                        col = darkGreen;
                    }
                    colorLabel(lblFace, col);
                }
            }
        } catch (PrIns.Exceptions.General ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
        }
    }

    final void processAfterFileRead(final Pr22.ECardHandling.File file) {
        try {
            Pr22.Processing.BinData rawFileContent = card.getFile(file);
            Pr22.Processing.Document fileDoc = pr.getEngine().analyze(rawFileContent);

            Pr22.Processing.FieldReference faceFieldId = new FieldReference(FieldSource.ECard, FieldId.Face);
            Pr22.Processing.FieldReference mrzFieldId = new FieldReference(FieldSource.ECard, FieldId.CompositeMrz);
            Pr22.Processing.FieldReference signatureFieldId = new FieldReference(FieldSource.ECard, FieldId.Signature);
            Pr22.Processing.FieldReference fingerFieldId = new FieldReference(FieldSource.ECard, FieldId.Fingerprint);

            if (fileDoc.getFields().contains(faceFieldId)) {
                faceDoc = fileDoc;
                drawImage(lblPicBox1, fileDoc.getField(faceFieldId).getImage().toImage());
            }
            if (fileDoc.getFields().contains(mrzFieldId)) {
                String mrz = fileDoc.getField(mrzFieldId).getRawStringValue();
                if (mrz.length() == 90) {
                    mrz = mrz.substring(0, 30) + "\n" + mrz.substring(30, 60) + "\n" + mrz.substring(60);
                } else if (mrz.length() > 50) {
                    mrz = mrz.substring(0, mrz.length() / 2) + "\n" + mrz.substring(mrz.length() / 2);
                }
                printMrzLines(mrz);
            }
            if (fileDoc.getFields().contains(signatureFieldId)) {
                drawImage(lblSignPicBox1, fileDoc.getField(signatureFieldId).getImage().toImage());
            }
            if (fileDoc.getFields().contains(fingerFieldId)) {
                try {
                    drawImage(lblFingerPicBox1, fileDoc.getField(FieldSource.ECard, FieldId.Fingerprint, 0).getImage().toImage());
                    drawImage(lblFingerPicBox2, fileDoc.getField(FieldSource.ECard, FieldId.Fingerprint, 1).getImage().toImage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                }
            }

        } catch (General ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
        }
    }

    final void addFileChecked() throws General {
        pr.addEventListener(new FileChecked() {

            @Override
            public void onFileChecked(final FileEventArgs e) {
                logText("File Checked: " + e.fileId.toString());
                boolean ok = (e.getResult() == ErrorCodes.ENOERR);
                colorFileName(e.fileId, (ok ? darkGreen : darkYellow));
            }
        });
    }

    // </editor-fold>
    // <editor-fold desc="General tools">
    //--------------------------------------------------------------------------
    final void colorFileName(Pr22.ECardHandling.File file, final Color color) {
        int i = checkedListBoxFiles.findItem(file.toString());

        try {
            if (i == -1) {
                file = card.convertFileId(file);
                i = checkedListBoxFiles.findItem(file.toString());
            }
            if (i != -1) {
                checkedListBoxFiles.setRowColor(i, color);
            }
        } catch (General ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
        }
    }

    final void stripMenu(final int selrow) throws General {
        if (card == null) {
            return;
        }

        Pr22.Processing.BinData filedata = null;
        if (checkedListBoxFiles.getRowColor(selrow) != Color.BLACK && checkedListBoxFiles.getRowColor(selrow) != Color.RED) {
            Pr22.ECardHandling.FileId file;
            String fileName = checkedListBoxFiles.getRowText(selrow);
            file = Pr22.ECardHandling.FileId.valueOf(fileName);

            JFileChooser fileChooser = new JFileChooser();
            OpenFileFilter xmlFilter = new OpenFileFilter("xml", "document file");
            OpenFileFilter binFilter = new OpenFileFilter("bin", "binary file");
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(binFilter);
            fileChooser.addChoosableFileFilter(xmlFilter);
            fileChooser.setSelectedFile(new java.io.File(fileName));

            if (fileChooser.showSaveDialog(frmReadCard) == JFileChooser.APPROVE_OPTION) {
                java.io.File resFile = fileChooser.getSelectedFile();
                OpenFileFilter selectedFilter = (OpenFileFilter) fileChooser.getFileFilter();
                fileName = resFile.getPath();
                if (!resFile.getName().contains(".")) {
                    fileName = fileName + "." + selectedFilter.getExtension();
                }

                try {
                    filedata = card.getFile(file);
                    if (selectedFilter.equals(binFilter)) {
                        filedata.save(fileName);
                    } else if (selectedFilter.equals(xmlFilter)) {
                        pr.getEngine().analyze(filedata).save(Pr22.Processing.Document.FileFormat.Xml).
                                save(fileName);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                } catch (General ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                }
            }
        }
    }

    final void loadCertificates(final String dir) {
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
                        pr.getGlobalCertificateManager().load(fd);
                        System.out.println("Certificate " + file + " is loaded.");
                    } else {
                        pr.getGlobalCertificateManager().load(fd, new BinData().load(pk));
                        System.out.println("Certificate " + file + " is loaded with private key.");
                    }
                    ++cnt;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", 0);
                }
            }
        }
        if (cnt == 0) {
            System.out.println("No certificates loaded from " + dir);
        }
        System.out.println();
    }

    // </editor-fold>
    // <editor-fold desc="Display">
    //--------------------------------------------------------------------------
    final void logText(final String s) {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                textArea1.append(s + "\n");
            }
        });
    }

    final void printMrzLines(final String mrz) {
        textFieldMrz.setText(mrz);
    }

    final void colorAuthLabel(final Pr22.Util.CompatEnum<Pr22.ECardHandling.AuthProcess> auth, final Color color) {

        switch (auth.value) {
            case BAC:
            case BAP:
                colorLabel(lblBAC, color);
                break;
            case Active:
                colorLabel(lblAa, color);
                break;
            case Chip:
                colorLabel(lblCa, color);
                break;
            case PACE:
                colorLabel(lblPace, color);
                break;
            case Passive:
                colorLabel(lblPa, color);
                break;
            case Terminal:
                colorLabel(lblTa, color);
                break;
            default:
                return;
        }
    }

    final void colorLabel(final JLabel lbl, final Color col) {
        lbl.setForeground(col);
    }

    final void drawImage(final JLabel label, Image img) {
        if (img.getWidth(null) > label.getWidth() || img.getHeight(null) > label.getHeight()) {
            float fx = (float) label.getWidth() / (float) img.getWidth(null);
            float fy = (float) label.getHeight() / (float) img.getHeight(null);
            fx = Math.min(fx, fy);
            img = img.getScaledInstance((int) (img.getWidth(null) * fx), (int) (img.getHeight(null) * fx), Image.SCALE_FAST);
        }

        Rectangle rect = label.getBounds();
        int offsetX = label.getWidth() - img.getWidth(null);
        rect.x += offsetX / 2;
        //int offsetY = label.getHeight() - img.getHeight(null);
        //rect.y += offsetY / 2;                            //null layout used
        label.setBounds(rect);
        label.setIcon(new ImageIcon(img));
    }

    final void clearControls() {
        textFieldMrz.setText("");
        lblPicBox1.setIcon(null);
        lblPicBox2.setIcon(null);
        lblFingerPicBox1.setIcon(null);
        lblFingerPicBox2.setIcon(null);
        lblSignPicBox1.setIcon(null);
        lblSignPicBox2.setIcon(null);

        lblBAC.setForeground(Color.BLACK);
        lblCa.setForeground(Color.BLACK);
        lblPa.setForeground(Color.BLACK);
        lblMrz.setForeground(Color.BLACK);
        lblPace.setForeground(Color.BLACK);
        lblTa.setForeground(Color.BLACK);
        lblAa.setForeground(Color.BLACK);
        lblFace.setForeground(Color.BLACK);

        resetLabelPos();

        try {
            checkedListBoxFiles.setRowColor(-1, Color.BLACK); // If row is negative then all the rows will have the given color
        } catch (General ex) {
        }
    }

    final void resetLabelPos() {
        lblPicBox1.setBounds(10, 24, 182, 222);
        lblPicBox2.setBounds(188, 24, 182, 222);
        lblFingerPicBox1.setBounds(10, 22, 92, 107);
        lblFingerPicBox2.setBounds(10, 133, 92, 107);
        lblSignPicBox1.setBounds(10, 21, 360, 48);
        lblSignPicBox2.setBounds(10, 70, 360, 48);
    }

    // </editor-fold>
    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        darkGreen = new Color(85, 170, 0);
        darkYellow = new Color(206, 201, 6);

        frmReadCard = new JFrame();
        frmReadCard.setTitle("Read Card");
        frmReadCard.setBounds(100, 100, 811, 592);
        frmReadCard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmReadCard.getContentPane().setLayout(null);
        addMainWindowEvents();

        //left side
        {
            JPanel panelDevices = new JPanel();
            panelDevices.setBounds(10, 11, 269, 139);
            frmReadCard.getContentPane().add(panelDevices);
            panelDevices.setLayout(null);

            TitledBorder borderDev = new TitledBorder("Devices");
            borderDev.setTitleJustification(TitledBorder.LEFT);
            borderDev.setTitlePosition(TitledBorder.TOP);
            borderDev.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelDevices.setBorder(borderDev);

            listDevices.setBorder(new LineBorder(new Color(0, 0, 0)));
            listDevices.setBounds(10, 21, 249, 67);
            listDevices.setFont(new Font("Arial", Font.PLAIN, 11));
            panelDevices.add(listDevices);

            btnConnect = new JButton("Connect");
            btnConnect.setBounds(38, 99, 87, 23);
            btnConnect.setFont(new Font("Arial", Font.PLAIN, 11));
            panelDevices.add(btnConnect);
            addConnectCallback();

            btnDisconnect = new JButton("Disconnect");
            btnDisconnect.setBounds(145, 99, 93, 23);
            btnDisconnect.setEnabled(false);
            btnDisconnect.setFont(new Font("Arial", Font.PLAIN, 11));
            panelDevices.add(btnDisconnect);
            addDisconnectCallback();
        }

        {
            JPanel panelReaders = new JPanel();
            panelReaders.setBounds(10, 151, 269, 117);
            frmReadCard.getContentPane().add(panelReaders);
            panelReaders.setLayout(null);

            TitledBorder borderReader = new TitledBorder("Card Readers");
            borderReader.setTitleJustification(TitledBorder.LEFT);
            borderReader.setTitlePosition(TitledBorder.TOP);
            borderReader.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelReaders.setBorder(borderReader);

            JScrollPane scrollPaneCardReaders = new JScrollPane();
            scrollPaneCardReaders.setBounds(10, 21, 249, 85);
            panelReaders.add(scrollPaneCardReaders);

            checkedListBoxCardReaders = new CheckedListBox(scrollPaneCardReaders);
        }

        {
            JPanel panelFiles = new JPanel();
            panelFiles.setBounds(10, 270, 269, 199);
            frmReadCard.getContentPane().add(panelFiles);
            panelFiles.setLayout(null);

            TitledBorder borderFiles = new TitledBorder("Files");
            borderFiles.setTitleJustification(TitledBorder.LEFT);
            borderFiles.setTitlePosition(TitledBorder.TOP);
            borderFiles.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelFiles.setBorder(borderFiles);

            JScrollPane scrollPaneFiles = new JScrollPane();
            scrollPaneFiles.setBounds(10, 21, 249, 167);
            panelFiles.add(scrollPaneFiles);

            checkedListBoxFiles = new CheckedListBox(scrollPaneFiles, Pr22.ECardHandling.FileId.values().length);
        }

        {
            JLabel lblAuthenticationLevel = new JLabel("Authentication level:");
            lblAuthenticationLevel.setFont(new Font("Arial", Font.PLAIN, 11));
            lblAuthenticationLevel.setBounds(10, 480, 104, 19);
            frmReadCard.getContentPane().add(lblAuthenticationLevel);

            comboBoxAuthSel = new JComboBox();
            comboBoxAuthSel.setBounds(161, 479, 118, 20);
            frmReadCard.getContentPane().add(comboBoxAuthSel);

            btnRead = new JButton("Read");
            btnRead.setBounds(171, 510, 108, 29);
            btnRead.setEnabled(false);
            btnRead.setFont(new Font("Arial", Font.PLAIN, 11));
            frmReadCard.getContentPane().add(btnRead);
            addReadCallback();
        }

        //right side
        {
            JPanel panelFaceImages = new JPanel();
            panelFaceImages.setBounds(289, 11, 380, 257);
            frmReadCard.getContentPane().add(panelFaceImages);
            panelFaceImages.setLayout(null);

            TitledBorder borderFaces = new TitledBorder("Face images");
            borderFaces.setTitleJustification(TitledBorder.LEFT);
            borderFaces.setTitlePosition(TitledBorder.TOP);
            borderFaces.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelFaceImages.setBorder(borderFaces);

            lblPicBox1 = new JLabel("");
            panelFaceImages.add(lblPicBox1);

            lblPicBox2 = new JLabel("");
            panelFaceImages.add(lblPicBox2);
        }

        {
            JPanel panelFingerPrints = new JPanel();
            panelFingerPrints.setBounds(673, 11, 112, 257);
            frmReadCard.getContentPane().add(panelFingerPrints);
            panelFingerPrints.setLayout(null);

            TitledBorder borderFingerprints = new TitledBorder("Fingerprints");
            borderFingerprints.setTitleJustification(TitledBorder.LEFT);
            borderFingerprints.setTitlePosition(TitledBorder.TOP);
            borderFingerprints.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelFingerPrints.setBorder(borderFingerprints);

            lblFingerPicBox1 = new JLabel("");
            panelFingerPrints.add(lblFingerPicBox1);

            lblFingerPicBox2 = new JLabel("");
            panelFingerPrints.add(lblFingerPicBox2);
        }

        textFieldMrz = new JTextArea();
        textFieldMrz.setEditable(false);
        textFieldMrz.setBorder(new LineBorder(Color.BLACK));
        textFieldMrz.setFont(new Font("Courier New", Font.PLAIN, 14));
        textFieldMrz.setAlignmentY(JTextArea.CENTER_ALIGNMENT);
        textFieldMrz.setBounds(289, 279, 496, 63);
        frmReadCard.getContentPane().add(textFieldMrz);
        textFieldMrz.setColumns(10);

        {
            JPanel panelSignatures = new JPanel();
            panelSignatures.setBounds(289, 348, 380, 122);
            frmReadCard.getContentPane().add(panelSignatures);
            panelSignatures.setLayout(null);

            TitledBorder borderSignatures = new TitledBorder("Signatures");
            borderSignatures.setTitleJustification(TitledBorder.LEFT);
            borderSignatures.setTitlePosition(TitledBorder.TOP);
            borderSignatures.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelSignatures.setBorder(borderSignatures);

            lblSignPicBox1 = new JLabel("");
            panelSignatures.add(lblSignPicBox1);

            lblSignPicBox2 = new JLabel("");
            panelSignatures.add(lblSignPicBox2);
        }

        {
            JPanel panelAuth = new JPanel();
            panelAuth.setBounds(673, 348, 112, 122);
            frmReadCard.getContentPane().add(panelAuth);
            panelAuth.setLayout(null);

            TitledBorder borderAuth = new TitledBorder("Authentications");
            borderAuth.setTitleJustification(TitledBorder.LEFT);
            borderAuth.setTitlePosition(TitledBorder.TOP);
            borderAuth.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelAuth.setBorder(borderAuth);

            lblBAC = new JLabel("BAC");
            lblBAC.setFont(new Font("Arial", Font.PLAIN, 11));
            lblBAC.setBounds(10, 25, 28, 14);
            panelAuth.add(lblBAC);

            lblCa = new JLabel("CA");
            lblCa.setFont(new Font("Arial", Font.PLAIN, 11));
            lblCa.setBounds(10, 50, 28, 14);
            panelAuth.add(lblCa);

            lblPa = new JLabel("PA");
            lblPa.setFont(new Font("Arial", Font.PLAIN, 11));
            lblPa.setBounds(10, 75, 28, 14);
            panelAuth.add(lblPa);

            lblMrz = new JLabel("MRZ");
            lblMrz.setFont(new Font("Arial", Font.PLAIN, 11));
            lblMrz.setBounds(10, 100, 28, 14);
            panelAuth.add(lblMrz);

            lblPace = new JLabel("PACE");
            lblPace.setFont(new Font("Arial", Font.PLAIN, 11));
            lblPace.setBounds(70, 25, 28, 14);
            panelAuth.add(lblPace);

            lblTa = new JLabel("TA");
            lblTa.setFont(new Font("Arial", Font.PLAIN, 11));
            lblTa.setBounds(70, 50, 28, 14);
            panelAuth.add(lblTa);

            lblAa = new JLabel("AA");
            lblAa.setFont(new Font("Arial", Font.PLAIN, 11));
            lblAa.setBounds(70, 75, 28, 14);
            panelAuth.add(lblAa);

            lblFace = new JLabel("Face");
            lblFace.setFont(new Font("Arial", Font.PLAIN, 11));
            lblFace.setBounds(70, 100, 28, 14);
            panelAuth.add(lblFace);
        }

        JScrollPane scrollPaneTextArea1 = new JScrollPane();
        scrollPaneTextArea1.setBounds(289, 481, 496, 71);
        frmReadCard.getContentPane().add(scrollPaneTextArea1);

        textArea1 = new JTextArea();
        textArea1.setEditable(false);
        textArea1.setRows(4);
        scrollPaneTextArea1.setViewportView(textArea1);

        resetLabelPos();
    }

    public final boolean match(final String entry, final String mask) {
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

    public final List<String> fileList(final String dirname, final String mask) {
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
        } catch (RuntimeException ex) {
        }
        return list;
    }
}
