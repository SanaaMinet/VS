package vanetsim.gui.controlpanels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import vanetsim.ErrorLog;
import vanetsim.gui.Renderer;
import vanetsim.gui.helpers.ButtonCreator;
import vanetsim.gui.helpers.EventJListRenderer;
import vanetsim.gui.helpers.TextAreaLabel;
import vanetsim.localization.Messages;
import vanetsim.scenario.events.Event;
import vanetsim.scenario.events.EventList;


public final class EditEventControlPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -8161612114065521616L;

    private final JList<Event> list_;

    private final DefaultListModel<Event> listModel_ = new DefaultListModel<Event>();

    private final JComboBox<String> eventTypeChoice_;

    private final JFormattedTextField timeTextField_ = new JFormattedTextField(NumberFormat.getIntegerInstance());

    private final JFormattedTextField xTextField_ = new JFormattedTextField(NumberFormat.getIntegerInstance());

    private final JFormattedTextField yTextField_ = new JFormattedTextField(NumberFormat.getIntegerInstance());

    private final JComboBox<String> directionChoice_;

    private final JFormattedTextField lanesTextField_ = new JFormattedTextField(NumberFormat.getIntegerInstance());

    private final JComboBox<String> penaltyType_;

    @SuppressWarnings("unchecked")
    public EditEventControlPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.insets = new Insets(5, 5, 5, 5);
        JLabel jLabel1 = new JLabel("<html><u><b>" + Messages.getString("EditEventControlPanel.eventList") + "</b></u></html>");
        add(jLabel1, c);
        updateList();
        list_ = new JList<Event>(listModel_);
        list_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list_.setCellRenderer(new EventJListRenderer());
        list_.setSelectedIndex(0);
        list_.setVisibleRowCount(10);
        JScrollPane listScrollPane = new JScrollPane(list_);
        listScrollPane.setPreferredSize(new Dimension(100, 200));
        ++c.gridy;
        c.weighty = 1.0;
        add(listScrollPane, c);
        c.weighty = 0;
        c.gridwidth = 1;
        c.gridx = 1;
        ++c.gridy;
        add(ButtonCreator.getJButton("delEvent.png", "delEvent", Messages.getString("EditEventControlPanel.deleteEvent"), this), c);
        c.gridx = 0;
        ++c.gridy;
        jLabel1 = new JLabel(Messages.getString("EditEventControlPanel.type"));
        add(jLabel1, c);
        c.gridx = 1;
        String[] choices = { Messages.getString("EditEventControlPanel.startBlocking"), Messages.getString("EditEventControlPanel.stopBlocking") };
        eventTypeChoice_ = new JComboBox<String>(choices);
        eventTypeChoice_.setSelectedIndex(0);
        add(eventTypeChoice_, c);
        c.gridx = 0;
        ++c.gridy;
        jLabel1 = new JLabel(Messages.getString("EditEventControlPanel.penaltyType"));
        add(jLabel1, c);
        c.gridx = 1;
        String[] choices3 = { "HUANG_EEBL", "HUANG_PCN", "HUANG_RHCN", "HUANG_RFN", "HUANG_SVA", "HUANG_CCW", "HUANG_CVW", "HUANG_CL", "HUANG_EVA" };
        penaltyType_ = new JComboBox<String>(choices3);
        penaltyType_.setSelectedIndex(0);
        add(penaltyType_, c);
        c.gridx = 0;
        ++c.gridy;
        jLabel1 = new JLabel(Messages.getString("EditEventControlPanel.time"));
        add(jLabel1, c);
        c.gridx = 1;
        timeTextField_.setPreferredSize(new Dimension(60, 20));
        timeTextField_.setValue(0);
        add(timeTextField_, c);
        c.gridx = 0;
        ++c.gridy;
        jLabel1 = new JLabel(Messages.getString("EditEventControlPanel.y"));
        add(jLabel1, c);
        c.gridx = 1;
        xTextField_.setPreferredSize(new Dimension(60, 20));
        xTextField_.setValue(0);
        add(xTextField_, c);
        c.gridx = 0;
        ++c.gridy;
        jLabel1 = new JLabel(Messages.getString("EditEventControlPanel.x"));
        add(jLabel1, c);
        c.gridx = 1;
        yTextField_.setPreferredSize(new Dimension(60, 20));
        yTextField_.setValue(0);
        add(yTextField_, c);
        c.gridx = 0;
        ++c.gridy;
        jLabel1 = new JLabel(Messages.getString("EditEventControlPanel.direction"));
        add(jLabel1, c);
        c.gridx = 1;
        String[] choices2 = { Messages.getString("EditEventControlPanel.both"), Messages.getString("EditEventControlPanel.fromStartNode"), Messages.getString("EditEventControlPanel.fromEndNode") };
        directionChoice_ = new JComboBox<String>(choices2);
        directionChoice_.setSelectedIndex(0);
        add(directionChoice_, c);
        c.gridx = 0;
        ++c.gridy;
        jLabel1 = new JLabel(Messages.getString("EditEventControlPanel.lanes"));
        add(jLabel1, c);
        c.gridx = 1;
        lanesTextField_.setPreferredSize(new Dimension(60, 20));
        lanesTextField_.setValue(1);
        add(lanesTextField_, c);
        ++c.gridy;
        c.gridx = 0;
        add(new JLabel(Messages.getString("EditEventControlPanel.isFake")), c);
        
        c.gridx = 1;
     
        ++c.gridy;
        add(ButtonCreator.getJButton("addEvent.png", "addEvent", Messages.getString("EditEventControlPanel.addEvent"), this), c);
        TextAreaLabel jlabel1 = new TextAreaLabel(Messages.getString("EditEventControlPanel.note"));
        ++c.gridy;
        c.gridx = 0;
        c.gridwidth = 2;
        add(jlabel1, c);
    }

    public void receiveMouseEvent(int x, int y) {
        xTextField_.setValue(x);
        yTextField_.setValue(y);
    }

    public void updateList() {
        Iterator<Event> eventIterator = EventList.getInstance().getIterator();
        listModel_.clear();
        while (eventIterator.hasNext()) {
            listModel_.addElement(eventIterator.next());
        }
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("addEvent".equals(command)) {
            String item = (String) eventTypeChoice_.getSelectedItem();
            try {
                xTextField_.commitEdit();
                yTextField_.commitEdit();
 
                updateList();
                Renderer.getInstance().ReRender(false, false);
            } catch (Exception e2) {
                ErrorLog.log(Messages.getString("EditEventControlPanel.errorCreatingEvent"), 6, getClass().getName(), "actionPerformed", e2);
            }
        } else if ("delEvent".equals(command)) {
            if (list_.getSelectedIndex() > -1) {
                Event deleteEvent = (Event) list_.getSelectedValue();
                boolean doDelete = true;
 
                if (doDelete) {
                    listModel_.remove(list_.getSelectedIndex());
                    EventList.getInstance().delEvent(deleteEvent);
                    Renderer.getInstance().ReRender(false, false);
                }
            }
        }
    }
}
