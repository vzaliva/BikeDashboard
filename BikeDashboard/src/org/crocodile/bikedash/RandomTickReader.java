
package org.crocodile.bikedash;

public class RandomTickReader extends TickReader implements Runnable
{
    private static final long PERIOD_MS = 300;
    private Thread            thread;

    public RandomTickReader()
    {
        thread = new Thread(this);
    }

    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                Thread.sleep(PERIOD_MS);
            } catch(InterruptedException e)
            {
                break;
            }
            broadcastTick(System.currentTimeMillis());
        }
    }

    @Override
    public void start()
    {
        thread.start();
    }

    @Override
    public void stop()
    {
        thread.interrupt();
        try
        {
            thread.join();
        } catch(InterruptedException e)
        {
            // ignore
        }
    }

}
