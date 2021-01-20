package jmri.jmrit.logixng.tools.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import jmri.jmrit.logixng.FemaleSocket;
import jmri.InstanceManager;
import jmri.jmrit.logixng.*;
import jmri.jmrit.logixng.tools.debugger.AbstractDebuggerMaleSocket;
import jmri.jmrit.logixng.tools.debugger.Debugger;
import jmri.util.JmriJFrame;

/**
 * Editor of ConditionalNG
 * 
 * @author Daniel Bergqvist 2018
 */
public class ConditionalNGDebugger extends JmriJFrame implements PropertyChangeListener {

    private static final int panelWidth = 700;
    private static final int panelHeight = 500;
    
    private final Debugger _debugger = InstanceManager.getDefault(Debugger.class);
    private final TreePane _treePane;
    private final JMenuItem _runItem;
    private final JMenuItem _stepOverItem;
    private final JMenuItem _stepIntoItem;
    protected final ConditionalNG _conditionalNG;
    private AbstractDebuggerMaleSocket _currentMaleSocket;
    private State _currentState = State.None;
    private boolean _run = false;
    
    private final Object _lock = new Object();
    
    /**
     * Maintain a list of listeners -- normally only one.
     */
    private final List<ConditionalNGEventListener> listenerList = new ArrayList<>();
    
    /**
     * This contains a list of commands to be processed by the listener
     * recipient.
     */
    final Map<String, String> logixNGData = new HashMap<>();
    
    /**
     * Construct a ConditionalEditor.
     *
     * @param conditionalNG the ConditionalNG to be edited
     */
    public ConditionalNGDebugger(@Nonnull ConditionalNG conditionalNG) {
        
        _conditionalNG = conditionalNG;
        
        _treePane = new TreePane(conditionalNG.getFemaleSocket());
        _treePane.initComponents((FemaleSocket femaleSocket, JPanel panel) -> {
            
            if (femaleSocket.isConnected()) {
                MaleSocket maleSocket = femaleSocket.getConnectedSocket();
                AbstractDebuggerMaleSocket debugMaleSocket =
                        (AbstractDebuggerMaleSocket) maleSocket.find(AbstractDebuggerMaleSocket.class);
                if (debugMaleSocket == null) throw new RuntimeException("AbstractDebuggerMaleSocket is not found");
                boolean breakpointBefore = debugMaleSocket.getBreakpointBefore();
                boolean breakpointAfter = debugMaleSocket.getBreakpointAfter();
                if (breakpointBefore || breakpointAfter) {
                    JPanel newPanel = new JPanel();
                    newPanel.setBorder(BorderFactory.createMatteBorder(
                            breakpointBefore ? 5 : 1,
                            5,
                            breakpointAfter ? 5 : 1,
                            1, Color.red));
                    newPanel.add(panel);
                    panel = newPanel;
                }
            }
            if (_currentMaleSocket != null) {
                Base parent = _currentMaleSocket.getParent();
                while ((parent != null) && (!(parent instanceof FemaleSocket))) {
                    parent = parent.getParent();
                }
                if (parent == femaleSocket) {
                    JPanel newPanel = new JPanel();
                    switch (_currentState) {
                        case Before:
                            newPanel.setBorder(BorderFactory.createMatteBorder(8, 5, 1, 1, Color.black));
                            break;
                        case After:
                            newPanel.setBorder(BorderFactory.createMatteBorder(1, 5, 8, 1, Color.black));
                            break;
                        default:
                            // Return without adding a border
                            return panel;
                    }
                    newPanel.add(panel);
                    return newPanel;
                }
            }
            return panel;
        });
        
        // build menu
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu(Bundle.getMessage("MenuFile"));
        JMenuItem closeWindowItem = new JMenuItem(Bundle.getMessage("CloseWindow"));
        closeWindowItem.addActionListener((ActionEvent e) -> {
            dispose();
        });
        fileMenu.add(closeWindowItem);
        menuBar.add(fileMenu);
        
        JMenu debugMenu = new JMenu(Bundle.getMessage("Debug_MenuDebug"));
        
        _runItem = new JMenuItem(Bundle.getMessage("Debug_MenuItem_Run"));
        _runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, ActionEvent.CTRL_MASK));
        debugMenu.add(_runItem);
        
        _stepOverItem = new JMenuItem(Bundle.getMessage("Debug_MenuItem_StepOver"));
        _stepOverItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, ActionEvent.SHIFT_MASK));
        _stepIntoItem = new JMenuItem(Bundle.getMessage("Debug_MenuItem_StepInto"));
        _stepIntoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
        _stepOverItem.setEnabled(false);
        _stepIntoItem.setEnabled(false);
        
        _stepOverItem.addActionListener((ActionEvent e) -> {
            AbstractDebuggerMaleSocket maleSocket = _currentMaleSocket;
            _currentMaleSocket.setStepInto(false);
            _currentMaleSocket = null;
            _currentState = State.None;
            _stepOverItem.setEnabled(false);
            _stepIntoItem.setEnabled(false);
            _treePane.updateTree(maleSocket);
            synchronized(_lock) {
                _lock.notify();
            }
        });
        debugMenu.add(_stepOverItem);
        
        _stepIntoItem.addActionListener((ActionEvent e) -> {
            AbstractDebuggerMaleSocket maleSocket = _currentMaleSocket;
            _currentMaleSocket.setStepInto(true);
            _currentMaleSocket = null;
            _currentState = State.None;
            _stepOverItem.setEnabled(false);
            _stepIntoItem.setEnabled(false);
            _treePane.updateTree(maleSocket);
            synchronized(_lock) {
                _lock.notify();
            }
        });
        debugMenu.add(_stepIntoItem);
        
        _runItem.addActionListener((event) -> {
            _run = true;
            
            if (_currentMaleSocket != null) {
                AbstractDebuggerMaleSocket maleSocket = _currentMaleSocket;
                _currentMaleSocket.setStepInto(false);
                _currentMaleSocket = null;
                _currentState = State.None;
                _stepOverItem.setEnabled(false);
                _stepIntoItem.setEnabled(false);
                _treePane.updateTree(maleSocket);
                synchronized(_lock) {
                    _lock.notify();
                }
            }
        });
        
        menuBar.add(debugMenu);
        
        setJMenuBar(menuBar);
