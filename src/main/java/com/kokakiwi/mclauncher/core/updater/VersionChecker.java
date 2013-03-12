package com.kokakiwi.mclauncher.core.updater;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

import com.kokakiwi.mclauncher.api.LauncherAPI;
import com.kokakiwi.mclauncher.utils.Version;

public class VersionChecker
{	
    public static void checkVersion(LauncherAPI api) throws Exception
    {
    	
        boolean update = false;
        if(isInternetReachable())
        {
        	
            if (!update)
            {
                final File file = new File(api.getMinecraftDirectory(),"version");
                final String lastVersion = getLastVersion(api,"https://dl.dropbox.com/u/66055117/retrogame/version.html");
                
                if (lastVersion != null)
                {
                    if (!file.exists())
                    {
                        update = true;
                        api.getUpdater().setAskUpdate(false);
                        api.getUpdater().setDoUpdate(true);
                    }
                    else
                    {
                        final String current = readVersionFile(api, file);
                        final Version v1 = Version.parseString(current);
                        final Version v2 = Version.parseString(lastVersion);
                        
                        if (v2.compareTo(v1) > 0)
                        {
                            update = true;
                            boolean force = true;
                            if (!force)
                            {
                                force = false;
                            }
                            
                            if (!force)
                            {
                                api.getUpdater().setAskUpdate(true);
                            }
                            api.getUpdater().setDoUpdate(true);
                        }
                    }
                }
                
                if (update)
                {
                    updateVersionFile(file, lastVersion);
                }
            }
        }
   }
    
  //checks for connection to the internet through dummy request
    public static boolean isInternetReachable()
    {
        try {
            //make a URL to a known source
            URL url = new URL("http://www.google.com");

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            urlConnect.setConnectTimeout(1000);
            urlConnect.setReadTimeout(1000);
            Object objData = urlConnect.getContent();

        } 
        
        catch (SocketTimeoutException e) {
        	   e.printStackTrace();
               return false;
        	   
        }catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
    public static String readVersionFile(LauncherAPI api, File file)
            throws Exception
    {
        if (!file.exists())
        {
            file.createNewFile();
            updateVersionFile(file, "-1");
        }
        
        String version = null;
        
        final DataInputStream in = new DataInputStream(
                new FileInputStream(file));
        version = in.readUTF();
        in.close();
        
        return version;
    }
    
    public static void updateVersionFile(LauncherAPI api, File file,
            Map<String, Object> version) throws Exception
    {
        updateVersionFile(file,
                getLastVersion(api, (String) version.get("source")));
    }
    
    public static void updateVersionFile(File file, String lastVersion)
            throws Exception
    {
        if (!file.exists())
        {
            file.createNewFile();
        }
        
        final DataOutputStream out = new DataOutputStream(new FileOutputStream(
                file));
        out.writeUTF(lastVersion);
        out.close();
    }
    
    public static String getLastVersion(LauncherAPI api, String source)
    {
        String version = null;
        
        if (source == null)
        {
            if (api.getLoginer().getLastLogin() != null)
            {
                version = String.valueOf(api.getLoginer().getLastLogin()
                        .getTimestamp());
            }
        }
        else
        {
            version = api.getUrl(source);
        }
        
        return version;
    }
}
