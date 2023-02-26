package org.bukkit.map;

import org.bukkit.entity.Player;

/**
 * Represents a renderer for a map.
 */
public abstract class MapRenderer {

    private boolean contextual;


    public MapRenderer() {
        this(false);
    }

    public MapRenderer(boolean contextual) {
        this.contextual = contextual;
    }

    /**
     * Get whether the renderer is contextual, i.e. has different canvases for
     * different players.
     *
     * @return True if contextual, false otherwise.
     */
    final public boolean isContextual() {
        return contextual;
    }

    /**
     * Initialize this MapRenderer for the given map.
     *
     * @param map The MapView being initialized.
     */
    public void initialize(MapView map) {
    }

    /**
     * Render to the given map.
     *
     * @param map    The MapView being rendered to.
     * @param canvas The canvas to use for rendering.
     * @param player The player who triggered the rendering.
     */
    abstract public void render(MapView map, MapCanvas canvas, Player player);

}
