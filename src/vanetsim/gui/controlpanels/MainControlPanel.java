package vanetsim.gui.controlpanels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import vanetsim.localization.Messages;

public final class MainControlPanel extends JPanel implements ChangeListener {

    private static final long serialVersionUID = 5716484200018988368L;

    private final JTabbedPane tabbedPane_ = new JTabbedPane();

    
//------------------------------------------------------------------------------
    private final SimulateControlPanel simulatePanel_       = new SimulateControlPanel();
    private final EditControlPanel editPanel_               = new EditControlPanel();
    private final ReportingControlPanel reportingPanel_     = new ReportingControlPanel(); 
    private final ClusteringControlPanel clusteringPanel_   = new ClusteringControlPanel();
    private final AboutControlPanel aboutPanel_             = new AboutControlPanel();
//------------------------------------------------------------------------------
    private final FileFilter xmlFileFilter_;

    private final FileFilter osmFileFilter_;

    private JFileChooser fileChooser_ = null;

    private boolean hideBar_ = false;

    public MainControlPanel() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFileChooser tmpChooser = new JFileChooser();
                tmpChooser.setMultiSelectionEnabled(false);
                fileChooser_ = tmpChooser;
            }
        });
        xmlFileFilter_ = new FileFilter() {

            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                return f.getName().toLowerCase().endsWith(".xml");
            }

            public String getDescription() {
                return Messages.getString("MainControlPanel.xmlFiles") + " (*.xml)";
            }
        };
        osmFileFilter_ = new FileFilter() {

            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                return f.getName().toLowerCase().endsWith(".osm");
            }

            public String getDescription() {
                return Messages.getString("MainControlPanel.openStreetMapFiles") + " (*.osm)";
            }
        };
        setLayout(new GridBagLayout());
        
        
        Dimension size = simulatePanel_.getPreferredSize();
        size.setSize(size.width + 155, size.height < 800 ? 800 : size.height);
        setMinimumSize(new Dimension(size.width + 50, 400));
        
        
        editPanel_.setMinimumSize(size);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        
        tabbedPane_.addTab(Messages.getString("MainControlPanel.simulateTab"), simulatePanel_);
        tabbedPane_.addTab(Messages.getString("MainControlPanel.editTab"), editPanel_);
        tabbedPane_.addTab(Messages.getString("MainControlPanel.reporting"), reportingPanel_);
        tabbedPane_.addTab(Messages.getString("MainControlPanel.clusteringTab"), clusteringPanel_);
        tabbedPane_.addTab(Messages.getString("MainControlPanel.about"), aboutPanel_);
        
        tabbedPane_.setMinimumSize(new Dimension(size.width + 50, 500));
        tabbedPane_.addChangeListener(this);
        
        
        UIManager.put("TabbedPane.contentOpaque", false);
        
        JScrollPane scrollPane = new JScrollPane(tabbedPane_);
        tabbedPane_.setOpaque(false);
        
        simulatePanel_.setOpaque(false);
        editPanel_.setOpaque(false);
        reportingPanel_.setOpaque(false);
        clusteringPanel_.setOpaque(false);
        aboutPanel_.setOpaque(false);
        
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        JViewport jv = scrollPane.getViewport();
        jv.setViewPosition(new Point(0, 0));
        scrollPane.getVerticalScrollBar().setValue(0);
        add(scrollPane, c);
    }

    public JFileChooser getFileChooser() {
        if (fileChooser_ == null) {
            do {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                }
                ;
            } while (fileChooser_ == null);
        }
        return fileChooser_;
    }

    public void changeFileChooser(boolean acceptAll, boolean acceptXML, boolean acceptOSM) {
        if (fileChooser_ == null) {
            do {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                }
                ;
            } while (fileChooser_ == null);
        }
        fileChooser_.resetChoosableFileFilters();
        if (acceptAll)
            fileChooser_.setAcceptAllFileFilterUsed(true);
        else
            fileChooser_.setAcceptAllFileFilterUsed(true);
        if (acceptOSM) {
            fileChooser_.addChoosableFileFilter(osmFileFilter_);
            fileChooser_.setFileFilter(osmFileFilter_);
        }
        if (acceptXML) {
            fileChooser_.addChoosableFileFilter(xmlFileFilter_);
            fileChooser_.setFileFilter(xmlFileFilter_);
        }
    }

    public SimulateControlPanel getSimulatePanel() {
        return simulatePanel_;
    }

    public EditControlPanel getEditPanel() {
        return editPanel_;
    }
    
    public ClusteringControlPanel getClusteringPanel() {
        return clusteringPanel_;
    }

    public ReportingControlPanel getReportingPanel() {
        return reportingPanel_;
    }

    
    
    public Component getSelectedTabComponent() {
        return tabbedPane_.getSelectedComponent();
    }

    public void activateEditPane() {
        tabbedPane_.setSelectedIndex(1);
    }

    public void stateChanged(ChangeEvent e) {
        if (tabbedPane_.getSelectedComponent() instanceof ReportingControlPanel) {
            reportingPanel_.setActive(true);
        } else {
            reportingPanel_.setActive(false);
        }
    }

    public void resizeSideBar(boolean maxOut) {
        if (maxOut) {
            Dimension newSize = simulatePanel_.getPreferredSize();
            newSize.setSize(newSize.width + 300, newSize.height < 800 ? 800 : newSize.height);
            setMinimumSize(new Dimension(newSize.width + 50, 400));
            editPanel_.setMinimumSize(newSize);
        } else {
            Dimension newSize = simulatePanel_.getPreferredSize();
            newSize.setSize(newSize.width + 120, newSize.height < 800 ? 800 : newSize.height);
            setMinimumSize(new Dimension(newSize.width + 50, 400));
            editPanel_.setMinimumSize(newSize);
        }
        this.revalidate();
        this.repaint();
    }

    public void tooglePanel() {
        Dimension size = simulatePanel_.getPreferredSize();
        hideBar_ = !hideBar_;
        if (hideBar_) {
            size.setSize(0, size.height < 800 ? 800 : size.height);
        } else {
            size.setSize(size.width + 155, size.height < 800 ? 800 : size.height);
        }
        setMinimumSize(new Dimension(size.width + 50, 400));
        editPanel_.setMinimumSize(new Dimension(size.width, size.height));
        this.revalidate();
        this.repaint();
    }

    public void switchToTab(int tabNr) {
        tabbedPane_.setSelectedIndex(tabNr);
    }
}
