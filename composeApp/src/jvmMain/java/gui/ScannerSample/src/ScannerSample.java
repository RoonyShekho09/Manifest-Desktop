
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
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import Pr22.Events.*;
import Pr22.Processing.FieldId;
import Pr22.Processing.FieldReference;
import Pr22.Processing.FieldSource;
import Pr22.Util.CompatEnum;
import PrIns.Exceptions.*;

public class ScannerSample {

    private JFrame frmScannerSample;
    private Pr22.DocumentReaderDevice pr;
    private boolean deviceIsConnected;
    private Pr22.Task.TaskControl scanCtrl;
    private Pr22.Processing.Document analyzeResult;
    private DefaultListModel listModelDevices = new DefaultListModel();
    private JButton btnConnect;
    private JButton btnDisconnect;
    private JList listDevices = new JList(listModelDevices);
    private JButton btnStart;
    private CheckedListBox checkedListBoxImages;
    private DefaultTableModel modelOcr;
    private JLabel lblFieldImagePic;
    private JTabbedPane tabbedPaneOthers;
    private JLabel lblRawValue;
    private JLabel lblFormattedValue;
    private JLabel lblStandardizedValue;
    private JLabel lblName1Value;
    private JLabel lblName2Value;
    private JLabel lblBirthValue;
    private JLabel lblNationalityValue;
    private JLabel lblIssuerValue;
    private JLabel lblTypeValue;
    private JLabel lblNumberValue;
    private JLabel lblValidValue;
    private JLabel lblSexValue;
    private JLabel lblPageValue;
    private JLabel lblSignaturePic;
    private JLabel lblFacePhotoPic;
    private JCheckBox checkboxDocView;
    private JCheckBox checkboxMrz;
    private JCheckBox checkboxViz;
    private JCheckBox checkboxBcr;
    private JScrollPane scrollPaneOcr;
    private JTable tableOcr;

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

