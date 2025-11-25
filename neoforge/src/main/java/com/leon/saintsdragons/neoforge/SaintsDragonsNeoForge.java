package com.leon.saintsdragons.neoforge;

import com.leon.saintsdragons.common.SaintsDragonsCommon;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(SaintsDragonsCommon.MOD_ID)
public class SaintsDragonsNeoForge {
    public SaintsDragonsNeoForge(IEventBus modEventBus) {
        NeoForgeModContext.setModEventBus(modEventBus);
        SaintsDragonsCommon.init();
    }
}
