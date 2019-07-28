package de.ironjan.metalonly.api.model

data class Stats(val maxNoOfWishesReached:Boolean,
                 val maxNoOfGreetingsReached:Boolean,
                 val maxNoOfWishes:Int,
                 val maxNoOfGreetings:Int,
                 val showInformation: ShowInfo,
                 val track: TrackInfo
)