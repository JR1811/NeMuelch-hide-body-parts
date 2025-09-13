package net.shirojr.hidebodyparts.init;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.shirojr.hidebodyparts.HideBodyParts;

import java.util.ArrayList;
import java.util.List;

public class HideBodyPartsTags {
    public static final List<TagKey<?>> ALL_TAGS = new ArrayList<>();

    public interface Items {
        List<TagKey<Item>> ALL_ITEM_TAGS = new ArrayList<>();

        TagKey<Item> INVISIBLE_ARMOR = createTag("invisible_armor");

        @SuppressWarnings("SameParameterValue")
        private static TagKey<Item> createTag(String name) {
            TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, HideBodyParts.getId(name));
            ALL_ITEM_TAGS.add(tagKey);
            ALL_TAGS.add(tagKey);
            return tagKey;
        }
    }
}
