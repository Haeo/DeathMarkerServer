package com.haeo.deathmarkerserver;

import net.minecraft.world.entity.decoration.ArmorStand;
import java.util.Objects;
import java.util.UUID;

public class DeathMarkerInfo {
    private final ArmorStand markerEntity;
    private final long creationTimeMillis; // 현실 시간 기준 생성 시각 (System.currentTimeMillis())
    private final UUID markerUUID; // ArmorStand의 UUID를 직접 저장하여 비교 용이

    public DeathMarkerInfo(ArmorStand markerEntity) {
        this.markerEntity = markerEntity;
        this.creationTimeMillis = System.currentTimeMillis();
        this.markerUUID = markerEntity.getUUID();
    }

    public ArmorStand getMarkerEntity() {
        return markerEntity;
    }

    public long getCreationTimeMillis() {
        return creationTimeMillis;
    }

    public UUID getMarkerUUID() {
        return markerUUID;
    }

    // Set에서 중복 방지 및 검색을 위해 UUID 기반으로 equals와 hashCode 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeathMarkerInfo that = (DeathMarkerInfo) o;
        return Objects.equals(markerUUID, that.markerUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(markerUUID);
    }
}