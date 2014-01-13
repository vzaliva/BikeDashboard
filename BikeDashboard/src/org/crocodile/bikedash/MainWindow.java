
package org.crocodile.bikedash;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Timer;
import java.util.logging.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.scribe.builder.ServiceBuilder;
import org.crocodile.fitbit.FitbitApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

import javax.swing.*;

public class MainWindow
{
    private static final String PREF_PORT                    = "port";
    private static final String PREF_FITBITSECRET            = "fitbitsecret";
    private static final String PREF_FITBITTOKEN             = "fitbittoken";
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

    private Timer               timer                        = new Timer();
    private Preferences         prefs;
    private Logger              log;
    private Token               token;

    private JMenuItem           mntmLogin;
    private JMenuItem           mntmLogout;

    private static final String fITBIT_API_KEY               = "800918c46b204134aca64fa1e1bb8a38";
    private static final String FITBIT_API_SECRET            = "1826e44b68fd4ff6b855a3e3722a7af7";

    public static boolean isOSX()
    {
        String osName = System.getProperty("os.name");
        return osName.contains("OS X");
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
        loadToken();

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
        if(token == null)
        {
            mntmLogin.setEnabled(true);
            mntmLogout.setEnabled(false);
        } else
        {
            mntmLogin.setEnabled(false);
            mntmLogout.setEnabled(true);
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
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 29, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
        frame.getContentPane().setLayout(gridBagLayout);

        JLabel lblTime = new JLabel("Time");
        lblTime.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTime.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
        GridBagConstraints gbc_lblTime = new GridBagConstraints();
        gbc_lblTime.insets = new Insets(0, 0, 5, 5);
        gbc_lblTime.gridx = 1;
        gbc_lblTime.gridy = 1;
        frame.getContentPane().add(lblTime, gbc_lblTime);

        lblTimeVal = new JLabel("10:25");
        lblTimeVal.setForeground(STOPPED_COLOR);
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

        lblRPMVal = new JLabel("25");
        lblRPMVal.setForeground(STOPPED_COLOR);
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

        lblCalVal = new JLabel("415");
        lblCalVal.setForeground(STOPPED_COLOR);
        lblCalVal.setFont(new Font("Lucida Grande", Font.BOLD, 40));
        lblCalVal.setBackground(Color.GRAY);
        GridBagConstraints gbc_lblCalVal = new GridBagConstraints();
        gbc_lblCalVal.gridwidth = 2;
        gbc_lblCalVal.insets = new Insets(0, 0, 5, 5);
        gbc_lblCalVal.gridx = 2;
        gbc_lblCalVal.gridy = 3;
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
        gbc_btnRecord.gridy = 5;
        frame.getContentPane().add(btnRecord, gbc_btnRecord);
        GridBagConstraints gbc_btnStop = new GridBagConstraints();
        gbc_btnStop.insets = new Insets(0, 0, 0, 5);
        gbc_btnStop.gridx = 2;
        gbc_btnStop.gridy = 5;
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
        gbc_btnReset.gridy = 5;
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
        gbc_btnSave.gridy = 5;
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
        JOptionPane.showMessageDialog(frame, "Not yet implemented!", "Warning", JOptionPane.WARNING_MESSAGE);
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
            break;
        case STOPPED:
            btnRecord.setEnabled(true);
            btnStop.setEnabled(false);
            btnSave.setEnabled(true);
            lblTimeVal.setForeground(STOPPED_COLOR);
            lblRPMVal.setForeground(STOPPED_COLOR);
            lblCalVal.setForeground(STOPPED_COLOR);
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
                // reader = new RandomTickReader();
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
            saveToken();
            token = null;
            updateMenu();
        } catch(BackingStoreException e)
        {
            JOptionPane
                    .showMessageDialog(frame, "Error clearning" + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            log.log(Level.WARNING, "Error clearning token", e);
        }

    }

    protected void onLogin()
    {
        OAuthService service = new ServiceBuilder().provider(FitbitApi.class).apiKey(fITBIT_API_KEY)
                .apiSecret(FITBIT_API_SECRET).build();

        log.log(Level.FINE, "Starting Fitbit's OAuth Workflow");
        log.log(Level.FINE, "Fetching the Request Token...");
        Token requestToken = service.getRequestToken();
        log.log(Level.FINE, "Got the Request Token!");

        String url = service.getAuthorizationUrl(requestToken);
        log.log(Level.FINE, "Now go and authorize Scribe here: " + url);
        try
        {
            openBrowser(url);
        } catch(Exception e)
        {
            JOptionPane.showMessageDialog(frame, "Error opening browser:\n" + e.getMessage(), "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            log.log(Level.WARNING, "Error opening browswer reader", e);
            return;
        }

        String verifier_s = (String) JOptionPane.showInputDialog(frame, "Please enter PIN string provided by FitBit",
                "FitBit PIT", JOptionPane.QUESTION_MESSAGE);
        Verifier verifier = new Verifier(verifier_s);

        log.log(Level.FINE, "Trading the Request Token for an Access Token...");
        token = service.getAccessToken(requestToken, verifier);
        log.log(Level.FINE, "Got the Access Token: " + token + " )");

        try
        {
            saveToken();
        } catch(BackingStoreException e)
        {
            token = null;
            JOptionPane.showMessageDialog(frame, "Error saving token" + e.getMessage(), "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            log.log(Level.WARNING, "Error saving token", e);
        }
        updateMenu();
    }

    private void loadToken()
    {
        String t = prefs.get(PREF_FITBITTOKEN, null);
        String s = prefs.get(PREF_FITBITSECRET, null);
        if(t == null || s == null)
            token = null;
        else
            token = new Token(t, s);
    }

    private void saveToken() throws BackingStoreException
    {
        if(token == null)
        {
            prefs.remove(PREF_FITBITTOKEN);
            prefs.remove(PREF_FITBITSECRET);
        } else
        {
            prefs.put(PREF_FITBITTOKEN, token.getToken());
            prefs.put(PREF_FITBITSECRET, token.getSecret());
        }
        prefs.sync();
    }

    private void openBrowser(String url) throws Exception
    {
        if(Desktop.isDesktopSupported())
        {
            Desktop.getDesktop().browse(new URI(url));
        } else
        {
            // MacOS
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("/usr/bin/open '" + url + "'");
        }
    }

}
