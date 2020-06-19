package com.jayreturns.lobbygenerator.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.jayreturns.lobbygenerator.LobbyGenerator;

public class ConfigReloadCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("lobbygenerator.reload")) {
			sender.sendMessage(MainCommand.fromConfig("no-permissions"));
			return true;
		}
		
		LobbyGenerator.getInstance().reloadConfig();
		
		sender.sendMessage(MainCommand.fromConfig("config-reload"));
		
		return true;
	}

}
