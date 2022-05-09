package com.trverse.busvalidator.enumirations

import com.trverse.busvalidator.App
import com.trverse.busvalidator.utilities.SharePrefData

data class Actions(val Code: String, val desc: String?)
enum class AppAction(action: Actions) {
    GENERATE_QR(Actions("POS-001", "Generate QR")),
    RE_GENERATE_QR(Actions("POS-002", "Regenerate QR")),
    GENERATE_RECENT_QR(Actions("POS-003", "Generate Recent QR")),
    INITIALIZE_CARD(Actions("POS-011", "Initialize Card")),
    ISSUE_CARD(Actions("POS-012", "Issue Card")),
    RECHARGE_CARD(Actions("POS-013", "Recharge Card")),
    QR_CHECKIN(Actions("QR_CHECK_IN", "QR CheckIN Successfully")),
    QR_CHECKOUT(Actions("QR_CHECK_OUT", "QR CheckOUT Successfully")),
    QR_USED(Actions("QR_ALREADY_USED", "QR Already used")),
    QR_EXPIRED(Actions("QR_EXPIRED", "QR Expired")),
    QR_INVALID(Actions("QR_INVALID", "QR is INVALID")),
    CARD_CHECKIN(Actions("CARD_CHECK_IN", "CARD CheckIN Successfully")),
    CARD_CHECKOUT(Actions("CARD_CHECK_OUT", "CARD CheckOUT Successfully")),
    CARD_INVALID(Actions("CARD_INVALID", "CARD is invalid")),
    CARD_INSUFFiCIENT(Actions("CARD_INSUFFICIENT", "CARD balance is insufficient")),
    CARD_ALREADY_CHECKIN(Actions("CARD_ALREADY_IN", "CARD Already Checked In")),
    CARD_ALREADY_CHECKOUT(Actions("CARD_ALREADY_OUT", "CARD Already Checked Out")),
    BLOCK_CARD(Actions("POS-014", "Block Card")),
    APP_CRASH(Actions("APP_CRASH", "APP Crashed Unknown Reason")),
    APP_RESTART(Actions("APP_RESTART", "APP Restarted ")),
    FARE_TABLE_DOWNLOAD(Actions("FARE_TABLE", "Unable to Download FareTable"));

    var appAction = action
}