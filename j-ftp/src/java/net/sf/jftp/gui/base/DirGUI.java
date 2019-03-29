package net.sf.jftp.gui.base;

import net.sf.jftp.JFtp;
import net.sf.jftp.config.Settings;
import net.sf.jftp.gui.base.dir.DirCanvas;
import net.sf.jftp.gui.base.dir.DirComponent;
import net.sf.jftp.gui.base.dir.DirEntry;
import net.sf.jftp.gui.base.dir.TableUtils;
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
    static final String queueString = "que";
    static final String zipString = "zip";
    static final String cpString = "cp";
    static final String cdUpString = "cdUp";
    static final String rnString = "rn";

    HImageButton deleteButton;
    HImageButton mkdirButton;
    HImageButton cmdButton;
    HImageButton refreshButton;
    HImageButton cdButton;
    HImageButton uploadButton;
    HImageButton downloadButton;
    HImageButton zipButton;
    HImageButton cpButton;
    HImageButton queueButton;
    HImageButton cdUpButton;
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
    JMenuItem runFile = new JMenuItem("Launch file");
    JMenuItem viewFile = new JMenuItem("View file");
    JMenuItem props = new JMenuItem("Properties");
    DirEntry currentPopup = null;
    String sortMode = null;
    String[] sortTypes = new String[]{"Normal", "Reverse", "Size", "Size/Re"};
    JComboBox sorter = new JComboBox(sortTypes);
    boolean dateEnabled = false;

    public void initMouseListener() {
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DirGUI.this.mouseClicked(e);
            }

            public void mousePressed(MouseEvent e) {
                DirGUI.this.mousePressed(e);
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

            // mousewheel bugfix, ui refresh bugfix
            if (index < -1) {
                return;
            }

            String tgt = (String) jl.getSelectedValue().toString();

            //System.out.println("List selection: "+index);
            if (index < 0) {
                doChdir(path + tgt);
            } else if ((dirEntry == null) || (dirEntry.length < index) ||
                    (dirEntry[index] == null)) {
                return;
            } else if (dirEntry[index].isDirectory()) {
                doChdir(path + tgt);
            } else {
                showContentWindow(path + dirEntry[index].toString(),
                        dirEntry[index]);
            }
        }
    }

    public void doChdir(String path) {

    }

    public void showContentWindow(String url, DirEntry d) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
