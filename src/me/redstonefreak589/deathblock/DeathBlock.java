package me.redstonefreak589.deathblock;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathBlock extends JavaPlugin implements Listener {
	public final Logger logger = Logger.getLogger("Minecraft");
	public static DeathBlock plugin;
	private boolean dbEnabled;

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " has been disabled!");
	}

	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion()
				+ " has been enabled!");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		getConfig().options().copyDefaults(true);
		getConfig().options().header("A radius of 3 is your best option. 2 & 1 are very buggy and don't work very well. A radius of 3 will only test if you are right next to the block anyway :)");
		getConfig().options().copyHeader(true);
		saveConfig();
	}

	@SuppressWarnings("deprecation")
	public boolean isLocationNearBlock(Location loc, List<Integer> blocks,
			int radius) {
		World world = loc.getWorld();
		int x = (int) loc.getX(), y = (int) loc.getY(), z = (int) loc.getZ();

		for (int ox = 0; ox > -radius; ox--) {
			for (int oz = 0; oz > -radius; oz--) {
				if (blocks.contains(world.getBlockAt(x + ox, y, z + oz)
						.getTypeId())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if(label.equalsIgnoreCase("deathblock") || label.equalsIgnoreCase("db")){
				if(player.hasPermission("deathblock.toggle")){
					if(dbEnabled){
						dbEnabled = false;
						player.sendMessage(ChatColor.GREEN + "DeathBlock disabled");
					}else{
						dbEnabled = true;
						player.sendMessage(ChatColor.RED + "DeathBlock enabled.");
					}
				}else{
					player.sendMessage(ChatColor.RED + "You must have the permission 'deathblock.toggle' to use this command!");
				}
			}else if(label.equalsIgnoreCase("dbstatus")){
				if(player.hasPermission("deathblock.checkstatus")){
					if(dbEnabled){
						player.sendMessage(ChatColor.RED + "DeathBlock is currently enabled. Watch out for those Death Blocks!");
					}else{
						player.sendMessage(ChatColor.GREEN + "DeathBlock is currently disabled. You are safe...for now!");
					}
				}else{
					player.sendMessage(ChatColor.RED + "You must have the permission 'deathblock.checkstatus' to use this command!");
				}
			}else if(label.equalsIgnoreCase("dbreload")){
				if(player.hasPermission("deathblock.reload")){
					reloadConfig();
					player.sendMessage(ChatColor.GREEN + "The DeathBlock configuration has been reloaded.");
				}else{
					player.sendMessage(ChatColor.RED + "You must have the permission 'deathblock.reload' to use this command!");
				}
			}
		} else {
			getLogger().info("Please run this command ingame!");
		}
		return false;
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if(dbEnabled){
			Player player = e.getPlayer();
			if(player.getGameMode() == GameMode.CREATIVE) return;
			if(player.hasPermission("deathblock.bypass")) return;
			final Location loc = player.getLocation();
			List<Integer> blockList = new ArrayList<Integer>();
			blockList.add(152);
			int radius = getConfig().getInt("radius");
			if(isLocationNearBlock(loc, blockList, radius)){
				if(!player.isDead()){
					player.setHealth(0);
				}
			}
		}
	}
}
