package com.github.projektmagma.magmaquiz.server.data.util

import com.github.projektmagma.magmaquiz.data.domain.Resource
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall

suspend inline fun RoutingCall.respondToResource(resource: Resource<Any, HttpStatusCode>) {
    when (resource) {
        is Resource.Error -> {
            this.respond(resource.error)
        }

        is Resource.Success -> {
            this.respond(message = resource.data, status = HttpStatusCode.OK)
        }
    }
}
