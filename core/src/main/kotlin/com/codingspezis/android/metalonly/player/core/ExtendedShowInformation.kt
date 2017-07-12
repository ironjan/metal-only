package com.codingspezis.android.metalonly.player.core

interface ExtendedShowInformation : BasicShowInformation, WishAndGreetConstraints {
    val canNeitherWishNorGreet: Boolean
}