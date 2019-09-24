package jmri.jmrit.logixng.digital.actions_with_change.configureswing;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.swing.JPanel;
import jmri.InstanceManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.digital.actions_with_change.OnChangeAction;
import jmri.jmrit.logixng.swing.SwingConfiguratorInterface;
import jmri.jmrit.logixng.DigitalActionWithChangeManager;

/**
 * Configures an ActionTurnout object with a Swing JPanel.
 */
public class OnChangeActionSwing implements SwingConfiguratorInterface {

    private JPanel panel;
    OnChangeAction.ChangeType type = OnChangeAction.ChangeType.CHANGE;
    
    
    /** {@inheritDoc} */
    @Override
    public JPanel getConfigPanel() throws IllegalArgumentException {
        createPanel();
        return panel;
    }
    
    /** {@inheritDoc} */
    @Override
    public JPanel getConfigPanel(@Nonnull Base object) throws IllegalArgumentException {
        createPanel();
        return panel;
    }
    
    private void createPanel() {
        panel = new JPanel();
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean validate(@Nonnull StringBuilder errorMessage) {
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    public MaleSocket createNewObject(@Nonnull String systemName, @CheckForNull String userName) {
        OnChangeAction action = new OnChangeAction(systemName, userName, type);
        return InstanceManager.getDefault(DigitalActionWithChangeManager.class).registerAction(action);
    }
    
    /** {@inheritDoc} */
    @Override
    public void updateObject(@Nonnull Base object) {
        // Do nothing
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return Bundle.getMessage("IfThen_Short");
    }
    
    @Override
    public void dispose() {
    }
    
}
