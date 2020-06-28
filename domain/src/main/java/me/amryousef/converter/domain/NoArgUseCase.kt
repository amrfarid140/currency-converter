package me.amryousef.converter.domain

import kotlinx.coroutines.flow.Flow

interface NoArgUseCase<OUTPUT> {
    fun execute(): Flow<UseCaseResult<OUTPUT>>
}