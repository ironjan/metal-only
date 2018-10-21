package com.github.ironjan.metalonly.client_library

import arrow.core.Either
import com.github.ironjan.metalonly.client_library.model.Track
import com.github.ironjan.metalonly.client_library.model.Track.Deserializer
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.androidannotations.annotations.EBean

@EBean(scope = EBean.Scope.Singleton)
open class MetalOnlyClientV2Implementation : MetalOnlyClientV2 {
    override fun getTrack(): Either<String, Track> {
        val (_, _, result) = getAndDeserialize("/track", Deserializer())
        val (data, error) = result

        return if (error == null) {
            Either.right(data!!)
        } else {
            Either.left(error.localizedMessage)
        }
    }

    fun getAndDeserialize(path: String, deserializer: Deserializer): Triple<Request, Response, Result<Track, FuelError>> {
        FuelManager.instance.basePath = "http://mensaupb.herokuapp.com/metalonly"
        return path.httpGet().responseObject(deserializer)
    }
}
