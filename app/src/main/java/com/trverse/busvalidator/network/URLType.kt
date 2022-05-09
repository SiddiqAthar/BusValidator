package com.trverse.busvalidator.network

import com.trverse.busvalidator.App
import com.trverse.busvalidator.utilities.SharePrefData

enum class URLType(url: URL) {
    BASE_URL(URL("http://103.173.186.89:8080/Api/")),
    QR_USAGE_URL(URL("http://103.173.186.85:9005/api/")),
    QR_SERVICE(URL(
        if (SharePrefData(App.INSTANCE?.applicationContext!!).getStringValue(SharePrefData.RSA_ENC_URL).toString()
                .isNullOrEmpty()
        )
            "http://103.173.186.85:9001/api/"
        else
            SharePrefData(App.INSTANCE?.applicationContext!!).getStringValue(SharePrefData.RSA_ENC_URL).toString()
    )),
    DATA_SYNC_SERVICE(URL(
        if (SharePrefData(App.INSTANCE?.applicationContext!!).getStringValue(SharePrefData.SYNC_QRService).toString()
            .isNullOrEmpty()
    )
        "http://103.173.186.85:9003/api/"
    else
        SharePrefData(App.INSTANCE?.applicationContext!!).getStringValue(SharePrefData.SYNC_QRService).toString()
    ));

    var baseURL = url
}

class URL(ServerURL: String) {
    var url = ServerURL
}

