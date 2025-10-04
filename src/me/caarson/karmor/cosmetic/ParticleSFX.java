package me.caarson.karmor.cosmetic;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.util.Vector;
import java.util.Random;

public class ParticleSFX {
    private static final Random random = new Random();

    public static void spawnRedstoneParticles(Location location, Color color, double radius, int count) {
        World world = location.getWorld();
        if (world == null) {
            System.out.println("DEBUG: World is null in spawnRedstoneParticles");
            return;
        }
        
        System.out.println("DEBUG: Spawning " + count + " redstone particles at " + location + " with color " + color);
        System.out.println("DEBUG: World name: " + world.getName() + ", Players in world: " + world.getPlayers().size());
        
        // Use DUST particle with DustOptions for newer versions
        DustOptions dustOptions = new DustOptions(color, 1.0f);
        int particlesSpawned = 0;
        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Location particleLoc = location.clone().add(x, 0, z);
            try {
                world.spawnParticle(Particle.DUST, particleLoc, 1, dustOptions);
                particlesSpawned++;
            } catch (Exception e) {
                System.out.println("DEBUG: Error spawning particle: " + e.getMessage());
            }
        }
        System.out.println("DEBUG: Successfully spawned " + particlesSpawned + " particles");
    }

    public static void spawnFlameParticles(Location location, Color color, int density) {
        World world = location.getWorld();
        if (world == null) return;
        
        for (int i = 0; i < density; i++) {
            double offsetX = random.nextDouble() * 0.5 - 0.25;
            double offsetY = random.nextDouble() * 0.5;
            double offsetZ = random.nextDouble() * 0.5 - 0.25;
            Location particleLoc = location.clone().add(offsetX, offsetY, offsetZ);
            world.spawnParticle(Particle.FLAME, particleLoc, 1);
        }
    }

    public static void spawnSoulFireParticles(Location location, Color color, int density) {
        World world = location.getWorld();
        if (world == null) return;
        
        for (int i = 0; i < density; i++) {
            double offsetX = random.nextDouble() * 0.5 - 0.25;
            double offsetY = random.nextDouble() * 0.5;
            double offsetZ = random.nextDouble() * 0.5 - 0.25;
            Location particleLoc = location.clone().add(offsetX, offsetY, offsetZ);
            world.spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc, 1);
        }
    }

    public static void spawnSpellWitchParticles(Location location, Color color, int density) {
        World world = location.getWorld();
        if (world == null) return;
        
        for (int i = 0; i < density; i++) {
            double offsetX = random.nextDouble() * 0.5 - 0.25;
            double offsetY = random.nextDouble() * 0.5;
            double offsetZ = random.nextDouble() * 0.5 - 0.25;
            Location particleLoc = location.clone().add(offsetX, offsetY, offsetZ);
            world.spawnParticle(Particle.WITCH, particleLoc, 1);
        }
    }

    public static void spawnCloudCritParticles(Location location, Color color, int density) {
        World world = location.getWorld();
        if (world == null) return;
        
        for (int i = 0; i < density; i++) {
            double offsetX = random.nextDouble() * 0.3 - 0.15;
            double offsetY = random.nextDouble() * 0.2;
            double offsetZ = random.nextDouble() * 0.3 - 0.15;
            Location particleLoc = location.clone().add(offsetX, offsetY, offsetZ);
            if (i % 2 == 0) {
                world.spawnParticle(Particle.CLOUD, particleLoc, 1);
            } else {
                world.spawnParticle(Particle.CRIT, particleLoc, 1);
            }
        }
    }

    public static void spawnSparkParticles(Location location, Vector velocity, Color color, int density) {
        World world = location.getWorld();
        if (world == null) return;
        
        for (int i = 0; i < density; i++) {
            double offsetX = random.nextDouble() * 0.2 - 0.1;
            double offsetY = random.nextDouble() * 0.2;
            double offsetZ = random.nextDouble() * 0.2 - 0.1;
            Location particleLoc = location.clone().add(offsetX, offsetY, offsetZ);
            // Use ENCHANTED_HIT for spark effect instead of CRIT_MAGIC
            world.spawnParticle(Particle.ENCHANTED_HIT, particleLoc, 1, velocity.getX(), velocity.getY(), velocity.getZ(), 0.1);
        }
    }

    public static void spawnNoteParticles(Location location, Vector velocity, Color color, int density) {
        World world = location.getWorld();
        if (world == null) return;
        
        for (int i = 0; i < density; i++) {
            double offsetX = random.nextDouble() * 0.2 - 0.1;
            double offsetY = random.nextDouble() * 0.2;
            double offsetZ = random.nextDouble() * 0.2 - 0.1;
            Location particleLoc = location.clone().add(offsetX, offsetY, offsetZ);
            world.spawnParticle(Particle.NOTE, particleLoc, 1, velocity.getX(), velocity.getY(), velocity.getZ(), 0.1);
        }
    }

    public static void spawnHeartParticles(Location location, Vector velocity, Color color, int density) {
        World world = location.getWorld();
        if (world == null) return;
        
        for (int i = 0; i < density; i++) {
            double offsetX = random.nextDouble() * 0.2 - 0.1;
            double offsetY = random.nextDouble() * 0.2;
            double offsetZ = random.nextDouble() * 0.2 - 0.1;
            Location particleLoc = location.clone().add(offsetX, offsetY, offsetZ);
            world.spawnParticle(Particle.HEART, particleLoc, 1, velocity.getX(), velocity.getY(), velocity.getZ(), 0.1);
        }
    }

    public static void spawnCritParticles(Location location, Color color, int count) {
        World world = location.getWorld();
        if (world == null) return;
        
        DustOptions dustOptions = new DustOptions(color, 1.0f);
        for (int i = 0; i < count; i++) {
            double offsetX = random.nextDouble() * 1.0 - 0.5;
            double offsetY = random.nextDouble() * 1.0;
            double offsetZ = random.nextDouble() * 1.0 - 0.5;
            Location particleLoc = location.clone().add(offsetX, offsetY, offsetZ);
            world.spawnParticle(Particle.CRIT, particleLoc, 1);
            world.spawnParticle(Particle.DUST, particleLoc, 1, dustOptions);
        }
    }

    public static void spawnSoulParticles(Location location, Color color, int count) {
        World world = location.getWorld();
        if (world == null) return;
        
        for (int i = 0; i < count; i++) {
            double offsetX = random.nextDouble() * 1.0 - 0.5;
            double offsetY = random.nextDouble() * 1.0;
            double offsetZ = random.nextDouble() * 1.0 - 0.5;
            Location particleLoc = location.clone().add(offsetX, offsetY, offsetZ);
            world.spawnParticle(Particle.SOUL, particleLoc, 1);
        }
    }

    public static void spawnEnchantmentTableParticles(Location location, Color color, int count) {
        World world = location.getWorld();
        if (world == null) return;
        
        for (int i = 0; i < count; i++) {
            double offsetX = random.nextDouble() * 1.0 - 0.5;
            double offsetY = random.nextDouble() * 1.0;
            double offsetZ = random.nextDouble() * 1.0 - 0.5;
            Location particleLoc = location.clone().add(offsetX, offsetY, offsetZ);
            world.spawnParticle(Particle.ENCHANT, particleLoc, 1);
        }
    }

    public static void spawnBlockCrackParticles(Location location, Color color, int count) {
        World world = location.getWorld();
        if (world == null) return;
        
        // Use a default block material for crack particles
        Material blockMaterial = Material.STONE;
        for (int i = 0; i < count; i++) {
            double offsetX = random.nextDouble() * 1.0 - 0.5;
            double offsetY = random.nextDouble() * 1.0;
            double offsetZ = random.nextDouble() * 1.0 - 0.5;
            Location particleLoc = location.clone().add(offsetX, offsetY, offsetZ);
            world.spawnParticle(Particle.BLOCK, particleLoc, 1, blockMaterial.createBlockData());
        }
    }
}
