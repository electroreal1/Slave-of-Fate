package com.github.slave_of_fate.flaw.return_by_death;

import com.github.slave_of_fate.capabilities.PlayerSoul;
import com.github.slave_of_fate.network.payload.BlackoutPayload;
import com.github.slave_of_fate.registries.SlaveOfFateAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

@EventBusSubscriber(modid = "slave_of_fate")
public class ReturnByDeathEngine {

    private static final Map<BlockPos, BlockState> blockHistory = new HashMap<>();
    private static final Map<BlockPos, CompoundTag> containerHistory = new HashMap<>();
    private static final Map<UUID, CompoundTag> entityHistory = new HashMap<>();
    private static long savedTime;

    private static CompoundTag savedInventory;
    private static float savedHealth;
    private static int savedFood;
    private static float savedSaturation;
    private static int savedXpLevel;
    private static float savedXpProgress;
    private static final Collection<MobEffectInstance> savedEffects = new ArrayList<>();
    private static double savedX, savedY, savedZ;
    private static float savedYaw, savedPitch;
    private static ResourceKey<Level> savedDimension;

    private static int loopCount = 0;

    private static boolean isValidEnvironment(ServerPlayer player) {
        boolean isSinglePlayer = player.getServer() != null && player.getServer().isSingleplayer();
        PlayerSoul soul = player.getData(SlaveOfFateAttachments.SOUL_DATA);

        return isSinglePlayer && soul.getFlawId().toString().equals("slave_of_fate:return_by_death");
    }

