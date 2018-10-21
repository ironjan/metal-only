package com.github.ironjan.metalonly.client_library

import arrow.core.Either
import com.github.ironjan.metalonly.client_library.model.ShowInformation
import com.github.ironjan.metalonly.client_library.model.StatsV2
import com.github.ironjan.metalonly.client_library.model.Track
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import org.androidannotations.annotations.EBean

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
}
