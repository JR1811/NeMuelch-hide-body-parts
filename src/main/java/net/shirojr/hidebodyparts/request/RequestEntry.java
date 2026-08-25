package net.shirojr.hidebodyparts.request;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.shirojr.hidebodyparts.cca.components.BodyPartComponent;
import net.shirojr.hidebodyparts.util.BodyPart;
import net.shirojr.hidebodyparts.util.EntityUtil;
import net.shirojr.hidebodyparts.util.HideBodyPartsNbtKeys;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.UUID;

/**
 * A system to allow normal users to request a change of visible {@link BodyPart BodyParts}
 *
 * @param requester      user, who handed in the request
 * @param changeTarget   user, who's entries are requested to be changed
 * @param requestedParts parts to change
 * @param hidden         visibility of the requested parts
 * @param handler        user, who changed the {@link RequestState request state}
 * @param requestTime    time of when the request was created
 * @param state          request entry status
 */
public record RequestEntry(UUID requester, UUID changeTarget, HashSet<BodyPart> requestedParts, boolean hidden,
                           @Nullable UUID handler, long requestTime, RequestState state) {

    public RequestEntry(UUID requester, HashSet<BodyPart> requestedParts, boolean hidden) {
        this(requester, requester, requestedParts, hidden, null, System.currentTimeMillis(), RequestState.REQUESTED);
    }

    public RequestEntry copyWithState(RequestState state) {
        return new RequestEntry(requester, changeTarget, requestedParts, hidden, handler, requestTime, state);
    }

    public RequestEntry copyWithHandler(@Nullable UUID handler) {
        return new RequestEntry(requester, changeTarget, requestedParts, hidden, handler, requestTime, state);
    }

    public String getFormattedRequestedTime() {
        Instant instant = Instant.ofEpochMilli(this.requestTime);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        return zonedDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm z"));
    }

    public String getFormattedPartsList() {
        StringBuilder sb = new StringBuilder();
        this.requestedParts.forEach(part -> {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(part.asString());
        });
        return sb.toString();
    }

    public boolean apply(MinecraftServer server) {
        Entity entity = EntityUtil.get(server, this.changeTarget);
        if (entity == null) return false;
        BodyPartComponent bodyPartComponent = BodyPartComponent.fromEntity(entity);
        if (bodyPartComponent == null) return false;
        return bodyPartComponent.modifyHiddenBodyParts(bodyParts -> {
            if (hidden) bodyParts.addAll(requestedParts);
            else bodyParts.removeAll(requestedParts);
        }, true);
    }

    public static RequestEntry fromNbt(NbtCompound nbt) {
        return new RequestEntry(
                nbt.getUuid(HideBodyPartsNbtKeys.REQUESTER_UUID),
                nbt.getUuid(HideBodyPartsNbtKeys.CHANGE_TARGET_UUID),
                BodyPart.fromNbt(nbt),
                nbt.getBoolean(HideBodyPartsNbtKeys.HIDDEN),
                nbt.contains(HideBodyPartsNbtKeys.HANDLER_UUID) ? nbt.getUuid(HideBodyPartsNbtKeys.HANDLER_UUID) : null,
                nbt.getLong(HideBodyPartsNbtKeys.REQUEST_TIME),
                RequestState.get(nbt.getString(HideBodyPartsNbtKeys.REQUEST_STATE))
        );
    }

    public void toNbt(NbtCompound nbt) {
        nbt.putUuid(HideBodyPartsNbtKeys.REQUESTER_UUID, this.requester);
        nbt.putUuid(HideBodyPartsNbtKeys.CHANGE_TARGET_UUID, this.changeTarget);
        BodyPart.toNbt(this.requestedParts, nbt);
        nbt.putBoolean(HideBodyPartsNbtKeys.HIDDEN, this.hidden);
        if (this.handler != null) {
            nbt.putUuid(HideBodyPartsNbtKeys.HANDLER_UUID, this.handler);
        }
        nbt.putLong(HideBodyPartsNbtKeys.REQUEST_TIME, this.requestTime);
        nbt.putString(HideBodyPartsNbtKeys.REQUEST_STATE, this.state.asString());
    }
}
