import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import java.util.concurrent.TimeUnit;

public class LEDClient {
    private ZContext zctx;
    private ZMQ.Socket zsocket;
    private Gson gson;
    private String connStr;
    private final String topic = "GPIO";

    private static final int[] OFF = {0, 0, 0};

    public LEDClient(String protocol, String host, int port) {
        zctx = new ZContext();
        zsocket = zctx.createSocket(SocketType.PUB);
        this.connStr = String.format("%s://%s:%d", protocol, "*", port);
        zsocket.bind(connStr);
        this.gson = new Gson();
    }

    public void send(int[] color) throws InterruptedException {
        JsonArray ja = gson.toJsonTree(color).getAsJsonArray();
        String message = topic + " " + ja.toString();
        System.out.println(message);
        zsocket.send(message);
    }
    public void SquareGrade(int spd) throws InterruptedException {
        send(LEDClient.OFF);
        int red=0; int green=0; int blue=0;
        for (int i=0;i<255;i+=spd) {
            red = i; green = 0; blue = 0;
            int[] color1 = {red,green,blue};
            send(color1);
            TimeUnit.MILLISECONDS.sleep(100);
        }
        for (int i=255;i<0;i+=spd) {
            red=0-i; green=i; blue=85;
            int[] color2 = {red,green,blue};
            send(color2);
            TimeUnit.MILLISECONDS.sleep(100);
        }
        for (int i=0;i<255;i+=spd) {
            red=0; green=255-i; blue=i;
            int[] color3 = {red,green,blue};
            send(color3);
            TimeUnit.MILLISECONDS.sleep(100);
        }
        for (int i=255;i<157;i+=spd) {
            red=i; green=0; blue=255-i;
            int[] color4 = {red,green,blue};
            send(color4);
            TimeUnit.MILLISECONDS.sleep(100);
        }
        for (int i=0;i<162;i+=spd) {
            red=255-i; green=0; blue=58+i;
            int[] color5 = {red,green,blue};
            send(color5);
            TimeUnit.MILLISECONDS.sleep(100);
        }
        send(LEDClient.OFF);
    }

    public void close() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2); // Allow the socket a chance to flush.
        this.zsocket.close();
        this.zctx.close();
    }

    public static void main(String[] args) {
        LEDClient ledClient = new LEDClient("tcp", "192.168.1.117", 5001);
        try {
            int[] color = {0, 0, 255};
            ledClient.SquareGrade(10);
            ledClient.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}