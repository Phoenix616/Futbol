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

package me.morrango.arenafutbol.listeners;

import me.morrango.arenafutbol.ArenaFutbol;
import org.bukkit.ChatColor;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Map;
import java.util.UUID;

public class MobClickListener implements Listener {
    
    @EventHandler
    public void onMobClick(PlayerInteractEntityEvent event) {
        Map<UUID, Long> bc = ArenaFutbol.plugin.ballentityClicks;
        UUID id = event.getPlayer().getUniqueId();
        if(bc.containsKey(id) && bc.get(id) + 11000 > System.currentTimeMillis()) {
            ArenaFutbol.plugin.ballEntityType = event.getRightClicked().getType();
            ArenaFutbol.plugin.getConfig().set("ballentity.type", event.getRightClicked().getType().toString());
            int size = 0;
            if(event.getRightClicked() instanceof Slime) {
                size = ((Slime) event.getRightClicked()).getSize();
            }
            ArenaFutbol.plugin.ballEntitySize = size;
            ArenaFutbol.plugin.getConfig().set("ballentity.size", size);
            ArenaFutbol.plugin.useEntity = true;
            ArenaFutbol.plugin.getConfig().set("useentity", true);
            ArenaFutbol.plugin.saveConfig();
            event.getPlayer().sendMessage(ChatColor.GREEN + "Ball set to " + event.getRightClicked().getType() + " (size: " + size + ")");
            event.setCancelled(true);
        }

    }
}
