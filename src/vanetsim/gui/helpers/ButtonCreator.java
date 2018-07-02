package vanetsim.gui.helpers;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import vanetsim.ErrorLog;
import vanetsim.localization.Messages;

public final class ButtonCreator {

    public static JButton getJButton(String imageName, String command, String altString, ActionListener listener) {
        JButton button;
     //  JOptionPane.showConfirmDialog(null, "b1");
        if (imageName.equals("")) 
        {
            button = new JButton(altString);
            button.setPreferredSize(new Dimension(42, 42));
        } else 
        {
       //     JOptionPane.showConfirmDialog(null, "A1");
            URL url = ClassLoader.getSystemResource("vanetsim/images/" + imageName);
            if (url != null) {
                button = new JButton(new ImageIcon(url));
            } else {
                button = new JButton(altString);
                button.setPreferredSize(new Dimension(42, 42));
                ErrorLog.log(Messages.getString("ButtonCreator.imageNotFound") + imageName, 5, ButtonCreator.class.getName(), "getJButton", null);
            }
        //    JOptionPane.showConfirmDialog(null, "B1");
        }
    //    JOptionPane.showConfirmDialog(null, "DEBUT1");
        button.setFocusPainted(false);
        button.setToolTipText(altString);
        button.setActionCommand(command);
        button.addActionListener(listener);
      //  JOptionPane.showConfirmDialog(null, "FIN1");
        return button;
    }

    public static JButton getJButton(String imageName, String command, String altString, boolean resize, ActionListener listener) {
        JButton button;
      //  JOptionPane.showConfirmDialog(null, "b2");
        if (imageName.equals("")) 
        {
            button = new JButton(altString);
            button.setPreferredSize(new Dimension(42, 42));
        } else {
           // JOptionPane.showConfirmDialog(null, "A2");
            URL url = ClassLoader.getSystemResource("vanetsim/images/" + imageName);
            if (url != null) {
                ImageIcon tmpImg = new ImageIcon(url);
                button = new JButton(tmpImg);
                button.setPreferredSize(new Dimension(tmpImg.getIconWidth(), tmpImg.getIconHeight()));
                button.setMaximumSize(new Dimension(tmpImg.getIconWidth(), tmpImg.getIconHeight()));
            } else {
                button = new JButton(altString);
                button.setPreferredSize(new Dimension(42, 42));
                ErrorLog.log(Messages.getString("ButtonCreator.imageNotFound") + imageName, 5, ButtonCreator.class.getName(), "getJButton", null);
            }
          //  JOptionPane.showConfirmDialog(null, "B2");
        }
     //   JOptionPane.showConfirmDialog(null, "DEBUT2");
        button.setFocusPainted(false);
        button.setToolTipText(altString);
        button.setActionCommand(command);
        button.addActionListener(listener);
     //   JOptionPane.showConfirmDialog(null, "FIN2");
        return button;
    }
}
