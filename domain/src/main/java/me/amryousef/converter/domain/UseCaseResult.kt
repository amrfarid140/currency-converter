package me.amryousef.converter.domain

sealed class UseCaseResult<T> {
    data class Success<T>(val data: T): UseCaseResult<T>()
    data class Error<T>(val throwable: Throwable): UseCaseResult<T>()
}