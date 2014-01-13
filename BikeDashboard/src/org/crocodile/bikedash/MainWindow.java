
package org.crocodile.bikedash;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;
import java.util.prefs.Preferences;

import javax.swing.*;

import org.crocodile.fitbit.FitBitHelper;

public class MainWindow
{
    private static final int    METERS_IN_MILE               = 1609;

    private static final String PREF_PORT                    = "port";
    private static final String VERSION                      = "1.0";

    private Estimator           estimator                    = new Estimator();

    private static final Color  STOPPED_COLOR                = new Color(0, 128, 0);
    private static final Color  RUNNING_COLOR                = new Color(128, 0, 0);
    private static final long   MEASURMENTS_UI_UPDATE_PERIOD = 1000l;

    private JFrame              frame;
    private TickReader          reader;

    private JButton             btnRecord;
    private JButton             btnStop;
    private JButton             btnReset;
    private JButton             btnSave;

    private JLabel              lblTimeVal;
    private JLabel              lblRPMVal;
    private JLabel              lblCalVal;
    private JLabel              lblDistanceVal;

    private Timer               timer                        = new Timer();
    private Preferences         prefs;
    private Logger              log;

    private JMenuItem           mntmLogin;
    private JMenuItem           mntmLogout;

    private FitBitHelper        fitbit_helper;

    public static boolean isOSX()
    {
        String osName = System.getProperty("os.name");
        return osName != null && osName.contains("OS X");
    }

