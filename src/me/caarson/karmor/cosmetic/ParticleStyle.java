package me.caarson.karmor.cosmetic;

public enum ParticleStyle {
    // Auras
    AURA_ARCANE(true, 5, 6, 0.8, 1.0, 32.0),
    AURA_VOID(true, 5, 6, 0.8, 1.0, 32.0),
    AURA_NATURE(false, 5, 6, 0.8, 1.0, 32.0),

    // Wings
    WINGS_FLAME(true, 6, 8, 0.9, 1.0, 32.0),
    WINGS_FROST(true, 6, 8, 0.9, 1.0, 32.0),

    // Trails
    TRAIL_SPARK(false, 5, 6, 0.8, 1.0, 32.0),
    TRAIL_NOTE(false, 5, 6, 0.8, 1.0, 32.0),
    TRAIL_HEART(false, 5, 6, 0.8, 1.0, 32.0),

    // Impacts
    IMPACT_HIT_BURST(true, 5, 6, 0.8, 1.0, 32.0),
    IMPACT_KILL_BURST(true, 5, 6, 0.8, 1.0, 32.0),
    IMPACT_BLOCK_BURST(false, 5, 6, 0.8, 1.0, 32.0),

    // Footsteps
    FOOTSTEP_DUST(false, 5, 6, 0.8, 1.0, 32.0),
    HELMET_HALO(true, 5, 6, 0.8, 1.0, 32.0),
    CHEST_GLYPHS(false, 5, 6, 0.8, 1.0, 32.0),
    LEGS_SWIRL(false, 5, 6, 0.8, 1.0, 32.0),
    BOOTS_FOOTPRINTS(false, 5, 6, 0.8, 1.0, 32.0);

    private final boolean colorable;
    private final int rateTps;
    private final int density;
    private final double radius;
    private final double scale;
    private final double visibleRange;

    ParticleStyle(boolean colorable, int rateTps, int density, double radius, double scale, double visibleRange) {
        this.colorable = colorable;
        this.rateTps = rateTps;
        this.density = density;
        this.radius = radius;
        this.scale = scale;
        this.visibleRange = visibleRange;
    }

    public boolean isColorable() { return colorable; }
    public int getRateTps() { return rateTps; }
    public int getDensity() { return density; }
    public double getRadius() { return radius; }
    public double getScale() { return scale; }
    public double getVisibleRange() { return visibleRange; }

}
