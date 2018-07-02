package vanetsim.gui.controlpanels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.CyclicBarrier;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import vanetsim.ErrorLog;
import vanetsim.VanetSimStart;
import vanetsim.gui.Renderer;
import vanetsim.gui.helpers.ButtonCreator;
import vanetsim.localization.Messages;
import vanetsim.map.Map;
import vanetsim.map.Node;
import vanetsim.map.Region;
import vanetsim.map.OSM.OSMLoader;
import vanetsim.scenario.Scenario;
import vanetsim.scenario.Vehicle;

public final class EditControlPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -7019659218394560856L;

    private final JRadioButton enableEdit_;

    private final JRadioButton disableEdit_;

    private final JComboBox<String> editChoice_;

    private final JPanel editPanel_;

    private final JPanel editCardPanel_;

    private final JTabbedPane tabbedPane_;

    private final JTabbedPane privacyTabbedPane_;

    private final EditStreetControlPanel editStreetPanel_ = new EditStreetControlPanel();

    private final EditVehicleControlPanel editVehiclePanel_ = new EditVehicleControlPanel();

    private final EditOneVehicleControlPanel editOneVehiclePanel_ = new EditOneVehicleControlPanel();

 
 
    private final SlowPanel editSlowPanel_ = new SlowPanel();

    

   

    private final EditEventControlPanel editEventPanel_ = new EditEventControlPanel();

    private final EditEventSpotsControlPanel editEventSpotsPanel_ = new EditEventSpotsControlPanel();

    private final EditSettingsControlPanel editSettingsPanel_ = new EditSettingsControlPanel();

    private final EditTrafficLightsControlPanel editTrafficLightsPanel_ = new EditTrafficLightsControlPanel();

    private final EditLogControlPanel editLogControlPanel_ = new EditLogControlPanel();

    private final EditIDSControlPanel editIDSControlPanel_ = new EditIDSControlPanel();

    private final EditTrafficModelControlPanel editTrafficModelControlPanel_ = new EditTrafficModelControlPanel();

    private final EditDataAnalysisControlPanel editDataAnalysisControlPanel_ = new EditDataAnalysisControlPanel();

    private final EditPresentationModeControlPanel editPresentationModeControlPanel_ = new EditPresentationModeControlPanel();

    private boolean editMode_ = true;

    private final JTabbedPane tabbedPaneEvents_;

    public EditControlPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.insets = new Insets(5, 5, 5, 5);
        add(ButtonCreator.getJButton("newmap.png", "newmap", Messages.getString("EditControlPanel.newMap"), this), c);
        c.gridx = 1;
        add(ButtonCreator.getJButton("newscenario.png", "newscenario", Messages.getString("EditControlPanel.newScenario"), this), c);
        c.gridx = 0;
        ++c.gridy;
        add(ButtonCreator.getJButton("savemap.png", "savemap", Messages.getString("EditControlPanel.saveMap"), this), c);
        c.gridx = 1;
        add(ButtonCreator.getJButton("savescenario.png", "savescenario", Messages.getString("EditControlPanel.saveScenario"), this), c);
        c.gridx = 0;
        ++c.gridy;
        add(ButtonCreator.getJButton("importOSM.png", "importOSM", Messages.getString("EditControlPanel.importOSM"), this), c);
        c.gridx = 1;
        add(ButtonCreator.getJButton("scenariocreatoricon2.png", "openScenarioCreator", Messages.getString("EditControlPanel.openScenarioCreator"), this), c);
        c.gridx = 0;
        ++c.gridy;
        JLabel jLabel1 = new JLabel("<html><b>" + Messages.getString("EditControlPanel.editMode") + "</b></html>");
        c.gridwidth = 2;
        ++c.gridy;
        add(jLabel1, c);
        c.gridwidth = 1;
        ButtonGroup group = new ButtonGroup();
        enableEdit_ = new JRadioButton(Messages.getString("EditControlPanel.enable"));
        enableEdit_.setActionCommand("enableEdit");
        enableEdit_.setSelected(true);
        enableEdit_.addActionListener(this);
        group.add(enableEdit_);
        ++c.gridy;
        add(enableEdit_, c);
        disableEdit_ = new JRadioButton(Messages.getString("EditControlPanel.disable"));
        disableEdit_.setActionCommand("disableEdit");
        disableEdit_.addActionListener(this);
        group.add(disableEdit_);
        c.gridx = 1;
        add(disableEdit_, c);
        c.gridx = 0;
        editPanel_ = new JPanel();
        editPanel_.setLayout(new BorderLayout(0, 5));
        String[] choices = { Messages.getString("EditControlPanel.settings"), Messages.getString("EditControlPanel.trafficModel"), Messages.getString("EditControlPanel.dataAnalysis"), Messages.getString("EditControlPanel.presentationMode"), Messages.getString("EditControlPanel.street"), Messages.getString("EditControlPanel.trafficLights"), Messages.getString("EditControlPanel.vehicles"), Messages.getString("EditControlPanel.privacy"), Messages.getString("EditControlPanel.rsus"), Messages.getString("EditControlPanel.attackers"), Messages.getString("EditControlPanel.event"), Messages.getString("EditControlPanel.ids"), Messages.getString("EditControlPanel.logs") };
        editChoice_ = new JComboBox<String>(choices);
        editChoice_.setSelectedIndex(0);
        editChoice_.setMaximumRowCount(100);
        editChoice_.addActionListener(this);
        editPanel_.add(editChoice_, BorderLayout.PAGE_START);
        editCardPanel_ = new JPanel(new CardLayout());
        editCardPanel_.setOpaque(false);
        editCardPanel_.add(editSettingsPanel_, "settings");
        editSettingsPanel_.setOpaque(false);
        editCardPanel_.add(editTrafficModelControlPanel_, "trafficmodel");
        editTrafficModelControlPanel_.setOpaque(false);
        editCardPanel_.add(editDataAnalysisControlPanel_, "dataanalysis");
        editDataAnalysisControlPanel_.setOpaque(false);
        editCardPanel_.add(editPresentationModeControlPanel_, "presentationmode");
        editPresentationModeControlPanel_.setOpaque(false);
        editCardPanel_.add(editStreetPanel_, "street");
        editStreetPanel_.setOpaque(false);
        editCardPanel_.add(editTrafficLightsPanel_, "trafficLights");
        editTrafficLightsPanel_.setOpaque(false);
        tabbedPaneEvents_ = new JTabbedPane();
        tabbedPaneEvents_.setOpaque(false);
        editEventPanel_.setOpaque(false);
        editEventSpotsPanel_.setOpaque(false);
        tabbedPaneEvents_.add(Messages.getString("EditEventControlPanel.events"), editEventPanel_);
        tabbedPaneEvents_.add(Messages.getString("EditEventControlPanel.eventSpots"), editEventSpotsPanel_);
        editCardPanel_.add(tabbedPaneEvents_, "event");
        privacyTabbedPane_ = new JTabbedPane();
        privacyTabbedPane_.setOpaque(false);
        
 
        editSlowPanel_.setOpaque(false);
        privacyTabbedPane_.add(Messages.getString("EditControlPanel.slow"), editSlowPanel_);
        editCardPanel_.add(privacyTabbedPane_, "privacy");
 
        tabbedPane_ = new JTabbedPane();
        tabbedPane_.setOpaque(false);
        editVehiclePanel_.setOpaque(false);
        editOneVehiclePanel_.setOpaque(false);
        tabbedPane_.add(Messages.getString("EditVehiclesControlPanel.vehicle1"), editVehiclePanel_);
        tabbedPane_.add(Messages.getString("EditVehiclesControlPanel.vehicle2"), editOneVehiclePanel_);
 
        editCardPanel_.add(tabbedPane_, "vehicles");
        editCardPanel_.add(editIDSControlPanel_, "ids");
        editIDSControlPanel_.setOpaque(false);
        editCardPanel_.add(editLogControlPanel_, "logs");
        editLogControlPanel_.setOpaque(false);
        editPanel_.add(editCardPanel_, BorderLayout.PAGE_END);
        editPanel_.setOpaque(false);
        c.gridwidth = 2;
        ++c.gridy;
        add(editPanel_, c);
        c.weighty = 1.0;
        ++c.gridy;
        JPanel pane = new JPanel();
        pane.setOpaque(false);
        add(pane, c);
    }

    public void receiveMouseEvent(int x, int y) {
        String item = (String) editChoice_.getSelectedItem();
        if (item.equals(Messages.getString("EditControlPanel.street"))) {
            editStreetPanel_.receiveMouseEvent(x, y);
        } else if (item.equals(Messages.getString("EditControlPanel.vehicles")) && tabbedPane_.getTitleAt(tabbedPane_.getSelectedIndex()).equals(Messages.getString("EditVehiclesControlPanel.vehicle2"))) {
            editOneVehiclePanel_.receiveMouseEvent(x, y);
        }  else if (item.equals(Messages.getString("EditControlPanel.trafficLights"))) {
            editTrafficLightsPanel_.receiveMouseEvent(x, y);
        } else if (item.equals(Messages.getString("EditControlPanel.event")) && tabbedPaneEvents_.getTitleAt(tabbedPaneEvents_.getSelectedIndex()).equals(Messages.getString("EditEventControlPanel.events"))) {
            editEventPanel_.receiveMouseEvent(x, y);
        }  
        }
   

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("savemap".equals(command)) {
            
            VanetSimStart.getMainControlPanel().changeFileChooser(false, true, false);
            final JFileChooser filechooser = VanetSimStart.getMainControlPanel().getFileChooser();
            int returnVal = filechooser.showSaveDialog(VanetSimStart.getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Runnable job = new Runnable() {

                    public void run() {
                        File file = filechooser.getSelectedFile();
                        if (filechooser.getAcceptAllFileFilter() != filechooser.getFileFilter() && !file.getName().toLowerCase().endsWith(".xml"))
                            file = new File(file.getAbsolutePath() + ".xml");
                            
                        Map.getInstance().save(file, false);
                    }
                };
                new Thread(job).start();
            }
        } else if ("savescenario".equals(command)) 
        {
            //JOptionPane.showConfirmDialog(null, "savescenario");
            //------------------------------------------------------------------
            VanetSimStart.getMainControlPanel().changeFileChooser(false, true, false);
            final JFileChooser filechooser = VanetSimStart.getMainControlPanel().getFileChooser();
            int returnVal = filechooser.showSaveDialog(VanetSimStart.getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) 
            {
                Runnable job = new Runnable() {

                    public void run() {
                        File file = filechooser.getSelectedFile();
                        if (filechooser.getAcceptAllFileFilter() != filechooser.getFileFilter() && !file.getName().toLowerCase().endsWith(".xml"))
                            file = new File(file.getAbsolutePath() + ".xml");
                        //------------------------------------------------------
                            Scenario.getInstance().save(file, false);
                        //------------------------------------------------------
                    }
                };
                new Thread(job).start();
            }
            //------------------------------------------------------------------
        } else if ("importOSM".equals(command)) {
            VanetSimStart.getMainControlPanel().changeFileChooser(true, true, true);
            int returnVal = VanetSimStart.getMainControlPanel().getFileChooser().showOpenDialog(VanetSimStart.getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Runnable job = new Runnable() {

                    public void run() {
                        OSMLoader.getInstance().loadOSM(VanetSimStart.getMainControlPanel().getFileChooser().getSelectedFile());
                    }
                };
                new Thread(job).start();
            }
        } else if ("newmap".equals(command)) {
            CyclicBarrier barrier = new CyclicBarrier(2);
            new MapSizeDialog(100000, 100000, 50000, 50000, barrier);
            try {
                barrier.await();
            } catch (Exception e2) {
            }
            enableEdit_.setSelected(false);
            disableEdit_.setSelected(true);
            Map.getInstance().signalMapLoaded();
        } else if ("newscenario".equals(command)) {
            Scenario.getInstance().initNewScenario();
            Scenario.getInstance().setReadyState(true);
            VanetSimStart.getMainControlPanel().getEditPanel().getEditEventPanel().updateList();
            Renderer.getInstance().ReRender(false, false);
        } else if ("enableEdit".equals(command)) {
            if (Renderer.getInstance().getTimePassed() > 0) {
                enableEdit_.setSelected(false);
                disableEdit_.setSelected(true);
                
                ErrorLog.log(Messages.getString("EditControlPanel.editingOnlyOnCleanMap"), 6, this.getName(), "enableEdit", null);
            } else {
                editMode_ = true;
                if (((String) editChoice_.getSelectedItem()).equals(Messages.getString("EditControlPanel.street"))) {
                    Renderer.getInstance().setHighlightNodes(true);
                    Renderer.getInstance().ReRender(true, false);
                } else if (((String) editChoice_.getSelectedItem()).equals(Messages.getString("EditControlPanel.event"))) {
                    Renderer.getInstance().setShowAllBlockings(true);
                    Renderer.getInstance().ReRender(true, false);
                } else if (((String) editChoice_.getSelectedItem()).equals(Messages.getString("EditControlPanel.trafficmodel"))) {
                    Renderer.getInstance().ReRender(true, false);
                } else if (((String) editChoice_.getSelectedItem()).equals(Messages.getString("EditControlPanel.dataAnalysis"))) {
                    Renderer.getInstance().ReRender(true, false);
                } else if (((String) editChoice_.getSelectedItem()).equals(Messages.getString("EditControlPanel.presentationMode"))) {
                    Renderer.getInstance().ReRender(true, false);
                } else if (((String) editChoice_.getSelectedItem()).equals(Messages.getString("EditControlPanel.privacy"))) {
                    Renderer.getInstance().setShowMixZones(true);
                    Renderer.getInstance().setHighlightNodes(true);
                    Renderer.getInstance().ReRender(true, false);
                 
                    editSlowPanel_.loadAttributes();
                } else if (((String) editChoice_.getSelectedItem()).equals(Messages.getString("EditControlPanel.rsus"))) {
                     
                    Renderer.getInstance().setHighlightCommunication(true);
                    Renderer.getInstance().ReRender(true, false);
                } else if (((String) editChoice_.getSelectedItem()).equals(Messages.getString("EditControlPanel.attackers"))) {
                    Renderer.getInstance().setShowVehicles(true);
                    Renderer.getInstance().setShowAttackers(true);
                    Renderer.getInstance().setShowMixZones(true);
                    Renderer.getInstance().setHighlightCommunication(true);
                    Renderer.getInstance().ReRender(true, false);
                    editLogControlPanel_.refreshGUI();
                } else if (((String) editChoice_.getSelectedItem()).equals(Messages.getString("EditControlPanel.vehicles"))) {
                    Renderer.getInstance().setShowVehicles(true);
                    Renderer.getInstance().ReRender(true, false);
                } else if (((String) editChoice_.getSelectedItem()).equals(Messages.getString("EditControlPanel.trafficLights"))) {
                    Renderer.getInstance().setHighlightNodes(true);
                    Renderer.getInstance().ReRender(false, false);
                } else if (((String) editChoice_.getSelectedItem()).equals(Messages.getString("EditControlPanel.ids"))) {
                    Renderer.getInstance().ReRender(false, false);
                } else if (((String) editChoice_.getSelectedItem()).equals(Messages.getString("EditControlPanel.logs"))) {
                    Renderer.getInstance().ReRender(false, false);
                }
                editPanel_.setVisible(true);
            }
        } else if ("disableEdit".equals(command)) {
            editMode_ = false;
            editPanel_.setVisible(false);
            Renderer.getInstance().setHighlightNodes(false);
            Renderer.getInstance().setShowAllBlockings(false);
            Renderer.getInstance().setShowVehicles(false);
            Renderer.getInstance().setShowMixZones(false);
             
            Renderer.getInstance().setShowAttackers(false);
            Renderer.getInstance().setHighlightCommunication(false);
            Renderer.getInstance().setMarkedVehicle(null);
            Renderer.getInstance().setMarkedJunction_(null);
            Renderer.getInstance().ReRender(true, false);
          
            setMaxMixZoneRadius();
 
            editIDSControlPanel_.saveAttributes();
            editSlowPanel_.saveAttributes();
        } else if ("comboBoxChanged".equals(command)) {
            String item = (String) editChoice_.getSelectedItem();
            CardLayout cl = (CardLayout) (editCardPanel_.getLayout());
            Renderer.getInstance().setHighlightNodes(false);
            Renderer.getInstance().setShowAllBlockings(false);
            Renderer.getInstance().setShowVehicles(false);
            Renderer.getInstance().setShowMixZones(false);
            
            Renderer.getInstance().setHighlightCommunication(false);
            Renderer.getInstance().setShowAttackers(false);
            Renderer.getInstance().setMarkedJunction_(null);
            Renderer.getInstance().setMarkedVehicle(null);
            Renderer.getInstance().ReRender(true, false);
            if (Messages.getString("EditControlPanel.street").equals(item)) {
                cl.show(editCardPanel_, "street");
                Renderer.getInstance().setHighlightNodes(true);
                Renderer.getInstance().setShowAllBlockings(false);
                Renderer.getInstance().ReRender(true, false);
            } else if (Messages.getString("EditControlPanel.vehicles").equals(item)) {
                cl.show(editCardPanel_, "vehicles");
                Renderer.getInstance().setHighlightNodes(false);
                Renderer.getInstance().setShowAllBlockings(false);
                Renderer.getInstance().setShowVehicles(true);
                Renderer.getInstance().ReRender(true, false);
                editOneVehiclePanel_.getAddNote().setForeground(Color.black);
                editOneVehiclePanel_.getAddNote().setText(Messages.getString("EditOneVehicleControlPanel.noteAdd"));
                stateChanged(null);
            } else if (Messages.getString("EditControlPanel.privacy").equals(item)) {
                cl.show(editCardPanel_, "privacy");
                Renderer.getInstance().setHighlightNodes(true);
                Renderer.getInstance().setShowAllBlockings(false);
                Renderer.getInstance().setShowMixZones(true);
                Renderer.getInstance().ReRender(true, false);
              
                editSlowPanel_.loadAttributes();
            } else if (Messages.getString("EditControlPanel.trafficModel").equals(item)) {
                cl.show(editCardPanel_, "trafficmodel");
                Renderer.getInstance().ReRender(true, false);
            } else if (Messages.getString("EditControlPanel.dataAnalysis").equals(item)) {
                cl.show(editCardPanel_, "dataanalysis");
                Renderer.getInstance().ReRender(true, false);
            } else if (Messages.getString("EditControlPanel.presentationMode").equals(item)) {
                cl.show(editCardPanel_, "presentationmode");
                Renderer.getInstance().ReRender(true, false);
            } else if (Messages.getString("EditControlPanel.rsus").equals(item)) {
                cl.show(editCardPanel_, "rsus");
                
                Renderer.getInstance().setHighlightCommunication(true);
                Renderer.getInstance().ReRender(true, false);
            } else if (Messages.getString("EditControlPanel.attackers").equals(item)) {
                cl.show(editCardPanel_, "attackers");
                Renderer.getInstance().setShowMixZones(true);
                Renderer.getInstance().setShowVehicles(true);
                Renderer.getInstance().setShowAttackers(true);
                Renderer.getInstance().setHighlightCommunication(true);
                Renderer.getInstance().ReRender(true, false);
            } else if (Messages.getString("EditControlPanel.event").equals(item)) {
                cl.show(editCardPanel_, "event");
                Renderer.getInstance().setHighlightNodes(false);
                Renderer.getInstance().setShowAllBlockings(true);
                Renderer.getInstance().ReRender(true, false);
            } else if (Messages.getString("EditControlPanel.settings").equals(item)) {
                cl.show(editCardPanel_, "settings");
                Renderer.getInstance().setHighlightNodes(false);
                Renderer.getInstance().setShowAllBlockings(false);
                Renderer.getInstance().ReRender(true, false);
            } else if (Messages.getString("EditControlPanel.trafficLights").equals(item)) {
                cl.show(editCardPanel_, "trafficLights");
                Renderer.getInstance().setHighlightNodes(true);
                Renderer.getInstance().ReRender(true, false);
            } else if (Messages.getString("EditControlPanel.logs").equals(item)) {
                cl.show(editCardPanel_, "logs");
                editLogControlPanel_.refreshGUI();
                Renderer.getInstance().ReRender(true, false);
            } else if (Messages.getString("EditControlPanel.ids").equals(item)) {
                cl.show(editCardPanel_, "ids");
                Renderer.getInstance().ReRender(true, false);
            }
        } else if ("openScenarioCreator".equals(command)) {
            ResearchSeriesDialog.getInstance().setVisible(true);
        }
    }

    public void stateChanged(ChangeEvent arg0) {
        Renderer.getInstance().setMarkedVehicle(null);
        if (editChoice_.getSelectedItem().toString().equals(Messages.getString("EditControlPanel.vehicles")) && tabbedPane_.getTitleAt(tabbedPane_.getSelectedIndex()).equals(Messages.getString("EditVehiclesControlPanel.vehicle1"))) {
        } else if (editChoice_.getSelectedItem().toString().equals(Messages.getString("EditControlPanel.vehicles")) && tabbedPane_.getTitleAt(tabbedPane_.getSelectedIndex()).equals(Messages.getString("EditVehiclesControlPanel.vehicle2"))) {
            Renderer.getInstance().setMarkedVehicle(null);
            Renderer.getInstance().setShowVehicles(true);
        }
        Renderer.getInstance().ReRender(false, false);
    }

    public void setEditMode(boolean state) {
        if (state)
            enableEdit_.setSelected(true);
        else
            disableEdit_.setSelected(true);
        editPanel_.setVisible(state);
        editMode_ = state;
    }

    public void setMaxMixZoneRadius() {
        Region[][] tmpRegions = Map.getInstance().getRegions();
        int regionCountX = Map.getInstance().getRegionCountX();
        int regionCountY = Map.getInstance().getRegionCountY();
        int maxMixRadius = 0;
        for (int i = 0; i <= regionCountX - 1; i++) {
            for (int j = 0; j <= regionCountY - 1; j++) {
                Region tmpRegion = tmpRegions[i][j];
                Node[] mixZones = tmpRegion.getMixZoneNodes();
                for (int k = 0; k < mixZones.length; k++) {
                    if (maxMixRadius < mixZones[k].getMixZoneRadius())
                        maxMixRadius = mixZones[k].getMixZoneRadius();
                }
            }
        }
        Vehicle.setMaxMixZoneRadius(maxMixRadius);
    }

    public boolean getEditMode() {
        return editMode_;
    }

    public EditStreetControlPanel getEditStreetPanel() {
        return editStreetPanel_;
    }

    public EditVehicleControlPanel getEditVehiclePanel() {
        return editVehiclePanel_;
    }

    public EditEventControlPanel getEditEventPanel() {
        return editEventPanel_;
    }

    public EditSettingsControlPanel getEditSettingsPanel() {
        return editSettingsPanel_;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane_;
    }

    public EditOneVehicleControlPanel getEditOneVehiclePanel() {
        return editOneVehiclePanel_;
    }

 
    public EditLogControlPanel getEditLogControlPanel_() {
        return editLogControlPanel_;
    }

    public EditIDSControlPanel getEditIDSControlPanel_() {
        return editIDSControlPanel_;
    }

    public EditEventSpotsControlPanel getEditEventSpotsPanel_() {
        return editEventSpotsPanel_;
    }

    public JRadioButton getEnableEdit_() {
        return enableEdit_;
    }

    public JComboBox<String> getEditChoice_() {
        return editChoice_;
    }
}
