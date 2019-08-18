package me.amryousef.converter.domain

interface UseCase<I, T> {
    fun execute(input: I? = null, onResult: (UseCaseResult<T>) -> Unit)
    fun cancel()
}