package mono.common

fun Boolean?.nullToFalse(): Boolean = this ?: false
