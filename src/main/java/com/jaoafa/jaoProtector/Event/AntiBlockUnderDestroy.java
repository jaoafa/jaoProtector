package com.jaoafa.jaoProtector.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.jaoProtector.Lib.PermissionsManager;

public class AntiBlockUnderDestroy implements Listener {
	JavaPlugin plugin;
	public AntiBlockUnderDestroy(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public static Map<UUID, Location> destroy = new HashMap<>();
	public static Map<UUID, Integer> destroycount = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAntiBlockUnderDestroy(BlockBreakEvent event){
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Block block = event.getBlock();
		Location loc = block.getLocation();
		World world = loc.getWorld();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		if (!(player instanceof Player)) {
			return; // いるのか？
		}

		if(!world.getName().equalsIgnoreCase("SandBox")){
			return; // SandBox以外では適用しない
		}

		String group = PermissionsManager.getPermissionMainGroup(player);
		int destroyOK;
		if(group.equalsIgnoreCase("QPPE")){
			destroyOK = 3;
		}else if(group.equalsIgnoreCase("Default")){
			destroyOK = 5;
		}else{
			return; // QD以外は特に規制設けない
		}

		if(!destroy.containsKey(uuid)){
			destroy.put(uuid, loc);
			destroycount.put(uuid, 1);
			return;
		}

		int oldx = destroy.get(uuid).getBlockX();
		int oldy = destroy.get(uuid).getBlockY();
		int oldz = destroy.get(uuid).getBlockZ();

		if(x != oldx || z != oldz){
			destroy.put(uuid, loc);
			return;
		}

		if(oldy <= y){
			// 前回掘ったときよりも上(その高さを含む)
			destroy.put(uuid, loc);
			return;
		}

		if((oldy - y) <= 1 || (oldy - y) >= 3){
			// oldYからYを引いた数(どれだけ下か)は、1ブロックから3ブロック以内ではないか
			destroy.put(uuid, loc);
			return; // じゃないなら判定しない
		}

		if(!destroycount.containsKey(uuid)){
			destroy.put(uuid, loc);
			destroycount.put(uuid, 1);
			return;
		}

		int nowdestroyCount = destroycount.get(uuid);
		if(nowdestroyCount <= destroyOK){
			// 以内だったらカウントアップしておわり
			destroy.put(uuid, loc);
			destroycount.put(uuid, nowdestroyCount + 1);
			return;
		}

		// アウト？
		// カウント処理とか座標処理とかあえてしない。
		player.sendMessage("[BlockDestroy] " + ChatColor.RED + "荒らし対策のため、ブロックの直下掘りは禁止されています。");
		event.setCancelled(true);
	}
}
