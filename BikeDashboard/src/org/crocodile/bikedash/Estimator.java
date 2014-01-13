
package org.crocodile.bikedash;

import java.util.ArrayList;
import java.util.List;

import org.crocodile.bikedash.TickReader.TickListener;

public class Estimator implements TickListener
{
    private static final int   TICKS_PER_ROTATION = 4;
    private static final int   WINDOW_SIZE        = 5;
    private static final long  MAX_DELAY          = 3000l;
    private static final float WHEEL_DIAMETER     = 0.597f;                                     // http://en.wikipedia.org/wiki/Bicycle_wheel#26_inch
    private List<Long>         buf                = new ArrayList<Long>(3 * TICKS_PER_ROTATION);
    private float              last_rpm           = 0f;
    private long               last_rpm_time      = -1l;
    private float              calories           = 0f;
    private long               ticks;

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

        // Energy expenditure estimation
        float rpm = getRPM();
        if(last_rpm != 0 && last_rpm_time != -1)
        {
            long dt = timeStamp - last_rpm_time;
            float avgRPM = (last_rpm + rpm) / 2;
            float cal_per_hour = 18.7f * avgRPM - 1132f;
            if(cal_per_hour > 0 && dt > 0)
                calories += (cal_per_hour / (60l * 60l * 1000l)) * dt;
        }

        last_rpm = rpm;
        last_rpm_time = timeStamp;
        if(state == State.RUNNING)
            ticks++;
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
        last_rpm = 0f;
        last_rpm_time = -1l;
        calories = 0l;
        ticks = 0l;
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
        return (float) (1.0 / (TICKS_PER_ROTATION * p / 60000.0));
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
        return calories;
    }

    /**
     * 
     * @return Distance "cycled" during last active period in meters;
     */
    public float getDistance()
    {
        return (float) ((ticks / TICKS_PER_ROTATION) * Math.PI * WHEEL_DIAMETER);
    }

    /**
     * @return average "moving" speed during last time period in M/s
     */
    public float getAverageSpeed()
    {
        long t = getTime();
        if(t == 0)
            return 0f;
        return (float) (getDistance() / (t / 1000l));
    }
}
