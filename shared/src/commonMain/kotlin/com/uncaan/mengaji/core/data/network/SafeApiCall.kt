package com.uncaan.mengaji.core.data.network

import com.uncaan.mengaji.core.domain.model.AppResult
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
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
    } catch (e: IOException) {
        AppResult.Error(e, "No internet connection or network timeout.")
    } catch (e: Exception) {
        AppResult.Error(e, e.message ?: "An unexpected error occurred.")
    }
}
