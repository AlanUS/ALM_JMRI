// PaneProgAction.java

package jmri.jmrit.symbolicprog.tabbedframe;

import jmri.jmrit.decoderdefn.DecoderFile;
import jmri.jmrit.roster.RosterEntry;
import jmri.jmrit.symbolicprog.CombinedLocoSelTreePane;
import jmri.util.JmriJFrame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * Swing action to create and register a
 * frame for selecting the information needed to
 * open a PaneProgFrame in service mode.
 * <P>
 * The name is a historical accident, and probably should have
 * included "ServiceMode" or something.
 * <P>
 * The resulting JFrame
 * is constructed on the fly here, and has no specific type.
 *
 * @see  jmri.jmrit.symbolicprog.tabbedframe.PaneOpsProgAction
 *
 * @author			Bob Jacobsen    Copyright (C) 2001
 * @version			$Revision: 1.35 $
 */
public class PaneProgAction 			extends AbstractAction {

    Object o1, o2, o3, o4;
    JLabel statusLabel;
    jmri.jmrit.progsupport.ProgModeSelector  modePane    = new jmri.jmrit.progsupport.ProgDeferredServiceModePane();

    static final java.util.ResourceBundle rbt 
        = java.util.ResourceBundle.getBundle("jmri.jmrit.symbolicprog.SymbolicProgBundle");

    public PaneProgAction() {
        this("DecoderPro service programmer");
    }

    public PaneProgAction(String s) {
        super(s);

        statusLabel = new JLabel(rbt.getString("StateIdle"));

        // disable ourself if programming is not possible
        if (jmri.InstanceManager.programmerManagerInstance()==null ||
            !jmri.InstanceManager.programmerManagerInstance().isGlobalProgrammerAvailable()) {
            setEnabled(false);
            // This needs to return, so we don't start the xmlThread
	    return;
        }

    }

    public void actionPerformed(ActionEvent e) {

        if (log.isDebugEnabled()) log.debug("Pane programmer requested");

        // create the initial frame that steers
        final JmriJFrame f = new JmriJFrame(rbt.getString("FrameServiceProgrammerSetup"));
        f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
        
        // ensure status line is cleared on close so it is normal if re-opened
        f.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we){
            	statusLabel.setText(rbt.getString("StateIdle"));
            	f.windowClosing(we);}});

        // add the Roster menu
        JMenuBar menuBar = new JMenuBar();
        // menuBar.setBorder(new BevelBorder(BevelBorder.RAISED));
        JMenu j = new JMenu("File");
        j.add(new jmri.jmrit.decoderdefn.PrintDecoderListAction(rbt.getString("MenuPrintDecoderDefinitions"), f, false));
        j.add(new jmri.jmrit.decoderdefn.PrintDecoderListAction(rbt.getString("MenuPrintPreviewDecoderDefinitions"), f, true));
        menuBar.add(j);
        menuBar.add(new jmri.jmrit.roster.RosterMenu(rbt.getString("MenuRoster"), jmri.jmrit.roster.RosterMenu.MAINMENU, f));
        f.setJMenuBar(menuBar);

        // new Loco on programming track
        JPanel pane1 = new CombinedLocoSelTreePane(statusLabel){
                protected void startProgrammer(DecoderFile decoderFile, RosterEntry re,
                                                String filename) {
                    String title = java.text.MessageFormat.format(rbt.getString("FrameServiceProgrammerTitle"),
                                                        new Object[]{"new decoder"});
                    if (re!=null) title = java.text.MessageFormat.format(rbt.getString("FrameServiceProgrammerTitle"),
                                                        new Object[]{re.getId()});
                    JFrame p = new PaneServiceProgFrame(decoderFile, re,
                                                 title, "programmers"+File.separator+filename+".xml",
                                                 modePane.getProgrammer());
                    p.pack();
                    p.setVisible(true);

                    // f.setVisible(false);
                    // f.dispose();
                }
            };

        // load primary frame
        f.getContentPane().add(modePane);
        f.getContentPane().add(new JSeparator(javax.swing.SwingConstants.HORIZONTAL));

        pane1.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        f.getContentPane().add(pane1);
        f.getContentPane().add(new JSeparator(javax.swing.SwingConstants.HORIZONTAL));

        statusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        f.getContentPane().add(statusLabel);

        f.pack();
        if (log.isDebugEnabled()) log.debug("Tab-Programmer setup created");
        f.setVisible(true);
    }

    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PaneProgAction.class.getName());

}

/* @(#)PaneProgAction.java */
