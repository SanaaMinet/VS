package vanetsim.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.RepaintManager;
import vanetsim.ErrorLog;
import vanetsim.VanetSimStart;
import vanetsim.gui.controlpanels.ResearchSeriesDialog;
import vanetsim.gui.helpers.MouseClickManager;
import vanetsim.gui.helpers.ReRenderManager;
import vanetsim.localization.Messages;
import vanetsim.map.Map;
import vanetsim.scenario.Scenario;

public final class DrawingArea extends JComponent implements MouseWheelListener, KeyListener, MouseListener {

    private static final long serialVersionUID = -5210801710805449919L;

    private static final double ZOOM_VALUE = 5.0;

    private final Renderer renderer_ = Renderer.getInstance();

    private final boolean drawManualBuffered_;

    private final AffineTransform nullTransform_ = new AffineTransform();

    private BufferedImage streetsImage_ = null;

    private BufferedImage temporaryImage_ = null;

    private BufferedImage scaleImage_ = null;

    private Graphics2D temporaryG2d_ = null;

    public DrawingArea(boolean useDoubleBuffer, boolean drawManualBuffered) {
        drawManualBuffered_ = drawManualBuffered;
        setBackground(Color.white);
        setDoubleBuffered(useDoubleBuffer);
        setOpaque(true);
        setIgnoreRepaint(false);
        setFocusable(true);
        addMouseWheelListener(this);
        addKeyListener(this);
        addMouseListener(this);
        ErrorLog.log(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getAvailableAcceleratedMemory() / (1024 * 1024) + Messages.getString("DrawingArea.acceleratedVRAM"), 4, this.getName(), "init", null);
    }

    public void paintComponent(Graphics g) {
        synchronized (renderer_) {
            if (streetsImage_ == null || getWidth() != streetsImage_.getWidth() || getHeight() != streetsImage_.getHeight()) {
                prepareBufferedImages();
            }
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            if (drawManualBuffered_ == true) {
                temporaryG2d_.setTransform(nullTransform_);
                temporaryG2d_.drawImage(streetsImage_, 0, 0, null, this);
                renderer_.drawMovingObjects(temporaryG2d_);
                temporaryG2d_.drawImage(scaleImage_, getWidth() - 120, getHeight() - 40, null, this);
                g2d.drawImage(temporaryImage_, 0, 0, null, this);
            } else {
                g2d.drawImage(streetsImage_, 0, 0, null, this);
                renderer_.drawMovingObjects(g2d);
                g2d.drawImage(scaleImage_, getWidth() - 120, getHeight() - 40, null, this);
            }
            g2d.dispose();
        }
    }

