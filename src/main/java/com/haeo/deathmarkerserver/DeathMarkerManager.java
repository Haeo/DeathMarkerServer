package com.haeo.deathmarkerserver; // 패키지명 변경

import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.Iterator;
import java.util.Set;
// import java.util.UUID; // UUID는 현재 이 클래스에서 직접 사용되지 않음
import java.util.concurrent.ConcurrentHashMap;

public class DeathMarkerManager {
    private static final Set<ArmorStand> activeMarkers = ConcurrentHashMap.newKeySet();

    public static void addMarker(ArmorStand marker) {
        activeMarkers.add(marker);
    }

    public static void removeMarker(ArmorStand marker) {
        if (marker != null && !marker.isRemoved()) {
            marker.discard();
        }
        activeMarkers.remove(marker);
        // 로그 메시지에서 모드 이름이나 표식의 커스텀 이름을 사용하는 것이 더 유용할 수 있습니다.
        DeathMarkerServerMod.LOGGER.info("사망 표식을 제거했습니다: " + (marker != null ? marker.getCustomName().getString() + " (" + marker.getUUID() + ")" : "null")); // 변경된 LOGGER 사용 및 정보 추가
    }

    public static Set<ArmorStand> getActiveMarkers() {
        return activeMarkers;
    }

    public static void clearAllMarkers() {
        // Iterator를 사용하여 안전하게 제거
        Iterator<ArmorStand> iterator = activeMarkers.iterator();
        while (iterator.hasNext()) {
            ArmorStand marker = iterator.next();
            if (marker != null && !marker.isRemoved()) {
                marker.discard();
            }
            iterator.remove(); // Set에서 제거
        }
        DeathMarkerServerMod.LOGGER.info("모든 사망 표식을 제거했습니다."); // 변경된 LOGGER 사용
    }
}