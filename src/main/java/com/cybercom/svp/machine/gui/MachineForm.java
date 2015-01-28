/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybercom.svp.machine.gui;

import com.cybercom.svp.machine.dto.ColorBlock;
import com.cybercom.svp.machine.dto.Embroidery;
import com.cybercom.svp.machine.dto.FirmwareAssembleResponse;
import com.cybercom.svp.machine.dto.FirmwareDownloadRequest;
import com.cybercom.svp.machine.dto.FirmwareDownloadResponse;
import com.cybercom.svp.machine.dto.FirmwareUpdateRequest;
import com.cybercom.svp.machine.dto.FirmwareUpdateRequest.Units;
import com.cybercom.svp.machine.dto.FirmwareUpdateRequest.Units.Unit;
import com.cybercom.svp.machine.dto.FirmwareUpdateResponse;
import com.cybercom.svp.machine.dto.LoginMachine;
import com.cybercom.svp.machine.dto.MachineEvent;
import com.cybercom.svp.machine.dto.MachineEvent.ExtraParameters;
import com.cybercom.svp.machine.dto.MachineEvent.RequiredParameters;
import com.cybercom.svp.machine.dto.MachineEvent.RequiredParameters.Parameter;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

/**
 *
 * @author hajoh1
 */
public class MachineForm extends javax.swing.JFrame {
    
    

    /**
     * thread-breakage
     *
     * Fired each time a thread breakage is detected on the sewing machine.
     *
     * <pre>
     * Required parameters: none
     * </pre>
     */
    public final static String EVENT_THREAD_BREAKAGE = "thread-breakage";

    /**
     * embroidery-stitchout-start
     *
     * Fired when entering embroidery stitch out mode.
     *
     * <pre>
     * Required parameters:
     *   design-image-png (TBD: size of image)
     *   design-num-stitches
     *   For each color block:
     *     color-block-num-stitches-N
     *     color-block-thread-name-N
     *     color-block-thread-color-N
     *     color-block-thread-manufacturer-N
     *     color-block-thread-nickname-N
     *     color-block-thread-number-N
     * </pre>
     */
    public final static String EVENT_EMBROIDERY_STITCHOUT_START = "embroidery-stitchout-start";

    /**
     * embroidery-stitchout-end
     *
     * Fired when leaving embroidery stitch out mode.
     *
     * <pre>
     * Required parameters: none
     * </pre>
     */
    public final static String EVENT_EMBROIDERY_STITCHOUT_END = "embroidery-stitchout-end";

    /**
     * embroidery-stitchout-update
     *
     * Fired once every ??? stitches (TBD) in embroidery stitch out mode.
     *
     * <pre>
     * Required parameters:
     *   design-stitch-number
     *   color-block-stitch-number
     *   color-block-number
     * </pre>
     */
    public final static String EVENT_EMBROIDERY_STITCHOUT_UPDATE = "embroidery-stitchout-update";

    /**
     * embroidery-finished
     *
     * Fired when an embroidery is finished.
     *
     * <pre>
     * Required parameters: none
     * </pre>
     */
    public final static String EVENT_EMBROIDERY_FINISHED = "embroidery-finished";
    /**
     * cut-thread
     *
     * Fired in embroidery stitchout when the user needs to cut threads.
     *
     * <pre>
     * Required parameters: none
     * </pre>
     */
    public final static String EVENT_CUT_THREAD = "cut-thread";
    /**
     * change-thread
     *
     * Fired in embroidery stitchout when the user needs to change threads (due
     * to a color block change).
     *
     * <pre>
     * Required parameters:
     *   color-block-number
     * </pre>
     */
    public final static String EVENT_CHANGE_THREAD = "change-thread";
    /**
     * bobbin-empty
     *
     * Fired in embroidery stitchout when the bobbin is detected to be empty.
     *
     * <pre>
     * Required parameters: none
     * </pre>
     */
    public final static String EVENT_BOBBIN_EMPTY = "bobbin-empty";

    private boolean loggedIn = false;
    private enum Mode{PRODUCTION,DEVLOP}
    private Mode selectedMode = Mode.DEVLOP;
    private final DefaultListModel listModelStatus = new DefaultListModel();
    private Thread processThread = null;
    private boolean interrupted = false;
    private MachineEvent machineEvent;
    private FirmwareUpdateRequest firmwareUpdateRequestSetting;

    private class ProcessRunnable implements Runnable {

        Embroidery embroidery = new Embroidery();

