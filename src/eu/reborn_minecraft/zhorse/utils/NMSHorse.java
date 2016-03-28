package eu.reborn_minecraft.zhorse.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Horse;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import net.minecraft.server.v1_9_R1.EntityHorse;
import net.minecraft.server.v1_9_R1.EntityTypes;
import net.minecraft.server.v1_9_R1.World;

/*
 * EntityHorse source : https://github.com/Bukkit/mc-dev/blob/master/net/minecraft/server/EntityHorse.java
 */

public class NMSHorse extends EntityHorse {

	private static final int NMS_ID = 100;
	private static final String NMS_NAME = "Horse";

	public NMSHorse(World world) {
		super(world);
	}
	
	public static Horse spawn(Location location) {
        World nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        NMSHorse nmsHorse = new NMSHorse(nmsWorld);
        nmsHorse.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftLivingEntity) nmsHorse.getBukkitEntity()).setRemoveWhenFarAway(false);
        nmsWorld.addEntity(nmsHorse, SpawnReason.CUSTOM);
        return (Horse) nmsHorse.getBukkitEntity();
    }
	
	public static void registerClass() {
		registerEntity(NMS_NAME, NMS_ID, EntityHorse.class, NMSHorse.class);
	}
	
	private static void registerEntity(String name, int id, Class<? extends EntityHorse> nmsClass, Class<? extends NMSHorse> customClass) {
        try {     
            List<Map<?, ?>> dataMap = new ArrayList<Map<?, ?>>();
            for (Field f : EntityTypes.class.getDeclaredFields()){
                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())){
                    f.setAccessible(true);
                    dataMap.add((Map<?, ?>) f.get(null));
                }
            }
     
            if (dataMap.get(2).containsKey(id)){
                dataMap.get(0).remove(name);
                dataMap.get(2).remove(id);
            }
     
            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
            method.setAccessible(true);
            method.invoke(null, customClass, name, id);
     
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public static void unregisterClass() {
		unregisterEntity();
	}
	
	@SuppressWarnings("rawtypes")
	public static void unregisterEntity() {
		try {
			Field field = EntityHorse.class.getDeclaredField("d");
			field.setAccessible(true);
			((Map) field.get(null)).remove(NMSHorse.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Field field = EntityHorse.class.getDeclaredField("f");
			field.setAccessible(true);
			((Map) field.get(null)).remove(NMSHorse.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		try {
			a(EntityHorse.class, "Horse" , 100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}

}
