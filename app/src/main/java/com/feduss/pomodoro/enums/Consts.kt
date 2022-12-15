package com.feduss.pomodoro.enums

sealed class Consts(val value: String) {
    object AlarmEnd: Consts("321")
    object ChannelId: Consts("TomatoChannelId")
    object NotificationChannelId: Consts("TomatoNotificationChannelId")
    object NotificationId: Consts("16")
    object NotificationVisibleChannel: Consts("Notifica del timer")
}
