
package org.crocodile.bikedash;

import java.util.ArrayList;
import java.util.List;

import org.crocodile.bikedash.TickReader.TickListener;

public class Estimator implements TickListener
{
    static final int  TICKS_PER_ROTATION = 4;
    static final int  WINDOW_SIZE        = 5;
    static final long MAX_DELAY          = 1000l;
    static List<Long> buf                = new ArrayList<Long>(5);

    public enum State
    {
        STOPPED, RUNNING
    };

    private State state                     = State.STOPPED;
    private long  last_start_time           = -1;
    private long  previously_exhausted_time = 0l;

    @Override
    public void tick(Object context, long timeStamp)
    {
        synchronized(buf)
        {
            buf.add(timeStamp);
            if(buf.size() > WINDOW_SIZE)
                buf.remove(0);
        }
    }

    public void start()
    {
        if(state != State.STOPPED)
            throw new IllegalStateException();
        state = State.RUNNING;
        last_start_time = System.currentTimeMillis();
    }

    public void stop()
    {
        if(state != State.RUNNING)
            throw new IllegalStateException();
        state = State.STOPPED;
        previously_exhausted_time += System.currentTimeMillis() - last_start_time;
        last_start_time = -1;
    }

    /**
     * Reset does not change state, but resets all internal data counters
     */
    public void reset()
    {
        if(last_start_time != -1)
            last_start_time = System.currentTimeMillis();
        previously_exhausted_time = 0l;
        synchronized(buf)
        {
            buf.clear();
        }
    }

    public State getState()
    {
        return state;
    }

    /**
     * 
     * @return current RPM
     */
    public float getRPM()
    {
        if(state != State.RUNNING)
            return 0;

        int n;
        long last, first;
        synchronized(buf)
        {
            n = buf.size();
            if(n < 2)
                return 0;
            last = buf.get(n - 1);
            if((System.currentTimeMillis() - last) > MAX_DELAY)
                return 0;
            first = buf.get(0);
        }
        if(last == first)
            return 0;
        float p = (last - first) / (float) (n - 1);
        return (float) (1.0 / (TICKS_PER_ROTATION*p / 60000.0));
    }

    /**
     * @return active time in milliseconds
     */
    public long getTime()
    {
        if(last_start_time == -1)
            return previously_exhausted_time;
        else
            return previously_exhausted_time + (System.currentTimeMillis() - last_start_time);
    }

    /**
     * 
     * @return number of calories spent
     */
    public float getCalories()
    {
        return 416;
    }

}
