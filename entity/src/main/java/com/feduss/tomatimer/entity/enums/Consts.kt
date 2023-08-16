package com.feduss.tomatimer.entity.enums

sealed class Consts(val value: String) {
    object AlarmEnd: Consts("321")
    object MainChannelId: Consts("TomatoMainChannelId")
    object SubChannelId: Consts("TomatoSubChannelId")
    object NotificationChannelId: Consts("TomatoNotificationChannelId")
    object MainNotificationId: Consts("16")
    object SubNotificationId: Consts("10")
    object MainNotificationVisibleChannel: Consts("Notifica del timer attivo")
    object SubNotificationVisibleChannel: Consts("Notifica del timer scaduto")
    object NewValueKey: Consts("NewValue")
    object FromOngoingNotification: Consts("FromOngoingNotification")
    object NotificationActionIntentExtra: Consts("notificationAction")
    object NotificationActionIntentExtraNextTimer: Consts("nexttimer")
    object NotificationActionIntentExtraCancelQueue: Consts("cancelQueue")
}
