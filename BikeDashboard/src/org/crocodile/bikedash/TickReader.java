package org.crocodile.bikedash;

import java.util.LinkedList;
import java.util.List;

public abstract class TickReader
{
    public interface TickListener
    {
        public void tick(Object context, long timeStamp);
    }
    
    private List<TickListener> listeners = new LinkedList<TickListener>();
    private Object context;
    
    protected TickReader(Object context)
    {
        this.context=context;
    }

    protected TickReader()
    {
        this(null);
    }

    public abstract void start() throws Exception;
    public abstract void stop();
    
    public void addListener(TickListener x)
    {
        listeners.add(x);
    }
    
    public void removeListener(TickListener x)
    {
        listeners.remove(x);
    }
    
    public void clear()
    {
        listeners.clear();
    }
    
    protected void broadcastTick(long timestamp)
    {
        for(TickListener l:listeners)
            l.tick(context, timestamp);
    }
}
