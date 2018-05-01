package com.jaoafa.jaoProtector.Command;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.jaoafa.jaoProtector.JaoProtector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Cmd_Protector implements CommandExecutor {
	JavaPlugin plugin;
	public Cmd_Protector(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!(sender instanceof Player)){
			JaoProtector.SendMessage(sender, cmd, "このコマンドはプレイヤーからのみ実行できます。");
			return true;
		}
		Player player = (Player) sender;
		WorldGuardPlugin wg = JaoProtector.getWorldGuard();
		WorldEditPlugin we = JaoProtector.getWorldEdit();
		Location loc = player.getLocation();
		Vector v = new Vector(loc.getX(), loc.getY(), loc.getZ());

		RegionManager rm = wg.getRegionManager(player.getWorld());

		List<String> regionlist = rm.getApplicableRegionsIDs(v);

		if(regionlist.size() == 0){
			JaoProtector.SendMessage(sender, cmd, "この場所は登録されていません。");
			return true;
		}
		String last_ID = regionlist.get(0);
		ProtectedRegion region = rm.getRegion(last_ID);
		Polygonal2DSelection selection = new Polygonal2DSelection(player.getWorld(), region.getPoints(), region.getMinimumPoint().getBlockY(), region.getMaximumPoint().getBlockY());
		we.setSelection(player, selection);
		JaoProtector.SendMessage(sender, cmd, "リージョン「" + last_ID + "」の範囲をWorldEditにセットしました。");
		JaoProtector.SendMessage(sender, cmd, "WorldEditCUI等で確認してください。");
		return true;
	}
}
