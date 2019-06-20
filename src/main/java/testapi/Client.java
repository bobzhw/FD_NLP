package testapi;

import edu.stanford.nlp.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread{
    private Socket sk = null;
    private BufferedReader reader = null;
    private PrintWriter wtr = null;
    private BufferedReader keyin = null;
    public Client()
    {
        keyin = new BufferedReader(new InputStreamReader(System.in));
        try {
            sk = new Socket("192.168.1.123",59997);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        try {
            reader = new BufferedReader(new InputStreamReader(sk.getInputStream()));
            wtr = new PrintWriter(sk.getOutputStream());
            String get = keyin.readLine();

            if (null != get && get.length() > 0) {
                wtr.println(get);
                wtr.flush();
            }
            int x = 0;
            while (reader != null) {

                String line = reader.readLine();

                if(StringUtils.isNumeric(line))
                    x = Integer.valueOf(line);
                else
                    x--;
                System.out.println(line);
                if(x==0)
                    break;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Client().start();
    }
}