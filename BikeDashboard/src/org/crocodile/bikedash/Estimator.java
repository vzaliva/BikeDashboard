
package org.crocodile.bikedash;

import org.crocodile.bikedash.TickReader.TickListener;

public class Estimator implements TickListener
{
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
        // TODO Auto-generated method stub
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
        last_start_time = -1;
        previously_exhausted_time += System.currentTimeMillis() - last_start_time;
    }

    /**
     * Reset does not change state, but resets all internal data counters
     */
    public void reset()
    {
        last_start_time = System.currentTimeMillis();
        previously_exhausted_time = 0l;
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
        return 13;
    }

    /**
     * @return active time in milliseconds
     */
    public long getTime()
    {
        return 13 * 60 * 60 + 10;
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
