package com.haeo.deathmarkerserver;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("deathmarkerserver") // mods.toml의 modId와 일치
public class DeathMarkerServerMod {
    public static final Logger LOGGER = LogManager.getLogger();

    public DeathMarkerServerMod() {
        LOGGER.info("Death Coordinates Mod initializing...");
        // 이벤트 리스너 등록 등의 초기화 코드는 여기에 작성합니다.
    }
}