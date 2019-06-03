package testapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
    private ServerSocket server = null;
    private Socket socket = null;
    BufferedReader rdr = null;
    PrintWriter wtr = null;
    public Server()
    {
        try{
            server = new ServerSocket(8888);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        while (true)
        {
            try{
                System.out.println("listening");
                socket = server.accept();
                ServerThread th = new ServerThread(socket);
                th.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        new Server().start();
    }


    class ServerThread extends Thread
    {
        private Socket sk = null;
        public ServerThread(Socket sk)
        {
            this.sk = sk;
        }
        public void run()
        {
            try{
                wtr = new PrintWriter(sk.getOutputStream());
                rdr = new BufferedReader(new InputStreamReader(sk.getInputStream()));
                String line = rdr.readLine();
                System.out.println("从客户端传来的消息: "+line);
                String result = testapi.singleTest(1,line.trim());
                wtr.println(result);
                wtr.flush();


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

}

