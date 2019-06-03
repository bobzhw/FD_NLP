import com.sun.security.ntlm.Server;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
    public static void main(String[] args) throws Exception
    {

        System.out.println("-------server-----------");
        ServerSocket server = new ServerSocket(9877);
        Socket socket = server.accept();
        while (true)
        {
            DataInput dis = new DataInputStream(socket.getInputStream());
            while(true)
            {
                String datas = dis.readUTF();
                if(datas.equals(""))
                    break;
                System.out.println(datas);
            }

        }
    }
}
