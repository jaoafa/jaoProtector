package com.jaoafa.jaoProtector.Event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.plugin.java.JavaPlugin;

public class Event_AntiItemFrameBreak implements Listener {
	JavaPlugin plugin;
	public Event_AntiItemFrameBreak(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onHangingBreakEvent(HangingBreakEvent event){
		if(event.getCause() != RemoveCause.EXPLOSION){
			return;
		}
		event.setCancelled(true);
	}
}
