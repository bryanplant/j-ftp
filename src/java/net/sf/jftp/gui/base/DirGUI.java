package net.sf.jftp.gui.base;

import net.sf.jftp.config.Settings;
import net.sf.jftp.gui.base.dir.DirCanvas;
import net.sf.jftp.gui.base.dir.DirComponent;
import net.sf.jftp.gui.base.dir.DirEntry;
import net.sf.jftp.gui.framework.HImage;
import net.sf.jftp.gui.framework.HImageButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        label.setSize(getSize().width - 10, 24);
        currDirPanel.add(label);
        currDirPanel.setSize(getSize().width - 10, 32);
        label.setSize(getSize().width - 20, 24);

        p.setLayout(new BorderLayout());
        p.add("North", currDirPanel);
    }

    public void initButtons() {

    }

    public void addCommonButtons() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
