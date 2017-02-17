import java.io.IOException;

/**
 * Created by cahya on 16/02/17.
 */
public class MainActivity {
    public static void main(String[] args){
        Runnable runnable=new Runnable() {
            public void run() {
                try {
                    new ClientHandler().startServer();
                }catch (IOException e){
                    e.printStackTrace();
                }catch (InterruptedException e){
                    return;
                }
            }
        };

        new Thread(runnable,"client-1").start();
    }
}
