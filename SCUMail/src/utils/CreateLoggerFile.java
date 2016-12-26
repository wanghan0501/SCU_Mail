package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CreateLoggerFile implements Runnable {  
    private FileOutputStream out;  
    private ConcurrentLinkedQueue<String> queue;  
  
    public CreateLoggerFile() {  
    }  
  
    public CreateLoggerFile(FileOutputStream out, ConcurrentLinkedQueue<String> queue) {  
        this.out = out;  
        this.queue = queue;  
    }  
  
    @Override  
    public void run() {  
        synchronized (queue) {  
            while (true) {  
                if (!queue.isEmpty()) {  
                    try {  
                        out.write(queue.poll().getBytes("UTF-8"));  
                    } catch (IOException e) {  
                        // TODO Auto-generated catch block  
                        e.printStackTrace();  
                    }  
                }  
                try {  
                    Thread.sleep(100);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
  
    }  
  
}  