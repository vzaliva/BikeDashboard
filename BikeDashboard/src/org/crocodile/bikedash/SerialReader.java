
package org.crocodile.bikedash;

import jssc.*;
import java.util.regex.Pattern;

public class SerialReader extends TickReader implements SerialPortEventListener
{
    StringBuffer       data         = new StringBuffer();
    static Pattern     PORT_PATTERN = Pattern.compile("tty\\.usb.*");

    // TODO: move to preferences
    static String      port         = null;
    private SerialPort serialPort;

    public SerialReader(String sport) throws Exception
    {
        port = sport;
    }

    public static String[] getPorts()
    {
        return SerialPortList.getPortNames(PORT_PATTERN);
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
