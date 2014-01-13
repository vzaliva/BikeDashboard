
package org.crocodile.fitbit;

import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.scribe.model.Token;

public class ActivityLogger
{
    private static final int   METERS_IN_MILE     = 1609;

    private Preferences prefs;
    private Logger      log;
    private Token       token;

    public ActivityLogger(Preferences prefs, Logger log, Token token)
    {
        this.prefs = prefs;
        this.log = log;
        this.token = token;
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

}
