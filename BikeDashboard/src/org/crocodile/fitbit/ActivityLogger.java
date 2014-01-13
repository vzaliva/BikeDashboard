
package org.crocodile.fitbit;

import java.awt.Desktop;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class ActivityLogger
{
    private static final int    METERS_IN_MILE    = 1609;

    private static final String PREF_FITBITSECRET = "fitbitsecret";
    private static final String PREF_FITBITTOKEN  = "fitbittoken";

    private static final String fITBIT_API_KEY    = "800918c46b204134aca64fa1e1bb8a38";
    private static final String FITBIT_API_SECRET = "1826e44b68fd4ff6b855a3e3722a7af7";

    private Preferences         prefs;
    private Logger              log;
    private Token               token;

    public ActivityLogger(Preferences prefs, Logger log)
    {
        this.prefs = prefs;
        this.log = log;
        loadToken();
    }

    public boolean isLoggedIn()
    {
        return token != null;
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

    public void saveToken() throws Exception
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

    public void send(long currentTimeMillis, long duration, float averagespeed, float calories)
    {
        float mph = averagespeed * METERS_IN_MILE / 3600;

        log.info("Recroding: duration=" + duration / 1000l + "s, avg. speed=" + mph + "MPH, calories=" + calories);
        // TODO: actually submit

        // {
        // "activityId": 1020,
        // "calories": 10,
        // "description": "Leisurely - 10 to 11.9mph",
        // "distance": 0,
        // "duration": 600000,
        // "name": "Bicycling"
        // }
    }

    public void login(JFrame frame)
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
        } catch(Exception e)
        {
            token = null;
            JOptionPane.showMessageDialog(frame, "Error saving token" + e.getMessage(), "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            log.log(Level.WARNING, "Error saving token", e);
        }
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

    public void logout() throws Exception
    {
        saveToken();
        token = null;
    }

}
