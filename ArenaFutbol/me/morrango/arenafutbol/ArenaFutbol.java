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
 */

package me.morrango.arenafutbol;

import java.util.logging.Logger;

import mc.alk.arena.BattleArena;
import me.morrango.arenafutbol.commands.CommandExecutor_ArenaFutbol;
import me.morrango.arenafutbol.listeners.FutbolArena;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenaFutbol extends JavaPlugin {
	private Logger log;
	private PluginDescriptionFile description;
	private String prefix;
	public static ArenaFutbol plugin;

	@Override
	public void onEnable() {
		plugin = this;
		log = Logger.getLogger("Minecraft");
		description = getDescription();
		prefix = "[" + description.getName() + "] ";
		log("loading " + description.getFullName());
		this.loadConfig();
		BattleArena.registerCompetition(this, "Futbol", "fb",
				FutbolArena.class, new CommandExecutor_ArenaFutbol());
		BattleArena.registerCompetition(this, "MaxFutbol", "mfb",
				FutbolArena.class, new CommandExecutor_ArenaFutbol());
	}

	@Override
	public void onDisable() {
		log("disabled " + description.getFullName());
	}

	public void log(String message) {
		log.info(prefix + message);
	}

	public void loadConfig() {
		this.getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		saveConfig();
	}
}
