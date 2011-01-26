package com.bukkit.zand.reply;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.nijikokun.bukkit.General.General;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * BlockHead for Bukkit
 *
 * @author zand
 */
public class Reply extends JavaPlugin {
	public final String name; 
	public final String versionInfo; 
	private static Logger log = Logger.getLogger("Minecraft");
	
    private final ReplyPlayerListener playerListener = new ReplyPlayerListener(this);
    protected final HashMap<Player, Player> lastSent = new HashMap<Player, Player>();
    protected final HashMap<Player, Player> lastReceived = new HashMap<Player, Player>();
    
    public Permissions Permissions = null;
    public boolean worldEditFound = false;

    public Reply(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);

        name = desc.getName();
        String authors = "";
        for (String author : desc.getAuthors()) authors += ", " + author;
        versionInfo = name + " version " + desc.getVersion() + 
        	(authors.isEmpty() ? "" : " by" + authors.substring(1));
        
        //setupCommands();
        
        // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
    }
    
    public void onEnable() {
        // Register our events
		PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(org.bukkit.event.Event.Type.PLAYER_COMMAND, playerListener, org.bukkit.event.Event.Priority.Lowest, this);
        
        setupOtherPlugins();
        
        log.info( versionInfo + " is enabled!" );
    }
    
    public void onDisable() {
    }
    
	private void setupOtherPlugins() {
		
		// General
     	Plugin test = this.getServer().getPluginManager().getPlugin("General");
     	if (test != null) {
     	    General General = (General) test;
     	    // You can use color codes in the description, &[code] just like the simoleons!
     	    General.l.save_command("/reply (msg)" , "Replies to the last receved.");
     	    General.l.save_command("//(msg)" , "Replies to the last sent.");
     	}
     	
     	// Permissions
    	test = this.getServer().getPluginManager().getPlugin("Permissions");
    	if (this.Permissions == null) {
    		if(test != null) {
    			this.Permissions = (Permissions)test;
    	    	log.info("[" + name + "] Found Permissions plugin. Using it.");
    	    }
    	}
    	
    	test = this.getServer().getPluginManager().getPlugin("WorldEdit");
    	if(test != null) {
    			this.worldEditFound = true;
    	    	log.info("[" + name + "] Found WorldEdit plugin. Ignoreing \"//\".");
    	}
    }
	
	@SuppressWarnings("static-access")
	boolean checkPermission(Player player, String nodes) {
    	if (this.Permissions == null)
    		return true;
    	return this.Permissions.Security.permission(player, nodes);
    }
}
