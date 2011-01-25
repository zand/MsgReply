package com.bukkit.zand.reply;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;


/**
 * Handle events for all Player related events
 * @author zand
 */
public class ReplyPlayerListener extends PlayerListener {
	public final Reply plugin;
	
	public ReplyPlayerListener(Reply plugin) {
		this.plugin = plugin;
	}
    
    public void onPlayerCommand(PlayerChatEvent event) {
    	if (event.isCancelled()) return;
    	Player player = event.getPlayer();
    	String command = event.getMessage();
    	String args[] = command.split(" ", 3);
    	
    	if (args[0].equalsIgnoreCase("/msg") ||
    		args[0].equalsIgnoreCase("/tell") ||
    		args[0].equalsIgnoreCase("/m")) {
    		if (args.length > 2) {
    			List<Player> players = plugin.getServer().matchPlayer(args[1]);
    			if (players.size() == 1) {
    				update(player, players.get(0));
    			}
    		}
    		
    	}
    	
    	else if (args[0].equalsIgnoreCase("/reply")) { // Received 
    		if (plugin.checkPermission(player, "reply.received")) {
	    		if (args.length > 1) {
	    			String pre = replyTo(player, false);
	    			if (!pre.isEmpty())
	    				event.setMessage(pre + command.substring(7));
	    		}
	    		else {
	    			player.sendMessage(ChatColor.RED + "Correct usage is: /reply (msg)");
	    			event.setCancelled(true);
	    		}
	    	}
			else {
				player.sendMessage(ChatColor.RED + "You'r not allowed to use that command");
				event.setCancelled(true);
			}
    	}
    	
    	else if (command.length() >= 2)
    	if (command.startsWith("//")) { // Sent
    		if (plugin.checkPermission(player, "reply.sent")) {
	    		if (command.length() > 2) {
	    			String pre = replyTo(player, true);
	    			if (!pre.isEmpty())
	    				event.setMessage(pre + command.substring(2));
	    		}
	    		else {
	    			player.sendMessage(ChatColor.RED + "Correct usage is: //(msg)");
	    			event.setCancelled(true);
	    		}
    		}
    		else {
    			player.sendMessage(ChatColor.RED + "You'r not allowed to use that command");
    			event.setCancelled(true);
    		}
    	}
    }
    
    public void update(Player from, Player to) {
    	plugin.lastSent.put(from, to);
    	plugin.lastReceived.put(to, from);
    }
    
    public String replyTo(Player player, boolean sent) {
    	if ((sent ? plugin.lastSent 
    			: plugin.lastReceived)
    			.containsKey(player)) {
    		
    		Player to = (sent ? plugin.lastSent 
        			: plugin.lastReceived)
        			.get(player);
    		
    		if (to.isOnline())
    			return "/msg " + to.getName() + " ";
    		
    		player.sendMessage(ChatColor.RED + to.getName() + " is Offline");
    		
    	} else player.sendMessage(ChatColor.RED + "You have not "
    			+ (sent ? "sent" : "receved")
    			+" a message yet.");
    	
    	return "";
    }
}
