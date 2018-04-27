package test;
/*
 * Decompiled with CFR 0_123.
 */
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import javax.swing.JFrame;

public class ChatServer
extends JFrame {
    private static int a;
    private Vector b = new Vector();
    private int c = 5555;

    private ChatServer(int n) {
        this.setSize(500, 450);
        this.setTitle("Dummy Chat Server Window");
        this.setDefaultCloseOperation(2);
    }

    private synchronized void a(String string) {
        System.out.println(string);
        int n = this.b.size();
        while (--n >= 0) {
            Object object = (a)this.b.get(n);
            if (a.a((a)object, string)) continue;
            this.b.remove(n);
            object = "Disconnected Client  removed from list.";
            System.out.println((String)object);
        }
    }

    public static void main(String[] object) {
        object = new ChatServer(5555);
        try {
            ServerSocket serverSocket = new ServerSocket(object.c);
            do {
                Object object2 = serverSocket.accept();
                object2 = new a((ChatServer)object, (Socket)object2);
                object.b.add(object2);
                object2.start();
            } while (true);
        }
        catch (IOException iOException) {
            String string = " Exception on new ServerSocket: " + iOException + "\n";
            object = string;
            System.out.println((String)object);
            return;
        }
    }

    static /* synthetic */ int a() {
        return a;
    }

    static /* synthetic */ void a(int n) {
        a = n;
    }

    static /* synthetic */ void a(ChatServer object, String string) {
        object = string;
        System.out.println((String)object);
    }

    static /* synthetic */ void b(ChatServer chatServer, String string) {
        chatServer.a(string);
    }
}