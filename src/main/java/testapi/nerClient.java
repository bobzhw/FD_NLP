package testapi;

import java.io.*;
import java.net.Socket;

public class nerClient {
    private Socket client;
    private OutputStream ots;
    private PrintWriter pw;
    private InputStream is;
    private InputStreamReader isr;
    private BufferedReader br;
    public nerClient(String ip,int port)
    {
        try{
            client = new Socket(ip,port);
            ots = client.getOutputStream();
            pw = new PrintWriter(ots);
            is = client.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void sendMsg(String msg)
    {
        try{
            pw.write(msg);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public String receive()
    {
        String result = "";
        try{
            String info;
            while((info = br.readLine())!=null)
            {
                result+=info;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
}
