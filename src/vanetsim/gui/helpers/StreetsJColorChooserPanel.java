package vanetsim.gui.helpers;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import vanetsim.localization.Messages;

public final class StreetsJColorChooserPanel extends AbstractColorChooserPanel implements ItemListener {

    private static final long serialVersionUID = -8693021938856630099L;

    private static final String LABELS[] = { Messages.getString("StreetsJColorChooserPanel.blue"), Messages.getString("StreetsJColorChooserPanel.green"), Messages.getString("StreetsJColorChooserPanel.red"), Messages.getString("StreetsJColorChooserPanel.orange"), Messages.getString("StreetsJColorChooserPanel.yellow"), Messages.getString("StreetsJColorChooserPanel.white") };

    private static final Color COLORS[] = { new Color(117, 146, 185), new Color(116, 194, 116), new Color(225, 98, 102), new Color(253, 184, 100), new Color(252, 249, 105), Color.WHITE };

    private JComboBox<String> comboBox_;

    private void setColor(Color color) 
    {
        comboBox_.setSelectedIndex(findColorPosition(color));
    }

    private int findColorLabel(Object label) {
        String stringLabel = label.toString();
        int position = -1;
        for (int i = 0; i < LABELS.length; ++i) {
            if (stringLabel.equals(LABELS[i])) {
                position = i;
                break;
            }
        }
        return position;
    }

    private int findColorPosition(Color color) {
        int position = COLORS.length - 1;
        int colorRGB = color.getRGB();
        for (int i = 0; i < COLORS.length; ++i) {
            if ((COLORS[i] != null) && (colorRGB == COLORS[i].getRGB())) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void itemStateChanged(ItemEvent itemEvent) {
        int state = itemEvent.getStateChange();
        if (state == ItemEvent.SELECTED) {
            int position = findColorLabel(itemEvent.getItem());
            if (position != -1) {
                ColorSelectionModel selectionModel = getColorSelectionModel();
                selectionModel.setSelectedColor(COLORS[position]);
            }
        }
    }

    public String getDisplayName() {
        return Messages.getString("StreetsJColorChooserPanel.streetcolors");
    }

    public Icon getSmallDisplayIcon() {
        URL url = ClassLoader.getSystemResource("vanetsim/images/streeticon.gif");
        if (url != null)
            return (Icon) new ImageIcon(url);
        else
            return (Icon) new ImageIcon();
    }

    public Icon getLargeDisplayIcon() {
        URL url = ClassLoader.getSystemResource("vanetsim/images/streeticon.gif");
        if (url != null)
            return (Icon) new ImageIcon(url);
        else
            return (Icon) new ImageIcon();
    }

    protected void buildChooser() {
        comboBox_ = new JComboBox<String>(LABELS);
        comboBox_.addItemListener(this);
        add(comboBox_);
    }

    public void updateChooser() {
        Color color = getColorFromModel();
        setColor(color);
    }
}
