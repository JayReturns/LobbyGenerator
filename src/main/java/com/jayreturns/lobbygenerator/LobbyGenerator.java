package com.jayreturns.lobbygenerator;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;
import com.jayreturns.lobbygenerator.command.ConfigReloadCommand;
import com.jayreturns.lobbygenerator.command.MainCommand;

import lombok.Getter;

public class LobbyGenerator extends JavaPlugin {

	@Getter
	private static LobbyGenerator instance;

	@Override
	public void onEnable() {
		instance = this;

		getConfig().options().copyDefaults(true);
		saveDefaultConfig();

		getLogger().info("Plugin enabled and ready to use!");

		getCommand("generateworld").setExecutor(new MainCommand());
		getCommand("generateworld").setTabCompleter(new MainCommand());
		
		getCommand("reloadgeneratorconfig").setExecutor(new ConfigReloadCommand());
		getCommand("reloadgeneratorconfig").setTabCompleter(new TabCompleter() {
			
			public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
				return Lists.newArrayList();
			}
		});
	}

	@Override
	public void onDisable() {
		getLogger().info("Plugin disabled!");
	}

}
