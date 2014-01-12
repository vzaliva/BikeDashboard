
package org.crocodile.bikedash;

import jssc.*;

public class SerialReader extends TickReader implements SerialPortEventListener
{
    StringBuffer        data = new StringBuffer();

    // TODO: move to preferences
    static String port;
    private SerialPort  serialPort;

    public SerialReader()
    {
        String[] portNames = SerialPortList.getPortNames();
        for(int i = 0; i < portNames.length; i++)
        {
            port = portNames[i];
            System.out.println(portNames[i]);
        }
    }

    @Override
    public void start() throws Exception
    {
        serialPort = new SerialPort(port);

        serialPort.openPort();
        serialPort.setParams(9600, 8, 1, 0);
        int mask = SerialPort.MASK_RXCHAR;
        serialPort.setEventsMask(mask);
        serialPort.addEventListener(this);
    }

    @Override
    public void stop() throws Exception
    {
        serialPort.removeEventListener();
    }

    @Override
    public void serialEvent(SerialPortEvent event)
    {
        if(!event.isRXCHAR())
            return;
        int n = event.getEventValue();
        try
        {
            byte buffer[] = serialPort.readBytes(n);
            for(byte b : buffer)
            {
                if(b == '\n')
                {
                    broadcastTick(System.currentTimeMillis());
                    data = new StringBuffer();
                } else if(b != '\r')
                    data.append(b);
            }
        } catch(SerialPortException ex)
        {
            System.out.println(ex);
        }
    }
}