        public void run() {
            interrupted = false;
            int period = 50;
            try {
                machineEvent = getStartEventfromMachine(embroidery);
                sendEvent(objToXml(MachineEvent.class, machineEvent));
            } catch (JAXBException ex) {
                Logger.getLogger(MachineForm.class.getName()).log(Level.SEVERE, null, ex);
            }

            while (!interrupted) {
                if (period % 50 == 0) {
                    try {
                        addMessage("Sending status");
                        ColorBlock currentBlock = embroidery.getColorBlocks().get(embroidery.getCurrentBlock());
                        machineEvent = getUpdateEventfromMachine(embroidery.getCurrentBlock(), embroidery.getDesignStitchNumber(), currentBlock.getStichNumber());

                        sendEvent(objToXml(MachineEvent.class, machineEvent));
                        period = 0;
                    } catch (JAXBException ex) {
                        Logger.getLogger(MachineForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                period++;

                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }

                try {
                    embroidery.increase(9);
                    ColorBlock currentBlock = embroidery.getColorBlocks().get(embroidery.getCurrentBlock());
                    textTotalStitches.setText(String.valueOf(embroidery.getDesignStitchNumber()));
                    textStitchesColorblock.setText(String.valueOf(currentBlock.getStichNumber()));
                    textColor.setText(currentBlock.getThreadColor());

                } catch (Embroidery.EmbroideryFinishException ex) {
                    try {
                        interrupted = true;
                        buttonStartStopSewing.setText("Start sewing");
                        sendEvent(objToXml(MachineEvent.class, getEmbroideryFinishedEventfromMachine()));
                        sendEvent(objToXml(MachineEvent.class, getEndEventfromMachine()));
                    } catch (JAXBException ex1) {
                        Logger.getLogger(MachineForm.class.getName()).log(Level.SEVERE, null, ex1);
                    }

                }

            }
            addMessage("Stop sewing");
        }
    }

    /**
     * Creates new form MachineForm
     */
    public MachineForm() {
        initComponents();
        listStatus.setModel(listModelStatus);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        groupNotification = new javax.swing.ButtonGroup();
        groupMode = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        textMachineId = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        textEmail = new javax.swing.JTextField();
        textPassword = new javax.swing.JPasswordField();
        buttonLogin = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        textSessionToken = new javax.swing.JTextField();
        buttonLogout = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        cbServer = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        radioDevelop = new javax.swing.JRadioButton();
        radioProduction = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        textUserUUID = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        cbBrand = new javax.swing.JComboBox();
        buttonGenUUID = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        buttonStartStopSewing = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        textTotalStitches = new javax.swing.JTextField();
        textStitchesColorblock = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        textColor = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        buttonThreadBreak = new javax.swing.JRadioButton();
        buttonSendNotification = new javax.swing.JButton();
        buttonBobbinEmpty = new javax.swing.JRadioButton();
        buttonChangeColor = new javax.swing.JRadioButton();
        buttonCutThreadEnds = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listStatus = new javax.swing.JList();
        jPanel5 = new javax.swing.JPanel();
        buttomLoadFirmwareSettingFile = new javax.swing.JButton();
        buttonCheckFirmware = new javax.swing.JButton();
        buttonCreateAssembly = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        textModel = new javax.swing.JTextField();
        textSystemVersion = new javax.swing.JTextField();
        textCheckFirmwareResult = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        buttonDownload = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        textChunkSize = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        textDirectory = new javax.swing.JTextField();
        textAssemblyId = new javax.swing.JTextField();
        textFileSize = new javax.swing.JTextField();
        textMD5 = new javax.swing.JTextField();
        buttomLoadDefaultFirmwareSetting = new javax.swing.JButton();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setText("Machine ID");

        jLabel3.setText("Brand");

        jLabel4.setText("Password");

        buttonLogin.setText("Login");
        buttonLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLoginActionPerformed(evt);
            }
        });

        jLabel6.setText("Sessiontoken");

        textSessionToken.setEditable(false);
        textSessionToken.setEnabled(false);

