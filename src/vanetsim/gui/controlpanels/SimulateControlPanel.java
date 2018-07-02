package vanetsim.gui.controlpanels;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import vanetsim.ErrorLog;
import vanetsim.VanetSimStart;
import vanetsim.VanetSimStarter;
import vanetsim.gui.Renderer;
import vanetsim.gui.helpers.ButtonCreator;
import vanetsim.gui.helpers.ReRenderManager;
import vanetsim.localization.Messages;
import vanetsim.map.Map;
import vanetsim.scenario.Scenario;
import vanetsim.simulation.SimulationMaster;

public final class SimulateControlPanel extends JPanel implements ActionListener, ChangeListener, ItemListener {

    private static final long serialVersionUID = 7292404190066585320L;

    private final JSlider zoomSlider_;

    private final JPanel startStopJPanel_;

    private final JTextArea informationTextArea_;

    private final JCheckBox communicationDisplayCheckBox_;

    

    private final JCheckBox vehicleIDDisplayCheckBox_;

 

    private final JCheckBox knownVehiclesConnectionsCheckBox_;

    private final JFormattedTextField targetStepTime_;

    private final JButton targetStepTimeApplyButton_;

    private final JFormattedTextField jumpToTargetTime_;

    private final JButton jumpToTargetApplyButton_;

    private boolean dontReRenderZoom_ = false;

    private final JButton hideBar_;

    private int mode_ = 0;

    public SimulateControlPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.insets = new Insets(5, 5, 5, 5);
        JPanel panning = new JPanel();
        panning.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton changeGUI = new JButton("*");
        changeGUI.setPreferredSize(new Dimension(30, 30));
        changeGUI.setSize(new Dimension(30, 20));
        changeGUI.addActionListener(this);
        changeGUI.setActionCommand("guimode");
        hideBar_ = ButtonCreator.getJButton("hide.png", "hide", "hide", true, this);
        hideBar_.setPreferredSize(new Dimension(15, 31));
        hideBar_.setSize(new Dimension(15, 31));
        hideBar_.setBorder(BorderFactory.createEmptyBorder());
        hideBar_.setActionCommand("toogleBar");
        panning.add(hideBar_, c);
        panning.add(ButtonCreator.getJButton("DE_flag.png", "de", "de", true, this), c);
        panning.add(ButtonCreator.getJButton("EN_flag.png", "en", "en", true, this), c);
        c.gridwidth = 2;
        add(panning, c);
        ++c.gridy;
        c.gridx = 0;
        c.gridwidth = 1;
        add(ButtonCreator.getJButton("loadmap.png", "loadmap", Messages.getString("SimulateControlPanel.loadMap"), this), c);
        c.gridx = 1;
        add(ButtonCreator.getJButton("loadscenario.png", "loadscenario", Messages.getString("SimulateControlPanel.loadScenario"), this), c);
        c.gridx = 0;
        c.gridwidth = 2;
        JLabel jLabel1 = new JLabel("<html><b>" + Messages.getString("SimulateControlPanel.mapControl") + "</b></html>");
        ++c.gridy;
        add(jLabel1, c);
        panning = new JPanel();
        panning.setLayout(new GridBagLayout());
        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.NONE;
        c1.weightx = 0.5;
        c1.gridx = 1;
        c1.gridy = 0;
        c1.gridheight = 1;
        panning.add(ButtonCreator.getJButton("up.png", "up", Messages.getString("SimulateControlPanel.upButton"), this), c1);
        c1.gridx = 0;
        c1.gridy = 1;
        panning.add(ButtonCreator.getJButton("left.png", "left", Messages.getString("SimulateControlPanel.leftButton"), this), c1);
        c1.gridx = 2;
        c1.gridy = 1;
        panning.add(ButtonCreator.getJButton("right.png", "right", Messages.getString("SimulateControlPanel.rightButton"), this), c1);
        c1.gridx = 1;
        c1.gridy = 2;
        panning.add(ButtonCreator.getJButton("down.png", "down", Messages.getString("SimulateControlPanel.downButton"), this), c1);
        ++c.gridy;
        add(panning, c);
        jLabel1 = new JLabel("<html><b>" + Messages.getString("SimulateControlPanel.zoom") + "</b></html>");
        ++c.gridy;
        add(jLabel1, c);
        zoomSlider_ = getZoomSlider();
        ++c.gridy;
        add(zoomSlider_, c);
        jLabel1 = new JLabel("<html><b>" + Messages.getString("SimulateControlPanel.simulation") + "</b></html>");
        ++c.gridy;
        add(jLabel1, c);
        startStopJPanel_ = new JPanel(new CardLayout());
        startStopJPanel_.add(ButtonCreator.getJButton("start.png", "start", Messages.getString("SimulateControlPanel.start"), this), "start");
        startStopJPanel_.add(ButtonCreator.getJButton("pause.png", "pause", Messages.getString("SimulateControlPanel.pause"), this), "pause");
        ++c.gridy;
        c.gridwidth = 1;
        add(startStopJPanel_, c);
        c.gridx = 1;
        add(ButtonCreator.getJButton("onestep.png", "onestep", Messages.getString("SimulateControlPanel.onestep"), this), c);
        c.gridx = 0;
        c.gridwidth = 2;
        JPanel tmpPanel = new JPanel();
        jLabel1 = new JLabel(Messages.getString("SimulateControlPanel.jumpToTime"));
        tmpPanel.add(jLabel1);
        jumpToTargetTime_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        jumpToTargetTime_.setPreferredSize(new Dimension(40, 20));
        jumpToTargetTime_.setValue(0);
        tmpPanel.add(jumpToTargetTime_);
        jumpToTargetApplyButton_ = ButtonCreator.getJButton("ok_small.png", "targetTimeApply", Messages.getString("SimulateControlPanel.applyTargetTime"), this);
        tmpPanel.add(jumpToTargetApplyButton_);
        ++c.gridy;
        c.insets = new Insets(0, 0, 0, 0);
        add(tmpPanel, c);
        tmpPanel = new JPanel();
        jLabel1 = new JLabel(Messages.getString("SimulateControlPanel.targetStepTime"));
        tmpPanel.add(jLabel1);
        targetStepTime_ = new JFormattedTextField(NumberFormat.getIntegerInstance());
        targetStepTime_.setPreferredSize(new Dimension(30, 20));
        targetStepTime_.setValue(SimulationMaster.TIME_PER_STEP);
        tmpPanel.add(targetStepTime_);
        targetStepTimeApplyButton_ = ButtonCreator.getJButton("ok_small.png", "targetStepTimeApply", Messages.getString("SimulateControlPanel.applyTargetStepTime"), this);
        tmpPanel.add(targetStepTimeApplyButton_);
        ++c.gridy;
        add(tmpPanel, c);
        c.insets = new Insets(5, 5, 5, 5);
        jLabel1 = new JLabel("<html><b>" + Messages.getString("SimulateControlPanel.information") + "</b></html>");
        ++c.gridy;
        add(jLabel1, c);
        ++c.gridy;
        communicationDisplayCheckBox_ = new JCheckBox(Messages.getString("SimulateControlPanel.displayDistance"), false);
        communicationDisplayCheckBox_.addItemListener(this);
        add(communicationDisplayCheckBox_, c);
        c.insets = new Insets(0, 5, 5, 5);
        ++c.gridy;
     
