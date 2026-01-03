package com.github.projektmagma.magmaquiz.server.data.util

import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.NetworkResource
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend inline fun ApplicationCall.respondToResource(networkResource: NetworkResource<Any>) {
    when (networkResource) {
        is NetworkResource.Error -> {
            this.respond(
                message = networkResource.errorDescription,
                status = networkResource.statusCode
            )
        }

        is NetworkResource.Success -> {
            this.respond(
                message = networkResource.data,
                status = networkResource.statusCode
            )
        }
    }
}
