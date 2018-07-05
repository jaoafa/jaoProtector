package com.jaoafa.jaoProtector.Event;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.jaoProtector.Lib.PermissionsManager;

public class Event_Lava implements Listener {
	JavaPlugin plugin;
	public Event_Lava(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	/* --------------------- CONFIG START --------------------- */

	Material RegulationBlock = Material.LAVA_BUCKET; // 規制するブロックマテリアル
	String[] RegulationGroups = {"Limited", "QPPE", "Default"}; // 規制権限グループ

	/* --------------------- CONFIG END --------------------- */

	/**
	 * 規制するべき権限グループに所属しているかを確認する
	 * @return 規制すべきであればTrue
	 */
	boolean isRegulationGroup(Player player){
		String group = PermissionsManager.getPermissionMainGroup(player);
		for(String RegulationGroup : RegulationGroups){
			if(group.equalsIgnoreCase(RegulationGroup)){
				return true;
			}
		}
		return false;
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event){
		Player player = event.getPlayer();
		if(event.getBlock() == null){
			return;
		}
		if(event.getBlock().getType() != RegulationBlock){
			return;
		}
		if(!isRegulationGroup(player)){
			return;
		}
		event.setCancelled(true);
		player.getInventory().remove(RegulationBlock);
	}
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event){
		if(event.getWhoClicked().getType() != EntityType.PLAYER) return;
		if(event.getClickedInventory() == null) return;
		Player player = (Player) event.getWhoClicked();

		ItemStack is = event.getClickedInventory().getItem(event.getSlot());

		if(is == null){
			return;
		}
		if(is.getType() != RegulationBlock){
			return;
		}
		if(!isRegulationGroup(player)){
			return;
		}

		is = event.getCurrentItem();

		if(is == null){
			return;
		}
		if(is.getType() != RegulationBlock){
			return;
		}
		if(!isRegulationGroup(player)){
			return;
		}

		is = event.getCursor();

		if(is == null){
			return;
		}
		if(is.getType() != RegulationBlock){
			return;
		}
		if(!isRegulationGroup(player)){
			return;
		}

		event.setCancelled(true);
		event.getInventory().remove(RegulationBlock);
	}
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event){
		Player player = (Player) event.getPlayer();
		PlayerInventory inv = player.getInventory();

		for(ItemStack is : inv.getContents()){
			if(is == null){
				continue;
			}
			if(is.getType() != RegulationBlock){
				return;
			}
			if(!isRegulationGroup(player)){
				return;
			}
			player.getInventory().remove(RegulationBlock);
			break;
		}
	}
}
