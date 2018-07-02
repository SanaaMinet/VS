package vanetsim.gui.helpers;

import java.awt.Dimension;
import javax.swing.JTextArea;

public final class TextAreaLabel extends JTextArea {

    private static final long serialVersionUID = 6703416429165263141L;

    public TextAreaLabel(String text) {
        super(text);
        setOpaque(false);
        setBorder(null);
        setFocusable(false);
        setWrapStyleWord(true);
        setLineWrap(true);
    }

    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        dim.width -= 6;
        return dim;
    }
}
