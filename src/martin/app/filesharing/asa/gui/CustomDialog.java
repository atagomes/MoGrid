package martin.app.filesharing.asa.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CustomDialog extends JDialog implements ActionListener {
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = -1187355998225926440L;
   
   private String     textInfo      = null;
   private JButton    option        = null;
   private JButton    cancel        = null;
   private JLabel     subTitleLabel = null;
   private JLabel     exampleLabel  = null;
   private JTextField textfield     = null;

   /**
    * Contructor.
    */
   public CustomDialog(JFrame frame, String title, String subTitle, String example, String optTitle, int sx, int sy, String defaultInfo) {
      
      super(frame, title, true);
      
      JPanel mainDialogPane = new JPanel(new GridBagLayout());
      setSize(sx, sy);
      setLocationRelativeTo(frame);
      setResizable(false);
      
      GridBagConstraints bgConstraints = new GridBagConstraints();
      bgConstraints.fill               = GridBagConstraints.HORIZONTAL;
      bgConstraints.insets = new Insets(10,10,10,10);  //top,left,bottom,right
      bgConstraints.gridy = 0;                         //linha
      bgConstraints.gridx = 0;                         //coluna

      JPanel textPan = new JPanel();
      textPan.setLayout(new BorderLayout(2, 2)); //horizontalGap, verticalGap
      this.textInfo = defaultInfo;
      this.subTitleLabel = new JLabel(subTitle);
      this.exampleLabel = new JLabel(example);
      if (textInfo == null) {
         textfield = new JTextField();
      } else {
         textfield = new JTextField(textInfo);
      }
      textPan.add(subTitleLabel, BorderLayout.NORTH);
      textPan.add(exampleLabel, BorderLayout.CENTER);
      textPan.add(textfield, BorderLayout.SOUTH);
      mainDialogPane.add(textPan, bgConstraints); 
      

      bgConstraints.insets = new Insets(10,10,10,10);  //top,left,bottom,right
      bgConstraints.gridy = 1;                         //linha      
      bgConstraints.gridx = 0;                         //coluna
      
      JPanel selectPan = new JPanel();
      selectPan.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
      option = new JButton(optTitle);
      cancel = new JButton("Cancel");
      option.addActionListener(this);
      cancel.addActionListener(this);
      selectPan.add(option);
      selectPan.add(cancel);      
      mainDialogPane.add(selectPan, bgConstraints);
      
      add(mainDialogPane);
      pack();
   }

   public CustomDialog(JFrame frame, String title, String subTitle, String example, String optTitle) {
      this(frame, title, subTitle, example, optTitle, 300, 160, "");
   }

   /**
    * Returns filename.
    */
   public String getTextValue() {
      return textInfo;
   }

   /**
    * ActionListener implementation.
    */
   public void actionPerformed(ActionEvent e) {
      if (e.getSource() == option) {
         textInfo = textfield.getText();
      } else if (e.getSource() == cancel) {
         textInfo = null;
      }
      setVisible(false);
   }
}
