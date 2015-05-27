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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import mc.alk.arena.BattleArena;
import mc.alk.arena.objects.LocationType;
import mc.alk.arena.objects.arenas.Arena;
import me.morrango.arenafutbol.commands.CommandExecutor_ArenaFutbol;
import me.morrango.arenafutbol.listeners.FutbolArena;
import me.morrango.arenafutbol.listeners.MobClickListener;
import me.morrango.arenafutbol.listeners.TestBallListeners;
import me.morrango.arenafutbol.tasks.Task_PlayEffect;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ArenaFutbol extends JavaPlugin {
	private Logger log;
	private PluginDescriptionFile description;
	private String prefix;
	public static ArenaFutbol plugin;
	public static Map<Entity, FutbolArena> balls = new HashMap<Entity, FutbolArena>();
	public Map<UUID, Vector> vectors = new HashMap<UUID, Vector>();
	private boolean particles = false;
    public Map<UUID, Long> ballentityClicks = new HashMap<UUID, Long>();

    public float configAdjPitch;
    public float configMaxPitch;
    public double configPower;
    public FutbolArena testBallArena;
    
    public boolean useEntity;
    public ItemStack ballItemStack;
    public EntityType ballEntityType;
    public int ballEntitySize;
    
    public String nmsVersion;

    @Override
	public void onEnable() {
        String packageName = getServer().getClass().getPackage().getName();
        nmsVersion = packageName.substring(packageName.lastIndexOf('.') + 1);
        
		plugin = this;
		log = Logger.getLogger("Minecraft");
		description = getDescription();
		prefix = "[" + description.getName() + "] ";
		log("loading " + description.getFullName());

		if (this.getConfig().getBoolean("particles")) {
			this.particles = true;
		}
		else {
			this.particles = false;
		}

		this.loadConfig();
		log(this.getServer().getVersion());
		BattleArena.registerCompetition(this, "Futbol", "fb",
				FutbolArena.class, new CommandExecutor_ArenaFutbol());
		getServer().getScheduler().scheduleSyncRepeatingTask(this,
				new Task_PlayEffect(this), 1L, 1L);
        getServer().getPluginManager().registerEvents(new MobClickListener(), this);
        getServer().getPluginManager().registerEvents(new TestBallListeners(), this);

		// try {
		// Metrics metrics = new Metrics(this);
		// metrics.start();
		// } catch (IOException e) {
		// // Failed to submit the stats :-(
		// }

	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelAllTasks();
		log("disabled " + description.getFullName());
	}

	public void log(String message) {
		log.info(prefix + message);
	}

	public void loadConfig() {
		this.getConfig().addDefault("particles", false);
		this.getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		saveConfig();

        configAdjPitch = -(float) getConfig().getInt("pitch");
        configMaxPitch = -(float) getConfig().getInt("maxpitch");
        configPower = getConfig().getDouble("power");

        useEntity = plugin.getConfig().getBoolean("useentity", true);
        ballItemStack = plugin.getConfig().getItemStack("ball");
        ballEntityType = EntityType.valueOf(plugin.getConfig().getString("ballentity.type", "MAGMA_CUBE"));
        ballEntitySize = plugin.getConfig().getInt("ballentity.size", 1);
	}

	public void doBallPhysics() {
		for (Entity ball : ArenaFutbol.balls.keySet()) {
			UUID uuid = ball.getUniqueId();
			Vector velocity = ball.getVelocity();
			if (this.vectors.containsKey(uuid)) {
				velocity = (Vector) this.vectors.get(uuid);
			}
			Vector newVector = ball.getVelocity();
			if (newVector.getX() == 0.0D) {
				newVector.setX(-velocity.getX() * 0.9D);
			} else if (Math.abs(velocity.getX() - newVector.getX()) < 0.15D) {
				newVector.setX(velocity.getX() * 0.975D);
			}
			if ((newVector.getY() == 0.0D) && (velocity.getY() < -0.1D)) {
				newVector.setY(-velocity.getY() * 0.9D);
			}
			if (newVector.getZ() == 0.0D) {
				newVector.setZ(-velocity.getZ() * 0.9D);
			} else if (Math.abs(velocity.getZ() - newVector.getZ()) < 0.15D) {
				newVector.setZ(velocity.getZ() * 0.975D);
			}
			ball.setVelocity(newVector);
			this.vectors.put(uuid, newVector);
			if (particles) {
				showEffect(ball);
			}
		}
	}

	public void showEffect(Entity entity) {
		Location location = entity.getLocation();
		World world = entity.getWorld();
		world.playEffect(location, Effect.INSTANT_SPELL, 0, 128);
	}


    public void removeBall(FutbolArena arena, Entity ball) {
        if (ball != null) {
            ArenaFutbol.balls.remove(ball);
            if(arena != null) {
                arena.kickedBalls.remove(ball);
                arena.kickedBy.remove(ball);
            }
            Entity e = ball.getPassenger();
            if(e != null) {
                e.remove();
            }
            ball.remove();
        }
    }

    public void spawnBall(FutbolArena arena, Location location) {
        ItemStack ballIS = ballItemStack;
        boolean useE = useEntity;
        EntityType ballET = ballEntityType;
        int ballSz = ballEntitySize;
        if(arena != null) {
            ballIS = arena.ballItemStack;
            useE = arena.useEntity;
            ballET = arena.ballEntityType;
            ballSz = arena.ballEntitySize;
        }
        Entity item = location.getWorld().dropItem(location, ballIS);
        if(arena != null) {
            arena.cleanUpList.put(arena.getMatch(), item);
            balls.put(item, arena);
        } else {
            if(testBallArena == null) {
                testBallArena = new FutbolArena();
                testBallArena.setName("TestBallArena");
            }
            balls.put(item, testBallArena);
        }
        if(useE) {
            Entity e = location.getWorld().spawnEntity(location, ballET);
            setTag(e, "NoAI", true);
            setTag(e, "Invulnerable", true);
            if(e instanceof Slime) {
                ((Slime) e).setSize(ballSz);
            }
            item.setPassenger(e);
        }
    }

    void setTag(Entity bukkitEntity, String tagName, boolean value) {
        if(plugin.nmsVersion.equals("v1_8_R1")) {
            net.minecraft.server.v1_8_R1.Entity nmsEntity = ((org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity) bukkitEntity).getHandle();
            net.minecraft.server.v1_8_R1.NBTTagCompound tag = nmsEntity.getNBTTag();
            if (tag == null) {
                tag = new net.minecraft.server.v1_8_R1.NBTTagCompound();
            }
            nmsEntity.c(tag);
            tag.setInt(tagName, (value) ? 1 : 0);
            nmsEntity.f(tag);
        } else if(plugin.nmsVersion.equals("v1_8_R2")) {
            net.minecraft.server.v1_8_R2.Entity nmsEntity = ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity) bukkitEntity).getHandle();
            net.minecraft.server.v1_8_R2.NBTTagCompound tag = nmsEntity.getNBTTag();
            if (tag == null) {
                tag = new net.minecraft.server.v1_8_R2.NBTTagCompound();
            }
            nmsEntity.c(tag);
            tag.setInt(tagName, (value) ? 1 : 0);
            nmsEntity.f(tag);
        } else if(plugin.nmsVersion.equals("v1_8_R3")) {
            net.minecraft.server.v1_8_R3.Entity nmsEntity = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity) bukkitEntity).getHandle();
            net.minecraft.server.v1_8_R3.NBTTagCompound tag = nmsEntity.getNBTTag();
            if (tag == null) {
                tag = new net.minecraft.server.v1_8_R3.NBTTagCompound();
            }
            nmsEntity.c(tag);
            tag.setInt(tagName, (value) ? 1 : 0);
            nmsEntity.f(tag);
        } else {
            plugin.getLogger().warning("The mobs require NMS code and there is no support for NMS version " + plugin.nmsVersion + " in the plugin yet! Use only items as balls for now. (/fb useentity false)");
        }
    }
    
    public Vector kickVector(Player player) {
        Location loc = player.getEyeLocation();
        if (player.getEquipment().getBoots() != null) {
            ItemStack boots = player.getEquipment().getBoots();
            if (boots.isSimilar(new ItemStack(Material.DIAMOND_BOOTS))) {
                configPower = configPower + 0.5;
            }
            if (boots.isSimilar(new ItemStack(Material.IRON_BOOTS))) {
                configPower = configPower + 0.4;
            }
            if (boots.isSimilar(new ItemStack(Material.GOLD_BOOTS))) {
                configPower = configPower + 0.3;
            }
            if (boots.isSimilar(new ItemStack(Material.CHAINMAIL_BOOTS))) {
                configPower = configPower + 0.2;
            }
            if (boots.isSimilar(new ItemStack(Material.LEATHER_BOOTS))) {
                configPower = configPower + 0.1;
            }
        }
        float pitch = loc.getPitch();
        pitch = pitch + configAdjPitch;
        if (pitch > 0) {
            pitch = 0.0f;
        }
        if (pitch < configMaxPitch) {
            pitch = 0.0f + configMaxPitch;
        }
        loc.setPitch(pitch);
        Vector vector = loc.getDirection();
        vector = vector.multiply(configPower);
        return vector;
    }
}
