package de.ironjan.metalonly.api

import android.content.Context
import com.google.gson.reflect.TypeToken
import com.koushikdutta.ion.Ion
import de.ironjan.metalonly.api.model.Stats
import java.util.concurrent.TimeUnit

// FIXME add error handling
class Client (val context: Context) {
    private val baseUrl = "http://mensaupb.herokuapp.com/metalonly"

    private val statsPath = "/stats"

    fun getStats(): Stats? {
        return Ion.with(context)
            .load("$baseUrl$statsPath")
            .`as`(object : TypeToken<Stats>() {})
            .get(5, TimeUnit.SECONDS)
    }

    /* TODO
GET /stats            metalonly.controllers.StatsController.stats()
GET /showinformation  metalonly.controllers.StatsController.showinformation()
GET /track            metalonly.controllers.StatsController.track()
GET /plan            metalonly.controllers.PlanController.plan()
GET /mods            metalonly.controllers.ModController.getMods()

POST /wish           metalonly.controllers.WishController.submit()
     */
}