package com.jayreturns.lobbygenerator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.jayreturns.lobbygenerator.LobbyGenerator;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;


public class SchematicLoader {

	public static void loadSchematicFromConfig(Location loc) throws FileNotFoundException, UnsupportedOperationException {
		String fileName = LobbyGenerator.getInstance().getConfig().getString("schematic-name");
		File file;
		if (LobbyGenerator.getInstance().getConfig().getBoolean("use-worldedit-folder")) {
			file = new File(Bukkit.getPluginManager().getPlugin("WorldEdit").getDataFolder() + File.separator + "schematics", fileName);
		} else {
			file = new File(LobbyGenerator.getInstance().getDataFolder(), fileName);
		}
		
		if (!file.exists()) {
			throw new FileNotFoundException(fileName);
		}

		List<Integer> offsetList = LobbyGenerator.getInstance().getConfig().getIntegerList("offset");
		if (offsetList.size() != 3) {
			throw new UnsupportedOperationException("offset is has more/less than 3 values!");
		}

		World world = loc.getWorld();
		Location offset = new Location(world, offsetList.get(0), offsetList.get(1), offsetList.get(2));
		loc.add(offset);

		ClipboardFormat format = ClipboardFormats.findByFile(file);
		try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
			Clipboard clipboard = reader.read();

			try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
					.getEditSession(BukkitAdapter.adapt(world), -1)) {
				Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
						.to(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).ignoreAirBlocks(false)
						.copyEntities(true).build();
				Operations.complete(operation);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
