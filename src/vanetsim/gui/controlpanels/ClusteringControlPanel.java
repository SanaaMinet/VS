package vanetsim.gui.controlpanels;

import adhoc.Param;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import vanetsim.Poisson;
 

public final class ClusteringControlPanel extends JPanel implements ActionListener, ItemListener, ListSelectionListener {

   public static int NbreDeVehicule; 
    
   public static double lambda;
   public static int tsim;
   public static DefaultTableModel S = new DefaultTableModel(); 
    
    
    
    
    /*
    private static final long serialVersionUID = 5121974914528330821L;

    private static final int STATISTICS_ACTUALIZATION_INTERVAL = 500;

    private static final int BEACONINFO_ACTUALIZATION_INTERVAL = 500;

    private static final DecimalFormat INTEGER_FORMAT = new DecimalFormat(",##0");

    private static final DecimalFormat INTEGER_FORMAT_FRACTION = new DecimalFormat(",##0.00");
    */
  
//------------------------------------------------------------------------------    
   private JPanel    jPanel2 = null;
   private JPanel    jPanel3 = null;
   private JPanel    jPanel4 = null;
   private JPanel    jPanel5 = null;
   //private JPanel    jPanel6 = null;
   private JPanel    jPanel7 = null;
   private JPanel    jPanel8 = null;
   
   
   private JLabel    jLabel2 = null;
   private JLabel    jLabel3 = null;
   private JLabel    jLabel4 = null;
     
   private JTextField Tlambda = null;
   private JTextField Tsim   = null;
   
   private JTable    jTable1 = null;
   //private JTable    jTable2 = null;
   
   private JScrollPane jScrollPane1 = null;
   //private JScrollPane jScrollPane2 = null;
   
   private JButton jButton1         =null;
   private JButton jButtonParamAlgo =null;
   private JButton createButton_    =null;
   private JButton saveButton_      =null;
   
   private JButton loadButton_   =null;
   private JButton startButton_  =null;
   private JButton pauseButton_  =null;
//------------------------------------------------------------------------------    
 