    public void prepareBufferedImages() {
        if (streetsImage_ == null || getWidth() != streetsImage_.getWidth() || getHeight() != streetsImage_.getHeight()) {
            renderer_.setDrawHeight(getHeight());
            renderer_.setDrawWidth(getWidth());
            renderer_.updateParams();
            streetsImage_ = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(getWidth(), getHeight(), Transparency.OPAQUE);
        }
        if (drawManualBuffered_ == true && (temporaryImage_ == null || getWidth() != temporaryImage_.getWidth() || getHeight() != temporaryImage_.getHeight())) {
            temporaryImage_ = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(getWidth(), getHeight(), Transparency.OPAQUE);
            temporaryG2d_ = temporaryImage_.createGraphics();
            temporaryG2d_.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        if (scaleImage_ == null) {
            scaleImage_ = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(100, 30, Transparency.OPAQUE);
            Graphics2D tmpgraphics = scaleImage_.createGraphics();
            tmpgraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            tmpgraphics.setColor(Color.black);
            tmpgraphics.fillRect(0, 0, 100, 30);
        }
        renderer_.drawStaticObjects(streetsImage_);
        renderer_.drawScale(scaleImage_);
    }

    public void revalidate() {
        super.revalidate();
        if (this.getWidth() > 0 && this.getHeight() > 0 && (this.getWidth() != streetsImage_.getWidth() || this.getHeight() != streetsImage_.getHeight())) {
            this.prepareBufferedImages();
        }
    }

    public void paintImmediately(int x, int y, int width, int height) {
        RepaintManager repaintManager = null;
        boolean save = true;
        if (!isDoubleBuffered()) {
            repaintManager = RepaintManager.currentManager(this);
            save = repaintManager.isDoubleBufferingEnabled();
            repaintManager.setDoubleBufferingEnabled(false);
        }
        super.paintImmediately(x, y, width, height);
        if (repaintManager != null)
            repaintManager.setDoubleBufferingEnabled(save);
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL && e.getUnitsToScroll() != 0) {
            int scrollValue = e.getUnitsToScroll();
            double newzoom = renderer_.getMapZoom();
            if (scrollValue > 0) {
                for (int i = 0; i < scrollValue; i += 3) {
                    newzoom -= newzoom / ZOOM_VALUE;
                }
            } else {
                for (int i = 0; i > scrollValue; i -= 3) {
                    newzoom += newzoom / ZOOM_VALUE;
                }
            }
            renderer_.setMapZoom(newzoom);
            VanetSimStart.getMainControlPanel().getSimulatePanel().setZoomValue((int) Math.round(Math.log(renderer_.getMapZoom() * 1000) * 50));
            ReRenderManager.getInstance().doReRender();
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int keycode = e.getKeyCode();
        if (keycode == 38) {
            renderer_.pan('u');
            ReRenderManager.getInstance().doReRender();
        } else if (keycode == 40) {
            renderer_.pan('d');
            ReRenderManager.getInstance().doReRender();
        } else if (keycode == 37) {
            renderer_.pan('l');
            ReRenderManager.getInstance().doReRender();
        } else if (keycode == 39) {
            renderer_.pan('r');
            ReRenderManager.getInstance().doReRender();
        } else if (keycode == KeyEvent.VK_SPACE) {
            VanetSimStart.getMainControlPanel().getSimulatePanel().toggleSimulationStatus();
        } else if (keycode == KeyEvent.VK_H) {
            VanetSimStart.getMainControlPanel().tooglePanel();
        }  
         else if (keycode == KeyEvent.VK_I) {
            VanetSimStart.getMainControlPanel().getSimulatePanel().toggleVehileIDs();
        } else if (keycode == KeyEvent.VK_C) {
            VanetSimStart.getMainControlPanel().getSimulatePanel().toggleCommunicationDistance();
        } else if (keycode == KeyEvent.VK_K) {
            VanetSimStart.getMainControlPanel().getSimulatePanel().toggleVehicleConnections();
        } else if (keycode == KeyEvent.VK_R) {
            toggleResearchCreator();
        } else if (keycode == KeyEvent.VK_1) {
            VanetSimStart.getMainControlPanel().switchToTab(0);
        } else if (keycode == KeyEvent.VK_2) {
            VanetSimStart.getMainControlPanel().switchToTab(1);
        } else if (keycode == KeyEvent.VK_3) {
            VanetSimStart.getMainControlPanel().switchToTab(2);
        } else if (keycode == KeyEvent.VK_4) {
            VanetSimStart.getMainControlPanel().switchToTab(3);
        } else if (keycode == KeyEvent.VK_S) {
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
        } else if (keycode == KeyEvent.VK_O) {
            VanetSimStart.getMainControlPanel().changeFileChooser(true, true, false);
            int returnVal = VanetSimStart.getMainControlPanel().getFileChooser().showOpenDialog(VanetSimStart.getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Runnable job = new Runnable() {

                    public void run() {
                        
                        Map.getInstance().load(VanetSimStart.getMainControlPanel().getFileChooser().getSelectedFile(), false);
                    }
                };
                new Thread(job).start();
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            requestFocusInWindow();
            MouseClickManager.getInstance().signalPressed(e.getX(), e.getY());
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            MouseClickManager.getInstance().signalReleased(e.getX(), e.getY());
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        requestFocusInWindow();
        MouseClickManager.getInstance().setActive(true);
    }

    public void mouseExited(MouseEvent e) {
        MouseClickManager.getInstance().setActive(false);
    }

    public void toggleResearchCreator() {
        ResearchSeriesDialog.getInstance().setVisible(true);
    }
}
