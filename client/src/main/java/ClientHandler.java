
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * Created by cahya on 16/02/17.
 */
public class ClientHandler {
    private Selector selector;
    private SocketChannel clientChannel;
    public void startServer()throws IOException,InterruptedException{
        selector=Selector.open();
        clientChannel=SocketChannel.open(new InetSocketAddress("localhost",8888));
        clientChannel.configureBlocking(false);
        ReadableByteChannel channel=Channels.newChannel(System.in);
        System.out.println("Client started...");

        String threadName=Thread.currentThread().getName();
        clientChannel.register(selector, SelectionKey.OP_READ);

        Runnable sendMessageThread=new Runnable() {
            public void run() {
                try {
                    sendMessage();
                }catch (IOException e){
                    e.printStackTrace();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };

        new Thread(sendMessageThread).start();

        while(true) {
            selector.select();

            Iterator keys=selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key=(SelectionKey)keys.next();

                keys.remove();

                if(!key.isValid()) continue;
                else if(key.isReadable()){
                    try {
                        readMessage(key);
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private void sendMessage() throws IOException, InterruptedException{
        while (true) {
            System.out.println("Send message");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String input = in.readLine();

            //create json

            System.out.print(input);
            if (input.length() > 0) {
                byte[] message = new String(input).getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(message);
                clientChannel.write(buffer);
                System.out.println(input);
                buffer.clear();
            } else {
                throw new InterruptedException("Anda menghentikan program");
            }
        }
    }

    private void readMessage(SelectionKey key) throws IOException{
        System.out.println("Read message");
        SocketChannel channel=(SocketChannel)key.channel();
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        int numRead=channel.read(buffer);

        if(numRead<0){
            SocketAddress socketAddress=channel.socket().getRemoteSocketAddress();
            System.out.println("Connection closed by server: "+socketAddress);
            channel.close();
            key.cancel();
            return;
        }

        byte[] data=new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);
        System.out.println(new String(data));
    }
}
