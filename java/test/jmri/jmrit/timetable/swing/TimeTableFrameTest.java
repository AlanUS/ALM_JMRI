package jmri.jmrit.timetable.swing;

import java.awt.GraphicsEnvironment;
import jmri.jmrit.timetable.*;
import jmri.util.JUnitUtil;
import org.junit.*;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.operators.*;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

/**
 * Tests for the TimeTableFrame Class
 * @author Dave Sand Copyright (C) 2018
 */
public class TimeTableFrameTest {
    TimeTableFrame _ttf = null;
    JFrameOperator _jfo = null;
    JTreeOperator _jto = null;
    JTextFieldOperator _jtxt = null;
    JButtonOperator _jbtn = null;

    @Test
    public void testCreatEmtpy() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        TimeTableFrame f = new TimeTableFrame();
        Assert.assertNotNull(f);
    }

    @Test
    public void testDriver() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        _ttf = new TimeTableFrame("");
        Assert.assertNotNull(_ttf);
        _ttf.setVisible(true);
        _jfo = new JFrameOperator(Bundle.getMessage("TitleTimeTable"));  // NOI18N
        Assert.assertNotNull(_jfo);
        _jto = new JTreeOperator(_jfo);
        Assert.assertNotNull(_jto);

        menuTests();
        addTests();
        deleteTests();
        addTests();
        deleteLayout();
        addTests();
        deleteSections();
        editTests();
        timeRangeTests();
        deleteDialogTests();
        buttonTests();
    }

