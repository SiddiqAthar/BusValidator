package com.trverse.busvalidator.enumirations

data class Desc(val Code: Int, val desc: String?)
enum class Errors(desc: Desc) {
    UNKNOWN_HOST_EXCEPPTION(Desc(-10000001, "No address associated with hostname")),
    QR_SYNC_ERROR(Desc(-10000002, "URL for Service is \n not accessible or may be invalid")),
    SERVER_RESPONDING(Desc(-10000002, "Server is not responding.")),
    NO_INTERNET(Desc(-10000003, "Internet connectivity problem.")),
    GENERAL_ERROR(Desc(-10000004, "Please try again later.")),
    INVALID_RESPONSE(Desc(-10000005, "Invalid response. Please try again!"));

    var errorEnum = desc
}
