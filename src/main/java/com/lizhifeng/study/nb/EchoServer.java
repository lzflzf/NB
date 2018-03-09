package javac;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class EchoServer {
    public static void main(String[] args) throws IOException {
        // establish server socket
        try (ServerSocket s = new ServerSocket(80)) {

            while (true) {
                // wait for client connection
                Socket incoming = s.accept();
                new Thread(new HandleThread(incoming)).start();
            }
        }
    }


    public static byte[] readFileContent(String fileName) {
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filecontent;
    }
}


class HandleThread implements Runnable {

    private Socket incoming;

    public HandleThread(Socket socket) {
        this.incoming = socket;
    }

    @Override
    public void run() {
        try {

            System.out.println(Thread.currentThread());

            InputStream inStream = incoming.getInputStream();
            OutputStream outStream = incoming.getOutputStream();

            PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);

            boolean flag = false;

            String filePath = "";
            String ContentType = "";

            try (Scanner in = new Scanner(inStream)) {
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    // System.out.println(line);
                    if (!flag) {
                        String[] MethonPathHttpversion = line.split(" ");
                        filePath = MethonPathHttpversion[1];
                        if (MethonPathHttpversion[1].equals("/")) {
                            System.out.println("this is index");
                            filePath = "/index.html";            // 默认首页
                        }

                        if (filePath.indexOf('?') > 0) {
                            filePath = filePath.substring(0, filePath.indexOf('?'));
                        }

                        if (filePath.endsWith("html") || filePath.endsWith("htm")) {
                            ContentType = "text/html";
                        } else if (filePath.endsWith("css")) {
                            ContentType = "text/css";
                        } else if (filePath.endsWith("js")) {
                            ContentType = "text/javascript";
                        } else {
                            int pot = filePath.lastIndexOf(".");
                            String imageType = filePath.substring(pot + 1);
                            ContentType = "image/" + imageType;
                        }

                        flag = true;    //  解析第一行
                        continue;
                    }

                    if (line.equals("")) {
                        break;
                    }
                }

                out.flush();
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: " + ContentType);
                out.println();                             //  输出header头

                String rootPath = "D:\\moban2770\\moban2770";

                filePath = rootPath + filePath;

                byte[] fileContent = EchoServer.readFileContent(filePath);
                outStream.write(fileContent);            //  输出正文
                outStream.flush();

                out.flush();
                outStream.close();
                incoming.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
