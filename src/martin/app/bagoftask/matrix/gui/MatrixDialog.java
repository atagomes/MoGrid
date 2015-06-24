package martin.app.bagoftask.matrix.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MatrixDialog extends JDialog implements ActionListener {
   
   /** Comment for <code>serialVersionUID</code> */
   private static final long serialVersionUID = 9049990763822932474L;
   
   private String     parameters     = null;
   private JButton    option         = null;
   private JButton    cancel         = null;
   private JLabel     subTitleLabel1 = null;
   private JLabel     subTitleLabel2 = null;
   private JLabel     exampleLabel   = null;
   private JTextField textfield      = null;

   /**
    * Contructor.
    */
   public MatrixDialog(JFrame frame, String[] titles) {      
      super(frame, titles[0], true);
      JPanel mainDialogPane = new JPanel(new GridBagLayout());
      setSize(300, 160);
      setLocationRelativeTo(frame);
      setResizable(false);

      //configura acao a ser executada qdo a GUI eh fechada
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent we) {
            parameters = null;
         }
      });
      
      GridBagConstraints bgConstraints = new GridBagConstraints();
      bgConstraints.fill               = GridBagConstraints.HORIZONTAL;
      bgConstraints.insets = new Insets(10,10,10,10);  //top,left,bottom,right
      bgConstraints.gridy = 0;                         //linha
      bgConstraints.gridx = 0;                         //coluna

      JPanel textPan = new JPanel();
      textPan.setLayout(new BorderLayout(2, 2)); //horizontalGap, verticalGap
      this.subTitleLabel1 = new JLabel(titles[1]);
      this.subTitleLabel2 = new JLabel(titles[2]);
      this.parameters = titles[3];
      this.exampleLabel = new JLabel(titles[4]);
      if (parameters == null) {
         textfield = new JTextField();
      } else {
         textfield = new JTextField(parameters);
      }
      JPanel panel = new JPanel();
      panel.setLayout(new BorderLayout()); 
      panel.add(subTitleLabel1, BorderLayout.NORTH);
      panel.add(subTitleLabel2, BorderLayout.SOUTH);
      
      textPan.add(panel, BorderLayout.NORTH);
      textPan.add(exampleLabel, BorderLayout.CENTER);
      textPan.add(textfield, BorderLayout.SOUTH);
      mainDialogPane.add(textPan, bgConstraints); 
      

      bgConstraints.insets = new Insets(10,10,10,10);  //top,left,bottom,right
      bgConstraints.gridy = 1;                         //linha      
      bgConstraints.gridx = 0;                         //coluna
      
      JPanel selectPan = new JPanel();
      selectPan.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
      option = new JButton(titles[5]);
      cancel = new JButton("Cancel");
      option.addActionListener(this);
      cancel.addActionListener(this);
      selectPan.add(option);
      selectPan.add(cancel);      
      mainDialogPane.add(selectPan, bgConstraints);
      
      getContentPane().add(mainDialogPane);
      pack();
   }

   /**
    * Returns filename.
    */
   public String getParameters() {
      return parameters;
   }

   /**
    * ActionListener implementation.
    */
   public void actionPerformed(ActionEvent e) {      
      if ( e.getSource() == option ) {
         parameters = textfield.getText();
      } else if ( e.getSource() == cancel ) {
         parameters = null;
      }
      setVisible(false);
   }
}
