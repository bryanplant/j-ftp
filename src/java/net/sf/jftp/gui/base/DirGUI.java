package net.sf.jftp.gui.base;

import net.sf.jftp.JFtp;
import net.sf.jftp.config.Settings;
import net.sf.jftp.gui.base.dir.DirCanvas;
import net.sf.jftp.gui.base.dir.DirComponent;
import net.sf.jftp.gui.base.dir.DirEntry;
import net.sf.jftp.gui.base.dir.TableUtils;
import net.sf.jftp.gui.framework.HImage;
import net.sf.jftp.gui.framework.HImageButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DirGUI extends DirComponent implements ActionListener {
    static final String deleteString = "rm";
    static final String mkdirString = "mkdir";
    static final String refreshString = "fresh";
    static final String cdString = "cd";
    static final String cmdString = "cmd";
    static final String downloadString = "<-";
    static final String uploadString = "->";
    static final String zipString = "zip";
    static final String cpString = "cp";
    static final String queueString = "que";
    static final String cdUpString = "cdUp";
    static final String rnString = "rn";
    HImageButton deleteButton;
    HImageButton mkdirButton;
    HImageButton cmdButton;
    HImageButton refreshButton;
    HImageButton cdButton;
    HImageButton uploadButton;
    HImageButton downloadButton;
    HImageButton queueButton;
    HImageButton cdUpButton;
    HImageButton zipButton;
    HImageButton cpButton;
    HImageButton rnButton;
    HImageButton list = new HImageButton(Settings.listImage, "list",
            "Show remote listing...", this);
    HImageButton transferType = new HImageButton(Settings.typeImage,
            "type",
            "Toggle transfer type...",
            this);

    DirCanvas label = new DirCanvas(this);
    boolean pathChanged = true;
    boolean firstGui = true;
    JPanel p = new JPanel();
    JToolBar buttonPanel = new JToolBar() {
        public Insets getInsets() {
            return new Insets(0, 0, 0, 0);
        }
    };
    JToolBar currDirPanel = new JToolBar() {
        public Insets getInsets() {
            return new Insets(0, 0, 0, 0);
        }
    };

    DefaultListModel jlm;
    JScrollPane jsp = new JScrollPane(jl);
    int tmpindex = -1;
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem props = new JMenuItem("Properties");
    JMenuItem runFile = new JMenuItem("Launch file");
    JMenuItem viewFile = new JMenuItem("View file");
    DirEntry currentPopup = null;
    String sortMode = null;
    String[] sortTypes = new String[]{"Normal", "Reverse", "Size", "Size/Re"};
    JComboBox sorter = new JComboBox(sortTypes);
    boolean dateEnabled = false;

    public void gui_init(FlowLayout f) {
        setLayout(new BorderLayout());
        currDirPanel.setFloatable(false);
        buttonPanel.setFloatable(false);

        f.setHgap(1);
        f.setVgap(2);

        buttonPanel.setLayout(f);
        buttonPanel.setMargin(new Insets(0, 0, 0, 0));

        props.addActionListener(this);
        popupMenu.add(props);

        label.setSize(getSize().width - 10, 24);
        currDirPanel.add(label);
        currDirPanel.setSize(getSize().width - 10, 32);
        label.setSize(getSize().width - 20, 24);

        p.setLayout(new BorderLayout());
        p.add("North", currDirPanel);
    }

    public void initButtons() {
        deleteButton = new HImageButton(Settings.deleteImage, deleteString,
                "Delete selected", this);
        deleteButton.setToolTipText("Delete selected");

        mkdirButton = new HImageButton(Settings.mkdirImage, mkdirString,
                "Create a new directory", this);
        mkdirButton.setToolTipText("Create directory");

        refreshButton = new HImageButton(Settings.refreshImage, refreshString,
                "Refresh current directory", this);
        refreshButton.setToolTipText("Refresh directory");
        refreshButton.setRolloverIcon(new ImageIcon(HImage.getImage(this, Settings.refreshImage2)));
        refreshButton.setRolloverEnabled(true);

        cdButton = new HImageButton(Settings.cdImage, cdString,
                "Change directory", this);
        cdButton.setToolTipText("Change directory");
    }

    public void addCommonButtons() {
        buttonPanel.add(refreshButton);
        buttonPanel.add(new JLabel("  "));

        buttonPanel.add(rnButton);
        buttonPanel.add(mkdirButton);

        buttonPanel.add(cdButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cdUpButton);
        buttonPanel.add(new JLabel("  "));
    }

    public void initMouseListener() {
        // add this because we need to fetch only doubleclicks
        MouseListener mouseListener = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                DirGUI.this.mousePressed(e);
            }

            public void mouseClicked(MouseEvent e) {
                DirGUI.this.mouseClicked(e);
            }
        };

        table.addMouseListener(mouseListener);
    }

    public void mousePressed(MouseEvent e) {
        if (JFtp.uiBlocked) {
            return;
        }

        if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
            int index = jl.getSelectedIndex() - 1;

            if (index < -1) {
                return;
            }

            String tgt = (String) jl.getSelectedValue().toString();

            if (index < 0) {
            } else if ((dirEntry == null) || (dirEntry.length < index) ||
                    (dirEntry[index] == null)) {
                return;
            } else {
                currentPopup = dirEntry[index];
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (JFtp.uiBlocked) {
            return;
        }

        TableUtils.copyTableSelectionsToJList(jl, table);

        //System.out.println("DirEntryListener::");
        if (e.getClickCount() == 2) {
            //System.out.println("2xList selection: "+jl.getSelectedValue().toString());
            int index = jl.getSelectedIndex() - 1;

            // mousewheel bugfix
            if (index < -1) {
                return;
            }

            String tgt = (String) jl.getSelectedValue().toString();

            if (index < 0) {
                doChdir(path + tgt);
            } else if ((dirEntry == null) || (dirEntry.length < index) ||
                    (dirEntry[index] == null)) {
                return;
            } else if (dirEntry[index].isDirectory()) {
                doChdir(path + tgt);
            } else if (dirEntry[index].isLink()) {
                if (!con.chdir(path + tgt)) {
                    showContentWindow(path +
                                    dirEntry[index].toString(),
                            dirEntry[index]);

                    //blockedTransfer(index);
                }
            } else {
                showContentWindow(path + dirEntry[index].toString(),
                        dirEntry[index]);

                //blockedTransfer(index);
            }
        }
    }

    public void doChdir(String path) {
        JFtp.setAppCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        con.chdir(path);
        JFtp.setAppCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void showContentWindow(String url, DirEntry d) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
