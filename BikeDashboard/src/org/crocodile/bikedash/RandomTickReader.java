
package org.crocodile.bikedash;

import java.util.Timer;
import java.util.TimerTask;

public class RandomTickReader extends TickReader
{
    private static final long PERIOD_MS = 180;

    private Timer             timer     = new Timer();

    public RandomTickReader()
    {
    }

    @Override
    public void start() throws Exception
    {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                broadcastTick(System.currentTimeMillis());
            }
        }, 0, PERIOD_MS);
    }

    @Override
    public void stop() throws Exception
    {
        timer.cancel();
    }

}
