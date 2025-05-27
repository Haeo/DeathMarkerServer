package com.haeo.deathmarkerserver;

import net.minecraft.world.entity.decoration.ArmorStand;
import java.util.Set;
import java.util.Iterator; // 추가
import java.util.concurrent.ConcurrentHashMap;

public class DeathMarkerManager {
    // ArmorStand 대신 DeathMarkerInfo 객체를 저장
    private static final Set<DeathMarkerInfo> activeMarkersInfo = ConcurrentHashMap.newKeySet();

    public static void addMarker(ArmorStand marker) {
        DeathMarkerInfo newMarkerInfo = new DeathMarkerInfo(marker);
        activeMarkersInfo.add(newMarkerInfo);
        DeathMarkerServerMod.LOGGER.info("[DEBUG] DeathMarkerManager에 새 표식 정보 추가: " + marker.getUUID() + ", 생성 시간: " + newMarkerInfo.getCreationTimeMillis());
    }

    // 특정 ArmorStand 엔티티를 가진 DeathMarkerInfo를 제거 (주로 ServerTickHandler에서 호출)
    public static void removeMarker(ArmorStand markerToRemove) {
        if (markerToRemove == null) return;

        Iterator<DeathMarkerInfo> iterator = activeMarkersInfo.iterator();
        boolean removed = false;
        while (iterator.hasNext()) {
            DeathMarkerInfo info = iterator.next();
            if (info.getMarkerEntity().equals(markerToRemove)) {
                if (!info.getMarkerEntity().isRemoved()) {
                    info.getMarkerEntity().discard();
                }
                iterator.remove(); // Set에서 정보 객체 제거
                DeathMarkerServerMod.LOGGER.info("사망 표식 정보를 제거했습니다 (엔티티 기준): " + markerToRemove.getCustomName().getString() + " (" + markerToRemove.getUUID() + ")");
                removed = true;
                break;
            }
        }
        if (!removed) {
            // activeMarkersInfo에 해당 엔티티가 없는 경우 (이미 다른 로직으로 제거되었을 수 있음)
            // 만약 markerToRemove가 아직 월드에 있다면, 여기서 한 번 더 discard 시도
            if (!markerToRemove.isRemoved()) {
                markerToRemove.discard();
                DeathMarkerServerMod.LOGGER.info("관리 목록에 없던 표식 엔티티를 월드에서 제거 시도: " + markerToRemove.getUUID());
            }
        }
    }

    // DeathMarkerInfo 객체를 직접 받아 제거하는 메소드도 유용할 수 있음 (ServerTickHandler에서 자동 소멸 시 사용)
    public static void removeMarkerInfo(DeathMarkerInfo markerInfoToRemove) {
        if (markerInfoToRemove == null) return;
        ArmorStand entity = markerInfoToRemove.getMarkerEntity();
        if (entity != null && !entity.isRemoved()) {
            entity.discard();
        }
        activeMarkersInfo.remove(markerInfoToRemove); // Set에서 DeathMarkerInfo 객체 직접 제거
        DeathMarkerServerMod.LOGGER.info("사망 표식 정보를 제거했습니다 (정보 객체 기준): " + (entity != null ? entity.getCustomName().getString() : "N/A") + " (" + markerInfoToRemove.getMarkerUUID() + ")");
    }


    public static Set<DeathMarkerInfo> getActiveMarkersInfo() {
        return activeMarkersInfo;
    }

    public static void clearAllMarkers() {
        Iterator<DeathMarkerInfo> iterator = activeMarkersInfo.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            DeathMarkerInfo info = iterator.next();
            ArmorStand entity = info.getMarkerEntity();
            if (entity != null && !entity.isRemoved()) {
                entity.discard();
            }
            iterator.remove();
            count++;
        }
        if (count > 0) {
            DeathMarkerServerMod.LOGGER.info(count + "개의 모든 사망 표식 정보를 제거했습니다.");
        } else {
            DeathMarkerServerMod.LOGGER.info("제거할 사망 표식이 없습니다.");
        }
    }

    // 명령어 클래스에서 사용할 현재 표식 개수 반환 메소드
    public static int getActiveMarkerCount() {
        return activeMarkersInfo.size();
    }
}