    private void initLog()
    {
        log = Logger.getLogger("org.crocodile.bikedash");
        log.setLevel(Level.FINE);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        handler.setFormatter(new SimpleFormatter());
        log.addHandler(handler);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        if(isOSX())
        {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "BikeDashboard");
            try
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch(ClassNotFoundException | InstantiationException | IllegalAccessException
                    | UnsupportedLookAndFeelException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

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
     * 
     * @throws Exception
     */
    public MainWindow() throws Exception
    {
        initLog();
        prefs = Preferences.userRoot().node("org.crocodile.bikedash");
        fitbit_helper = new FitBitHelper(prefs, log);

        initialize();
        updateButtonsAndColors();
        updateMenu();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                updateMesurementDisplay();
            }
        }, 0, MEASURMENTS_UI_UPDATE_PERIOD);
    }

    private void updateMenu()
    {
        if(fitbit_helper.isLoggedIn())
        {
            mntmLogin.setEnabled(false);
            mntmLogout.setEnabled(true);
        } else
        {
            mntmLogin.setEnabled(true);
            mntmLogout.setEnabled(false);
        }

    }

    protected void updateMesurementDisplay()
    {
        long t = estimator.getTime() / 1000l;
        long h = t / 3600;
        long m = (t % 3600) / 60;
        long s = t % 60;
        lblTimeVal.setText(String.format("%02d:%02d:%02d", h, m, s));
        lblRPMVal.setText("" + Math.round(estimator.getRPM()));
        lblCalVal.setText("" + Math.round(estimator.getCalories()));

        double roundedDist = BigDecimal.valueOf(estimator.getDistance() / METERS_IN_MILE)
                .setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        lblDistanceVal.setText("" + roundedDist);
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
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 88, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 29, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
        frame.getContentPane().setLayout(gridBagLayout);

        JLabel lblTime = new JLabel("Time");
        lblTime.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTime.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        GridBagConstraints gbc_lblTime = new GridBagConstraints();
        gbc_lblTime.insets = new Insets(0, 0, 5, 5);
        gbc_lblTime.gridx = 1;
        gbc_lblTime.gridy = 2;
        frame.getContentPane().add(lblTime, gbc_lblTime);

        lblTimeVal = new JLabel("10:25");
        lblTimeVal.setForeground(STOPPED_COLOR);
        lblTimeVal.setFont(new Font("Lucida Grande", Font.BOLD, 40));
        lblTimeVal.setBackground(Color.GRAY);
        GridBagConstraints gbc_lblTimeVal = new GridBagConstraints();
        gbc_lblTimeVal.gridwidth = 2;
        gbc_lblTimeVal.insets = new Insets(0, 0, 5, 5);
        gbc_lblTimeVal.gridx = 2;
        gbc_lblTimeVal.gridy = 2;
        frame.getContentPane().add(lblTimeVal, gbc_lblTimeVal);

        JLabel lblDistance = new JLabel("Distance");
        lblDistance.setHorizontalAlignment(SwingConstants.RIGHT);
        lblDistance.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        GridBagConstraints gbc_lblDistance = new GridBagConstraints();
        gbc_lblDistance.insets = new Insets(0, 0, 5, 5);
        gbc_lblDistance.gridx = 1;
        gbc_lblDistance.gridy = 3;
        frame.getContentPane().add(lblDistance, gbc_lblDistance);

        lblDistanceVal = new JLabel("0");
        lblDistanceVal.setForeground(new Color(0, 128, 0));
        lblDistanceVal.setFont(new Font("Lucida Grande", Font.BOLD, 40));
        lblDistanceVal.setBackground(Color.GRAY);
        GridBagConstraints gbc_lblSpeedVal = new GridBagConstraints();
        gbc_lblSpeedVal.gridwidth = 2;
        gbc_lblSpeedVal.insets = new Insets(0, 0, 5, 5);
        gbc_lblSpeedVal.gridx = 2;
        gbc_lblSpeedVal.gridy = 3;
        frame.getContentPane().add(lblDistanceVal, gbc_lblSpeedVal);

        JLabel lblMiles = new JLabel("Miles");
        GridBagConstraints gbc_lblMiles = new GridBagConstraints();
        gbc_lblMiles.insets = new Insets(0, 0, 5, 0);
        gbc_lblMiles.gridx = 4;
        gbc_lblMiles.gridy = 3;
        frame.getContentPane().add(lblMiles, gbc_lblMiles);

        JLabel lblCadence = new JLabel("Cadence");
        lblCadence.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCadence.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        GridBagConstraints gbc_lblCadence = new GridBagConstraints();
        gbc_lblCadence.insets = new Insets(0, 0, 5, 5);
        gbc_lblCadence.gridx = 1;
        gbc_lblCadence.gridy = 4;
        frame.getContentPane().add(lblCadence, gbc_lblCadence);

        lblRPMVal = new JLabel("25");
        lblRPMVal.setForeground(STOPPED_COLOR);
        lblRPMVal.setFont(new Font("Lucida Grande", Font.BOLD, 40));
        lblRPMVal.setBackground(Color.GRAY);
        GridBagConstraints gbc_lblRPMVal = new GridBagConstraints();
        gbc_lblRPMVal.gridwidth = 2;
        gbc_lblRPMVal.insets = new Insets(0, 0, 5, 5);
        gbc_lblRPMVal.gridx = 2;
        gbc_lblRPMVal.gridy = 4;
        frame.getContentPane().add(lblRPMVal, gbc_lblRPMVal);

        JLabel lblRpm = new JLabel("RPM");
        GridBagConstraints gbc_lblRpm = new GridBagConstraints();
        gbc_lblRpm.insets = new Insets(0, 0, 5, 0);
        gbc_lblRpm.gridx = 4;
        gbc_lblRpm.gridy = 4;
        frame.getContentPane().add(lblRpm, gbc_lblRpm);

        JLabel lblCalroies = new JLabel("Calroies");
        lblCalroies.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCalroies.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        GridBagConstraints gbc_lblCalroies = new GridBagConstraints();
        gbc_lblCalroies.insets = new Insets(0, 0, 5, 5);
        gbc_lblCalroies.gridx = 1;
        gbc_lblCalroies.gridy = 5;
        frame.getContentPane().add(lblCalroies, gbc_lblCalroies);

        lblCalVal = new JLabel("415");
        lblCalVal.setForeground(STOPPED_COLOR);
        lblCalVal.setFont(new Font("Lucida Grande", Font.BOLD, 40));
        lblCalVal.setBackground(Color.GRAY);
        GridBagConstraints gbc_lblCalVal = new GridBagConstraints();
        gbc_lblCalVal.gridwidth = 2;
        gbc_lblCalVal.insets = new Insets(0, 0, 5, 5);
        gbc_lblCalVal.gridx = 2;
        gbc_lblCalVal.gridy = 5;
        frame.getContentPane().add(lblCalVal, gbc_lblCalVal);

        btnStop = new JButton("Stop");
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                onStop();
            }
        });

        btnRecord = new JButton("Record");
        btnRecord.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                onStart();
            }
        });
        GridBagConstraints gbc_btnRecord = new GridBagConstraints();
        gbc_btnRecord.insets = new Insets(0, 0, 0, 5);
        gbc_btnRecord.gridx = 1;
        gbc_btnRecord.gridy = 7;
        frame.getContentPane().add(btnRecord, gbc_btnRecord);
        GridBagConstraints gbc_btnStop = new GridBagConstraints();
        gbc_btnStop.insets = new Insets(0, 0, 0, 5);
        gbc_btnStop.gridx = 2;
        gbc_btnStop.gridy = 7;
        frame.getContentPane().add(btnStop, gbc_btnStop);

        btnReset = new JButton("Reset");
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                onReset();
            }
        });
        GridBagConstraints gbc_btnReset = new GridBagConstraints();
        gbc_btnReset.insets = new Insets(0, 0, 0, 5);
        gbc_btnReset.gridx = 3;
        gbc_btnReset.gridy = 7;
        frame.getContentPane().add(btnReset, gbc_btnReset);

        btnSave = new JButton("Save");
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                onSave();
            }
        });
        GridBagConstraints gbc_btnSave = new GridBagConstraints();
        gbc_btnSave.gridx = 4;
        gbc_btnSave.gridy = 7;
        frame.getContentPane().add(btnSave, gbc_btnSave);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        if(!isOSX())
        {
            JMenu mnFile = new JMenu("File");
            mnFile.setMnemonic('F');
            menuBar.add(mnFile);

            JMenuItem mntmQuit = new JMenuItem("Quit");
            mntmQuit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    onQuit();
                }
            });
            mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_MASK));
            mnFile.add(mntmQuit);
        }

        JMenu mnFitBit = new JMenu("FitBit");
        menuBar.add(mnFitBit);

        mntmLogin = new JMenuItem("Login");
        mntmLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                onLogin();
            }
        });
        mnFitBit.add(mntmLogin);

        mntmLogout = new JMenuItem("Logout");
        mntmLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                onLogout();
            }
        });
        mnFitBit.add(mntmLogout);

        JMenu mnHelp = new JMenu("Help");
        mnHelp.setMnemonic('H');
        mnHelp.setMnemonic(KeyEvent.VK_HELP);
        menuBar.add(mnHelp);

        JMenuItem mntmAbout = new JMenuItem("About");
        mntmAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                onAbout();
            }
        });
        mnHelp.add(mntmAbout);
    }

    protected void onAbout()
    {
        JOptionPane.showMessageDialog(frame, "BikeDashboard v" + VERSION + "\n"
                + "https://github.com/vzaliva/BikeDashboard", "About", JOptionPane.PLAIN_MESSAGE);
    }

    protected void onQuit()
    {
        try
        {
            reader.stop();
        } catch(Exception e)
        {
            log.log(Level.WARNING, "Error stopping reader", e);
        }
        System.exit(0);
    }

    protected void onSave()
    {
        if(!fitbit_helper.isLoggedIn())
        {
            JOptionPane.showMessageDialog(frame, "You must log in to FitBit first!", "Please Log In",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        long duration = estimator.getTime();
        float calories = estimator.getCalories();
        float averagespeed = estimator.getAverageSpeed();
        try
        {
            submitToFitBit(System.currentTimeMillis(), duration, averagespeed, calories);
            JOptionPane.showMessageDialog(frame, "Successfully submitted to FitBit:\n", "Submitted to FitBit",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception e)
        {
            JOptionPane.showMessageDialog(frame, "Error submitting to FitBit:\n" + e.getMessage(), "FitBit Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitToFitBit(long currentTimeMillis, long duration, float averagespeed, float calories)
            throws Exception
    {
        // https://wiki.fitbit.com/display/API/API-Log-Activity
        fitbit_helper.logActivity(currentTimeMillis, duration, averagespeed, calories);
    }

    protected void onReset()
    {
        estimator.reset();
        updateMesurementDisplay();
        updateButtonsAndColors();
    }

    private void updateButtonsAndColors()
    {
        switch(estimator.getState())
        {
        case RUNNING:
            btnRecord.setEnabled(false);
            btnStop.setEnabled(true);
            btnSave.setEnabled(false);
            lblTimeVal.setForeground(RUNNING_COLOR);
            lblRPMVal.setForeground(RUNNING_COLOR);
            lblCalVal.setForeground(RUNNING_COLOR);
            lblDistanceVal.setForeground(RUNNING_COLOR);
            break;
        case STOPPED:
            btnRecord.setEnabled(true);
            btnStop.setEnabled(false);
            btnSave.setEnabled(true);
            lblTimeVal.setForeground(STOPPED_COLOR);
            lblRPMVal.setForeground(STOPPED_COLOR);
            lblCalVal.setForeground(STOPPED_COLOR);
            lblDistanceVal.setForeground(STOPPED_COLOR);
            break;
        }
    }

    protected void onStop()
    {
        estimator.stop();
        updateButtonsAndColors();
    }

    protected void onStart()
    {
        // reader = new RandomTickReader();
        // reader.addListener(estimator);
        // try
        // {
        // reader.start();
        // } catch(Exception e1)
        // {
        // }

        if(reader == null)
        {
            try
            {
                String port = prefs.get(PREF_PORT, null);
                if(port == null)
                {
                    // Port not set, try to find it automatically
                    String ports[] = SerialReader.getPorts();
                    if(ports == null || ports.length == 0)
                        throw new Exception("Device not connected!");
                    else if(ports.length > 1)
                        throw new Exception("More than one port available. Please choose via preferences!");
                    else
                        port = ports[0];
                }
                reader = new SerialReader(port);
                reader.addListener(estimator);
                reader.start();
            } catch(Exception e)
            {
                reader = null;
                JOptionPane.showMessageDialog(frame, "Error:\n" + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                log.log(Level.WARNING, "Error creating reader", e);
                return;
            }
        }

        estimator.start();
        updateButtonsAndColors();
    }

    protected void onLogout()
    {
        try
        {
            fitbit_helper.logout();
            updateMenu();
        } catch(Exception e)
        {
            JOptionPane
                    .showMessageDialog(frame, "Error clearning" + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            log.log(Level.WARNING, "Error clearning token", e);
        }

    }

    protected void onLogin()
    {
        fitbit_helper.login(frame);
        updateMenu();
    }

}
