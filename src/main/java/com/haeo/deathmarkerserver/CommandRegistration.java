package com.haeo.deathmarkerserver;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeathMarkerServerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandRegistration {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        ClearMarkersCommand.register(event.getDispatcher());
        DeathMarkerServerMod.LOGGER.info("DeathMarkerServerMod 명령어 등록됨.");
    }
}