// new EventTool().waitNoEvent(10000);

    void menuTests() {
        JMenuBarOperator jmbo = new JMenuBarOperator(_jfo); // there's only one menubar
        JMenuOperator jmo = new JMenuOperator(jmbo, Bundle.getMessage("MenuTimetable"));  // NOI18N
        JPopupMenu jpm = jmo.getPopupMenu();

        JMenuItem timeMenuItem = (JMenuItem)jpm.getComponent(0);
        Assert.assertTrue(timeMenuItem.getText().equals(Bundle.getMessage("MenuTrainTimes")));  // NOI18N
        new JMenuItemOperator(timeMenuItem).doClick();

        Thread openDialog = createModalDialogOperatorThread("Open", Bundle.getMessage("ButtonCancel"));  // NOI18N
        JMenuItem importMenuItem = (JMenuItem)jpm.getComponent(2);
        Assert.assertTrue(importMenuItem.getText().equals(Bundle.getMessage("MenuImport")));  // NOI18N
        new JMenuItemOperator(importMenuItem).doClick();
        JUnitUtil.waitFor(()->{return !(openDialog.isAlive());}, "open dialog finished");
    }

    void addTests() {
        // Add a new layout
        _jto.clickOnPath(_jto.findPath(new String[]{"Sample"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("AddLayoutButtonText")).doClick();  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();
        _jtxt.setText("Test Layout");  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 1);
        _jtxt.clickMouse();
        _jtxt.setText("6");
        _jtxt = new JTextFieldOperator(_jfo, 2);
        _jtxt.clickMouse();
        _jtxt.setText("5");
        new JCheckBoxOperator(_jfo, 0).doClick();
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        // Add a train type
        _jto.clickOnPath(_jto.findPath(new String[]{"Test Layout", "Train Types"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("AddTrainTypeButtonText")).doClick();  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();
        _jtxt.setText("New Train Type");  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        // Add a segment
        _jto.clickOnPath(_jto.findPath(new String[]{"Test Layout", "Segments"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("AddSegmentButtonText")).doClick();  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();
        _jtxt.setText("Mainline");  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        // Add a station 1
        _jto.clickOnPath(_jto.findPath(new String[]{"Test Layout", "Segments", "Mainline"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("AddStationButtonText")).doClick();  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();
        _jtxt.setText("Station 1");  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 1);
        _jtxt.clickMouse();
        _jtxt.setText("0");
        new JCheckBoxOperator(_jfo, 0).doClick();
        new JSpinnerOperator(_jfo, 0).setValue(1);
        new JSpinnerOperator(_jfo, 1).setValue(1);
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        // Add a station 2
        _jto.clickOnPath(_jto.findPath(new String[]{"Test Layout", "Segments", "Mainline"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("AddStationButtonText")).doClick();  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();
        _jtxt.setText("Station 2");  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 1);
        _jtxt.clickMouse();
        _jtxt.setText("50");
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        // Add a schedule
        _jto.clickOnPath(_jto.findPath(new String[]{"Test Layout", "Schedules"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("AddScheduleButtonText")).doClick();  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();
        _jtxt.setText("Test Schedule");  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 1);
        _jtxt.clickMouse();
        _jtxt.setText("Today");
        new JSpinnerOperator(_jfo, 0).setValue(1);
        new JSpinnerOperator(_jfo, 1).setValue(22);
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        // Add a train
        _jto.clickOnPath(_jto.findPath(new String[]{"Test Layout", "Schedules",  // NOI18N
                "Test Schedule   Effective Date: Today"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("AddTrainButtonText")).doClick();  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();
        _jtxt.setText("TRN");  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 1);
        _jtxt.clickMouse();
        _jtxt.setText("Test Train");  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 2);
        _jtxt.clickMouse();
        _jtxt.setText("10");
        _jtxt = new JTextFieldOperator(_jfo, 3);
        _jtxt.clickMouse();
        _jtxt.setText("12:00");
        new JComboBoxOperator(_jfo, 0).selectItem("New Train Type");  // NOI18N
        new JSpinnerOperator(_jfo, 0).setValue(1);
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        // Add stop 1
        new JButtonOperator(_jfo, Bundle.getMessage("AddStopButtonText")).doClick();  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        // Add stop 2
        _jto.clickOnPath(_jto.findPath(new String[]{"Test Layout", "Schedules",  // NOI18N
                "Test Schedule   Effective Date: Today", "TRN -- Test Train"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("AddStopButtonText")).doClick();  // NOI18N
        new JComboBoxOperator(_jfo, 0).selectItem("Station 2");  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 1);
        _jtxt.clickMouse();
        _jtxt.setText("20");
        new JSpinnerOperator(_jfo, 0).setValue(1);
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        JLabelOperator jlo = new JLabelOperator(_jfo, 7);
        Assert.assertEquals("14:37", jlo.getText());
    }

    void editTests() {
        // Layout: Bad fastclock value, good value to force recalc, throttle too low.
        _jto.clickOnPath(_jto.findPath(new String[]{"Sample"}));  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 1);
        _jtxt.clickMouse();
        _jtxt.setText("bad fast clock");  // NOI18N
        Thread edit1 = createModalDialogOperatorThread(Bundle.getMessage("WarningTitle"), Bundle.getMessage("ButtonOK"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(edit1.isAlive());}, "edit1 finished");  // NOI18N

        _jtxt = new JTextFieldOperator(_jfo, 1);
        _jtxt.clickMouse();
        _jtxt.setText("5");  // NOI18N

        _jtxt = new JTextFieldOperator(_jfo, 2);
        _jtxt.clickMouse();
        _jtxt.setText("1");  // NOI18N
        Thread edit2 = createModalDialogOperatorThread(Bundle.getMessage("WarningTitle"), Bundle.getMessage("ButtonOK"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(edit2.isAlive());}, "edit2 finished");  // NOI18N

        // Station:  Distance and staging track.
        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Segments", "Mainline", "Alpha"}));  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 1);
        _jtxt.clickMouse();
        _jtxt.setText("-5.0");  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        _jtxt.clickMouse();
        _jtxt.setText("bad distance");  // NOI18N
        Thread edit3 = createModalDialogOperatorThread(Bundle.getMessage("WarningTitle"), Bundle.getMessage("ButtonOK"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(edit3.isAlive());}, "edit3 finished");  // NOI18N

//         _jtxt = new JTextFieldOperator(_jfo, 0);
//         _jtxt.clickMouse();  // Activate Update button
//         new JSpinnerOperator(_jfo, 1).setValue(0);
//         Thread edit4 = createModalDialogOperatorThread(Bundle.getMessage("WarningTitle"), Bundle.getMessage("ButtonOK"));  // NOI18N
//         new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N
//         JUnitUtil.waitFor(()->{return !(edit4.isAlive());}, "edit4 finished");  // NOI18N

        // Schedule:  Start hour and duration.
        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Schedule", "114"}));  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();  // Activate Update button
        new JSpinnerOperator(_jfo, 0).setValue(30);
        new JSpinnerOperator(_jfo, 1).setValue(30);
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();  // Activate Update button
        new JSpinnerOperator(_jfo, 0).setValue(1);
        new JSpinnerOperator(_jfo, 1).setValue(22);
        Thread edit5 = createModalDialogOperatorThread(Bundle.getMessage("WarningTitle"), Bundle.getMessage("ButtonOK"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(edit5.isAlive());}, "edit5 finished");

        // Train:  Speed, Start time, Notes
        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Schedule", "114", "AMX"}));  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 2);
        _jtxt.clickMouse();
        _jtxt.setText("-5");  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        _jtxt = new JTextFieldOperator(_jfo, 3);
        _jtxt.clickMouse();
        _jtxt.setText("1234");  // NOI18N
        Thread edit6 = createModalDialogOperatorThread(Bundle.getMessage("WarningTitle"), Bundle.getMessage("ButtonOK"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(edit6.isAlive());}, "edit6 finished");

        JTextAreaOperator textArea = new JTextAreaOperator(_jfo, 0);
        textArea.clickMouse();
        textArea.setText("A train note");  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        // Stop:  Duration, next speed, notes
        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Schedule", "114", "AMX", "1"}));  // NOI18N

        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();
        _jtxt.setText("-5");  // NOI18N

        _jtxt = new JTextFieldOperator(_jfo, 1);
        _jtxt.clickMouse();
        _jtxt.setText("-5");  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N

        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();
        _jtxt.setText("5");  // NOI18N

        textArea = new JTextAreaOperator(_jfo, 0);
        textArea.clickMouse();
        textArea.setText("A stop note");  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N
        log.warn("editTests");
    } // TODO

    void timeRangeTests() {
        // Change schedule start time
        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Schedule", "123"}));  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();  // Activate Update button
        new JSpinnerOperator(_jfo, 0).setValue(10);
        Thread time1 = createModalDialogOperatorThread(Bundle.getMessage("WarningTitle"), Bundle.getMessage("ButtonOK"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N
        boolean t1 = time1.isAlive();
        log.warn("t1 = {}", t1);
//         JUnitUtil.waitFor(()->{return !(time1.isAlive());}, "time1 finished");
//         try {
//             time1.join();
//         } catch (InterruptedException ex) {
//             // do nothing
//         }

        // Change train start time
// new EventTool().waitNoEvent(5000);
        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Schedule", "123", "EXP"}));  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 3);
        _jtxt.clickMouse();
        _jtxt.setText("7:00");  // NOI18N
//         Thread time2 = createModalDialogOperatorThread(Bundle.getMessage("WarningTitle"), Bundle.getMessage("ButtonOK"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N
// new EventTool().waitNoEvent(5000);
//         JUnitUtil.waitFor(()->{return !(time2.isAlive());}, "time2 finished");

        // Change train start time to move stop times outside of schedule
        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Schedule", "123", "EXP"}));  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 3);
        _jtxt.clickMouse();
        _jtxt.setText("12:00");  // NOI18N
//         Thread time3 = createModalDialogOperatorThread(Bundle.getMessage("WarningTitle"), Bundle.getMessage("ButtonOK"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUpdate")).doClick();  // NOI18N
// new EventTool().waitNoEvent(5000);
//         JUnitUtil.waitFor(()->{return !(time3.isAlive());}, "time3 finished");
    }

    void deleteTests() {
        // Delete the test layout created by the add process
        _jto.clickOnPath(_jto.findPath(new String[]{"Test", "Schedules", "Test", "TRN", "2"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteStopButtonText")).doClick();  // NOI18N
        _jto.clickOnPath(_jto.findPath(new String[]{"Test", "Schedules", "Test", "TRN", "1"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteStopButtonText")).doClick();  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteTrainButtonText")).doClick();  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteScheduleButtonText")).doClick();  // NOI18N
        _jto.clickOnPath(_jto.findPath(new String[]{"Test", "Segments", "Mainline", "Station 2"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteStationButtonText")).doClick();  // NOI18N
        _jto.clickOnPath(_jto.findPath(new String[]{"Test", "Segments", "Mainline", "Station 1"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteStationButtonText")).doClick();  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteSegmentButtonText")).doClick();  // NOI18N
        _jto.clickOnPath(_jto.findPath(new String[]{"Test", "Train Types", "New"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteTrainTypeButtonText")).doClick();  // NOI18N
        _jto.clickOnPath(_jto.findPath(new String[]{"Test"}));  // NOI18N

        Thread finalDelete = createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonYes"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteLayoutButtonText")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(finalDelete.isAlive());}, "finalDelete finished");
    }

    void deleteDialogTests() {
        _jto.clickOnPath(_jto.findPath(new String[]{"Sample"}));  // NOI18N
        Thread delLayout = createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonNo"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteLayoutButtonText")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(delLayout.isAlive());}, "delLayout finished");

        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Train Types", "Yard"}));  // NOI18N
        Thread delTrainType = createModalDialogOperatorThread(Bundle.getMessage("WarningTitle"), Bundle.getMessage("ButtonOK"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteTrainTypeButtonText")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(delTrainType.isAlive());}, "delTrainType finished");  // NOI18N

        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Segments", "Mainline"}));  // NOI18N
        Thread delSegment = createModalDialogOperatorThread(Bundle.getMessage("WarningTitle"), Bundle.getMessage("ButtonOK"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteSegmentButtonText")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(delSegment.isAlive());}, "delSegment finished");  // NOI18N

        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Segments", "Mainline", "Alpha"}));  // NOI18N
        Thread delStation = createModalDialogOperatorThread(Bundle.getMessage("WarningTitle"), Bundle.getMessage("ButtonOK"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteStationButtonText")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(delStation.isAlive());}, "delStation finished");  // NOI18N

        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Schedules", "114   Effective Date: 11/4/93"}));  // NOI18N
        Thread delSchedule = createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonNo"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteScheduleButtonText")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(delSchedule.isAlive());}, "delSchedule finished");  // NOI18N

        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Schedules", "114   Effective Date: 11/4/93", "AMX -- Morning Express"}));  // NOI18N
        Thread delTrain = createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonNo"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteTrainButtonText")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(delTrain.isAlive());}, "delTrain finished");  // NOI18N
    }

    void deleteLayout() {
        _jto.clickOnPath(_jto.findPath(new String[]{"Test"}));  // NOI18N
        Thread layoutDelete = createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonYes"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteLayoutButtonText")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(layoutDelete.isAlive());}, "layoutDelete finished");
    }

    void deleteSections() {
        // Train and stops
        _jto.clickOnPath(_jto.findPath(new String[]{"Test", "Schedules", "Test", "TRN"}));  // NOI18N
        Thread section1 = createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonYes"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteTrainButtonText")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(section1.isAlive());}, "section1 finished");

        // Add a train back to force dialog for schedule delete
        _jto.clickOnPath(_jto.findPath(new String[]{"Test", "Schedules", "Test"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("AddTrainButtonText")).doClick();  // NOI18N

        // Delete schedule and train
        _jto.clickOnPath(_jto.findPath(new String[]{"Test", "Schedules", "Test"}));  // NOI18N
        Thread section2= createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonYes"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteScheduleButtonText")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(section2.isAlive());}, "section2 finished");

        // Delete segment and stations
        _jto.clickOnPath(_jto.findPath(new String[]{"Test", "Segments", "Mainline"}));  // NOI18N
        Thread section3 = createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonYes"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteSegmentButtonText")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(section3.isAlive());}, "section3 finished");

        // Delete layout
        _jto.clickOnPath(_jto.findPath(new String[]{"Test"}));  // NOI18N
        Thread section4 = createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonYes"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("DeleteLayoutButtonText")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(section4.isAlive());}, "section4 finished");
    }

    void buttonTests() {
        log.warn("buttonTests");
        // Test move buttons
        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Schedule", "114", "AMX", "3"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonUp"), 1).doClick();  // NOI18N
        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Schedule", "114", "AO", "3"}));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonDown")).doClick();  // NOI18N

// new EventTool().waitNoEvent(10000);

        // Test edit dialog and cancel button
        _jto.clickOnPath(_jto.findPath(new String[]{"Sample"}));  // NOI18N
        _jtxt = new JTextFieldOperator(_jfo, 0);
        _jtxt.clickMouse();
        _jtxt.setText("XYZ");  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonCancel")).doClick();  // NOI18N

        // Misc tests
        _ttf.makeDetailGrid("XYZ");  // NOI18N
        jmri.util.JUnitAppender.assertWarnMessage("Invalid grid type: 'XYZ'");  // NOI18N
        _ttf.showNodeEditMessage();  // NOI18N

        _jto.clickOnPath(_jto.findPath(new String[]{"Sample", "Segments", "Mainline"}));  // NOI18N
        Thread misc1 = createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonCancel"));  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonGraph")).doClick();  // NOI18N
        JUnitUtil.waitFor(()->{return !(misc1.isAlive());}, "misc1 finished");

        // Other buttons
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonSave")).doClick();  // NOI18N
        new JButtonOperator(_jfo, Bundle.getMessage("ButtonDone")).doClick();  // NOI18N
    }

    Thread createModalDialogOperatorThread(String dialogTitle, String buttonText) {
        Thread t = new Thread(() -> {
            // constructor for jdo will wait until the dialog is visible
            JDialogOperator jdo = new JDialogOperator(dialogTitle);
            JButtonOperator jbo = new JButtonOperator(jdo, buttonText);
              jbo.pushNoBlock();
//             jbo.doClick();
        });
        t.setName(dialogTitle + " Close Dialog Thread");  // NOI18N
        t.start();
        return t;
    }

    @Before
    public void setUp() {
        jmri.util.JUnitUtil.setUp();

        JUnitUtil.resetInstanceManager();
        JUnitUtil.resetProfileManager();
    }

    @After
    public  void tearDown() {
        jmri.util.JUnitUtil.tearDown();
    }

    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TimeTableFrameTest.class);
}