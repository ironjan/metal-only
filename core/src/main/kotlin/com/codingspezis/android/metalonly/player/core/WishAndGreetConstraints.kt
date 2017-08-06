package com.codingspezis.android.metalonly.player.core

interface WishAndGreetConstraints {
    val canWish: Boolean
    val canGreet: Boolean
    val wishLimit: Int
    val greetLimit: Int
    val unlimitedWishes: Boolean
    val unlimitedGreetings: Boolean
}