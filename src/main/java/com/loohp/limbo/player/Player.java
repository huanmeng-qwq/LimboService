/*
  ~ This file is part of Limbo.
  ~
  ~ Copyright (C) 2024. YourCraftMC <admin@ycraft.cn>
  ~ Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
  ~ Copyright (C) 2022. Contributors
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
 */

package com.loohp.limbo.player;

import cn.ycraft.limbo.config.ServerConfig;
import cn.ycraft.limbo.config.ServerMessages;
import cn.ycraft.limbo.network.ClientConnection;
import cn.ycraft.limbo.util.SoundUtil;
import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.entity.DataWatcher;
import com.loohp.limbo.entity.DataWatcher.WatchableField;
import com.loohp.limbo.entity.DataWatcher.WatchableObjectType;
import com.loohp.limbo.entity.EntityEquipment;
import com.loohp.limbo.entity.LivingEntity;
import com.loohp.limbo.events.inventory.InventoryCloseEvent;
import com.loohp.limbo.events.inventory.InventoryOpenEvent;
import com.loohp.limbo.events.player.PlayerChatEvent;
import com.loohp.limbo.events.player.PlayerTeleportEvent;
import com.loohp.limbo.inventory.*;
import com.loohp.limbo.location.Location;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Emitter;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.kyori.adventure.title.TitlePart;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerSpawnInfo;
import org.geysermc.mcprotocollib.protocol.data.game.entity.type.EntityType;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.DataComponentTypes;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.Filterable;
import org.geysermc.mcprotocollib.protocol.data.game.item.component.WrittenBookContent;
import org.geysermc.mcprotocollib.protocol.data.game.level.notify.GameEvent;
import org.geysermc.mcprotocollib.protocol.packet.common.clientbound.ClientboundResourcePackPushPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundRespawnPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundStopSoundPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundTabListPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.entity.player.ClientboundSetHeldSlotPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.inventory.ClientboundContainerClosePacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.inventory.ClientboundOpenBookPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.inventory.ClientboundOpenScreenPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundGameEventPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.level.ClientboundSoundPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.title.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Player extends LivingEntity implements CommandSender, InventoryHolder {

    public final ClientConnection clientConnection;
    public final PlayerInteractManager playerInteractManager;

    protected final String username;
    protected GameMode gamemode;
    protected DataWatcher watcher;
    protected byte selectedSlot;
    protected final PlayerInventory playerInventory;
    protected final InventoryView inventoryView;
    private final AtomicInteger containerIdCounter;

    @WatchableField(MetadataIndex = 15, WatchableObjectType = WatchableObjectType.FLOAT)
    protected float additionalHearts = 0.0F;
    @WatchableField(MetadataIndex = 16, WatchableObjectType = WatchableObjectType.VARINT)
    protected int score = 0;
    @WatchableField(MetadataIndex = 17, WatchableObjectType = WatchableObjectType.BYTE)
    protected byte skinLayers = 0;
    @WatchableField(MetadataIndex = 18, WatchableObjectType = WatchableObjectType.BYTE)
    protected byte mainHand = 1;
    //@WatchableField(MetadataIndex = 19, WatchableObjectType = WatchableObjectType.NBT)
    //protected Entity leftShoulder = null;
    //@WatchableField(MetadataIndex = 20, WatchableObjectType = WatchableObjectType.NBT)
    //protected Entity rightShoulder = null;

    public Player(ClientConnection clientConnection, String username, UUID uuid, int entityId, Location location, PlayerInteractManager playerInteractManager) {
        super(EntityType.PLAYER, entityId, uuid, location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.clientConnection = clientConnection;
        this.username = username;
        this.entityId = entityId;
        this.containerIdCounter = new AtomicInteger(1);
        this.playerInventory = new PlayerInventory(this);
        this.inventoryView = new InventoryView(this, null, null, playerInventory);
        this.playerInteractManager = playerInteractManager;
        this.playerInteractManager.setPlayer(this);
        this.watcher = new DataWatcher(this);
        try {
            this.watcher.update();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected int nextContainerId() {
        return containerIdCounter.updateAndGet(i -> ++i > Byte.MAX_VALUE ? 1 : i);
    }

    public byte getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(byte slot) {
        if (slot == selectedSlot) {
            return;
        }
        ClientboundSetHeldSlotPacket state = new ClientboundSetHeldSlotPacket(slot);
        clientConnection.sendPacket(state);
        this.selectedSlot = slot;
    }

    public GameMode getGamemode() {
        return gamemode;
    }

    public void setGamemode(GameMode gamemode) {
        if (!this.gamemode.equals(gamemode)) {
            ClientboundGameEventPacket state = new ClientboundGameEventPacket(GameEvent.CHANGE_GAME_MODE, gamemode);
            clientConnection.sendPacket(state);
        }
        this.gamemode = gamemode;
    }

    @Deprecated
    protected void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public float getAdditionalHearts() {
        return additionalHearts;
    }

    public void setAdditionalHearts(float additionalHearts) {
        this.additionalHearts = additionalHearts;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public byte getSkinLayers() {
        return skinLayers;
    }

    public void setSkinLayers(byte skinLayers) {
        this.skinLayers = skinLayers;
    }

    public byte getMainHand() {
        return mainHand;
    }

    public void setMainHand(byte mainHand) {
        this.mainHand = mainHand;
    }

    @Override
    public DataWatcher getDataWatcher() {
        return watcher;
    }

    @Override
    public boolean isValid() {
        return Limbo.getInstance().getPlayers().contains(this);
    }

    @Override
    public void remove() {

    }

	/*
	public Entity getLeftShoulder() {
		return leftShoulder;
	}

	public void setLeftShoulder(Entity leftShoulder) {
		this.leftShoulder = leftShoulder;
	}

	public Entity getRightShoulder() {
		return rightShoulder;
	}

	public void setRightShoulder(Entity rightShoulder) {
		this.rightShoulder = rightShoulder;
	}
	*/

    @Override
    public String getName() {
        return username;
    }

    @Override
    public boolean hasPermission(String permission) {
        return Limbo.getInstance().getPermissionsManager().hasPermission(this, permission);
    }

    @Override
    public void teleport(Location location) {
        PlayerTeleportEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerTeleportEvent(this, getLocation(), location));
        if (!event.isCancelled()) {
            location = event.getTo();
            super.teleport(location);
            if (!world.equals(location.getWorld())) {
                ClientboundRespawnPacket respawn = new ClientboundRespawnPacket(new PlayerSpawnInfo(
                    location.getWorld().getEnvironment().getId(), Key.key(location.getWorld().getName()), 100,
                    getGamemode(), getGamemode(),
                    !ServerConfig.LOGS.REDUCED_DEBUG_INFO.resolve(),
                    false, null, 100, 5
                ), false, true);
                clientConnection.sendPacket(respawn);
            }
            ClientboundPlayerPositionPacket positionLook = new ClientboundPlayerPositionPacket(
                1, location.getX(), location.getY(), location.getZ(),
                0, 0, 0, location.getYaw(), location.getPitch()
            );
            clientConnection.sendPacket(positionLook);
        }
    }

    protected void setLocation(Location location) {
        super.teleport(location);
    }

    public void sendPluginMessage(Key channel, byte[] data) throws IOException {
        sendPluginMessage(channel.toString(), data);
    }

    @Deprecated
    public void sendPluginMessage(String channel, byte[] data) throws IOException {
        clientConnection.sendPluginMessage(channel, data);
    }

    public void sendMessage(String message, UUID uuid) {
        sendMessage(Identity.identity(uuid), LegacyComponentSerializer.legacySection().deserialize(message));
    }

    public void sendMessage(String message) {
        sendMessage(LegacyComponentSerializer.legacySection().deserialize(message));
    }

    @Override
    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
        Packet packet;
        switch (type) {
            case CHAT:
            case SYSTEM:
            default:
                packet = new ClientboundSystemChatPacket(message, false);
                break;
        }
        clientConnection.sendPacket(packet);
    }


    public void disconnect() {
        disconnect(Component.translatable("multiplayer.disconnect.kicked"));
    }

    public void disconnect(String reason) {
        disconnect(LegacyComponentSerializer.legacySection().deserialize(reason));
    }

    public void disconnect(Component reason) {
        clientConnection.disconnect(reason);
    }

    public void chat(String message) {
        chat(message, false);
    }

    public void chat(String message, boolean verbose) {
        chat(message, verbose, null, Instant.now());
    }

    public void chat(String message, boolean verbose, byte[] saltSignature, Instant time) {
        String format = ServerConfig.PLAYER.CHAT_FORMAT.get();
        if (format == null || format.isEmpty()) {
            ServerMessages.CHAT.DISABLED.sendTo(this);
            return;
        }

        PlayerChatEvent event = Limbo.getInstance().getEventsManager().callEvent(new PlayerChatEvent(this, format, message, false));
        if (event.isCancelled()) return;

        if (!hasPermission("limbo.command.chat")) {
            ServerMessages.CHAT.DISALLOWED.sendTo(this);
            return;
        }

        String chat = event.getFormat().replace("%(name)", username).replace("%(message)", event.getMessage());
        Limbo.getInstance().getConsole().sendMessage(chat);

        for (Player each : Limbo.getInstance().getPlayers()) {
            each.sendMessage(Identity.identity(uuid), Component.translatable("chat.type.text").arguments(Component.text(this.getName()), Component.text(event.getMessage())), MessageType.CHAT);
        }

    }


    public void setResourcePack(String url, String hash, boolean forced, Component prompt) {
        ClientboundResourcePackPushPacket packsend = new ClientboundResourcePackPushPacket(UUID.randomUUID(), url, hash, forced, prompt);
        clientConnection.sendPacket(packsend);
    }

    public void setPlayerListHeaderFooter(String header, String footer) {
        sendPlayerListHeaderAndFooter(header == null ? Component.empty() : LegacyComponentSerializer.legacySection().deserialize(header), footer == null ? Component.empty() : LegacyComponentSerializer.legacySection().deserialize(footer));
    }


    public void setTitle(String title) {
        sendTitlePart(TitlePart.TITLE, LegacyComponentSerializer.legacySection().deserialize(title));
    }

    public void setSubTitle(String subTitle) {
        sendTitlePart(TitlePart.SUBTITLE, LegacyComponentSerializer.legacySection().deserialize(subTitle));
    }

    public void setTitleTimer(int fadeIn, int stay, int fadeOut) {
        sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(fadeIn * 50L), Duration.ofMillis(stay * 50L), Duration.ofMillis(fadeOut * 50L)));
    }

    public void setTitleSubTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(fadeIn * 50L), Duration.ofMillis(stay * 50L), Duration.ofMillis(fadeOut * 50L)));
        sendTitlePart(TitlePart.SUBTITLE, LegacyComponentSerializer.legacySection().deserialize(subTitle));
        sendTitlePart(TitlePart.TITLE, LegacyComponentSerializer.legacySection().deserialize(title));
    }

    @Override
    public void openBook(Book book) {
        ItemStack item = getInventory().getItem(8);
        byte selectedSlot = getSelectedSlot();
        ItemStack itemStack = new ItemStack(Key.key("written_book"));
        String title = LegacyComponentSerializer.legacySection().serialize(book.title());
        String author = LegacyComponentSerializer.legacySection().serialize(book.author());
        List<Filterable<Component>> pages = book.pages().stream().map(page -> new Filterable<>(page, page)).collect(Collectors.toList());
        itemStack.component(DataComponentTypes.WRITTEN_BOOK_CONTENT, new WrittenBookContent(new Filterable<>(title, title), author, 0, pages, false));
        getInventory().setItem(8, itemStack);
        setSelectedSlot((byte) 8);
        clientConnection.sendPacket(new ClientboundOpenBookPacket(Hand.MAIN_HAND));
        getInventory().setItem(8, item);
        setSelectedSlot(selectedSlot);
    }

    @Override
    public void stopSound(SoundStop stop) {
        ClientboundStopSoundPacket stopSound = new ClientboundStopSoundPacket(SoundUtil.from(stop.source()), stop.sound());
        clientConnection.sendPacket(stopSound);
    }

    @Override
    public void playSound(@NotNull Sound sound, @NotNull Emitter emitter) {
        throw new UnsupportedOperationException("This function has not been implemented yet.");
    }

    @Override
    public void playSound(@NotNull Sound sound, double x, double y, double z) {
        ClientboundSoundPacket namedSoundEffect = new ClientboundSoundPacket(
            SoundUtil.from(sound.name()), SoundUtil.from(sound.source()),
            x, y, z, sound.volume(), sound.pitch(), sound.seed().orElse(ThreadLocalRandom.current().nextLong())
        );
        clientConnection.sendPacket(namedSoundEffect);
    }

    @Override
    public void playSound(@NotNull Sound sound) {
        playSound(sound, x, y, z);
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        ClientboundSetActionBarTextPacket setActionBar = new ClientboundSetActionBarTextPacket(message);
        clientConnection.sendPacket(setActionBar);
    }

    @Override
    public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
        ClientboundTabListPacket listHeaderFooter = new ClientboundTabListPacket(header, footer);
        clientConnection.sendPacket(listHeaderFooter);
    }

    @Override
    public <T> void sendTitlePart(TitlePart<T> part, @NotNull T value) {
        if (part.equals(TitlePart.TITLE)) {
            ClientboundSetTitleTextPacket setTitle = new ClientboundSetTitleTextPacket((Component) value);
            clientConnection.sendPacket(setTitle);
        } else if (part.equals(TitlePart.SUBTITLE)) {
            ClientboundSetSubtitleTextPacket setSubTitle = new ClientboundSetSubtitleTextPacket((Component) value);
            clientConnection.sendPacket(setSubTitle);
        } else if (part.equals(TitlePart.TIMES)) {
            Times times = (Times) value;
            ClientboundSetTitlesAnimationPacket setSubTitle = new ClientboundSetTitlesAnimationPacket((int) (times.fadeIn().toMillis() / 50), (int) (times.stay().toMillis() / 50), (int) (times.fadeOut().toMillis() / 50));
            clientConnection.sendPacket(setSubTitle);
        }
    }

    @Override
    public void clearTitle() {
        ClientboundClearTitlesPacket clearTitle = new ClientboundClearTitlesPacket(false);
        clientConnection.sendPacket(clearTitle);
    }

    @Override
    public void resetTitle() {
        ClientboundClearTitlesPacket clearTitle = new ClientboundClearTitlesPacket(true);
        clientConnection.sendPacket(clearTitle);
    }

    /**
     * @deprecated Use {@link com.loohp.limbo.bossbar.KeyedBossBar#showPlayer(Player)} instead
     */
    @Override
    @Deprecated
    public void showBossBar(@NotNull BossBar bar) {
        Limbo.getInstance().getBossBars().values().stream().filter(each -> each.getProperties() == bar).findFirst().ifPresent(each -> each.showPlayer(this));
    }

    /**
     * Use {@link com.loohp.limbo.bossbar.KeyedBossBar#hidePlayer(Player)} instead
     */
    @Override
    @Deprecated
    public void hideBossBar(@NotNull BossBar bar) {
        Limbo.getInstance().getBossBars().values().stream().filter(each -> each.getProperties() == bar).findFirst().ifPresent(each -> each.hidePlayer(this));
    }

    @Override
    public PlayerInventory getInventory() {
        return playerInventory;
    }

    public InventoryView getInventoryView() {
        return inventoryView;
    }

    public void updateInventory() {
        playerInventory.updateInventory(this);
    }

    public void openInventory(Inventory inventory) {
        Component title = inventory instanceof TitledInventory ? ((TitledInventory) inventory).getTitle() : Component.translatable("container.chest");
        inventoryView.getUnsafe().a(inventory, title);
        int id = nextContainerId();
        inventory.getUnsafe().c().put(this, id);
        InventoryOpenEvent event = Limbo.getInstance().getEventsManager().callEvent(new InventoryOpenEvent(inventoryView));
        if (event.isCancelled()) {
            inventoryView.getUnsafe().a(null, null);
            inventory.getUnsafe().c().remove(this);
        } else {
            ClientboundOpenScreenPacket packet = new ClientboundOpenScreenPacket(id, inventory.getType().getRawType(inventory.getSize()), title);
            clientConnection.sendPacket(packet);
            inventoryView.updateView();
        }
    }

    public void closeInventory() {
        Inventory inventory = inventoryView.getTopInventory();
        if (inventory != null) {
            Integer id = inventory.getUnsafe().c().get(this);
            if (id != null) {
                Limbo.getInstance().getEventsManager().callEvent(new InventoryCloseEvent(inventoryView));
                inventoryView.getUnsafe().a(null, null);
                inventory.getUnsafe().c().remove(this);
                ClientboundContainerClosePacket packet = new ClientboundContainerClosePacket(id);
                clientConnection.sendPacket(packet);
            }
        }
    }

    public EntityEquipment getEquipment() {
        return playerInventory;
    }

    @Override
    public InventoryHolder getHolder() {
        return this;
    }
}
