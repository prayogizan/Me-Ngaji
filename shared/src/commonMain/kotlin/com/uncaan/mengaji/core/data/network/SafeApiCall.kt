package com.uncaan.mengaji.core.data.network

import com.uncaan.mengaji.core.domain.model.AppResult
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException

suspend inline fun <T> safeApiCall(crossinline apiCall: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(apiCall())
    } catch (e: CancellationException) {
        throw e // Do not swallow coroutine cancellation
    } catch (e: ClientRequestException) {
        val statusCode = e.response.status.value
        val message = when (statusCode) {
            404 -> "Requested resource not found"
            400 -> "Invalid request parameters"
            401, 403 -> "Unauthorized request"
            else -> "Client error: $statusCode"
        }
        AppResult.Error(e, message)
    } catch (e: ServerResponseException) {
        AppResult.Error(e, "Server error. Please try again later.")
    } catch (e: HttpRequestTimeoutException) {
        AppResult.Error(e, "No internet connection or network timeout.")
    } catch (e: ConnectTimeoutException) {
        AppResult.Error(e, "No internet connection or network timeout.")
    } catch (e: SocketTimeoutException) {
        AppResult.Error(e, "No internet connection or network timeout.")
    } catch (e: UnresolvedAddressException) {
        AppResult.Error(e, "No internet connection or network timeout.")
    } catch (e: IOException) {
        AppResult.Error(e, "No internet connection or network timeout.")
    } catch (e: Exception) {
        val message = e.message ?: ""
        val isNetworkIssue = message.contains("getaddrinfo", ignoreCase = true) ||
                message.contains("hostname", ignoreCase = true) ||
                message.contains("eai_nodata", ignoreCase = true) ||
                message.contains("network", ignoreCase = true) ||
                message.contains("connection", ignoreCase = true) ||
                message.contains("timeout", ignoreCase = true) ||
                e::class.simpleName?.contains("UnknownHost", ignoreCase = true) == true ||
                e::class.simpleName?.contains("Timeout", ignoreCase = true) == true

        if (isNetworkIssue) {
            AppResult.Error(e, "No internet connection or network timeout.")
        } else {
            AppResult.Error(e, e.message ?: "An unexpected error occurred.")
        }
    }
}

