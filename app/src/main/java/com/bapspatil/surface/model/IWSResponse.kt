package com.bapspatil.surface.model

import com.google.gson.annotations.SerializedName

data class IWSResponse(

	@SerializedName("identity_token")
	var identityToken: String? = null
)