//        addHelpMenu("package.jmri.jmrit.operations.Operations_Settings", true); // NOI18N
        
        if (_conditionalNG.getUserName() == null) {
            setTitle(Bundle.getMessage("TitleEditConditionalNG", _conditionalNG.getSystemName()));
        } else {
            setTitle(Bundle.getMessage("TitleEditConditionalNG2", _conditionalNG.getSystemName(), _conditionalNG.getUserName()));
        }
        
        JPanel variablePanel = new JPanel();
        JScrollPane variableScrollPane = new JScrollPane(variablePanel);
        
        JPanel watchPanel = new JPanel();
        JScrollPane watchScrollPane = new JScrollPane(watchPanel);
        
        JSplitPane watchSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                           variableScrollPane, watchScrollPane);
        watchSplitPane.setOneTouchExpandable(true);
        watchSplitPane.setDividerLocation(150);
        
        // Provide minimum sizes for the two components in the split pane
        Dimension minimumWatchSize = new Dimension(100, 50);
        variableScrollPane.setMinimumSize(minimumWatchSize);
        watchScrollPane.setMinimumSize(minimumWatchSize);
        
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                           _treePane, watchSplitPane);
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setDividerLocation(150);
        
        // Provide minimum sizes for the two components in the split pane
        Dimension minimumMainSize = new Dimension(100, 50);
        _treePane.setMinimumSize(minimumMainSize);
        watchSplitPane.setMinimumSize(minimumMainSize);
        
        // add panels
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(mainSplitPane);
        
        initMinimumSize(new Dimension(panelWidth, panelHeight));
        
        _debugger.addPropertyChangeListener(this);
        _debugger.activateDebugger(conditionalNG);
        
        PopupMenu popup = new PopupMenu();
        popup.init();
    }
    
    public void initMinimumSize(Dimension dimension) {
        setMinimumSize(dimension);
        pack();
        setVisible(true);
    }
    
    /** {@inheritDoc} */
    @Override
    public void windowClosed(WindowEvent e) {
        _debugger.deActivateDebugger();
        logixNGData.clear();
        logixNGData.put("Finish", _conditionalNG.getSystemName());  // NOI18N
        fireLogixNGEvent();
    }
    
    public void addLogixNGEventListener(ConditionalNGEventListener listener) {
        listenerList.add(listener);
    }
    
    /**
     * Notify the listeners to check for new data.
     */
    void fireLogixNGEvent() {
        for (ConditionalNGEventListener l : listenerList) {
            l.conditionalNGEventOccurred();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Debugger.STEP_BEFORE.equals(evt.getPropertyName())
                || Debugger.STEP_AFTER.equals(evt.getPropertyName())) {
            
            _currentMaleSocket = (AbstractDebuggerMaleSocket) evt.getNewValue();
            
            switch (evt.getPropertyName()) {
                case Debugger.STEP_BEFORE:
//                    if (!_run || _currentMaleSocket.getBreakpointBefore()) {
                        _currentState = State.Before;
//                    }
                    break;
                case Debugger.STEP_AFTER:
//                    if (!_run || _currentMaleSocket.getBreakpointBefore()) {
                        _currentState = State.After;
//                    } else {
//                        _currentState = State.None;
//                    }
                    break;
                default:
                    _currentState = State.None;
            }
            
            jmri.util.ThreadingUtil.runOnGUIEventually(() -> {
                _stepOverItem.setEnabled(true);
                _stepIntoItem.setEnabled(true);
                _treePane.updateTree(_currentMaleSocket);
            });
            
            try {
                synchronized(_lock) {
                    _lock.wait();
                }
            } catch (InterruptedException e) {
                log.error("LogixNG thread was interrupted: {}", _conditionalNG.getCurrentThread().getThreadName());
                Thread.currentThread().interrupt();
            }
        }
    }
    
    
    public interface ConditionalNGEventListener extends EventListener {
        
        public void conditionalNGEventOccurred();
    }
    
    
    private static enum State {
        None,
        Before,
        After,
    }
    
    
    protected class PopupMenu extends JPopupMenu implements ActionListener {
        
        private static final String ACTION_COMMAND_BREAKPOINT_BEFORE = "breakpoint_before";
        private static final String ACTION_COMMAND_BREAKPOINT_AFTER = "breakpoint_after";
//        private static final String ACTION_COMMAND_EXPAND_TREE = "expandTree";
        
        private final JTree _tree;
        private FemaleSocket _currentFemaleSocket;
        private TreePath _currentPath;
        
        private JMenuItem menuItemBreakpointBefore;
        private JMenuItem menuItemBreakpointAfter;
//        private JMenuItem menuItemExpandTree;
        
        PopupMenu() {
            if (_treePane._tree == null) throw new IllegalArgumentException("_tree is null");
            _tree = _treePane._tree;
        }
        
        private void init() {
            menuItemBreakpointBefore = new JMenuItem(Bundle.getMessage("PopupMenuBreakpointBefore"));
            menuItemBreakpointBefore.addActionListener(this);
            menuItemBreakpointBefore.setActionCommand(ACTION_COMMAND_BREAKPOINT_BEFORE);
            add(menuItemBreakpointBefore);
            addSeparator();
            menuItemBreakpointAfter = new JMenuItem(Bundle.getMessage("PopupMenuBreakpointAfter"));
            menuItemBreakpointAfter.addActionListener(this);
            menuItemBreakpointAfter.setActionCommand(ACTION_COMMAND_BREAKPOINT_AFTER);
            add(menuItemBreakpointAfter);
/*            
            addSeparator();
            menuItemExpandTree = new JMenuItem(Bundle.getMessage("PopupMenuExpandTree"));
            menuItemExpandTree.addActionListener(this);
            menuItemExpandTree.setActionCommand(ACTION_COMMAND_EXPAND_TREE);
            add(menuItemExpandTree);
*/            
            setOpaque(true);
            setLightWeightPopupEnabled(true);
            
            final PopupMenu popupMenu = this;
            
            _tree.addMouseListener(
                    new MouseAdapter() {
                        
                        // On Windows, the popup is opened on mousePressed,
                        // on some other OS, the popup is opened on mouseReleased
                        
                        @Override
                        public void mousePressed(MouseEvent e) {
                            openPopupMenu(e);
                        }
                        
                        @Override
                        public void mouseReleased(MouseEvent e) {
                            openPopupMenu(e);
                        }
                        
                        private void openPopupMenu(MouseEvent e) {
                            if (e.isPopupTrigger() && !popupMenu.isVisible()) {
                                // Get the row the user has clicked on
                                TreePath path = _tree.getClosestPathForLocation(e.getX(), e.getY());
                                if (path != null) {
                                    // Check that the user has clicked on a row.
                                    Rectangle rect = _tree.getPathBounds(path);
                                    if ((e.getY() >= rect.y) && (e.getY() <= rect.y + rect.height)) {
                                        FemaleSocket femaleSocket = (FemaleSocket) path.getLastPathComponent();
                                        _tree.getLocationOnScreen();
                                        _tree.getX();
                                        showPopup(e.getX(), e.getY(), femaleSocket, path);
                                    }
                                }
                            }
                        }
                    }
            );
        }
        
        private void showPopup(int x, int y, FemaleSocket femaleSocket, TreePath path) {
            _currentFemaleSocket = femaleSocket;
            _currentPath = path;
            
            boolean isConnected = femaleSocket.isConnected();
            
            menuItemBreakpointBefore.setEnabled(isConnected);
            menuItemBreakpointAfter.setEnabled(isConnected);
            
            show(_tree, x, y);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case ACTION_COMMAND_BREAKPOINT_BEFORE:
                    MaleSocket maleSocket1 = _currentFemaleSocket.getConnectedSocket();
                    AbstractDebuggerMaleSocket debugMaleSocket1 =
                            (AbstractDebuggerMaleSocket) maleSocket1.find(AbstractDebuggerMaleSocket.class);
                    if (debugMaleSocket1 == null) throw new RuntimeException("AbstractDebuggerMaleSocket is not found");
                    // Invert breakpoint setting
                    debugMaleSocket1.setBreakpointBefore(!debugMaleSocket1.getBreakpointBefore());
                    for (TreeModelListener l : _treePane.femaleSocketTreeModel.listeners) {
                        TreeModelEvent tme = new TreeModelEvent(
                                _currentFemaleSocket,
                                _currentPath.getPath()
                        );
                        l.treeNodesChanged(tme);
                    }
                    _treePane._tree.updateUI();
                    break;
                    
                case ACTION_COMMAND_BREAKPOINT_AFTER:
                    MaleSocket maleSocket2 = _currentFemaleSocket.getConnectedSocket();
                    AbstractDebuggerMaleSocket debugMaleSocket2 =
                            (AbstractDebuggerMaleSocket) maleSocket2.find(AbstractDebuggerMaleSocket.class);
                    if (debugMaleSocket2 == null) throw new RuntimeException("AbstractDebuggerMaleSocket is not found");
                    // Invert breakpoint setting
                    debugMaleSocket2.setBreakpointAfter(!debugMaleSocket2.getBreakpointAfter());
                    for (TreeModelListener l : _treePane.femaleSocketTreeModel.listeners) {
                        TreeModelEvent tme = new TreeModelEvent(
                                _currentFemaleSocket,
                                _currentPath.getPath()
                        );
                        l.treeNodesChanged(tme);
                    }
                    _treePane._tree.updateUI();
                    break;
/*                    
                case ACTION_COMMAND_EXPAND_TREE:
                    // jtree expand sub tree
                    // https://stackoverflow.com/questions/15210979/how-do-i-auto-expand-a-jtree-when-setting-a-new-treemodel
                    // https://www.tutorialspoint.com/how-to-expand-jtree-row-to-display-all-the-nodes-and-child-nodes-in-java
                    // To expand all rows, do this:
                    for (int i = 0; i < tree.getRowCount(); i++) {
                        tree.expandRow(i);
                    }
                    
                    tree.expandPath(_currentPath);
                    tree.updateUI();
                    break;
*/                    
                default:
                    // Do nothing
            }
        }
    }
    
    
    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConditionalNGDebugger.class);

}
