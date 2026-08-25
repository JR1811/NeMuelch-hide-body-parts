package net.shirojr.hidebodyparts.cca.implementation;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.shirojr.hidebodyparts.HideBodyParts;
import net.shirojr.hidebodyparts.cca.HideBodyPartsComponents;
import net.shirojr.hidebodyparts.event.custom.BodyPartCallbacks;
import net.shirojr.hidebodyparts.request.RequestEntry;
import net.shirojr.hidebodyparts.request.RequestState;
import net.shirojr.hidebodyparts.util.EntityUtil;
import net.shirojr.hidebodyparts.util.HideBodyPartsNbtKeys;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class RequestComponent implements Component, AutoSyncedComponent {
    public static final Identifier KEY = HideBodyParts.getId("request");
    private static final Function<UUID, UnaryOperator<Style>> APPLY_ENTRY_UUID_TO_CLIPBOARD = uuid -> style -> style
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Copy UUID to Clipboard")))
            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, uuid.toString()));

    @Nullable
    private final MinecraftServer server;
    private final Scoreboard scoreboard;

    private final LinkedHashMap<UUID, RequestEntry> requests;

    public RequestComponent(Scoreboard scoreboard, @Nullable MinecraftServer server) {
        this.scoreboard = scoreboard;
        this.server = server;
        this.requests = new LinkedHashMap<>();
    }

    public static RequestComponent get(MinecraftServer server) {
        return HideBodyPartsComponents.REQUEST.get(server.getScoreboard());
    }

    @Nullable
    public MinecraftServer getServer() {
        return this.server;
    }

    public boolean contains(UUID entryUuid) {
        return this.requests.containsKey(entryUuid);
    }

    public Optional<RequestEntry> get(UUID entryUuid) {
        return Optional.ofNullable(this.requests.get(entryUuid));
    }

    public LinkedHashMap<UUID, RequestEntry> getEntries(boolean pendingOnly) {
        LinkedHashMap<UUID, RequestEntry> entries = new LinkedHashMap<>(this.requests);
        if (pendingOnly) {
            entries.entrySet().removeIf(entry -> entry.getValue().state() != RequestState.REQUESTED);
        }
        return entries;
    }

    public void add(UUID entryUuid, RequestEntry entry) {
        HashMap<UUID, RequestEntry> old = new HashMap<>(this.requests);
        this.requests.put(entryUuid, entry);
        if (Objects.equals(old, this.requests)) return;
        BodyPartCallbacks.REQUEST_CREATED.invoker().onRequestCreated(this, entryUuid, entry);
        this.sync();
    }

    public UUID add(RequestEntry entry) {
        UUID uuid = UUID.randomUUID();
        this.add(uuid, entry);
        return uuid;
    }

    public boolean resolve(UUID entryUuid, RequestState state, @Nullable UUID handler) {
        Optional<RequestEntry> oldEntry = this.get(entryUuid);
        if (oldEntry.isPresent() && oldEntry.get().state() == state) {
            return false;
        } else if (oldEntry.isEmpty()) {
            return false;
        }
        if (state != RequestState.REQUESTED && handler == null) {
            throw new IllegalArgumentException("If RequestState is not pending, a handler needs to be linked");
        }
        RequestEntry newEntry = oldEntry.get().copyWithState(state).copyWithHandler(handler);
        this.requests.put(entryUuid, newEntry);
        BodyPartCallbacks.REQUEST_STATE_CHANGED.invoker()
                .onRequestStateChanged(this, entryUuid, oldEntry.get().state(), newEntry.state());
        this.sync();
        if (this.server != null && state == RequestState.APPROVED) {
            return newEntry.apply(this.server);
        }
        return true;
    }

    public boolean remove(UUID removalEntry) {
        HashMap<UUID, RequestEntry> old = new HashMap<>(this.requests);
        this.requests.entrySet().removeIf(entry -> entry.getKey().equals(removalEntry));
        if (Objects.equals(old, this.requests)) return false;
        this.sync();
        return true;
    }

    @SuppressWarnings("unused")
    public void remove(Predicate<RequestEntry> removeCriteria) {
        HashMap<UUID, RequestEntry> old = new HashMap<>(this.requests);
        this.requests.entrySet().removeIf(entry -> removeCriteria.test(entry.getValue()));
        if (!Objects.equals(old, this.requests)) {
            this.sync();
        }
    }

    public void clearHandled() {
        HashMap<UUID, RequestEntry> old = new HashMap<>(this.requests);
        this.requests.entrySet().removeIf(entry -> !entry.getValue().state().equals(RequestState.REQUESTED));
        if (!Objects.equals(old, this.requests)) {
            this.sync();
        }
    }

    public void clear() {
        boolean hadElements = !this.requests.isEmpty();
        this.requests.clear();
        if (hadElements) {
            this.sync();
        }
    }

    public int size(boolean pendingOnly) {
        if (pendingOnly) {
            return Math.toIntExact(this.requests.values().stream().filter(entry -> entry.state() == RequestState.REQUESTED).count());
        }
        return this.requests.size();
    }

    @Nullable
    public List<Text> printEntry(UUID entryUuid) {
        RequestEntry requestEntry = this.get(entryUuid).orElse(null);
        if (this.server == null || requestEntry == null) return null;
        String requesterName = EntityUtil.getCachedPlayerNameOrUuid(server, requestEntry.requester());
        String targetName = EntityUtil.getCachedPlayerNameOrUuid(server, requestEntry.changeTarget());
        String handlerName = Optional.ofNullable(EntityUtil.getCachedPlayerNameOrUuid(server, requestEntry.handler())).orElse("none");
        List<Text> result = new ArrayList<>();
        result.add(Text.literal("Entry: " + entryUuid)
                .styled(APPLY_ENTRY_UUID_TO_CLIPBOARD.apply(entryUuid))
                .styled(style -> style.withColor(Formatting.YELLOW)));
        result.add(Text.empty().append("Requester: ").append(Text.literal(requesterName))
                .styled(APPLY_ENTRY_UUID_TO_CLIPBOARD.apply(requestEntry.requester())));
        result.add(Text.empty().append("Change Target: ").append(Text.literal(targetName).styled(APPLY_ENTRY_UUID_TO_CLIPBOARD.apply(requestEntry.changeTarget()))));
        result.add(Text.literal("Changes: " + requestEntry.getFormattedPartsList()));
        result.add(Text.literal("Hidden: " + requestEntry.hidden()));
        result.add(Text.literal("Current State: %s (handled by %s)".formatted(requestEntry.state(), handlerName)));
        result.add(Text.literal("Requested at: " + requestEntry.getFormattedRequestedTime()));
        if (requestEntry.state() == RequestState.REQUESTED) {
            String actionPrefixCommand = "/hide bodyPart request handle %s ".formatted(entryUuid.toString());
            MutableText actionText = Text.empty();
            actionText.append(
                    Text.literal("Approve").styled(style -> style
                            .withColor(Formatting.GREEN)
                            .withUnderline(true)
                            .withClickEvent(
                                    new ClickEvent(
                                            ClickEvent.Action.RUN_COMMAND,
                                            actionPrefixCommand + RequestState.APPROVED.asString()
                                    )
                            )
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Approve Request")))
                    )
            ).append(
                    Text.literal(" | ")
            ).append(
                    Text.literal("Deny").styled(style -> style
                            .withColor(Formatting.RED)
                            .withUnderline(true)
                            .withClickEvent(
                                    new ClickEvent(
                                            ClickEvent.Action.RUN_COMMAND,
                                            actionPrefixCommand + RequestState.DENIED.asString()
                                    )
                            )
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Deny Request")))
                    )
            );
            result.add(actionText);
        }
        return result;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.requests.clear();
        NbtList requestsNbt = tag.getList(HideBodyPartsNbtKeys.REQUESTS, NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < requestsNbt.size(); i++) {
            NbtCompound entryNbt = requestsNbt.getCompound(i);
            UUID entryUuid = entryNbt.getUuid(HideBodyPartsNbtKeys.REQUEST_ENTRY_UUID);
            RequestEntry entry = RequestEntry.fromNbt(entryNbt);
            this.requests.put(entryUuid, entry);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList requestsNbt = new NbtList();
        for (var entry : this.requests.entrySet()) {
            NbtCompound entryNbt = new NbtCompound();
            entryNbt.putUuid(HideBodyPartsNbtKeys.REQUEST_ENTRY_UUID, entry.getKey());
            entry.getValue().toNbt(entryNbt);
            requestsNbt.add(entryNbt);
        }
        tag.put(HideBodyPartsNbtKeys.REQUESTS, requestsNbt);
    }

    public void sync() {
        if (this.server == null) return;
        HideBodyPartsComponents.REQUEST.sync(this.scoreboard);
    }
}
