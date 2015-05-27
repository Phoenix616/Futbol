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
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class PlayerAnimationListener implements Listener {

    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        List<Entity> ent = player.getNearbyEntities(1, 1, 1);
        for (Entity entity : ent) {
            if (entity instanceof Item && ArenaFutbol.balls.containsKey(entity) && ArenaFutbol.balls.get(entity) == null) {
                Location location = player.getLocation();
                World world = player.getWorld();
                Vector kickVector = ArenaFutbol.plugin.kickVector(player);
                entity.setVelocity(kickVector);
                world.playEffect(location, Effect.STEP_SOUND, 10);
            }
        }
    }
}