    public ClusteringControlPanel() {
        
        //---------------------------------------------
        setLayout(null);//new GridBagLayout());
       
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Tlambda = new javax.swing.JTextField();
        Tsim = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        
        //--------------------------------------------
        jPanel3      = new javax.swing.JPanel();
        jPanel4      = new javax.swing.JPanel();
        jPanel5      = new javax.swing.JPanel();
        //jPanel6      = new javax.swing.JPanel();
        jPanel7      = new javax.swing.JPanel();
        jPanel8      = new javax.swing.JPanel();
        jTable1      = new javax.swing.JTable();
        //jTable2      = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        //jScrollPane2 = new javax.swing.JScrollPane();
        jButton1     = new javax.swing.JButton();
        jButtonParamAlgo = new javax.swing.JButton();
        
        createButton_     = new javax.swing.JButton();
        saveButton_       = new javax.swing.JButton();
        loadButton_     = new javax.swing.JButton();
        startButton_     = new javax.swing.JButton();
        pauseButton_     = new javax.swing.JButton();
   
 
    //------------------------------JPanel2-------------------------------------
        
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Poisson Law Distribution ", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Times New Roman", 0, 12))); // NOI18N
        jPanel2.setLayout(null);

        jLabel2.setFont(new java.awt.Font("Times New Roman", 2, 12)); // NOI18N
        jLabel2.setText("Lambda :");
        jPanel2.add(jLabel2);
        jLabel2.setBounds(30, 30, 70, 14);

        jLabel3.setFont(new java.awt.Font("Times New Roman", 2, 12)); // NOI18N
        jLabel3.setText("sec");
        jPanel2.add(jLabel3);
        jLabel3.setBounds(280, 60, 40, 14);

        Tlambda.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        Tlambda.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Tlambda.setText("5");
        jPanel2.add(Tlambda);
        Tlambda.setBounds(180, 30, 90, 20);

        Tsim.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        Tsim.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Tsim.setText("300");
        jPanel2.add(Tsim);
        Tsim.setBounds(180, 60, 90, 20);

        jLabel4.setFont(new java.awt.Font("Times New Roman", 2, 12)); // NOI18N
        jLabel4.setText("Simulation time :");
        jPanel2.add(jLabel4);
        jLabel4.setBounds(30, 60, 130, 14);

        this.add(jPanel2);
        jPanel2.setBounds(10, 10, 520, 90);
        
        
        
    //-----------------------------------JPanel3--------------------------------  
    
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Distribution ", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Times New Roman", 0, 12))); // NOI18N
        jPanel3.setLayout(null);
        jPanel3.setBounds(10, 110, 520, 60);
        
        jButton1.setFont(new java.awt.Font("Times New Roman", 2, 12)); // NOI18N
        jButton1.setText("Poisson Distribution");
        
        jPanel3.add(jButton1);
        jButton1.setBounds(70, 23, 360, 25);
 
        this.add(jPanel3);
        
    //-----------------------------------JPanel4--------------------------------    

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Vehicles Arrival Time & Positions ", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Times New Roman", 0, 12))); // NOI18N
        jPanel4.setLayout(null);

        jTable1.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jTable1.setModel(S);
        jTable1.setGridColor(new java.awt.Color(204, 204, 204));
        jTable1.setRowHeight(20);
        jScrollPane1.setViewportView(jTable1);

        S.addColumn("N°");
        S.addColumn("ID-Veh");
        S.addColumn("Tps. arr.");
        S.addColumn("Xsrc");
        S.addColumn("Ysrc");
        S.addColumn("Xdest");
        S.addColumn("Ydest");
        S.addColumn("Etat");
        S.addColumn("CurPos");        
        S.addColumn("CurX");
        S.addColumn("CurY");
        S.addColumn("CurSpeed");
        S.setRowCount(1);
       

        jPanel4.add(jScrollPane1);
        jScrollPane1.setBounds(10, 20, 500, 400);
        

        
        this.add(jPanel4);
        jPanel4.setBounds(10, 180, 520, 435);   

    //-------------------------------------JPanel5------------------------------ 
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Scenario ", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Times New Roman", 0, 12))); // NOI18N
        jPanel5.setLayout(null);
         
        
        createButton_.setFont(new java.awt.Font("Times New Roman", 2, 12));// NOI18N
        createButton_.setText("Generate scenario");
        
        saveButton_.setFont(new java.awt.Font("Times New Roman", 2, 12)); // NOI18N
        saveButton_.setText("Save scenario");
        
        loadButton_.setFont(new java.awt.Font("Times New Roman", 2, 12)); // NOI18N
        loadButton_.setText("Load scenario");
        
        jPanel5.add(createButton_);
        createButton_.setBounds(70, 15, 360, 25);
         
        jPanel5.add(saveButton_);
        saveButton_.setBounds(70, 50, 360, 25);         
         
        jPanel5.add(loadButton_);
        loadButton_.setBounds(70, 85, 360, 25);
        
        
        this.add(jPanel5);    
        jPanel5.setBounds(10, 625, 520, 120);
        
        //-----------------------------------JPanel6--------------------------------
            
        /*jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Loaded Scenario - Vehicles Arrival Time & Positions ", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Times New Roman", 0, 12))); // NOI18N
        jPanel6.setLayout(null);

        jTable2.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jTable2.setModel(S);
        jTable2.setGridColor(new java.awt.Color(204, 204, 204));
        jTable2.setRowHeight(20);
        jScrollPane2.setViewportView(jTable2);

       
       

        jPanel6.add(jScrollPane2);
        jScrollPane2.setBounds(10, 20, 465, 400);
        

        //return here
        this.add(jPanel6);
        jPanel6.setBounds(10, 755, 500, 435); */
        
    //--------------------------------------------------------------------------  
        
    //-----------------------------------JPanel7--------------------------------
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Simulation ", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Times New Roman", 0, 12))); // NOI18N
        jPanel7.setLayout(null);
        
        startButton_.setFont(new java.awt.Font("Times New Roman", 2, 12)); // NOI18N
        startButton_.setText("Start simulation");
        
        pauseButton_.setFont(new java.awt.Font("Times New Roman", 2, 12)); // NOI18N
        pauseButton_.setText("Pause");
        
        jPanel7.add(startButton_);
        startButton_.setBounds(70, 15, 360, 25);
        
        jPanel7.add(pauseButton_);
        pauseButton_.setBounds(70, 50, 360, 25);
        

        this.add(jPanel7);    
        jPanel7.setBounds(10, 825, 520, 85);//To see
        
     
        
    //-----------------------------------JPanel8--------------------------------
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Algorithm's Parameters ", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Times New Roman", 0, 12))); // NOI18N
        jPanel8.setLayout(null);
        
        jButtonParamAlgo.setFont(new java.awt.Font("Times New Roman", 2, 12)); // NOI18N
        jButtonParamAlgo.setText("Edit Algorithms parameters");
        
        jPanel8.add(jButtonParamAlgo);
        jButtonParamAlgo.setBounds(70, 23, 360, 25);
        
        this.add(jPanel8);    
        jPanel8.setBounds(10, 755, 520, 60);//To see
        
        
    //--------------------------------------------------------------------------

        
        jButton1.addActionListener(new java.awt.event.ActionListener() 
        {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        
    //--------------------------------------------------------------------------

        
        jButtonParamAlgo.addActionListener(new java.awt.event.ActionListener() 
        {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonParamAlgoActionPerformed(evt);
            }
        });
             
        
    //--------------------------------------------------------------------------    
        createButton_.addActionListener(new java.awt.event.ActionListener() 
        {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
    //--------------------------------------------------------------------------     
        saveButton_.addActionListener(new java.awt.event.ActionListener() 
        {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });        
    //--------------------------------------------------------------------------     
        loadButton_.addActionListener(new java.awt.event.ActionListener() 
        {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });   
    //--------------------------------------------------------------------------    
                 startButton_.addActionListener(new java.awt.event.ActionListener() 
        {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
    //--------------------------------------------------------------------------             
        pauseButton_.addActionListener(new java.awt.event.ActionListener() 
        {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
                 
    //--------------------------------------------------------------------------    

        
        
    
    
        
    
        
/*        jPanel4.add(createButton_);
        createButton_.setBounds(70, 430, 360, 25);
         
        jPanel4.add(saveButton_);
        saveButton_.setBounds(70, 465, 360, 25);         
         
        jPanel4.add(loadButton_);
        loadButton_.setBounds(70, 500, 360, 25);*/
    
        JPanel space = new JPanel();
        space.setOpaque(false);
        add(space, jPanel2);
        add(space, jPanel3);
        add(space, jPanel4);
        add(space, jPanel5);
        //add(space, jPanel6);
        add(space, jPanel7);
        add(space, jPanel8);
  
}

        
//------------------------------------------------------------------------------        
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) 
    {                                         
        lambda   = Double.parseDouble(Tlambda.getText().toString());

        //NTN.setText(""+ps.nbObj);
        tsim     = Integer.parseInt(Tsim.getText().toString());
   Poisson  ps = new  Poisson(tsim); 
   ps.generatePoissonProcess(lambda);    
        
   NbreDeVehicule = S.getRowCount();
    //Simulation.Vehicule.CreationVehicule(S.getRowCount(), zone);
    //compteur = -1;
    //jTextField5.setText(""+0);
    //NbvActif    = 0;
    //NbvInactif  = S.getRowCount();
    //NbvHors     = 0;
     
    }
    
//------------------------------------------------------------------------------
    private void jButtonParamAlgoActionPerformed(java.awt.event.ActionEvent evt) 
    {                                         
 // Here Window Param
     //   JOptionPane.showConfirmDialog(null, "Param Algorithm Window here");
    Param pr = new Param();
    pr.setBounds(100, 10, 750, 600);
    pr.setTitle("Paramétres de la simulation");
    pr.setResizable(false);
 
    pr.setVisible(true);
    }
    
//------------------------------------------------------------------------------
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) 
    {                                         
        //---------appel de creation vehicule----------------
       //JOptionPane.showConfirmDialog(null, "A1");
        EditVehicleControlPanel evcp = new EditVehicleControlPanel();
        java.awt.event.ActionEvent ev = new ActionEvent(createButton_, 0, "createRandom");
   
       // evt.setSource("createRandom");
        evcp.actionPerformed(ev);
      //JOptionPane.showConfirmDialog(null, "A2");

    }
    
//------------------------------------------------------------------------------    
    
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) 
    {                                         
       //JOptionPane.showConfirmDialog(null, "B1");
        //---------appel save xml scenario----------------------
        EditControlPanel  ecp = new EditControlPanel();
        java.awt.event.ActionEvent ev = new ActionEvent(saveButton_, 0, "savescenario");
       
        ecp.actionPerformed(ev);
        
        //JOptionPane.showConfirmDialog(null, "B2");
   
    }
    
//------------------------------------------------------------------------------    
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) 
    {                                         
       //JOptionPane.showConfirmDialog(null, "B1");
        //---------appel save xml scenario----------------------
        SimulateControlPanel  scp = new SimulateControlPanel();
        java.awt.event.ActionEvent ev = new ActionEvent(loadButton_, 0, "loadscenario");
       
        scp.actionPerformed(ev);
        
        //JOptionPane.showConfirmDialog(null, "B2");
   
    }    
//------------------------------------------------------------------------------    
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) 
    {                                         
        //---------appel de creation vehicule----------------
       //JOptionPane.showConfirmDialog(null, "A1");
        SimulateControlPanel stcp = new SimulateControlPanel();
        java.awt.event.ActionEvent ev = new ActionEvent(startButton_, 0, "start");
   
       // evt.setSource("createRandom");
        stcp.actionPerformed(ev);
      //JOptionPane.showConfirmDialog(null, "A2");

    }
//------------------------------------------------------------------------------    
        private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) 
    {                                         
        //---------appel de creation vehicule----------------
       //JOptionPane.showConfirmDialog(null, "A1");
        SimulateControlPanel pscp = new SimulateControlPanel();
        java.awt.event.ActionEvent ev = new ActionEvent(pauseButton_, 0, "pause");
   
       // evt.setSource("createRandom");
        pscp.actionPerformed(ev);
      //JOptionPane.showConfirmDialog(null, "A2");

    }
//------------------------------------------------------------------------------    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
//------------------------------------------------------------------------------
    @Override
    public void itemStateChanged(ItemEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
//------------------------------------------------------------------------------
    @Override
    public void valueChanged(ListSelectionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
 
