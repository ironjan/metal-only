package com.github.ironjan.metalonly.client

import arrow.core.Either
import arrow.core.Either.Companion
import com.github.ironjan.metalonly.client.model.PlanEntry
import com.github.ironjan.metalonly.client.model.ShowInformation
import com.github.ironjan.metalonly.client.model.StatsV2
import com.github.ironjan.metalonly.client.model.Track
import com.github.ironjan.metalonly.client.model.Wish
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import org.androidannotations.annotations.EBean

/**
 * @todo Check for responseCode -1 => error in connection
 */
@EBean(scope = EBean.Scope.Singleton)
open class MetalOnlyClientV2Implementation : MetalOnlyClientV2 {
    override fun getTrack(): Either<String, Track> {
        FuelManager.instance.basePath = "http://mensaupb.herokuapp.com/metalonly"
        val (_, _, result) = "/track".httpGet().responseObject(Track.Deserializer())
        val (data, error) = result

        return if (error == null) {
            Either.right(data!!)
        } else {
            Either.left(error.localizedMessage)
        }
    }

    override fun getShowInfomation(): Either<String, ShowInformation> {
        FuelManager.instance.basePath = "http://mensaupb.herokuapp.com/metalonly"

        val (_, _, result) = "/showinformation".httpGet().responseObject(ShowInformation.Deserializer())
        val (data, error) = result

        return if (error == null) {
            Either.right(data!!)
        } else {
            Either.left(error.localizedMessage)
        }
    }

    override fun getStats(): Either<String, StatsV2> {
        FuelManager.instance.basePath = "http://mensaupb.herokuapp.com/metalonly"

        val (_, _, result) = "/stats".httpGet().responseObject(StatsV2.Deserializer())
        val (data, error) = result

        return if (error == null) {
            Either.right(data!!)
        } else {
            Either.left(error.localizedMessage)
        }
    }

    override fun getPlan(): Either<String, Array<PlanEntry>> {
        FuelManager.instance.basePath = "http://mensaupb.herokuapp.com/metalonly"

        val (_, _, result) = "/plan".httpGet().responseObject(PlanEntry.ArrayDeserializer())
        val (data, error) = result

        return if (error == null) {
            Either.right(data!!)
        } else {
            Either.left(error.localizedMessage)
        }
    }

    override fun sendWish(wish: Wish): Either<String, Boolean> {
        FuelManager.instance.basePath = "http://mensaupb.herokuapp.com/metalonly"

        val(rq, response, res) = "/wish".httpPost().jsonBody(Wish.Serializer().serialize(wish)).response()

        return if (200 == response.statusCode) {
            Either.right(true)
        }else {
            Either.left(response.responseMessage)
        }
    }
}
