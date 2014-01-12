
package org.crocodile.bikedash;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;


public class SerialReader extends TickReader implements SerialPortEventListener
{
    // TODO: move to preferences
    static final String      PORT     = "/dev/tty.usbmodem1411";
    private static final int BUF_SIZE = 1024;
    private SerialPort       serialPort;
    private InputStream      in;
    private byte[]           buffer   = new byte[BUF_SIZE];
    private int              buf_len  = 0;

    public SerialReader()
    {
        HashSet<CommPortIdentifier> ports = getAvailableSerialPorts();
        for(CommPortIdentifier p : ports)
        {
            System.err.println(p.getName());
        }

    }

    @Override
    public void start() throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(PORT);
        if(portIdentifier.isCurrentlyOwned())
        {
            throw new Exception("Port "+PORT+" in use!");
        }
        CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
        serialPort = (SerialPort) commPort;
        serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        in = serialPort.getInputStream();
        serialPort.addEventListener(this);
        serialPort.notifyOnDataAvailable(true);
    }

    @Override
    public void stop()
    {
        serialPort.removeEventListener();
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

    @Override
    public void serialEvent(SerialPortEvent arg0)
    {
        int data;
        try
        {
            while((data = in.read()) > -1)
            {
                if(data == '\n')
                {
                    buf_len = 0;
                    System.out.print("Result=" + new String(buffer, 0, buf_len));
                    break;
                }
                if(buf_len == BUF_SIZE)
                    buf_len = 0;
                buffer[buf_len++] = (byte) data;
            }
        } catch(IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
