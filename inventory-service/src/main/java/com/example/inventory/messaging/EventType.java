package com.example.inventory.messaging;

public enum EventType {
    // incoming commands
    ReserveInventory,
    ReleaseInventory,

    // outgoing events
    InventoryReserved,
    InventoryReservationFailed,
    InventoryReleased
}
