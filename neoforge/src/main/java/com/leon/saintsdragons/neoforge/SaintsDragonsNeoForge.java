package com.leon.saintsdragons.neoforge;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SaintsDragonsCommon.MOD_ID)
public class SaintsDragonsNeoForge {
    public SaintsDragonsNeoForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        SaintsDragonsCommon.init();
    }
}
