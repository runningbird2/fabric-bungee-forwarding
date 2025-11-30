package com.runningbird.fabricbungeeforwarding.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(GameProfile.class)
public interface GameProfileAccessor {
    @Accessor("name")
    String bff$getName();

    @Accessor("id")
    UUID bff$getId();

    @Accessor("properties")
    PropertyMap bff$getProperties();
}
