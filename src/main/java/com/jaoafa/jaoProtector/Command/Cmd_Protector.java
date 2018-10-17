package com.jaoafa.jaoProtector.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Cmd_Protector implements CommandExecutor {
	JavaPlugin plugin;
	public Cmd_Protector(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(args.length == 2){
			// つくる
		}
		return true;
	}
}
