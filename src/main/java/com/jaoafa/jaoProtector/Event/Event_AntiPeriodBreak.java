package com.jaoafa.jaoProtector.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.jaoafa.jaoProtector.JaoProtector;

public class Event_AntiPeriodBreak extends BukkitRunnable implements Listener {
	int BORDER = 100; // 10秒間に100ブロック破壊(コンフィグで変更可能)
	Map<UUID, Integer> BREAK = new HashMap<>();
	public Event_AntiPeriodBreak(){
		Integer i = JaoProtector.AntiBORDERLoad("BREAK");
		if(i == null){
			return;
		}
		BORDER = i;
	}

	// 10秒間に一定ブロック壊したら蹴る
	@EventHandler
	public void onBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		int i;
		if(BREAK.containsKey(uuid)){
			i = BREAK.get(uuid) + 1;
		}else{
			i = 1;
		}
		BREAK.put(uuid, i);
	}

	@Override
	public void run() {
		for(Player player : Bukkit.getOnlinePlayers()){
			UUID uuid = player.getUniqueId();
			if(!BREAK.containsKey(uuid)){
				continue;
			}
			int i = BREAK.get(uuid);
			if(i >= BORDER){
				// アウト
				player.kickPlayer("You are breaking too many blocks! (" + i + " blocks)");
			}
			Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " are breaking too many blocks! (" + i + " blocks)");
		}
	}
}
