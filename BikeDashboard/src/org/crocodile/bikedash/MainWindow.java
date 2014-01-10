package org.crocodile.bikedash;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Font;

import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

public class MainWindow
{
    private JFrame frame;
    private TickReader reader;
    private Estimator estimator = new Estimator();
    
    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            public void run()
            {
                try
                {
                    MainWindow window = new MainWindow();
                    window.frame.setVisible(true);
                } catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainWindow()
    {
        initialize();
        reader = new RandomTickReader();
        reader.addListener(estimator);
        reader.start();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        frame = new JFrame();
        frame.getContentPane().setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 88, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 29, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        frame.getContentPane().setLayout(gridBagLayout);
        
        JLabel lblTime = new JLabel("Time");
        lblTime.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTime.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        GridBagConstraints gbc_lblTime = new GridBagConstraints();
        gbc_lblTime.insets = new Insets(0, 0, 5, 5);
        gbc_lblTime.gridx = 1;
        gbc_lblTime.gridy = 1;
        frame.getContentPane().add(lblTime, gbc_lblTime);
        
        JLabel lblTimeVal = new JLabel("10:25");
        lblTimeVal.setForeground(new Color(0, 128, 0));
        lblTimeVal.setFont(new Font("Lucida Grande", Font.BOLD, 40));
        lblTimeVal.setBackground(Color.GRAY);
        GridBagConstraints gbc_lblTimeVal = new GridBagConstraints();
        gbc_lblTimeVal.gridwidth = 2;
        gbc_lblTimeVal.insets = new Insets(0, 0, 5, 5);
        gbc_lblTimeVal.gridx = 2;
        gbc_lblTimeVal.gridy = 1;
        frame.getContentPane().add(lblTimeVal, gbc_lblTimeVal);
        
        JLabel lblRpm = new JLabel("RPM");
        lblRpm.setHorizontalAlignment(SwingConstants.RIGHT);
        lblRpm.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        GridBagConstraints gbc_lblRpm = new GridBagConstraints();
        gbc_lblRpm.insets = new Insets(0, 0, 5, 5);
        gbc_lblRpm.gridx = 1;
        gbc_lblRpm.gridy = 2;
        frame.getContentPane().add(lblRpm, gbc_lblRpm);
        
        JLabel lblRPMVal = new JLabel("25");
        lblRPMVal.setForeground(new Color(0, 128, 0));
        lblRPMVal.setFont(new Font("Lucida Grande", Font.BOLD, 40));
        lblRPMVal.setBackground(Color.GRAY);
        GridBagConstraints gbc_lblRPMVal = new GridBagConstraints();
        gbc_lblRPMVal.gridwidth = 2;
        gbc_lblRPMVal.insets = new Insets(0, 0, 5, 5);
        gbc_lblRPMVal.gridx = 2;
        gbc_lblRPMVal.gridy = 2;
        frame.getContentPane().add(lblRPMVal, gbc_lblRPMVal);
        
        JLabel lblCalroies = new JLabel("Calroies");
        lblCalroies.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCalroies.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        GridBagConstraints gbc_lblCalroies = new GridBagConstraints();
        gbc_lblCalroies.insets = new Insets(0, 0, 5, 5);
        gbc_lblCalroies.gridx = 1;
        gbc_lblCalroies.gridy = 3;
        frame.getContentPane().add(lblCalroies, gbc_lblCalroies);
        
        JLabel lblCalVal = new JLabel("415");
        lblCalVal.setForeground(new Color(0, 128, 0));
        lblCalVal.setFont(new Font("Lucida Grande", Font.BOLD, 40));
        lblCalVal.setBackground(Color.GRAY);
        GridBagConstraints gbc_lblCalVal = new GridBagConstraints();
        gbc_lblCalVal.gridwidth = 2;
        gbc_lblCalVal.insets = new Insets(0, 0, 5, 5);
        gbc_lblCalVal.gridx = 2;
        gbc_lblCalVal.gridy = 3;
        frame.getContentPane().add(lblCalVal, gbc_lblCalVal);
        
        JButton btnRecord = new JButton("Record");
        btnRecord.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onRecord();
            }
        });
        GridBagConstraints gbc_btnRecord = new GridBagConstraints();
        gbc_btnRecord.insets = new Insets(0, 0, 0, 5);
        gbc_btnRecord.gridx = 0;
        gbc_btnRecord.gridy = 5;
        frame.getContentPane().add(btnRecord, gbc_btnRecord);
        
        JButton btnPause = new JButton("Pause");
        btnPause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onPause();
            }
        });
        GridBagConstraints gbc_btnPause = new GridBagConstraints();
        gbc_btnPause.insets = new Insets(0, 0, 0, 5);
        gbc_btnPause.gridx = 1;
        gbc_btnPause.gridy = 5;
        frame.getContentPane().add(btnPause, gbc_btnPause);
        
        JButton btnStop = new JButton("Stop");
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onStop();
            }
        });
        GridBagConstraints gbc_btnStop = new GridBagConstraints();
        gbc_btnStop.insets = new Insets(0, 0, 0, 5);
        gbc_btnStop.gridx = 2;
        gbc_btnStop.gridy = 5;
        frame.getContentPane().add(btnStop, gbc_btnStop);
        
        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onReset();
            }
        });
        GridBagConstraints gbc_btnReset = new GridBagConstraints();
        gbc_btnReset.insets = new Insets(0, 0, 0, 5);
        gbc_btnReset.gridx = 3;
        gbc_btnReset.gridy = 5;
        frame.getContentPane().add(btnReset, gbc_btnReset);
        
        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });
        GridBagConstraints gbc_btnSave = new GridBagConstraints();
        gbc_btnSave.gridx = 4;
        gbc_btnSave.gridy = 5;
        frame.getContentPane().add(btnSave, gbc_btnSave);
        
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu mnFile = new JMenu("File");
        mnFile.setMnemonic('F');
        menuBar.add(mnFile);
        
        JMenuItem mntmQuit = new JMenuItem("Quit");
        mntmQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onQuit();
            }
        });
        mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_MASK));
        mnFile.add(mntmQuit);
        
        JMenu mnHelp = new JMenu("Help");
        mnHelp.setMnemonic('H');
        mnHelp.setMnemonic(KeyEvent.VK_HELP);
        menuBar.add(mnHelp);
        
        JMenuItem mntmAbout = new JMenuItem("About");
        mntmAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAbout();
            }
        });
        mnHelp.add(mntmAbout);
    }

    protected void onAbout()
    {
        // TODO Auto-generated method stub
        
    }

    protected void onQuit()
    {
        // TODO Auto-generated method stub
        
    }

    protected void onSave()
    {
        // TODO Auto-generated method stub
        
    }

    protected void onReset()
    {
        // TODO Auto-generated method stub
        
    }

    protected void onStop()
    {
        // TODO Auto-generated method stub
        
    }

    protected void onPause()
    {
        // TODO Auto-generated method stub
        
    }

    protected void onRecord()
    {
        // TODO Auto-generated method stub
        
    }
}
