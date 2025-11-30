package com.runningbird.fabricbungeeforwarding.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameProfile.class)
public interface GameProfileAccessor {
    @Accessor("properties")
    PropertyMap bff$getProperties();
}
