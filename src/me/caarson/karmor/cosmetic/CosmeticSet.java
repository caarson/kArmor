package me.caarson.karmor.cosmetic;

import org.bukkit.configuration.ConfigurationSection;
import java.util.EnumSet;

public class CosmeticSet {
    private final ConfigurationSection section;

    public CosmeticSet(ConfigurationSection section) {
        this.section = section;
    }

    // Get cosmetic set items and defaults
    public String getStyle() { return section.getString("style", "AURA_ARCANE"); }
    public Color getColor() { return Color.parseHex(section.getString("color", "#7F00FF")); }
    public int getRateTps() { return section.getInt("rateTps", 5); }
    public int getDensity() { return section.getInt("density", 6); }
    public double getRadius() { return section.getDouble("radius", 0.8); }
    public double getScale() { return section.getDouble("scale", 1.0); }

    // Triggers
    public EnumSet<Trigger> getTriggers() {
        String triggersString = section.getString("triggers", "AURA");
        return EnumSet.of(Trigger.valueOf(triggersString));
    }

    private static class Color {
        private final int r, g, b;
        public Color(int r, int g, int b) { this.r = r; this.g = g; this.b = b; }

        public static Color parseHex(String hexString) {
            if (hexString.startsWith("#")) hexString = hexString.substring(1);
            return new Color(
                Integer.parseInt(hexString.substring(0, 2), 16),
                Integer.parseInt(hexString.substring(2, 4), 16),
                Integer.parseInt(hexString.substring(4, 6), 16)
            );
        }

        public int getR() { return r; }
        public int getG() { return g; }
        public int getB() { return b; }
    }

    enum Trigger {
        AURA,
        TRAIL,
        IMPACT_HIT,
        IMPACT_KILL,
        IMPACT_BLOCK
    }
}
