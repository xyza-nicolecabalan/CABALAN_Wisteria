package com.wisteria.model;

public record PlantBatch(
        int batchId,
        String speciesName,
        String motherPlantSource,
        String propagationMethod,
        String growthStage,
        String greenhouseZone,
        double price,
        String status,
        String createdAt
) {
}