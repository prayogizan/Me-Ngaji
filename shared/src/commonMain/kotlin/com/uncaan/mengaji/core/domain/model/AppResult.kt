package com.uncaan.mengaji.core.domain.model

/**
 * Discriminated union representing the result of an asynchronous operation.
 *
 * Serves as the standard return type across Repositories and Use Cases to provide
 * type-safe error handling and avoid unhandled exception propagation.
 *
 * @param T The type of the success payload. Covariant to support generic variance.
 * @see map
 */
sealed interface AppResult<out T> {

    /**
     * Represents a successful outcome containing data.
     *
     * @property data The computed or retrieved payload.
     */
    data class Success<out T>(val data: T) : AppResult<T>

    /**
     * Represents an operation failure.
     *
     * @property exception The root cause exception or error.
     * @property message A human-readable error description suitable for UI presentation.
     */
    data class Error(val exception: Throwable, val message: String? = exception.message) : AppResult<Nothing>

    /**
     * Represents an in-flight operation.
     */
    data object Loading : AppResult<Nothing>
}

/**
 * Transforms the value of a [AppResult.Success] using [transform], while propagating
 * [AppResult.Error] and [AppResult.Loading] states untouched.
 *
 * @receiver The source [AppResult] to transform.
 * @param T The original payload type.
 * @param R The target payload type after transformation.
 * @param transform Transformation lambda applied exclusively to the [AppResult.Success.data].
 * @return A new [AppResult] containing the transformed value or original error/loading state.
 */
inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Error -> this
    is AppResult.Loading -> this
}
