package com.bapspatil.surface.model

import com.google.gson.annotations.SerializedName

data class IWSResponse(
    @SerializedName("identity_token") var identityToken: String? = null,
    @SerializedName("layer_identity_token") var layerIdentityToken: String? = null
)