        vehicleIDDisplayCheckBox_ = new JCheckBox(Messages.getString("SimulateControlPanel.showVehicleIDs"), false);
        vehicleIDDisplayCheckBox_.addItemListener(this);
        add(vehicleIDDisplayCheckBox_, c);
        c.insets = new Insets(5, 5, 5, 5);
        c.insets = new Insets(0, 5, 5, 5);
        
 
        ++c.gridy;
        knownVehiclesConnectionsCheckBox_ = new JCheckBox(Messages.getString("SimulateControlPanel.showKnownVehiclesConnections"), false);
        knownVehiclesConnectionsCheckBox_.addItemListener(this);
        add(knownVehiclesConnectionsCheckBox_, c);
        c.insets = new Insets(5, 5, 5, 5);
        c.gridwidth = 2;
        informationTextArea_ = new JTextArea(20, 1);
        informationTextArea_.setEditable(false);
        informationTextArea_.setLineWrap(true);
        JScrollPane scrolltext = new JScrollPane(informationTextArea_);
        scrolltext.setOpaque(false);
        scrolltext.getViewport().setOpaque(false);
        c.weighty = 1.0;
        ++c.gridy;
        add(scrolltext, c);
    }

    public void setSimulationStop() {
        CardLayout cl = (CardLayout) (startStopJPanel_.getLayout());
        cl.show(startStopJPanel_, "start");
    }

    public void setInformation(String newText) {
        informationTextArea_.setText(newText);
    }

    public void setZoomValue(int zoom) {
        dontReRenderZoom_ = true;
        zoomSlider_.setValue(zoom);
    }

    private JSlider getZoomSlider() {
        JSlider slider = new JSlider(-75, 212, 1);
        Hashtable<Integer, JLabel> ht = new Hashtable<Integer, JLabel>();
        ht.put(-75, new JLabel("3km"));
        ht.put(-20, new JLabel("1km"));
        ht.put(45, new JLabel("200m"));
        ht.put(96, new JLabel("100m"));
        ht.put(157, new JLabel("30m"));
        ht.put(212, new JLabel("10m"));
        slider.setLabelTable(ht);
        slider.setPaintLabels(true);
        slider.setMinorTickSpacing(10);
        slider.setMajorTickSpacing(40);
        slider.addChangeListener(this);
        return slider;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("up".equals(command)) {
            Renderer.getInstance().pan('u');
            ReRenderManager.getInstance().doReRender();
        } else if ("down".equals(command)) {
            Renderer.getInstance().pan('d');
            ReRenderManager.getInstance().doReRender();
        } else if ("left".equals(command)) {
            Renderer.getInstance().pan('l');
            ReRenderManager.getInstance().doReRender();
        } else if ("right".equals(command)) {
            Renderer.getInstance().pan('r');
            ReRenderManager.getInstance().doReRender();
        } else if ("loadmap".equals(command)) {
            System.out.println("Here Load Map");
            VanetSimStart.getMainControlPanel().changeFileChooser(true, true, false); 
            //JOptionPane.showConfirmDialog(null, "Here Load Map 2");
            int returnVal = VanetSimStart.getMainControlPanel().getFileChooser().showOpenDialog(VanetSimStart.getMainFrame());
           
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Runnable job = new Runnable() {

                    public void run() {
                    
                        Map.getInstance().load(VanetSimStart.getMainControlPanel().getFileChooser().getSelectedFile(), false);
                    }
                };
                new Thread(job).start();
            }
        } else if ("loadscenario".equals(command)) {
            VanetSimStart.getMainControlPanel().changeFileChooser(true, true, false);
            int returnVal = VanetSimStart.getMainControlPanel().getFileChooser().showOpenDialog(VanetSimStart.getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Runnable job = new Runnable() {

                    public void run() {
                        Scenario.getInstance().load(VanetSimStart.getMainControlPanel().getFileChooser().getSelectedFile(), false);
                    }
                };
                new Thread(job).start();
            }
        } else if ("pause".equals(command)) {
            CardLayout cl = (CardLayout) (startStopJPanel_.getLayout());
            cl.show(startStopJPanel_, "start");
            Runnable job = new Runnable() {

                public void run() {
                    VanetSimStart.getSimulationMaster().stopThread();
                }
            };
            new Thread(job).start();
        } else if ("start".equals(command)) {
            
            if (VanetSimStart.getMainControlPanel().getEditPanel().getEditMode() == true){
                ErrorLog.log(Messages.getString("SimulateControlPanel.simulationNotPossibleInEditMode"), 6, this.getName(), "startSim", null);
            System.out.println("Simulation Not Possible In Edit Mode");
            }
            else {
                
                CardLayout cl = (CardLayout) (startStopJPanel_.getLayout());
                cl.show(startStopJPanel_, "pause");  
                //JOptionPane.showConfirmDialog(null, "Depart");
                VanetSimStart.getSimulationMaster().startThread();
                           
            }
        } else if ("onestep".equals(command)) {
            if (VanetSimStart.getMainControlPanel().getEditPanel().getEditMode() == true)
                ErrorLog.log(Messages.getString("SimulateControlPanel.simulationNotPossibleInEditMode"), 6, this.getName(), "oneStep", null);
            else {
                Runnable job = new Runnable() {

                    public void run() {
                        VanetSimStart.getSimulationMaster().doOneStep();
                    }
                };
                new Thread(job).start();
            }
        } else if ("targetTimeApply".equals(command)) {
            if (VanetSimStart.getMainControlPanel().getEditPanel().getEditMode() == true)
                ErrorLog.log(Messages.getString("SimulateControlPanel.simulationNotPossibleInEditMode"), 6, this.getName(), "startSim", null);
            else {
                int target = ((Number) jumpToTargetTime_.getValue()).intValue();
                if (target <= Renderer.getInstance().getTimePassed())
                    ErrorLog.log(Messages.getString("SimulateControlPanel.jumpingBackwardsNotPossible"), 6, this.getName(), "jumpToTime", null);
                else
                    VanetSimStart.getSimulationMaster().jumpToTime(target);
            }
        } else if ("targetStepTimeApply".equals(command)) {
            int tmp = ((Number) targetStepTime_.getValue()).intValue();
            if (tmp < 0) {
                ErrorLog.log(Messages.getString("SimulateControlPanel.noNegativeTargetStepTime"), 6, this.getName(), "targetStepTimeApply", null);
            } else {
                VanetSimStart.getSimulationMaster().setTargetStepTime(tmp);
            }
        } else if ("de".equals(command)) {
            VanetSimStarter.restartWithLanguage("de");
        } else if ("en".equals(command)) {
            VanetSimStarter.restartWithLanguage("");
        } else if ("toogleBar".equals(command)) {
            VanetSimStart.getMainControlPanel().tooglePanel();
        } else if ("guimode".equals(command)) {
            repaintGUI();
        }
    }

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            int value = source.getValue();
            double scale = Math.exp(value / 50.0) / 1000;
            if (dontReRenderZoom_)
                dontReRenderZoom_ = false;
            else {
                Renderer.getInstance().setMapZoom(scale);
                ReRenderManager.getInstance().doReRender();
            }
        }
    }

    public void itemStateChanged(ItemEvent e) {
        boolean state;
        if (e.getStateChange() == ItemEvent.SELECTED) state = true;
           
        else state = false;
            
        if (e.getSource() == communicationDisplayCheckBox_){
            Renderer.getInstance().setHighlightCommunication(state);
        //System.out.println("State === "+state);
        }
   
        if (e.getSource() == vehicleIDDisplayCheckBox_)
            Renderer.getInstance().setDisplayVehicleIDs(state);
 
        if (e.getSource() == knownVehiclesConnectionsCheckBox_)
            Renderer.getInstance().setShowKnownVehiclesConnections_(state);
        ReRenderManager.getInstance().doReRender();
    }

    public void startSimulation() {
        if (VanetSimStart.getMainControlPanel().getEditPanel().getEditMode() == true)
            ErrorLog.log(Messages.getString("SimulateControlPanel.simulationNotPossibleInEditMode"), 6, this.getName(), "startSim", null);
        else {
            CardLayout cl = (CardLayout) (startStopJPanel_.getLayout());
            cl.show(startStopJPanel_, "pause");
            VanetSimStart.getSimulationMaster().startThread();
        }
    }

    public void stopSimulation() {
        CardLayout cl = (CardLayout) (startStopJPanel_.getLayout());
        cl.show(startStopJPanel_, "start");
        Runnable job = new Runnable() {

            public void run() {
                VanetSimStart.getSimulationMaster().stopThread();
            }
        };
        new Thread(job).start();
    }

    public void toggleSimulationStatus() {
        if (VanetSimStart.getSimulationMaster().isSimulationRunning())
            stopSimulation();
        else
            startSimulation();
    }

 

    public void toggleCommunicationDistance() {
        communicationDisplayCheckBox_.setSelected(!communicationDisplayCheckBox_.isSelected());
        Renderer.getInstance().setHighlightCommunication(communicationDisplayCheckBox_.isSelected());
    }

    public void toggleVehileIDs() {
        vehicleIDDisplayCheckBox_.setSelected(!vehicleIDDisplayCheckBox_.isSelected());
        Renderer.getInstance().setDisplayVehicleIDs(vehicleIDDisplayCheckBox_.isSelected());
    }

    public void toggleVehicleConnections() {
        knownVehiclesConnectionsCheckBox_.setSelected(!knownVehiclesConnectionsCheckBox_.isSelected());
        Renderer.getInstance().setShowKnownVehiclesConnections_(knownVehiclesConnectionsCheckBox_.isSelected());
    }

    public JButton getHideBar_() {
        return hideBar_;
    }

    public void repaintGUI() {
        String[] styleArray = { "com.jtattoo.plaf.smart.SmartLookAndFeel", "com.jtattoo.plaf.aluminium.AluminiumLookAndFeel", "com.jtattoo.plaf.acryl.AcrylLookAndFeel", "com.jtattoo.plaf.aero.AeroLookAndFeel", "com.jtattoo.plaf.bernstein.BernsteinLookAndFeel", "com.jtattoo.plaf.graphite.GraphiteLookAndFeel", "com.jtattoo.plaf.fast.FastLookAndFeel", "com.jtattoo.plaf.hifi.HiFiLookAndFeel", "com.jtattoo.plaf.luna.LunaLookAndFeel", "com.jtattoo.plaf.mcwin.McWinLookAndFeel", "com.jtattoo.plaf.mint.MintLookAndFeel", "com.jtattoo.plaf.noire.NoireLookAndFeel" };
        try {
            UIManager.setLookAndFeel(styleArray[mode_ % styleArray.length]);
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    VanetSimStart.getMainFrame().invalidate();
                    VanetSimStart.getMainFrame().repaint();
                }
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        mode_++;
    }
}
