
package org.crocodile.fitbit;

import java.awt.Desktop;
import java.math.BigDecimal;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

public class FitBitHelper
{
    private static final String           ACTIVITY_NAME     = "Stationary Biking";
    private static final String           PREF_FITBITSECRET = "fitbitsecret";
    private static final String           PREF_FITBITTOKEN  = "fitbittoken";

    private static final String           fITBIT_API_KEY    = "800918c46b204134aca64fa1e1bb8a38";
    private static final String           FITBIT_API_SECRET = "1826e44b68fd4ff6b855a3e3722a7af7";

    private static final String           ACTIVITY_ADD_URL  = "http://api.fitbit.com/1/user/-/activities.json";

    private static final SimpleDateFormat TIME_FORMAT       = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat DATE_FORMAT       = new SimpleDateFormat("yyyy-MM-dd");

    private Preferences                   prefs;
    private Logger                        log;
    private Token                         token;

    public FitBitHelper(Preferences prefs, Logger log)
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

    public void login(JFrame frame)
    {
        OAuthService service = new ServiceBuilder().provider(FitbitApi.class).apiKey(fITBIT_API_KEY).apiSecret(FITBIT_API_SECRET)
                .build();

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

    public void logActivity(long start_time, long duration, float distance, float calories) throws Exception
    {
        log.info("Recroding: duration=" + duration / 1000l + "s, avg. distance=" + distance + "MPH, calories="
                + calories);

        if(token == null)
            throw new Exception("Must log in first to log activity");

        OAuthService service = new ServiceBuilder().provider(FitbitApi.class).apiKey(fITBIT_API_KEY).apiSecret(FITBIT_API_SECRET)
                .build();

        Date date = new Date(start_time);
        double roundedDist = BigDecimal.valueOf(distance / 1000).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

        OAuthRequest request = new OAuthRequest(Verb.POST, ACTIVITY_ADD_URL);

        request.addQuerystringParameter("activityName", ACTIVITY_NAME);
        request.addQuerystringParameter("manualCalories", "" + Math.round(calories));
        request.addQuerystringParameter("startTime", TIME_FORMAT.format(date));
        request.addQuerystringParameter("durationMillis", "" + duration);
        request.addQuerystringParameter("distance", "" + roundedDist);
        request.addQuerystringParameter("date", DATE_FORMAT.format(date));

        service.signRequest(token, request);
        Response response = request.send();
        if(!response.isSuccessful())
        {
            log.log(Level.SEVERE, "FitBit API Error: " + response.getMessage());
            log.fine(response.getBody());
            throw new Exception("FitBit API Error: " + response.getMessage());
        }
    }

}
