package com.haeo.deathmarkerserver;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// import net.minecraftforge.common.MinecraftForge; // 만약 수동으로 CommandRegistration 등을 등록했다면 이 부분과 등록 코드를 제거

@Mod(DeathMarkerServerMod.MOD_ID)
public class DeathMarkerServerMod {
    public static final String MOD_ID = "deathmarkerserver";
    public static final Logger LOGGER = LogManager.getLogger();

    public DeathMarkerServerMod() {
        LOGGER.info("DeathMarkerServerMod (Chat-Only Death Location) initializing...");
        // 만약 CommandRegistration.class 등을 수동으로 등록하는 코드가 있었다면 제거합니다.
        // 예: MinecraftForge.EVENT_BUS.unregister(CommandRegistration.class); 또는 관련 등록 코드 삭제
    }
}