package com.github.slave_of_fate.registries;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class SlaveOfFateKeybinds {
    public static final String CATEGORY = "key.categories.slave_of_fate";

    public static final KeyMapping USE_DORMANT = new KeyMapping(
            "key.slave_of_fate.use_dormant",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );

    public static final KeyMapping USE_AWAKENED = new KeyMapping(
            "key.slave_of_fate.use_awakened",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            CATEGORY
    );

    public static final KeyMapping USE_ASCENDED = new KeyMapping(
            "key.slave_of_fate.use_ascended",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            CATEGORY
    );
}
