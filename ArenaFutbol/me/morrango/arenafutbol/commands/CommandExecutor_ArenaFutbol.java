/*
 *    ArenaFutbol - by Morrango
 *    http://
 *
 *    This file is part of ArenaFutbol.
 *
 *    ArenaFutbol is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    ArenaFutbol is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with ArenaFutbol.  If not, see <http://www.gnu.org/licenses/>.
 *    
 *	  powered by: 
 *    KickStarter
 *    BattleArena
 *
 */

package me.morrango.arenafutbol.commands;

import mc.alk.arena.BattleArena;
import mc.alk.arena.executors.CustomCommandExecutor;
import mc.alk.arena.executors.MCCommand;
import mc.alk.arena.objects.arenas.Arena;
import me.morrango.arenafutbol.ArenaFutbol;

import me.morrango.arenafutbol.listeners.FutbolArena;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandExecutor_ArenaFutbol extends CustomCommandExecutor {

    @MCCommand(cmds = { "spawnball" }, op = true, admin = true)
    public boolean spawnball(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only a player can run this command!");
            return true;
        }
        Player player = (Player)sender;
        ArenaFutbol.plugin.spawnBall(null, player.getLocation());
        sender.sendMessage(ChatColor.GREEN + "Spawned a ball at your feet!");
        return true;
    }
    
    @MCCommand(cmds = { "spawnball" }, op = true, admin = true)
    public boolean spawnball(CommandSender sender, String arenaname) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only a player can run this command!");
            return true;
        }
        Player player = (Player)sender;
        Arena arena = BattleArena.getArena(arenaname);
        if(arena == null) {
            sender.sendMessage(ChatColor.RED + "No arena with the name " + arenaname + " found!");
        } else if(arena instanceof FutbolArena) {
            ArenaFutbol.plugin.spawnBall((FutbolArena) arena, player.getLocation());
            sender.sendMessage(ChatColor.GREEN + "Spawned a ball in arena " + arena.getName() + " at your feet!");
        } else {
            sender.sendMessage(ChatColor.RED + "Arena " + arena.getName() + " is not a FutbolArena!");
        }
        return true;
    }

    @MCCommand(cmds = { "removeballs" }, op = true, admin = true)
    public boolean removeballs(CommandSender sender, int radius) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only a player can run this command!");
            return true;
        }
        Player player = (Player)sender;
        int i = 0;
        for(Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if(ArenaFutbol.balls.containsKey(e)) {
                FutbolArena arena = ArenaFutbol.balls.get(e);
                ArenaFutbol.plugin.removeBall(arena, e);
                i++;
            }
        }
        sender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.YELLOW + i + ChatColor.GREEN + " balls in a " + ChatColor.YELLOW + radius + ChatColor.GREEN + " block radius!");
        return true;
    }    
    
	@MCCommand(cmds = { "ball" }, op = true, admin = true)
	public boolean ball(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only a player can run this command!");
            return true;
        }
		Player player = (Player)sender;
		ItemStack itemInHand = player.getItemInHand();
        ArenaFutbol.plugin.ballItemStack = itemInHand;
		ArenaFutbol.plugin.getConfig().set("ball", itemInHand);
		ArenaFutbol.plugin.saveConfig();
		sender.sendMessage(ChatColor.GREEN + "Ball set to " + itemInHand);
		return true;
	}

    @MCCommand(cmds = { "ballentity" }, op = true, admin = true)
    public boolean ballentity(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only a player can run this command!");
            return true;
        }
        Player player = (Player)sender;
        ArenaFutbol.plugin.ballentityClicks.put(player.getUniqueId(), System.currentTimeMillis());
        sender.sendMessage(ChatColor.GREEN + "Rightclick a mob in the next 10 seconds to set it as the ball!");
        return true;
    }

    @MCCommand(cmds = { "useentity" }, op = true, admin = true)
    public boolean useentity(CommandSender sender, boolean use) {
        ArenaFutbol.plugin.useEntity = use;
        ArenaFutbol.plugin.getConfig().set("useentity", use);
        ArenaFutbol.plugin.saveConfig();
        sender.sendMessage(((use) ? (ChatColor.GREEN + "Enabled") : (ChatColor.RED + "Disabled")) + ChatColor.YELLOW + " the usage of a mob as ball");
        return true;
    }

	@MCCommand(cmds = { "balltimer" }, op = true, admin = true)
	public boolean balltimer(CommandSender sender, int timer) {
		int newInt = timer;
		ArenaFutbol.plugin.getConfig().set("balltimer", newInt);
		ArenaFutbol.plugin.saveConfig();
		sender.sendMessage(ChatColor.GREEN + "Ball posession timer set to "
				+ timer + " Seconds");
		return true;
	}

	@MCCommand(cmds = { "pitch" }, op = true, admin = true)
	public boolean pitch(CommandSender sender, int pitch) {
		if (pitch > 90 || pitch < 0) {
			return false;
		}

        ArenaFutbol.plugin.configAdjPitch = -(float) pitch;
		ArenaFutbol.plugin.getConfig().set("pitch", pitch);
		ArenaFutbol.plugin.saveConfig();
		sender.sendMessage(ChatColor.GREEN + "Pitch adjustment set to " + pitch
				+ " degrees");
		return true;
	}

	@MCCommand(cmds = { "maxpitch" }, op = true, admin = true)
	public boolean maxpitch(CommandSender sender, int pitch) {
		if (pitch > 90 || pitch < 0) {
			return false;
		}
        ArenaFutbol.plugin.configMaxPitch = -(float) pitch;
		ArenaFutbol.plugin.getConfig().set("maxpitch", pitch);
		ArenaFutbol.plugin.saveConfig();
		sender.sendMessage(ChatColor.GREEN + "Maximum Pitch set to " + pitch
				+ " degrees");
		return true;
	}

	@MCCommand(cmds = { "power" }, op = true, admin = true)
	public boolean power(CommandSender sender, double power) {
		if (power > 2.0) {
			return false;
		}
        ArenaFutbol.plugin.configPower = power;
		ArenaFutbol.plugin.getConfig().set("power", power);
		ArenaFutbol.plugin.saveConfig();
		sender.sendMessage(ChatColor.GREEN + "Power adjustment set to "
				+ (int) (power * 100) + "%");
		return true;

	}
	
	@MCCommand(cmds = { "particles" }, op = true, admin = true)
	public boolean particles(CommandSender sender, boolean particles) {
		ArenaFutbol.plugin.getConfig().set("particles", particles);
		ArenaFutbol.plugin.saveConfig();
		sender.sendMessage(ChatColor.GREEN + "Particles set to "
				+ particles + " changes may not take effect until server reload/restart.");
		return true;

	}
}
