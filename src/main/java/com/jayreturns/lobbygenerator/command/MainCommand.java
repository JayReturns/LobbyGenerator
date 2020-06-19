package com.jayreturns.lobbygenerator.command;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.jayreturns.lobbygenerator.LobbyGenerator;
import com.jayreturns.lobbygenerator.util.SchematicLoader;

import net.md_5.bungee.api.ChatColor;

public class MainCommand implements TabExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!sender.hasPermission("lobbygenerator.use")) {
			sender.sendMessage(fromConfig("no-permissions"));
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(fromConfig("usage").replace("%COMMAND%", label));
			return true;
		} else if (args.length == 1 || args.length == 2) {

			if (args.length == 2 && (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport"))) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(fromConfig("no-player"));
					return true;
				}
				String worldName = args[1];
				World world = Bukkit.getWorld(worldName);
				if (world == null) {
					sender.sendMessage(fromConfig("invalid-world").replace("%WORLD%", worldName));
					return true;
				}

				Location loc = world.getSpawnLocation();

				((Player) sender).teleport(loc);

				sender.sendMessage(fromConfig("successfull-teleport").replace("%WORLD%", worldName));

				return true;
			}

			String seedName = args[0];
			long seed = -1;
			try {
				seed = Long.parseLong(seedName);
			} catch (NumberFormatException e) {
				sender.sendMessage(fromConfig("wrong-seed").replace("%SEED%", seedName));
				return true;
			}
			String name = null;
			if (args.length == 2) {
				name = args[1];
			}

			String environmentName = fromConfig("environment");
			String worldTypeName = fromConfig("world-type");
			boolean generateStructures = LobbyGenerator.getInstance().getConfig().getBoolean("generate-structures");
			World.Environment environment = null;
			WorldType worldType = null;
			try {
				environment = World.Environment.valueOf(environmentName.toUpperCase());
			} catch (Exception e) {
				sender.sendMessage(fromConfig("wrong-environment").replace("%ENVIRONMENT%", environmentName));
				return true;
			}
			try {
				worldType = WorldType.valueOf(worldTypeName.toUpperCase());
			} catch (Exception e) {
				sender.sendMessage(fromConfig("wrong-world-type").replace("%WORLD-TYPE%", worldTypeName));
				return true;
			}

			String realName = name == null ? "GeneratedLobby" : name;

			sender.sendMessage(fromConfig("pre-world-generation").replace("%NAME%", realName));

			WorldCreator creator = new WorldCreator(realName).seed(seed).type(worldType).environment(environment)
					.generateStructures(generateStructures);

			
			try {
//				Bukkit.createWorld(creator);
				creator.createWorld();
			} catch (Exception e) {
				sender.sendMessage(fromConfig("error").replace("%ERROR%", e.getLocalizedMessage()));
				LobbyGenerator.getInstance().getLogger().severe("An error occured! Pelase provide the developer with the following stack trace:");
				e.printStackTrace();
				return true;
			}
			
			String schemName = fromConfig("schematic-name");

			sender.sendMessage(fromConfig("loading-schematic").replace("%NAME%", realName).replace("%SCHEMATIC%", schemName));

			try {
				SchematicLoader.loadSchematicFromConfig(Bukkit.getWorld(realName).getSpawnLocation());
			} catch (FileNotFoundException e) {
				sender.sendMessage(fromConfig("file-not-found").replace("%FILENAME%", schemName));
				return true;
			} catch (UnsupportedOperationException e) {
				sender.sendMessage(fromConfig("wrong-offset"));
				return true;
			}

			sender.sendMessage(fromConfig("post-world-generation").replace("%NAME%", realName));
		}

		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1 && args[0].isBlank()) {
			return Arrays.asList("<seed|tp>");
		} else if (args.length == 2 && args[1].isBlank()) {
			return Arrays.asList("[name]");
		}
		return Lists.newArrayList();
	}

	public static String format(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static String fromConfig(String path) {
		return format(LobbyGenerator.getInstance().getConfig().getString(path));
	}

}
