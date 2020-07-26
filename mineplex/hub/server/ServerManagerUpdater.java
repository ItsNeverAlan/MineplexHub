package mineplex.hub.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.logger.Logger;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class ServerManagerUpdater implements Listener
{
  private ServerManager _plugin;
  private NautHashMap<String, String> _jarMd5Map = new NautHashMap();
  
  private File _updateDirectory;
  private boolean _needUpdate = false;
  
  public ServerManagerUpdater(ServerManager plugin)
  {
    this._plugin = plugin;
    
    getCurrentMd5s();
    
    boolean windows = System.getProperty("os.name").startsWith("Windows");
    
    this._updateDirectory = new File((windows ? "C:" : new StringBuilder(String.valueOf(File.separator)).append("home").append(File.separator).append("mineplex").toString()) + File.separator + "update" + File.separator + "lobby");
    
    this._updateDirectory.mkdirs();
    
    plugin.GetPluginManager().registerEvents(this, plugin.GetPlugin());
  }
  
  private void getCurrentMd5s()
  {
    File serverManagerDat = new File("ServerManager.dat");
    
    FileInputStream fis = null;
    
    try
    {
      fis = new FileInputStream(serverManagerDat);
      this._jarMd5Map.put(serverManagerDat.getName(), DigestUtils.md5Hex(fis));
    }
    catch (Exception ex)
    {
      Logger.Instance.log(ex);
      System.out.println("ServerManagerUpdater: Error parsing ServerManager dat md5's");
      ex.printStackTrace();
      


      if (fis != null)
      {
        try
        {
          fis.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
    finally
    {
      if (fis != null)
      {
        try
        {
          fis.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
  }
  
  @EventHandler
  public void checkForNewFiles(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    FilenameFilter statsFilter = new FilenameFilter()
    {
      public boolean accept(File paramFile, String paramString)
      {
        if (paramString.endsWith("dat"))
        {
          return true;
        }
        
        return false;
      }
    };
    
    for (File f : this._updateDirectory.listFiles(statsFilter))
    {
      FileInputStream fis = null;
      
      try
      {
        if (this._jarMd5Map.containsKey(f.getName()))
        {
          fis = new FileInputStream(f);
          String md5 = DigestUtils.md5Hex(fis);
          
          if (!md5.equals(this._jarMd5Map.get(f.getName())))
          {
            this._needUpdate = true;
          }
        }
      }
      catch (Exception ex)
      {
        Logger.Instance.log(ex);
        System.out.println("ServerManagerUpdater: Error parsing dat md5's");
        ex.printStackTrace();
        


        if (fis != null)
        {
          try
          {
            fis.close();
          }
          catch (IOException e)
          {
            e.printStackTrace();
          }
        }
      }
      finally
      {
        if (fis != null)
        {
          try
          {
            fis.close();
          }
          catch (IOException e)
          {
            e.printStackTrace();
          }
        }
      }
    }
    
    if (this._needUpdate)
    {
      updateFiles();
    }
  }
  
  private void updateFiles()
  {
    this._needUpdate = false;
    
    boolean windows = System.getProperty("os.name").startsWith("Windows");
    
    File updateDir = new File((windows ? "C:" : new StringBuilder(String.valueOf(File.separator)).append("home").append(File.separator).append("mineplex").toString()) + File.separator + "update" + File.separator + "lobby");
    File currentDir = new File(".");
    
    updateDir.mkdirs();
    
    FilenameFilter statsFilter = new FilenameFilter()
    {
      public boolean accept(File paramFile, String paramString)
      {
        if (paramString.endsWith("dat"))
        {
          return true;
        }
        
        return false;
      }
    };
    
    for (File f : updateDir.listFiles(statsFilter))
    {
      try
      {
        FileUtils.copyFileToDirectory(f, currentDir);
      }
      catch (Exception ex)
      {
        Logger.Instance.log(ex);
        System.out.println("ServerManagerUpdater: Error updating dats");
        ex.printStackTrace();
      }
    }
    
    getCurrentMd5s();
    this._plugin.LoadServers();
  }
}
