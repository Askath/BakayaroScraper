import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * Project: WebScraper Package: File: Application.java Created: 07.09.2018
 * Author: Ji-Soo (Sophos Technology GmbH) Copyright: (C) 2018 Sophos Technology
 * GmbH
 */

public class Application
{

    public static void main(String[] args) throws IOException
    {

        System.setProperty("java.net.useSystemProxies", "true");
        
        String URL = "";
        String ROOT_DOCUMENT = "root.html";

        String albumPrefix = "/albums/";
        URL = "https://bakayaro.de/";

        final String authUser = "askath";
        final String authPassword = "sp33d;1s;k3y";
        Authenticator.setDefault(new Authenticator()
        {
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(authUser, authPassword.toCharArray());
            }
        });

        File document = new File(ROOT_DOCUMENT);
        if (!document.exists())
        {
            try
            {
                document.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            Document workingDocument = Jsoup.connect(URL)
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.101.128.11", 3128)))
                    .userAgent("Mozillla/5.0").timeout(10 * 1000).get();
            
            
            
            Elements albums = workingDocument.select("a[href^= " + albumPrefix + "]");
            
            for(Element a : albums)
            {
                Document albumDocument = Jsoup.connect(URL + a.attr("href")).proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.101.128.11", 3128)))
                        .userAgent("Mozillla/5.0").timeout(10 * 1000).get();

                new File("/images/").mkdirs();
            
                    Elements jpgs = albumDocument.select("img[src$=.jpg]");
                   
                    for(Element pic : jpgs)
                    {
                        String imageURL = pic.attr("src");
                        InputStream in;
                        System.out.println(pic.toString());
                        try
                        {
                            in = new URL(imageURL.replace("large", "xlarge") ).openStream();

                            byte[] buffer = new byte[4096];
                            int n = -1;

                            OutputStream os = new FileOutputStream("/images/" + pic.attr("alt") + ".jpg");

                            // write bytes to the output stream
                            while ((n = in.read(buffer)) != -1)
                            {
                                os.write(buffer, 0, n);
                            }

                            // close the stream
                            os.close();
                            in.close();
                            System.out.println("saved: " +  pic.attr("src").replace("large", "xlarge"));
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        finally
                        {

                        }
                    }
            
            }
            
        }
        finally
        {

        }
    }
}
