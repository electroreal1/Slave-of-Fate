package com.github.slave_of_fate.registries;

import com.github.slave_of_fate.capabilities.PlayerSoul;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class SlaveOfFateAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES,
            "slave_of_fate");

    public static final Supplier<AttachmentType<PlayerSoul>> SOUL_DATA =
            ATTACHMENT_TYPES.register("soul_data", () ->
                    AttachmentType.builder(PlayerSoul::new)
                            .serialize(PlayerSoul.CODEC)
                            .copyOnDeath().build());
}
