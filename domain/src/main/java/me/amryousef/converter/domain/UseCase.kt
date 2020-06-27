package me.amryousef.converter.domain

import kotlinx.coroutines.flow.Flow

interface UseCase<I, T> {
    fun execute(input: I? = null): Flow<UseCaseResult<T>>
}