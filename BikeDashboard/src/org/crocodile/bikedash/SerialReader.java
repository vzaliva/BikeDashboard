
package org.crocodile.bikedash;

import java.util.Enumeration;
import java.util.HashSet;

import gnu.io.*;

public class SerialReader extends TickReader
{
    // TODO: move to preferences
    static final String PORT = "/dev/tty.usbmodem1411";

    public SerialReader()
    {
         HashSet<CommPortIdentifier> ports = getAvailableSerialPorts();
         for(CommPortIdentifier p:ports)
         {
             System.err.println(p);
         }
    }
    
    @Override
    public void start()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void stop()
    {
        // TODO Auto-generated method stub

    }

    /**
     * @return A HashSet containing the CommPortIdentifier for all serial ports
     *         that are not currently being used.
     */
    public static HashSet<CommPortIdentifier> getAvailableSerialPorts()
    {
        HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while(thePorts.hasMoreElements())
        {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            switch(com.getPortType())
            {
            case CommPortIdentifier.PORT_SERIAL:
                try
                {
                    CommPort thePort = com.open("CommUtil", 50);
                    thePort.close();
                    h.add(com);
                } catch(PortInUseException e)
                {
                    System.out.println("Port, " + com.getName() + ", is in use.");
                } catch(Exception e)
                {
                    System.err.println("Failed to open port " + com.getName());
                    e.printStackTrace();
                }
            }
        }
        return h;
    }

}
