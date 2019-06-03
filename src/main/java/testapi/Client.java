package testapi;

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
            sk = new Socket("121.48.165.44",54321);
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
            if (reader != null) {
                String line = reader.readLine();
                System.out.println(line);
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