    public static void setCheckpoint(ServerPlayer player) {
        if (!isValidEnvironment(player)) return;

        ReturnByDeathData data = ReturnByDeathData.get(player.serverLevel());
        CompoundTag nbt = new CompoundTag();

        data.snapshot = nbt;
        data.hasCheckpoint = true;
        data.setDirty();
        blockHistory.clear();
        containerHistory.clear();
        savedEffects.clear();

        savedDimension = player.level().dimension();
        savedX = player.getX();
        savedY = player.getY();
        savedZ = player.getZ();
        savedYaw = player.getYRot();
        savedPitch = player.getXRot();

        savedHealth = player.getHealth();
        savedFood = player.getFoodData().getFoodLevel();
        savedSaturation = player.getFoodData().getSaturationLevel();
        savedXpLevel = player.experienceLevel;
        savedXpProgress = player.experienceProgress;

        for (MobEffectInstance effect : player.getActiveEffects()) {
            savedEffects.add(new MobEffectInstance(effect));
        }

        savedInventory = new CompoundTag();
        savedInventory.put("items", player.getInventory().save(new ListTag()));

        savedTime = player.serverLevel().getDayTime();

        snapshotNearbyEntities(player);
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof ServerPlayer player)) return;
        if (!isValidEnvironment(player)) return;

        BlockPos pos = event.getPos();
        BlockEntity be = event.getLevel().getBlockEntity(pos);

        if (be != null && !containerHistory.containsKey(pos)) {
            containerHistory.put(pos, be.saveWithFullMetadata(event.getLevel().registryAccess()));
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player && isValidEnvironment(player)) {
            recordBlock(event.getPos(), (ServerLevel) event.getLevel());
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && isValidEnvironment(player)) {
            BlockPos pos = event.getPos().immutable();
            if (!blockHistory.containsKey(pos)) {
                blockHistory.put(pos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        recordExplosion(event);
    }

    private static void recordExplosion(ExplosionEvent.Detonate event) {
        for (BlockPos pos : event.getAffectedBlocks()) {
            recordBlock(pos, (ServerLevel) event.getLevel());
        }
    }

    private static void recordBlock(BlockPos pos, ServerLevel level) {
        BlockPos immutablePos = pos.immutable();

        if (!blockHistory.containsKey(immutablePos)) {
            BlockState state = level.getBlockState(immutablePos);

            blockHistory.put(immutablePos, state);

            if (state.hasBlockEntity()) {
                BlockEntity be = level.getBlockEntity(immutablePos);
                if (be != null) {
                    containerHistory.put(immutablePos, be.saveWithFullMetadata(level.registryAccess()));
                }
            }
        }
    }

    public static void snapshotNearbyEntities(ServerPlayer player) {
        entityHistory.clear();
        Level level = player.level();
        AABB area = player.getBoundingBox().inflate(64);

        level.getEntities((Entity) null, area, e -> e instanceof LivingEntity || e instanceof ItemEntity)
                .forEach(e -> {
                    if (!(e instanceof Player)) {
                        CompoundTag nbt = e.saveWithoutId(new CompoundTag());
                        nbt.putString("id", EntityType.getKey(e.getType()).toString());
                        entityHistory.put(e.getUUID(), nbt);
                    }
                });
    }


    public static void performReturn(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null || savedDimension == null) return;

        ServerLevel targetLevel = server.getLevel(savedDimension);
        if (targetLevel == null) targetLevel = player.serverLevel();

        PacketDistributor.sendToPlayer(player, new BlackoutPayload(1.0f));

        player.getServer().execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}

            PacketDistributor.sendToPlayer(player, new BlackoutPayload(0.0f));
        });

        final ServerLevel finalLevel = targetLevel;

        new HashMap<>(blockHistory).forEach((pos, state) -> {
            finalLevel.sendParticles(ParticleTypes.DRAGON_BREATH,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    5, 0.2, 0.2, 0.2, 0.02);
            finalLevel.setBlock(pos, state, 3);
        });

        new HashMap<>(containerHistory).forEach((pos, nbt) -> {
            BlockEntity be = finalLevel.getBlockEntity(pos);
            if (be != null) {
                be.loadWithComponents(nbt, finalLevel.registryAccess());
                be.setChanged();
                finalLevel.sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
            }
        });

        restoreEntities(player);

        if (player.level().dimension() != savedDimension) {
            ServerPlayer teleportedPlayer = (ServerPlayer) player.changeDimension(new net.minecraft.world.level.portal.DimensionTransition(
                    targetLevel, new Vec3(savedX, savedY, savedZ), Vec3.ZERO, savedYaw, savedPitch, DimensionTransition.DO_NOTHING));

            if (teleportedPlayer != null) {
                applyStatsAndInventory(teleportedPlayer);
            }
        } else {
            player.teleportTo(targetLevel, savedX, savedY, savedZ, savedYaw, savedPitch);
            applyStatsAndInventory(player);
        }

        targetLevel.setDayTime(savedTime);

        blockHistory.clear();
        containerHistory.clear();
        entityHistory.clear();

        ReturnByDeathData.get(finalLevel).hasCheckpoint = false;
        ReturnByDeathData.get(finalLevel).setDirty();

        player.displayClientMessage(Component.literal("Return by Death: Loop " + (++loopCount))
                .withStyle(ChatFormatting.DARK_PURPLE), true);
    }

    private static void applyStatsAndInventory(ServerPlayer player) {
        player.setHealth(savedHealth);
        player.getFoodData().setFoodLevel(savedFood);
        player.getFoodData().setSaturation(savedSaturation);
        player.experienceLevel = savedXpLevel;
        player.experienceProgress = savedXpProgress;

        player.removeAllEffects();
        for (MobEffectInstance effect : savedEffects) {
            player.addEffect(new MobEffectInstance(effect));
        }

        if (savedInventory != null && savedInventory.contains("items")) {
            player.getInventory().clearContent();
            player.getInventory().load(savedInventory.getList("items", 10));
        }
    }

    public static void restoreEntities(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        AABB area = player.getBoundingBox().inflate(64);

        level.getEntities((Entity) null, area, e -> !(e instanceof Player)).forEach(Entity::discard);

        entityHistory.forEach((uuid, nbt) -> level.getServer().execute(() -> {
            nbt.putUUID("UUID", uuid);
            Entity entity = EntityType.loadEntityRecursive(nbt, level, (ent) -> ent);
            if (entity != null) {
                level.addFreshEntity(entity);
            }
        }));
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (isValidEnvironment(player)) {
                event.setCanceled(true);
                player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1.0f, 0.5f);
                performReturn(player);
            }
        }
    }

    public static void saveHistoryToDisk(ServerLevel overworld) {
        ReturnByDeathData data = ReturnByDeathData.get(overworld);
        CompoundTag nbt = new CompoundTag();

        ListTag blocks = new ListTag();
        blockHistory.forEach((pos, state) -> {
            CompoundTag entry = new CompoundTag();
            entry.putLong("pos", pos.asLong());
            entry.put("state", NbtUtils.writeBlockState(state));
            blocks.add(entry);
        });
        nbt.put("Blocks", blocks);

        ListTag containers = new ListTag();
        containerHistory.forEach((pos, tag) -> {
            CompoundTag entry = new CompoundTag();
            entry.putLong("pos", pos.asLong());
            entry.put("nbt", tag);
            containers.add(entry);
        });
        nbt.put("Containers", containers);

        data.snapshot = nbt;
        data.hasCheckpoint = true;
        data.setDirty();
    }

    public static void loadHistoryFromDisk(ServerLevel overworld) {
        ReturnByDeathData data = ReturnByDeathData.get(overworld);
        if (!data.hasCheckpoint) return;

        blockHistory.clear();
        containerHistory.clear();

        CompoundTag nbt = data.snapshot;

        ListTag blocks = nbt.getList("Blocks", 10);
        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag entry = blocks.getCompound(i);
            BlockPos pos = BlockPos.of(entry.getLong("pos"));
            BlockState state = net.minecraft.nbt.NbtUtils.readBlockState(overworld.holderLookup(Registries.BLOCK), entry.getCompound("state"));
            blockHistory.put(pos, state);
        }

        ListTag containers = nbt.getList("Containers", 10);
        for (int i = 0; i < containers.size(); i++) {
            CompoundTag entry = containers.getCompound(i);
            containerHistory.put(BlockPos.of(entry.getLong("pos")), entry.getCompound("nbt"));
        }
    }
}