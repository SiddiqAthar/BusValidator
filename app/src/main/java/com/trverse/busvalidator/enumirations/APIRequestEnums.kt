package com.trverse.busvalidator.enumirations

enum class APIRequestEnums(requestCode: APIReqCode) {
    SYNC_PARAMETERS_SETTINGS(APIReqCode(1001)),
    FARE_POLICY(APIReqCode(1006)),
    RSA_ENCRYPTION_KEY(APIReqCode(1002)),
    CARD_TYPES(APIReqCode(1003)),
    QR_USAGE(APIReqCode(1004));

    var requestCode = requestCode
}

data class APIReqCode(var requestCode: Int)