        buttonLogout.setText("Logout");
        buttonLogout.setEnabled(false);
        buttonLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLogoutActionPerformed(evt);
            }
        });

        jLabel7.setText("Server");

        cbServer.setEditable(true);
        cbServer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "http://10.2.35.6:8080/", "http://localhost:8080/" }));
        cbServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbServerActionPerformed(evt);
            }
        });

        jLabel18.setText("Mode");

        groupMode.add(radioDevelop);
        radioDevelop.setSelected(true);
        radioDevelop.setText("Develop");
        radioDevelop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioDevelopActionPerformed(evt);
            }
        });

        groupMode.add(radioProduction);
        radioProduction.setText("Production");
        radioProduction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioProductionActionPerformed(evt);
            }
        });

        jLabel5.setText("User UUID");

        textUserUUID.setEnabled(false);

        jLabel19.setText("Email");

        cbBrand.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "PFAFF", "VIKING" }));

        buttonGenUUID.setText("Generate UUID");
        buttonGenUUID.setEnabled(false);
        buttonGenUUID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGenUUIDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel6))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(textSessionToken, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                                    .addComponent(textPassword)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel19))
                                .addGap(30, 30, 30)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(textEmail)
                                    .addComponent(textMachineId)
                                    .addComponent(cbServer, 0, 259, Short.MAX_VALUE)
                                    .addComponent(textUserUUID)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cbBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(radioDevelop)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(radioProduction)))
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGenUUID)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonLogin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonLogout)
                        .addGap(153, 153, 153))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cbServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(radioDevelop)
                    .addComponent(radioProduction))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(textPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(textSessionToken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonLogin)
                    .addComponent(buttonLogout))
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textUserUUID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(buttonGenUUID))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(textMachineId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        buttonStartStopSewing.setText("Start sewing");
        buttonStartStopSewing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartStopSewingActionPerformed(evt);
            }
        });

        jLabel8.setText("Total stitches");

        jLabel9.setText("Stitches colorblock");

        textTotalStitches.setEditable(false);
        textTotalStitches.setText("1/10000");

        textStitchesColorblock.setEditable(false);
        textStitchesColorblock.setText("1/2568");

        jLabel10.setText("SelectedColor");

        textColor.setEditable(false);
        textColor.setText("2201");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(textStitchesColorblock, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonStartStopSewing))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(42, 42, 42)
                        .addComponent(textTotalStitches, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textColor, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(textTotalStitches, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(textColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonStartStopSewing)
                    .addComponent(jLabel9)
                    .addComponent(textStitchesColorblock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        groupNotification.add(buttonThreadBreak);
        buttonThreadBreak.setSelected(true);
        buttonThreadBreak.setText("Thread Break");
        buttonThreadBreak.setActionCommand("<notification>\n<code>100</code>\n<message>Thread Break</message>\n</notification>\n\n");
        buttonThreadBreak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonThreadBreakActionPerformed(evt);
            }
        });

        buttonSendNotification.setText("Send notification");
        buttonSendNotification.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSendNotificationActionPerformed(evt);
            }
        });

        groupNotification.add(buttonBobbinEmpty);
        buttonBobbinEmpty.setText("Bobbin Empty");
        buttonBobbinEmpty.setActionCommand("<notification>\n<code>101</code>\n<message>Bobbin Empty</message>\n</notification>\n\n");

        groupNotification.add(buttonChangeColor);
        buttonChangeColor.setText("Change Color");
        buttonChangeColor.setActionCommand("<notification>\n<code>102</code>\n<message>Change Color</message>\n</notification>\n\n");

        groupNotification.add(buttonCutThreadEnds);
        buttonCutThreadEnds.setText("Cut thread ends");
        buttonCutThreadEnds.setActionCommand("<notification>\n<code>103</code>\n<message>Cut thread ends</message>\n</notification>\n\n");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonChangeColor)
                            .addComponent(buttonBobbinEmpty)
                            .addComponent(buttonThreadBreak))
                        .addGap(0, 374, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(buttonCutThreadEnds)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonSendNotification)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonThreadBreak)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonBobbinEmpty)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonChangeColor)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 5, Short.MAX_VALUE)
                        .addComponent(buttonSendNotification)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCutThreadEnds)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        listStatus.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listStatus);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        buttomLoadFirmwareSettingFile.setText("Load firmware setting from file...");
        buttomLoadFirmwareSettingFile.setToolTipText("Load machines current formware settings from file");
        buttomLoadFirmwareSettingFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttomLoadFirmwareSettingFileActionPerformed(evt);
            }
        });

        buttonCheckFirmware.setText("Check firmware");
        buttonCheckFirmware.setEnabled(false);
        buttonCheckFirmware.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCheckFirmwareActionPerformed(evt);
            }
        });

        buttonCreateAssembly.setText("Create Assembly");
        buttonCreateAssembly.setToolTipText("");
        buttonCreateAssembly.setEnabled(false);
        buttonCreateAssembly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCreateAssemblyActionPerformed(evt);
            }
        });

        jLabel11.setText("Model");

        jLabel12.setText("System version");

        textModel.setEditable(false);

        textSystemVersion.setEditable(false);

        textCheckFirmwareResult.setEditable(false);

        jLabel13.setText("Assembly ID");

        jLabel14.setText("File size");

        jLabel15.setText("MD5");

        buttonDownload.setText("Download");
        buttonDownload.setEnabled(false);
        buttonDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDownloadActionPerformed(evt);
            }
        });

        jLabel16.setText("Chunk size");

        textChunkSize.setText("5000000");

        jLabel17.setText("Directory");

        textDirectory.setText("c:\\temp");

        textAssemblyId.setEditable(false);

        textFileSize.setEditable(false);

        textMD5.setEditable(false);

        buttomLoadDefaultFirmwareSetting.setText("Load default firmware setting ");
        buttomLoadDefaultFirmwareSetting.setToolTipText("Load machines current formware settings from file");
        buttomLoadDefaultFirmwareSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttomLoadDefaultFirmwareSettingActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(buttonCheckFirmware)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textCheckFirmwareResult))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonCreateAssembly)
                            .addComponent(buttonDownload))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(42, 42, 42)
                                .addComponent(textFileSize))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(textAssemblyId))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(63, 63, 63)
                                .addComponent(textMD5))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel17))
                                .addGap(27, 27, 27)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(textChunkSize, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(textDirectory)))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(buttomLoadFirmwareSettingFile, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textModel, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textSystemVersion))
                            .addComponent(buttomLoadDefaultFirmwareSetting, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttomLoadFirmwareSettingFile)
                    .addComponent(buttomLoadDefaultFirmwareSetting))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(textModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(textSystemVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCheckFirmware)
                    .addComponent(textCheckFirmwareResult, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCreateAssembly)
                    .addComponent(jLabel13)
                    .addComponent(textAssemblyId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(textFileSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addComponent(textMD5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonDownload)
                    .addComponent(jLabel16)
                    .addComponent(textChunkSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(textDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(174, 174, 174))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLoginActionPerformed
        login();
    }//GEN-LAST:event_buttonLoginActionPerformed

    private void buttonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLogoutActionPerformed
        logout();
    }//GEN-LAST:event_buttonLogoutActionPerformed

    private void buttonStartStopSewingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartStopSewingActionPerformed

        try {
            validateForMachineActions();

            if (processThread != null && processThread.isAlive()) {
                interrupted = true;
                buttonStartStopSewing.setText("Start sewing");
            } else {
                processThread = new Thread(new MachineForm.ProcessRunnable());
                processThread.start();
                buttonStartStopSewing.setText("Stop sewing");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

    }//GEN-LAST:event_buttonStartStopSewingActionPerformed

    private void buttonSendNotificationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSendNotificationActionPerformed

        try {
            validateForMachineActions();

            if (processThread != null && processThread.isAlive()) {
                interrupted = true;
                buttonStartStopSewing.setText("Start sewing");
            }

            for (Enumeration<AbstractButton> buttons = groupNotification.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();

                if (button.isSelected()) {
                    if (button == buttonBobbinEmpty) {
                        sendEvent(objToXml(MachineEvent.class, getBobbinEmptyEventfromMachine()));
                    } else if (button == buttonChangeColor) {
                        sendEvent(objToXml(MachineEvent.class, getChangeThreadEventfromMachine(1)));
                    } else if (button == buttonCutThreadEnds) {
                        sendEvent(objToXml(MachineEvent.class, getCutThreadEventfromMachine()));
                    } else if (button == buttonThreadBreak) {
                        sendEvent(objToXml(MachineEvent.class, getThreadBreakEventfromMachine()));
                    }

                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

    }//GEN-LAST:event_buttonSendNotificationActionPerformed

    private void buttonThreadBreakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonThreadBreakActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonThreadBreakActionPerformed

    private void buttomLoadFirmwareSettingFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttomLoadFirmwareSettingFileActionPerformed
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setDialogTitle("Select firmware file");

        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML file", "xml");
        fileDialog.setFileFilter(filter);
        fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);
        int result = fileDialog.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {

            try {
                String xml = FileUtils.readFileToString(fileDialog.getSelectedFile());
                firmwareUpdateRequestSetting = xmlToObj(FirmwareUpdateRequest.class, xml);
                updateFirmwareSettings();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_buttomLoadFirmwareSettingFileActionPerformed

    private void buttonCheckFirmwareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCheckFirmwareActionPerformed

        try {
            validateForMachineActions();

            ResteasyClient client = new ResteasyClientBuilder().build();
            String server = (String) cbServer.getSelectedItem();

            addMessage("Sending firmware check to server");
            Response response = client.target(server + "/server/rest/machine/firmware/check")
                    .request(MediaType.APPLICATION_XML)
                    .headers(createHeaders())
                    .header("Machine-Id", textMachineId.getText())
                    .header("Machine-Brand", cbBrand.getSelectedItem())
                    .post(Entity.entity(objToXml(FirmwareUpdateRequest.class, firmwareUpdateRequestSetting), MediaType.APPLICATION_XML));

            addMessage("Response " + response.getStatus());
            if (response.getStatus() != 200) {
                throw new Exception("Error from server " + response.getStatus());
            }
            String resp = response.readEntity(String.class);
            FirmwareUpdateResponse firmwareUpdateResponse = xmlToObj(FirmwareUpdateResponse.class, resp);

            if (firmwareUpdateResponse.getUnits() == null || firmwareUpdateResponse.getUnits().getUnit().isEmpty()) {
                addMessage("Nothing to update");
                textCheckFirmwareResult.setBackground(Color.GREEN);
                textCheckFirmwareResult.setText("Nothing to update");
            } else {
                textCheckFirmwareResult.setBackground(Color.RED);
                textCheckFirmwareResult.setText("Update is available");
                addMessage("Update is available");
                for (FirmwareUpdateResponse.Units.Unit unit : firmwareUpdateResponse.getUnits().getUnit()) {
                    addMessage("------  Unit to update:" + unit.getName());
                }
                buttonDownload.setEnabled(true);
            }
            response.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_buttonCheckFirmwareActionPerformed

    private void buttonCreateAssemblyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCreateAssemblyActionPerformed
        try {
            validateForMachineActions();

            ResteasyClient client = new ResteasyClientBuilder().build();
            String server = (String) cbServer.getSelectedItem();

            addMessage("Sending firmware assebly request to server");
            Response response = client.target(server + "/server/rest/machine/firmware/assemble")
                    .request(MediaType.APPLICATION_XML)
                    .headers(createHeaders())
                    .header("Machine-Id", textMachineId.getText())
                    .header("Machine-Brand", cbBrand.getSelectedItem())
                    .post(Entity.entity(objToXml(FirmwareUpdateRequest.class, firmwareUpdateRequestSetting), MediaType.APPLICATION_XML));

            addMessage("Response " + response.getStatus());
            if (response.getStatus() == 500) {
                throw new Exception("Error from server " + response.getStatus());
            } else if (response.getStatus() == 474) {
                throw new Exception("No firmware is available");
            } else if (response.getStatus() == 475) {
                throw new Exception("Could not create firmware assembly");
            }

            String resp = response.readEntity(String.class);
            FirmwareAssembleResponse assembleResponse = xmlToObj(FirmwareAssembleResponse.class, resp);

            textAssemblyId.setText(assembleResponse.getAssemblyId());
            textFileSize.setText(String.valueOf(assembleResponse.getFileSize()));
            textMD5.setText(assembleResponse.getMd5());

            response.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }//GEN-LAST:event_buttonCreateAssemblyActionPerformed

    private void buttonDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDownloadActionPerformed
        try {
            validateForMachineActions();
            File downloadDirectory = new File(textDirectory.getText());
            if (!downloadDirectory.exists() || !downloadDirectory.canWrite()) {
                throw new Exception("Can't write files to " + textDirectory.getText());
            }

            //Ctreate temp file
            File tempFile = File.createTempFile("FIRMWARE_", ".zip", downloadDirectory);

            ResteasyClient client = new ResteasyClientBuilder().build();
            String server = (String) cbServer.getSelectedItem();

            //Calculate nr of chunks
            long fileSize = Long.parseLong(textFileSize.getText());
            long chunkSize = Long.parseLong(textChunkSize.getText());

            int numberOfChunks = (int) Math.ceil((float) fileSize / chunkSize);
            try (FileOutputStream firmwareFile = new FileOutputStream(tempFile)) {

                for (int i = 0; i < numberOfChunks; i++) {

                    FirmwareDownloadRequest request = new FirmwareDownloadRequest();
                    request.setAssemblyId(textAssemblyId.getText());
                    request.setChunkIndex(i);
                    request.setChunkSize(chunkSize);

                    addMessage("Sending firmware download request to server: chunk " + i);
                    Response response = client.target(server + "/server/rest/machine/firmware/download")
                            .request(MediaType.APPLICATION_XML)
                            .headers(createHeaders())
                            .header("Machine-Id", textMachineId.getText())
                            .header("Machine-Brand", cbBrand.getSelectedItem())
                            .post(Entity.entity(objToXml(FirmwareDownloadRequest.class, request), MediaType.APPLICATION_XML));

                    addMessage("Response " + response.getStatus());
                    if (response.getStatus() == 500) {
                        throw new Exception("Error from server " + response.getStatus());
                    }

                    String resp = response.readEntity(String.class);
                    FirmwareDownloadResponse downloadResponse = xmlToObj(FirmwareDownloadResponse.class, resp);
                    StringBuilder result = new StringBuilder();
                    result.append("Downloaded chunk ").append(i).append(" size ").append(downloadResponse.getChunkSize());
                    addMessage(result.toString());

                    //Calculate checksum for this chunk
                    String md5 = DigestUtils.md5Hex(downloadResponse.getChunk());

                    if (StringUtils.equals(md5, downloadResponse.getMd5())) {
                        addMessage("Checksum ND5 is correct");
                    } else {
                        throw new Exception("Check sum is not correct !!");
                    }

                    response.close();
                    firmwareFile.write(downloadResponse.getChunk());
                }
                
                addMessage("File " + tempFile.getName() + " is saved.");
                String md5 = DigestUtils.md5Hex(FileUtils.readFileToByteArray(tempFile));
                if (StringUtils.equals(md5, textMD5.getText())) {
                    addMessage("Checksum for entire download is correct");
                }else{
                    addMessage("Checksum for entire download is NOT correct: " + md5);
                }
                
                
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }//GEN-LAST:event_buttonDownloadActionPerformed

    private void buttomLoadDefaultFirmwareSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttomLoadDefaultFirmwareSettingActionPerformed
        firmwareUpdateRequestSetting = new FirmwareUpdateRequest();

        firmwareUpdateRequestSetting.setProductId("AHV291_Prototype");
        firmwareUpdateRequestSetting.setSystemVersion("123");
        firmwareUpdateRequestSetting.setUnits(new Units());
        firmwareUpdateRequestSetting.getUnits()
                .getUnit()
                .add(createUnit("u-boot", "445b8e4076104e9f811b08835be97538"));
        firmwareUpdateRequestSetting.getUnits()
                .getUnit()
                .add(createUnit("gui", "ec2e4e3d09501b8523d28a5b14aeb38a"));
        firmwareUpdateRequestSetting.getUnits()
                .getUnit()
                .add(createUnit("partitioning-rootfs", "b2d4b68605166605c5d62a6df7a998df"));
        firmwareUpdateRequestSetting.getUnits()
                .getUnit()
                .add(createUnit("mainnode", "8b6b3019b9cbc56118c8fb9eb4c3e989"));
        firmwareUpdateRequestSetting.getUnits()
                .getUnit()
                .add(createUnit("subnode", "1074ef94de55d9acea913f9ca4ea985a"));
        firmwareUpdateRequestSetting.getUnits()
                .getUnit()
                .add(createUnit("licenses", "0bff1f96ff1bace72bedfbfc852ffb84"));
        firmwareUpdateRequestSetting.getUnits()
                .getUnit()
                .add(createUnit("kernel", "ccc007f1bfd2146026cd3728d5375034"));
        firmwareUpdateRequestSetting.getUnits()
                .getUnit()
                .add(createUnit("designs-and-patterns", "18bb207bf9f40bf24c32362fc4de010a"));
        firmwareUpdateRequestSetting.getUnits()
                .getUnit()
                .add(createUnit("update-system", "272e5826b71a22261ae7911f6e0975fd"));
        firmwareUpdateRequestSetting.getUnits()
                .getUnit()
                .add(createUnit("sounds", "37f37eee0f62e661759338c3f81254a1"));

        updateFirmwareSettings();

    }//GEN-LAST:event_buttomLoadDefaultFirmwareSettingActionPerformed

    private void cbServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbServerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbServerActionPerformed

    private void radioDevelopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioDevelopActionPerformed
        selectedMode = Mode.DEVLOP;
        enableDisableComponents();
    }//GEN-LAST:event_radioDevelopActionPerformed

    private void radioProductionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioProductionActionPerformed
        selectedMode = Mode.PRODUCTION;
        enableDisableComponents();
    }//GEN-LAST:event_radioProductionActionPerformed

    private void buttonGenUUIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGenUUIDActionPerformed
        textUserUUID.setText(UUID.randomUUID().toString());
    }//GEN-LAST:event_buttonGenUUIDActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MachineForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MachineForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MachineForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MachineForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MachineForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttomLoadDefaultFirmwareSetting;
    private javax.swing.JButton buttomLoadFirmwareSettingFile;
    private javax.swing.JRadioButton buttonBobbinEmpty;
    private javax.swing.JRadioButton buttonChangeColor;
    private javax.swing.JButton buttonCheckFirmware;
    private javax.swing.JButton buttonCreateAssembly;
    private javax.swing.JRadioButton buttonCutThreadEnds;
    private javax.swing.JButton buttonDownload;
    private javax.swing.JButton buttonGenUUID;
    private javax.swing.JButton buttonLogin;
    private javax.swing.JButton buttonLogout;
    private javax.swing.JButton buttonSendNotification;
    private javax.swing.JButton buttonStartStopSewing;
    private javax.swing.JRadioButton buttonThreadBreak;
    private javax.swing.JComboBox cbBrand;
    private javax.swing.JComboBox cbServer;
    private javax.swing.ButtonGroup groupMode;
    private javax.swing.ButtonGroup groupNotification;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList listStatus;
    private javax.swing.JRadioButton radioDevelop;
    private javax.swing.JRadioButton radioProduction;
    private javax.swing.JTextField textAssemblyId;
    private javax.swing.JTextField textCheckFirmwareResult;
    private javax.swing.JTextField textChunkSize;
    private javax.swing.JTextField textColor;
    private javax.swing.JTextField textDirectory;
    private javax.swing.JTextField textEmail;
    private javax.swing.JTextField textFileSize;
    private javax.swing.JTextField textMD5;
    private javax.swing.JTextField textMachineId;
    private javax.swing.JTextField textModel;
    private javax.swing.JPasswordField textPassword;
    private javax.swing.JTextField textSessionToken;
    private javax.swing.JTextField textStitchesColorblock;
    private javax.swing.JTextField textSystemVersion;
    private javax.swing.JTextField textTotalStitches;
    private javax.swing.JTextField textUserUUID;
    // End of variables declaration//GEN-END:variables

    private void login() {

        try {
            validateForLogin();

            LoginMachine login = new LoginMachine();
            login.setEmail(textEmail.getText());
            login.setPassword(new String(textPassword.getPassword()));

            String sessionToken = sendLogin(login);
            if (sessionToken != null) {
                textSessionToken.setText(sessionToken);
                loggedIn = true;
            } else {
                textSessionToken.setText("");
                loggedIn = false;
            }

            enableDisableComponents();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

    }

    private void validateForLogin() throws Exception {

        //Check that  all fields are ok 
        if (StringUtils.isEmpty((String) cbBrand.getSelectedItem())) {
            throw new Exception("Brand is missing");
        }
        if (StringUtils.isEmpty(textMachineId.getText())) {
            throw new Exception("machineid is missing");
        }
        if (StringUtils.isEmpty(textEmail.getText())) {
            throw new Exception("email is missing");
        }
        if (textPassword.getPassword().length == 0) {
            throw new Exception("password is missing");
        }
        if (StringUtils.isEmpty((String) cbServer.getSelectedItem())) {
            throw new Exception("Server is missing");
        }

    }

    private void validateForMachineActions() throws Exception {

        //Check that  all fields are ok 
        if (StringUtils.isEmpty((String) cbBrand.getSelectedItem())) {
            throw new Exception("Brand is missing");
        }
        if (StringUtils.isEmpty(textMachineId.getText())) {
            throw new Exception("machineid is missing");
        }
    }

    public static <T> String objToXml(final Class<T> clazz, Object object)
            throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(object, sw);
        return sw.toString();
    }

    public static <T> T xmlToObj(final Class<T> clazz, String xml) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Object obj;
        try (StringReader reader = new StringReader(xml)) {
            obj = jaxbUnmarshaller.unmarshal(reader);
        }

        return (T) obj;
    }

    private String sendLogin(LoginMachine login) throws JAXBException {
        addMessage("Sending login to server");

        String server = (String) cbServer.getSelectedItem();
        String sessionToken = null;

        ResteasyClient client = new ResteasyClientBuilder().build();
        Response response = client
                .target(server + "/server/rest/im/login")
                .request(MediaType.APPLICATION_XML)
                .header("Machine-Id", textMachineId.getText())
                .header("Machine-Brand", cbBrand.getSelectedItem())
                .post(Entity.entity(objToXml(LoginMachine.class, login),
                                MediaType.APPLICATION_XML));

        if (response.getStatus() == 204) {
            sessionToken = response.getHeaderString("Session-Token");
            addMessage("Login succeeded sessiontoken " + sessionToken);

        } else {
            addMessage("Login failed response " + response.getStatus());
        }

        response.close();
        return sessionToken;
    }

    private boolean sendLogout() {

        addMessage("Sending logout for sessiontoken " + textSessionToken.getText());

        ResteasyClient client = new ResteasyClientBuilder().build();

        String server = (String) cbServer.getSelectedItem();
        Response response = client.target(server + "/server/rest/im/logout")
                .request(MediaType.APPLICATION_XML)
                .headers(createHeaders())
                .header("Machine-Id", textMachineId.getText())
                .header("Machine-Brand", cbBrand.getSelectedItem())
                .delete();

        int status = response.getStatus();
        addMessage("Response " + status);

        response.close();
        return status == 204;
    }

    private void enableDisableComponents() {
        textEmail.setEnabled(!loggedIn && selectedMode==Mode.DEVLOP);
        textPassword.setEnabled(!loggedIn && selectedMode==Mode.DEVLOP);
        if (!loggedIn && selectedMode==Mode.DEVLOP) {
            textSessionToken.setText(null);
        }
        buttonLogin.setEnabled(!loggedIn && selectedMode==Mode.DEVLOP);
        buttonLogout.setEnabled(loggedIn && selectedMode==Mode.DEVLOP);
        
        buttonGenUUID.setEnabled(selectedMode==Mode.PRODUCTION);
        textUserUUID.setEnabled(selectedMode==Mode.PRODUCTION);
        
        
        //buttonSendNotification.setEnabled(loggedIn);
        //buttonStartStopSewing.setEnabled(loggedIn);
    }

    private void sendEvent(String status) {
        System.out.println(status);
        ResteasyClient client = new ResteasyClientBuilder().build();
        String server = (String) cbServer.getSelectedItem();

        addMessage("Sending status to server");
        Response response = client.target(server + "/server/rest/machine/event")
                .request(MediaType.APPLICATION_XML)
                .headers(createHeaders())
                .header("Machine-Id", textMachineId.getText())
                .header("Machine-Brand", cbBrand.getSelectedItem())
                .post(Entity.entity(status, MediaType.APPLICATION_XML));

        addMessage("Response " + response.getStatus());

        response.close();

    }

    private void logout() {
        loggedIn = !sendLogout();
        enableDisableComponents();
    }

    public void addMessage(String msg) {
        if (msg != null && msg.length() > 0) {

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            // Sätt att komma runt problematiken med att SWING inte hanterar
            // multitrådning
            SwingUtilities.invokeLater(new UpdateStatus(listModelStatus,
                    formatter.format(new Date()) + " " + msg));

        }
    }

    private class UpdateStatus implements Runnable {

        private DefaultListModel lm;
        private String text;

        public UpdateStatus(DefaultListModel lm, String text) {
            this.lm = lm;
            this.text = text;
        }

        public void run() {
            lm.insertElementAt(text, 0);
        }
    }

    private MachineEvent getStartEventfromMachine(Embroidery embroidery) throws JAXBException {

        MachineEvent event = new MachineEvent();
        event.setId(EVENT_EMBROIDERY_STITCHOUT_START);
        event.setApiVersion(22222);
        event.setTime(new Date().getTime());
        event.setVolatile(true);
        event.setRequestId("111");
        event.setRequiredParameters(new RequiredParameters());

        event.getRequiredParameters()
                .getParameter()
                .add(createParameter("design-image-png", "STRING", embroidery.getDesignImg()));
        event.getRequiredParameters()
                .getParameter()
                .add(createParameter("design-num-stitches", "INTEGER", String.valueOf(embroidery.getDesignNumStitches())));

        for (int i = 0; i < embroidery.getColorBlocks().size(); i++) {
            ColorBlock block = embroidery.getColorBlocks().get(i);

            event.getRequiredParameters()
                    .getParameter()
                    .add(createParameter("color-block-num-stitches-" + i, "INTEGER", String.valueOf(block.getNumOfStitches())));
            event.getRequiredParameters()
                    .getParameter()
                    .add(createParameter("color-block-thread-name-" + i, "STRING", block.getThreadName()));
            event.getRequiredParameters()
                    .getParameter()
                    .add(createParameter("color-block-thread-color-" + i, "STRING", block.getThreadColor()));
            event.getRequiredParameters()
                    .getParameter()
                    .add(createParameter("color-block-thread-manufacturer-" + i, "STRING", block.getThreadManufacturer()));
            event.getRequiredParameters()
                    .getParameter()
                    .add(createParameter("color-block-thread-nickname-" + i, "STRING", block.getThreadNickname()));
            event.getRequiredParameters()
                    .getParameter()
                    .add(createParameter("color-block-thread-number-" + i, "INTEGER", String.valueOf(block.getThreadNumber())));
        }
        //
        event.setExtraParameters(new ExtraParameters());
        MachineEvent.ExtraParameters.Parameter extraParameter = new MachineEvent.ExtraParameters.Parameter();
        extraParameter.setKey("new-parameter");
        extraParameter.setType("STRING");
        extraParameter.setValue("some data");
        event.getExtraParameters()
                .getParameter()
                .add(extraParameter);
        extraParameter = new MachineEvent.ExtraParameters.Parameter();
        extraParameter.setKey("brand");
        extraParameter.setType("STRING");
        extraParameter.setValue("pfaff");
        event.getExtraParameters()
                .getParameter()
                .add(extraParameter);

        return event;
    }

    private MachineEvent getUpdateEventfromMachine(int block, int designStitchNumber, int blockStitchNumber) {
        MachineEvent event = new MachineEvent();
        event.setId(EVENT_EMBROIDERY_STITCHOUT_UPDATE);
        event.setApiVersion(22222);
        event.setTime(new Date().getTime());
        event.setVolatile(true);
        event.setRequestId("111");
        event.setRequiredParameters(new RequiredParameters());

        event.getRequiredParameters()
                .getParameter()
                .add(createParameter("design-stitch-number", "INTEGER", String.valueOf(designStitchNumber)));
        event.getRequiredParameters()
                .getParameter()
                .add(createParameter("color-block-stitch-number", "INTEGER", String.valueOf(blockStitchNumber)));
        event.getRequiredParameters()
                .getParameter()
                .add(createParameter("color-block-number", "INTEGER", String.valueOf(block)));

        return event;
    }

    private MachineEvent getEndEventfromMachine() {
        MachineEvent event = new MachineEvent();
        event.setId(EVENT_EMBROIDERY_STITCHOUT_END);
        event.setApiVersion(22222);
        event.setTime(new Date().getTime());
        event.setVolatile(true);
        event.setRequestId("111");
        event.setRequiredParameters(new RequiredParameters());

        return event;
    }

    private MachineEvent getThreadBreakEventfromMachine() {
        MachineEvent event = new MachineEvent();
        event.setId(EVENT_THREAD_BREAKAGE);
        event.setApiVersion(22222);
        event.setTime(new Date().getTime());
        event.setVolatile(true);
        event.setRequestId("111");
        event.setRequiredParameters(new RequiredParameters());
        return event;
    }

    private MachineEvent getBobbinEmptyEventfromMachine() {
        MachineEvent event = new MachineEvent();
        event.setId(EVENT_BOBBIN_EMPTY);
        event.setApiVersion(22222);
        event.setTime(new Date().getTime());
        event.setVolatile(true);
        event.setRequestId("111");
        event.setRequiredParameters(new RequiredParameters());
        return event;
    }

    private MachineEvent getEmbroideryFinishedEventfromMachine() {
        MachineEvent event = new MachineEvent();
        event.setId(EVENT_EMBROIDERY_FINISHED);
        event.setApiVersion(22222);
        event.setTime(new Date().getTime());
        event.setVolatile(true);
        event.setRequestId("111");
        event.setRequiredParameters(new RequiredParameters());
        return event;
    }

    private MachineEvent getCutThreadEventfromMachine() {
        MachineEvent event = new MachineEvent();
        event.setId(EVENT_CUT_THREAD);
        event.setApiVersion(22222);
        event.setTime(new Date().getTime());
        event.setVolatile(true);
        event.setRequestId("111");
        event.setRequiredParameters(new RequiredParameters());
        return event;
    }

    private MachineEvent getChangeThreadEventfromMachine(int block) {
        MachineEvent event = new MachineEvent();
        event.setId(EVENT_CHANGE_THREAD);
        event.setApiVersion(22222);
        event.setTime(new Date().getTime());
        event.setVolatile(true);
        event.setRequestId("111");
        event.setRequiredParameters(new RequiredParameters());
        event.getRequiredParameters()
                .getParameter()
                .add(createParameter("color-block-number", "INTEGER", String.valueOf(block)));
        return event;
    }

    private Parameter createParameter(String key, String type, String value) {
        Parameter parameter = new Parameter();
        parameter.setKey(key);
        parameter.setType(type);
        parameter.setValue(value);
        return parameter;
    }

    private Unit createUnit(String name, String checksum) {
        Unit unit = new Unit();
        unit.setName(name);
        unit.setChecksum(checksum);

        return unit;
    }

    private void updateFirmwareSettings() {
        StringBuilder units = new StringBuilder();
        units.append("Units in this firmware:\r\n");
        units.append("=======================\r\n");
        for (Unit unit : firmwareUpdateRequestSetting.getUnits().getUnit()) {
            units.append(unit.getName());
            units.append(" ---- ");
            units.append(unit.getChecksum());
            units.append("\r\n");
        }

        JOptionPane.showMessageDialog(this, new JTextArea(units.toString()));

        buttonCheckFirmware.setEnabled(true);
        buttonCreateAssembly.setEnabled(true);
        textModel.setText(firmwareUpdateRequestSetting.getProductId());
        textSystemVersion.setText(firmwareUpdateRequestSetting.getSystemVersion());
        textCheckFirmwareResult.setText("Not checked");
        textCheckFirmwareResult.setBackground(Color.BLUE);
        textAssemblyId.setText("");
        textFileSize.setText("");
        textMD5.setText("");

    }
    
    private MultivaluedMap<String, Object> createHeaders() {
            MultivaluedMap< String, Object> map = new MultivaluedHashMap<>();

            if(selectedMode == Mode.PRODUCTION){
                    map.add("x-user-id", textUserUUID.getText());
            }else{
                    map.add("Session-Token", textSessionToken.getText());
            }
            return map;
    }

}
