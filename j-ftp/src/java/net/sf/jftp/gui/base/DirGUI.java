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
    JToolBar buttonPanel;

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

    public void initButtons() {
        rnButton = new HImageButton(Settings.textFileImage, rnString,
                "Rename selected file or directory", this);
        rnButton.setToolTipText("Rename selected");

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

        cdUpButton = new HImageButton(Settings.cdUpImage, cdUpString,
                "Go to Parent Directory", this);
        cdUpButton.setToolTipText("Go to Parent Directory");

        label.setSize(getSize().width - 10, 24);
        currDirPanel.add(label);
        currDirPanel.setSize(getSize().width - 10, 32);
        label.setSize(getSize().width - 20, 24);
    }

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

    public void initPanels(int flowLayoutDirection, String secondDirection, HImageButton button) {
        buttonPanel = new JToolBar() {
            public Insets getInsets() {
                return new Insets(0, 0, 0, 0);
            }
        };

        FlowLayout f = new FlowLayout(flowLayoutDirection);
        f.setHgap(1);
        f.setVgap(2);

        buttonPanel.setFloatable(false);
        buttonPanel.setLayout(f);
        buttonPanel.setMargin(new Insets(0, 0, 0, 0));
        buttonPanel.setVisible(true);
        buttonPanel.setSize(getSize().width - 10, 32);

        setLayout(new BorderLayout());
        currDirPanel.setFloatable(false);
        p.setLayout(new BorderLayout());
        p.add("North", currDirPanel);
        p.add("South", buttonPanel);

        JPanel second = new JPanel();
        second.setLayout(new BorderLayout());
        second.add("Center", p);
        second.add(secondDirection, button);
        add("North", second);

        jsp = new JScrollPane(table);
        table.getSelectionModel().addListSelectionListener(this);

        AdjustmentListener adjustmentListener = new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                jsp.repaint();
                jsp.revalidate();
            }
        };

        jsp.getHorizontalScrollBar().addAdjustmentListener(adjustmentListener);
        jsp.getVerticalScrollBar().addAdjustmentListener(adjustmentListener);


        jsp.setSize(getSize().width - 20, getSize().height - 72);
        add("Center", jsp);
        jsp.setVisible(true);
    }

    public void addButtons() {
        buttonPanel.add(rnButton);
        buttonPanel.add(mkdirButton);

        buttonPanel.add(cdButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cdUpButton);
        buttonPanel.add(new JLabel("  "));
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