        private class PopClickListener extends MouseAdapter {

            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.getButton() > MouseEvent.BUTTON2) {
                }
            }
        }

        // It needs JScrollPane creation and bounds set. (With the form designer for example)
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

        // It needs JScrollPane creation and bounds set. (With the form designer for example)
        public CheckedListBox(final JScrollPane scrollpane) {
            this(scrollpane, 0);
        }
    }

    /**
     * Launch the application.
     */
    public static void main(final String[] args) {

        EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    ScannerSample window = new ScannerSample();
                    window.frmScannerSample.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     * @throws General
     * @throws IOException
     */
    public ScannerSample() throws General {

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
        frmScannerSample.addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(final WindowEvent arg0) {

                if (pr == null) {
                    arg0.getWindow().dispose();
                    return;
                }

                try {
                    addDeviceConnection();

                    addDocumentStateChanged();
                    addImageScanned();
                    addScanFinished();
                    addDocFrameFound();
                } catch (General ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                }
            }

            @Override
            public void windowClosing(final WindowEvent arg0) {

                if (deviceIsConnected) {
                    closeScan();
                    try {
                        pr.close();
                    } catch (General ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
                    }
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
                Component glassPane = frmScannerSample.getGlassPane();
                glassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                glassPane.setVisible(true);

                try {
                    pr.useDevice(listDevices.getSelectedValue().toString());
                    deviceIsConnected = true;
                    pr.getScanner().startTask(Pr22.Task.FreerunTask.detection());
                    btnDisconnect.setEnabled(true);

                    ArrayList<CompatEnum<Pr22.Imaging.Light>> lights = pr.getScanner().getInfo().getLights();
                    for (CompatEnum<Pr22.Imaging.Light> light : lights) {
                        checkedListBoxImages.addTableRow(false, light.toString());
                    }
                    btnStart.setEnabled(true);

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

    void disconnect() {
        if (deviceIsConnected) {

            closeScan();
            try {
                pr.close();
            } catch (General ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
            }
            deviceIsConnected = false;
        }

        btnConnect.setEnabled(true);
        btnDisconnect.setEnabled(false);
        btnStart.setEnabled(false);

        checkedListBoxImages.removeAllRows();

        int tabCount = tabbedPaneOthers.getTabCount();
        for (int i = tabCount - 1; i > 1; i--) {
            tabbedPaneOthers.removeTabAt(i);
        }

        for (int i = modelOcr.getRowCount() - 1; i > -1; i--) {
            modelOcr.removeRow(i);
        }

        clearOCRData();
        clearDataPage();
    }

    // </editor-fold>
    // <editor-fold desc="Scanning">
    //--------------------------------------------------------------------------
    final void addDocumentStateChanged() throws General {
        pr.addEventListener(new PresenceStateChanged() {

            /*
             * To raise this event FreerunTask.detection() has to be started.
             */
            @Override
            public void onStateChanged(final DetectionEventArgs e) {
                if (e.state == Pr22.Util.PresenceState.Present) {
                    startReading();
                }
            }
        });
    }

    final void addStartCallback() {
        btnStart.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent arg0) {
                startReading();
            }
        });
    }

    final void startReading() {
        int tabCount = tabbedPaneOthers.getTabCount();
        for (int i = tabCount - 1; i > 1; i--) {
            tabbedPaneOthers.removeTabAt(i);
        }

        int rowCount = modelOcr.getRowCount();
        for (int i = rowCount - 1; i > -1; i--) {
            modelOcr.removeRow(i);
        }

        clearOCRData();
        clearDataPage();
        btnStart.setEnabled(false);
        Pr22.Task.DocScannerTask scanTask = new Pr22.Task.DocScannerTask();
        for (int cnt = 0; cnt < checkedListBoxImages.getRowCount(); cnt++) {
            if (checkedListBoxImages.isRowChecked(cnt)) {
                String lightName = checkedListBoxImages.getRowText(cnt);
                addTabPage(lightName);
                scanTask.add(Pr22.Imaging.Light.valueOf(lightName));
            }
        }
        if (scanTask.lights.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No light selected to scan!", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            btnStart.setEnabled(true);
            return;
        }

        try {
            scanCtrl = pr.getScanner().startScanning(scanTask, Pr22.Imaging.PagePosition.First);
        } catch (General ex) {
        }
    }

    final void addImageScanned() throws General {
        pr.addEventListener(new ImageScanned() {

            @Override
            public void onImageScanned(final ImageEventArgs ev) {
                try {
                    drawScannedImage(ev);
                } catch (General ex) {
                } catch (IOException ex) {
                }
            }
        });
    }

    final void addDocFrameFound() throws General {
        pr.addEventListener(new DocFrameFound() {

            /*
             * To rotate the document to upside down direction the analyze()
             * should be called.
             */
            @Override
            public void onDocFrameFound(final PageEventArgs ev) {
                if (!checkboxDocView.isSelected()) {
                    return;
                }

                for (int i = 0; i < tabbedPaneOthers.getTabCount(); i++) {
                    try {
                        CompatEnum<Pr22.Imaging.Light> li = Pr22.Imaging.Light.createCompat(tabbedPaneOthers.getTitleAt(i));
                        drawScannedImage(new Pr22.Events.ImageEventArgs(this, ev.page, li));
                    } catch (General ex) {
                    } catch (IOException ex) {
                    } catch (IllegalArgumentException ex) {
                    }
                }
            }
        });
    }

    final void drawScannedImage(ImageEventArgs ev) throws General, IOException {
        Pr22.Imaging.DocImage docImage = pr.getScanner().getPage(ev.page).select(ev.light);

        JPanel comp = null;
        for (int i = 0; i < tabbedPaneOthers.getTabCount(); i++) {
            if (tabbedPaneOthers.getTitleAt(i).equals(ev.light.toString())) {
                comp = (JPanel) tabbedPaneOthers.getComponentAt(i);
                break;
            }
        }
        if (comp == null) {
            return;
        }
        comp.removeAll();

        JLabel lbl = new JLabel("");
        lbl.setBounds(0, 0, comp.getSize().width, comp.getSize().height);
        comp.add(lbl);

        Image imgTmp = docImage.toImage();
        if (checkboxDocView.isSelected()) {
            try {
                imgTmp = docImage.docView().toImage();
            } catch (General ex) {
            }
        }
        drawImage(lbl, imgTmp);
    }

    final void addScanFinished() throws General {
        pr.addEventListener(new ScanFinished() {

            @Override
            public void onScanFinished(final PageEventArgs ev) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        analyze();
                    }
                }).start();

                new Thread(new Runnable() { // It needs to run async. Otherwise scanCtrl.Wait() blocks main process execution!!

                    @Override
                    public void run() {
                        closeScan();
                    }
                }).start();
            }
        });
    }

    final void closeScan() {
        try {
            if (scanCtrl != null) {
                scanCtrl.Wait();
            }
        } catch (General ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
        }
        scanCtrl = null;
        btnStart.setEnabled(true);
    }

    // </editor-fold>
    // <editor-fold desc="Analyzing">
    //--------------------------------------------------------------------------
    final void analyze() {
        Pr22.Task.EngineTask ocrTask = new Pr22.Task.EngineTask();

        if (checkboxMrz.isSelected()) {
            ocrTask.add(FieldSource.Mrz, FieldId.All);
        }
        if (checkboxViz.isSelected()) {
            ocrTask.add(FieldSource.Viz, FieldId.All);
        }
        if (checkboxBcr.isSelected()) {
            ocrTask.add(FieldSource.Barcode, FieldId.All);
        }

        Pr22.Processing.Page page;
        try {
            page = pr.getScanner().getPage(0);
        } catch (PrIns.Exceptions.General ex) {
            return;
        }
        try {
            analyzeResult = pr.getEngine().analyze(page, ocrTask);
        } catch (General ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", 0);
            return;
        }

        fillOcrDataGrid();
        fillDataPage();
    }

    final void fillOcrDataGrid() {
        ArrayList<Pr22.Processing.FieldReference> fields = analyzeResult.getFields();

        for (int i = 0; i < fields.size(); i++) {
            try {
                Pr22.Processing.Field field = analyzeResult.getField(fields.get(i));
                String[] values = new String[3];
                values[0] = new StrCon().add(fields.get(i).toString(" ")).
                        add(getAmid(field)).toString();

                try {
                    values[1] = '\u202d' + field.getBestStringValue();
                } catch (InvalidParameter ex) {
                    values[1] = printBinary(field.getBinaryValue(), 0, 16);
                } catch (General ex) {
                }
                values[2] = field.getStatus().toString();

                modelOcr.addRow(values);

            } catch (PrIns.Exceptions.General ex) {
            }
        }
    }

    final void addTableValueChanged() {
        tableOcr.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent event) {

                if (event.getValueIsAdjusting()) {
                    return;
                }
                try {
                    if (tableOcr.getSelectedRowCount() == 0) {
                        return;
                    }
                    int ix = tableOcr.getSelectedRow();
                    int size = analyzeResult.getFields().size();
                    if (analyzeResult == null || ix < 0 || size <= ix) {
                        return;
                    }

                    clearOCRData();
                    Pr22.Processing.FieldReference selectedField = analyzeResult.getFields().get(ix);
                    Pr22.Processing.Field field = analyzeResult.getField(selectedField);
                    try {
                        lblRawValue.setText('\u202d' + field.getRawStringValue());
                    } catch (General ex) {
                    }
                    try {
                        lblFormattedValue.setText(field.getFormattedStringValue());
                    } catch (General ex) {
                    }
                    try {
                        lblStandardizedValue.setText(field.getStandardizedStringValue());
                    } catch (General ex) {
                    }
                    try {
                        Image imgTmp = field.getImage().toImage();
                        resetLabelPos();
                        drawImage(lblFieldImagePic, imgTmp);
                    } catch (General ex) {
                    } catch (OutOfMemoryError ex) {
                    }

                } catch (General ex) {
                }
            }
        });
    }

    void fillDataPage() {

        lblName1Value.setText(getFieldValue(FieldId.Surname));
        if (!lblName1Value.getText().isEmpty()) {
            lblName1Value.setText(lblName1Value.getText() + " " + getFieldValue(FieldId.Surname2));
            lblName2Value.setText(new StrCon().add(getFieldValue(FieldId.Givenname)).
                    add(getFieldValue(FieldId.MiddleName)).toString());
        } else {
            lblName1Value.setText(getFieldValue(FieldId.Name));
        }

        lblBirthValue.setText(new StrCon().addCon("on").add(getFieldValue(FieldId.BirthDate)).
                addCon("in").add(getFieldValue(FieldId.BirthPlace)).toString());

        lblNationalityValue.setText(getFieldValue(FieldId.Nationality));

        lblSexValue.setText(getFieldValue(FieldId.Sex));

        lblIssuerValue.setText(new StrCon().add(getFieldValue(FieldId.IssueCountry)).
                add(getFieldValue(FieldId.IssueState)).toString());

        lblTypeValue.setText(new StrCon().add(getFieldValue(FieldId.DocType)).
                add(getFieldValue(FieldId.DocTypeDisc)).toString());
        if (lblTypeValue.getText().isEmpty()) {
            lblTypeValue.setText(getFieldValue(FieldId.Type));
        }

        lblPageValue.setText(getFieldValue(FieldId.DocPage));

        lblNumberValue.setText(getFieldValue(FieldId.DocumentNumber));

        lblValidValue.setText(new StrCon().addCon("from").add(getFieldValue(FieldId.IssueDate)).
                addCon("to").add(getFieldValue(FieldId.ExpiryDate)).toString());

        Image imgTmpFace = null;
        try {
            imgTmpFace = analyzeResult.getField(FieldSource.Viz, FieldId.Face).getImage().toImage();
            drawImage(lblFacePhotoPic, imgTmpFace);
        } catch (General ex) {
        }

        Image imgTmpSign = null;
        try {
            imgTmpSign = analyzeResult.getField(FieldSource.Viz, FieldId.Signature).getImage().toImage();
            drawImage(lblSignaturePic, imgTmpSign);
        } catch (General ex) {
        }
    }

    // </editor-fold>
    // <editor-fold desc="General tools">
    //--------------------------------------------------------------------------
    final String getAmid(Pr22.Processing.Field field) {
        try {
            return field.toVariant().getChild(Pr22.Util.VariantId.AMID.id, 0).toString();
        } catch (General ex) {
            return "";
        }
    }

    String getFieldValue(Pr22.Processing.FieldId id) {
        try {
            FieldReference filter = new FieldReference(FieldSource.All, id);
            ArrayList<FieldReference> fields = analyzeResult.getFields(filter);

            for (FieldReference aFR : fields) {
                try {
                    String value = analyzeResult.getField(aFR).getBestStringValue();
                    if (!value.isEmpty()) {
                        return value;
                    }
                } catch (EntryNotFound ex) {
                }
            }
        } catch (Exception ex) {
        }
        return "";
    }

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

    final void addTabPage(String lightName) {
        JPanel panelOthersTabs = new JPanel();
        tabbedPaneOthers.addTab(lightName, null, panelOthersTabs, null);
        panelOthersTabs.setLayout(null);
    }

    void clearOCRData() {
        lblFieldImagePic.setIcon(null);

        lblRawValue.setText("");
        lblFormattedValue.setText("");
        lblStandardizedValue.setText("");
    }

    void clearDataPage() {
        lblName1Value.setText("");
        lblName2Value.setText("");
        lblBirthValue.setText("");
        lblNationalityValue.setText("");
        lblSexValue.setText("");
        lblIssuerValue.setText("");
        lblTypeValue.setText("");
        lblPageValue.setText("");
        lblNumberValue.setText("");
        lblValidValue.setText("");

        lblFacePhotoPic.setIcon(null);
        lblSignaturePic.setIcon(null);

        resetLabelPos();
    }

    void resetLabelPos() {
        lblFieldImagePic.setBounds(96, 11, 602, 51);
        lblFacePhotoPic.setBounds(10, 21, 114, 130);
        lblSignaturePic.setBounds(10, 22, 551, 123);
    }

    void drawImage(JLabel label, Image img) {
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

    // </editor-fold>
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

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmScannerSample = new JFrame();
        frmScannerSample.setTitle("Scanner Sample");
        frmScannerSample.setBounds(100, 100, 1032, 571);
        frmScannerSample.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmScannerSample.getContentPane().setLayout(null);
        addMainWindowEvents();

        //left side

        JTabbedPane tabbedPaneOptions = new JTabbedPane(JTabbedPane.TOP);
        tabbedPaneOptions.setBounds(0, 0, 280, 533);
        frmScannerSample.getContentPane().add(tabbedPaneOptions);

        JPanel panelMainOptions = new JPanel();
        tabbedPaneOptions.addTab("Options", null, panelMainOptions, null);
        tabbedPaneOptions.setFont(new Font("Arial", Font.PLAIN, 11));
        panelMainOptions.setLayout(null);

        {
            JPanel panelDevices = new JPanel();
            panelDevices.setBounds(10, 0, 255, 163);
            panelMainOptions.add(panelDevices);
            panelDevices.setLayout(null);

            TitledBorder borderDev = new TitledBorder("Devices");
            borderDev.setTitleJustification(TitledBorder.LEFT);
            borderDev.setTitlePosition(TitledBorder.TOP);
            borderDev.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelDevices.setBorder(borderDev);

            listDevices.setBorder(new LineBorder(new Color(0, 0, 0)));
            listDevices.setBounds(10, 22, 235, 75);
            listDevices.setFont(new Font("Arial", Font.PLAIN, 11));
            panelDevices.add(listDevices);

            btnConnect = new JButton("Connect");
            btnConnect.setBounds(30, 115, 91, 23);
            btnConnect.setFont(new Font("Arial", Font.PLAIN, 11));
            panelDevices.add(btnConnect);
            addConnectCallback();

            btnDisconnect = new JButton("Disconnect");
            btnDisconnect.setBounds(131, 115, 91, 23);
            btnDisconnect.setEnabled(false);
            btnDisconnect.setFont(new Font("Arial", Font.PLAIN, 11));
            panelDevices.add(btnDisconnect);
            addDisconnectCallback();
        }

        {
            JPanel panelImages = new JPanel();
            panelImages.setBounds(10, 174, 255, 156);
            panelMainOptions.add(panelImages);
            panelImages.setLayout(null);

            TitledBorder borderImg = new TitledBorder("Images");
            borderImg.setTitleJustification(TitledBorder.LEFT);
            borderImg.setTitlePosition(TitledBorder.TOP);
            borderImg.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelImages.setBorder(borderImg);

            JScrollPane scrollPaneImages = new JScrollPane();
            scrollPaneImages.setBounds(10, 18, 235, 103);
            panelImages.add(scrollPaneImages);

            checkedListBoxImages = new CheckedListBox(scrollPaneImages);

            checkboxDocView = new JCheckBox("Crop and rotate document");
            checkboxDocView.setBounds(10, 126, 162, 23);
            checkboxDocView.setFont(new Font("Arial", Font.PLAIN, 11));
            panelImages.add(checkboxDocView);
        }

        {
            JPanel panelOcr = new JPanel();
            panelOcr.setBounds(10, 336, 255, 108);
            panelMainOptions.add(panelOcr);
            panelOcr.setLayout(null);

            TitledBorder borderOcr = new TitledBorder("OCR");
            borderOcr.setTitleJustification(TitledBorder.LEFT);
            borderOcr.setTitlePosition(TitledBorder.TOP);
            borderOcr.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelOcr.setBorder(borderOcr);

            checkboxMrz = new JCheckBox("MRZ fields");
            checkboxMrz.setBounds(10, 22, 95, 22);
            checkboxMrz.setFont(new Font("Arial", Font.PLAIN, 11));
            panelOcr.add(checkboxMrz);

            checkboxViz = new JCheckBox("VIZ fields");
            checkboxViz.setBounds(10, 50, 95, 21);
            checkboxViz.setFont(new Font("Arial", Font.PLAIN, 11));
            panelOcr.add(checkboxViz);

            checkboxBcr = new JCheckBox("BCR fields");
            checkboxBcr.setBounds(10, 77, 95, 22);
            checkboxBcr.setFont(new Font("Arial", Font.PLAIN, 11));
            panelOcr.add(checkboxBcr);
        }

        btnStart = new JButton("Start");
        btnStart.setEnabled(false);
        btnStart.setBounds(76, 453, 133, 41);
        btnStart.setFont(new Font("Arial", Font.PLAIN, 11));
        panelMainOptions.add(btnStart);
        addStartCallback();

        //right side

        tabbedPaneOthers = new JTabbedPane(JTabbedPane.TOP);
        tabbedPaneOthers.setBounds(283, 0, 733, 533);
        frmScannerSample.getContentPane().add(tabbedPaneOthers);

        JPanel panelOthersOcr = new JPanel();
        tabbedPaneOthers.addTab("OCR", null, panelOthersOcr, null);
        tabbedPaneOthers.setFont(new Font("Arial", Font.PLAIN, 11));
        panelOthersOcr.setLayout(null);

        {
            JPanel panelValues = new JPanel();
            panelValues.setBounds(10, 335, 708, 96);
            panelOthersOcr.add(panelValues);
            panelValues.setLayout(null);

            TitledBorder borderValues = new TitledBorder("Values");
            borderValues.setTitleJustification(TitledBorder.LEFT);
            borderValues.setTitlePosition(TitledBorder.TOP);
            borderValues.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelValues.setBorder(borderValues);

            JLabel lblRaw = new JLabel("RAW:");
            lblRaw.setFont(new Font("Arial", Font.PLAIN, 11));
            lblRaw.setBounds(10, 21, 46, 14);
            panelValues.add(lblRaw);

            JLabel lblFormatted = new JLabel("Formatted:");
            lblFormatted.setFont(new Font("Arial", Font.PLAIN, 11));
            lblFormatted.setBounds(10, 46, 67, 14);
            panelValues.add(lblFormatted);

            JLabel lblStandardized = new JLabel("Standardized:");
            lblStandardized.setFont(new Font("Arial", Font.PLAIN, 11));
            lblStandardized.setBounds(10, 73, 84, 14);
            panelValues.add(lblStandardized);

            lblRawValue = new JLabel("");
            lblRawValue.setBounds(121, 21, 577, 14);
            panelValues.add(lblRawValue);

            lblFormattedValue = new JLabel("");
            lblFormattedValue.setBounds(121, 46, 577, 14);
            panelValues.add(lblFormattedValue);

            lblStandardizedValue = new JLabel("");
            lblStandardizedValue.setBounds(121, 73, 577, 14);
            panelValues.add(lblStandardizedValue);
        }

        {
            JPanel panelFieldImage = new JPanel();
            panelFieldImage.setBounds(10, 432, 708, 73);
            panelOthersOcr.add(panelFieldImage);
            panelFieldImage.setLayout(null);

            TitledBorder borderFieldImg = new TitledBorder("Field image");
            borderFieldImg.setTitleJustification(TitledBorder.LEFT);
            borderFieldImg.setTitlePosition(TitledBorder.TOP);
            borderFieldImg.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelFieldImage.setBorder(borderFieldImg);

            lblFieldImagePic = new JLabel("");
            panelFieldImage.add(lblFieldImagePic);
        }

        {
            scrollPaneOcr = new JScrollPane();
            scrollPaneOcr.setBounds(10, 11, 708, 319);
            panelOthersOcr.add(scrollPaneOcr);

            tableOcr = new JTable() {

                private static final long serialVersionUID = 1L;

                @Override
                public boolean isCellEditable(final int row, final int column) {
                    return false;
                }

                @Override
                public Component prepareRenderer(final TableCellRenderer renderer,
                        final int row, final int col) {
                    Component comp = super.prepareRenderer(renderer, row, col);

                    return comp;
                }
            };
            scrollPaneOcr.setColumnHeaderView(tableOcr);

            JTableHeader tableOcrHeader = tableOcr.getTableHeader();
            tableOcr.setShowGrid(true);
            tableOcr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            tableOcr.setModel(new DefaultTableModel(new Object[][]{ // {Boolean.FALSE, "sz1"}
                    }, new String[]{"Field ID", "Value", "Status"}) {

                private static final long serialVersionUID = 1L;
                private Class[] columnTypes = new Class[]{Object.class, Object.class, Object.class};

                @Override
                public Class getColumnClass(final int columnIndex) {
                    return columnTypes[columnIndex];
                }
            });
            modelOcr = (DefaultTableModel) tableOcr.getModel();
            Rectangle cellRect = tableOcr.getCellRect(0, 0, false);
            tableOcrHeader.setBounds(scrollPaneOcr.getX(), scrollPaneOcr.getY() - cellRect.height, scrollPaneOcr.getWidth(), cellRect.height);
            panelOthersOcr.add(tableOcrHeader);

            scrollPaneOcr.setViewportView(tableOcr);
            addTableValueChanged();
        }

        JPanel panelOthersData = new JPanel();
        tabbedPaneOthers.addTab("Data", null, panelOthersData, null);
        panelOthersData.setLayout(null);

        {
            JPanel panelPersonalData = new JPanel();
            panelPersonalData.setBounds(10, 0, 571, 162);
            panelOthersData.add(panelPersonalData);
            panelPersonalData.setLayout(null);

            TitledBorder borderPersonalData = new TitledBorder("Personal Data");
            borderPersonalData.setTitleJustification(TitledBorder.LEFT);
            borderPersonalData.setTitlePosition(TitledBorder.TOP);
            borderPersonalData.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelPersonalData.setBorder(borderPersonalData);

            JLabel lblName = new JLabel("Name:");
            lblName.setFont(new Font("Arial", Font.PLAIN, 11));
            lblName.setBounds(10, 23, 46, 14);
            panelPersonalData.add(lblName);

            JLabel lblBirth = new JLabel("Birth:");
            lblBirth.setFont(new Font("Arial", Font.PLAIN, 11));
            lblBirth.setBounds(10, 79, 46, 14);
            panelPersonalData.add(lblBirth);

            JLabel lblNationality = new JLabel("Nationality:");
            lblNationality.setFont(new Font("Arial", Font.PLAIN, 11));
            lblNationality.setBounds(10, 107, 76, 14);
            panelPersonalData.add(lblNationality);

            JLabel lblSex = new JLabel("Sex:");
            lblSex.setFont(new Font("Arial", Font.PLAIN, 11));
            lblSex.setBounds(10, 135, 46, 14);
            panelPersonalData.add(lblSex);

            lblName1Value = new JLabel("");
            lblName1Value.setBounds(123, 23, 438, 14);
            panelPersonalData.add(lblName1Value);

            lblName2Value = new JLabel("");
            lblName2Value.setBounds(123, 51, 438, 14);
            panelPersonalData.add(lblName2Value);

            lblBirthValue = new JLabel("");
            lblBirthValue.setBounds(123, 79, 438, 14);
            panelPersonalData.add(lblBirthValue);

            lblNationalityValue = new JLabel("");
            lblNationalityValue.setBounds(123, 107, 438, 14);
            panelPersonalData.add(lblNationalityValue);

            lblSexValue = new JLabel("");
            lblSexValue.setBounds(123, 135, 438, 14);
            panelPersonalData.add(lblSexValue);
        }

        {
            JPanel panelFacePhoto = new JPanel();
            panelFacePhoto.setBounds(584, 0, 134, 162);
            panelOthersData.add(panelFacePhoto);
            panelFacePhoto.setLayout(null);

            TitledBorder borderFacePhoto = new TitledBorder("Face Photo");
            borderFacePhoto.setTitleJustification(TitledBorder.LEFT);
            borderFacePhoto.setTitlePosition(TitledBorder.TOP);
            borderFacePhoto.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelFacePhoto.setBorder(borderFacePhoto);

            lblFacePhotoPic = new JLabel("");
            panelFacePhoto.add(lblFacePhotoPic);
        }

        {
            JPanel panelDocumentData = new JPanel();
            panelDocumentData.setBounds(10, 173, 571, 158);
            panelOthersData.add(panelDocumentData);
            panelDocumentData.setLayout(null);

            TitledBorder borderDocData = new TitledBorder("Document Data");
            borderDocData.setTitleJustification(TitledBorder.LEFT);
            borderDocData.setTitlePosition(TitledBorder.TOP);
            borderDocData.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelDocumentData.setBorder(borderDocData);

            JLabel lblIssuer = new JLabel("Issuer:");
            lblIssuer.setBounds(10, 21, 46, 14);
            lblIssuer.setFont(new Font("Arial", Font.PLAIN, 11));
            panelDocumentData.add(lblIssuer);

            JLabel lblType = new JLabel("Type:");
            lblType.setBounds(10, 49, 46, 14);
            lblType.setFont(new Font("Arial", Font.PLAIN, 11));
            panelDocumentData.add(lblType);

            JLabel lblPage = new JLabel("Page:");
            lblPage.setBounds(10, 77, 46, 14);
            lblPage.setFont(new Font("Arial", Font.PLAIN, 11));
            panelDocumentData.add(lblPage);

            JLabel lblNumber = new JLabel("Number:");
            lblNumber.setBounds(10, 105, 64, 14);
            lblNumber.setFont(new Font("Arial", Font.PLAIN, 11));
            panelDocumentData.add(lblNumber);

            JLabel lblValid = new JLabel("Valid:");
            lblValid.setBounds(10, 133, 46, 14);
            lblValid.setFont(new Font("Arial", Font.PLAIN, 11));
            panelDocumentData.add(lblValid);

            lblIssuerValue = new JLabel("");
            lblIssuerValue.setBounds(124, 21, 437, 14);
            panelDocumentData.add(lblIssuerValue);

            lblTypeValue = new JLabel("");
            lblTypeValue.setBounds(124, 49, 437, 14);
            panelDocumentData.add(lblTypeValue);

            lblPageValue = new JLabel("");
            lblPageValue.setBounds(124, 77, 437, 14);
            panelDocumentData.add(lblPageValue);

            lblNumberValue = new JLabel("");
            lblNumberValue.setBounds(124, 105, 437, 14);
            panelDocumentData.add(lblNumberValue);

            lblValidValue = new JLabel("");
            lblValidValue.setBounds(124, 133, 437, 14);
            panelDocumentData.add(lblValidValue);
        }

        {
            JPanel panelSignature = new JPanel();
            panelSignature.setBounds(10, 336, 571, 158);
            panelOthersData.add(panelSignature);
            panelSignature.setLayout(null);

            TitledBorder borderSignature = new TitledBorder("Signature");
            borderSignature.setTitleJustification(TitledBorder.LEFT);
            borderSignature.setTitlePosition(TitledBorder.TOP);
            borderSignature.setTitleFont(new Font("Arial", Font.PLAIN, 11));
            panelSignature.setBorder(borderSignature);

            lblSignaturePic = new JLabel("");
            panelSignature.add(lblSignaturePic);
        }

        resetLabelPos();
    }
}
