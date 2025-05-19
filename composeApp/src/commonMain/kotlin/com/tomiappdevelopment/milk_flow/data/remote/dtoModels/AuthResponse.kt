package com.tomiappdevelopment.milk_flow.data.remote.dtoModels



data class AuthResponse(
 val idToken: String,
  val refreshToken: String,
  val localId: String,
    //@SerialName("email") val email: String,
  //  @SerialName("expiresIn") val expiresIn: String
)
