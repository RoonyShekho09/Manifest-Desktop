package com.jawharat.manifest.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<S, E : Any>(
    initState: S,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    val state: StateFlow<S>
        field = MutableStateFlow(initState)

    private val _event = MutableSharedFlow<E?>()
    open val event = _event.asSharedFlow()

    protected fun <T> tryToExecute(
        block: suspend () -> T,
        onStart: suspend () -> Unit = {},
        onSuccess: suspend (T) -> Unit = {},
        onError: suspend (Throwable) -> Unit = {},
        onCompleted: suspend () -> Unit = {},
        checkSuccess: suspend (T) -> Boolean = { true },
        context: CoroutineContext = ioDispatcher,
        inScope: CoroutineScope = viewModelScope,
    ): Job {
        return inScope.launch(context) {
            runCatching {
                onStart()
                block()
            }.onSuccess { response ->
                if (checkSuccess(response)) {
                    onSuccess(response)
                    return@onSuccess
                }
            }
                .onFailure {
                    println("OnFailure $it")
                    mapExceptionToErrorState(
                        throwable = it,
                        onError = { onError(it) },
                    )
                }
            onCompleted()
        }
    }

    protected fun <T> tryToCollect(
        block: suspend () -> Flow<T>,
        context: CoroutineContext = ioDispatcher,
        onStart: () -> Unit = {},
        onNewValue: suspend (T) -> Unit = {},
        onError: () -> Unit = {},
        onCompleted: suspend () -> Unit = {},
        latest: Boolean = false,
        takeWhile: (T) -> Boolean = { true },
        inScope: CoroutineScope = viewModelScope,
        flowScope: Flow<T>.() -> Flow<T> = { this },
    ): Job = inScope
        .catch { mapExceptionToErrorState(throwable = it, onError = onError) }
        .launch(context) {
            runCatching {
                onStart()
                val baseFlow = block()
                    .run(flowScope)
                    .takeWhile(takeWhile)
                    .distinctUntilChanged()
                    .catch { mapExceptionToErrorState(throwable = it, onError = onError) }

                if (latest)
                    baseFlow.collectLatest(onNewValue)
                else
                    baseFlow.collect(onNewValue)
            }
                .onFailure { mapExceptionToErrorState(throwable = it, onError = onError) }
            onCompleted()
        }

    protected fun CoroutineScope.catch(onFailure: suspend (Throwable) -> Unit = {}) =
        this + CoroutineExceptionHandler { _, exception -> launchCatching { onFailure(exception) } }

    protected fun <R> ViewModel.launchCatching(
        context: CoroutineContext = ioDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        onSuccess: (value: R) -> Unit = {},
        onFailure: () -> Unit = {},
        onCompleted: () -> Unit = {},
        block: suspend CoroutineScope.() -> R,
    ): Job = viewModelScope
        .catch { mapExceptionToErrorState(throwable = it, onError = onFailure) }
        .launch(context = context, start = start) {
            runCatching { block() }
                .onFailure { println(it.toString()) }
                .onFailure { mapExceptionToErrorState(throwable = it, onError = onFailure) }
                .onSuccess(onSuccess)
            onCompleted()
        }

    private suspend fun mapExceptionToErrorState(
        throwable: Throwable,
        onError: suspend () -> Unit,
    ) {
        when (throwable) {
            else -> {}
        }.also { errorState ->
            println(errorState.toString())
        }.let { onError() }
    }

    protected fun updateState(notifyEvent: E? = null, updater: S.() -> S) {
        state.update(updater)
        emitEvent(notifyEvent ?: return)
    }

    protected fun emitEvent(newEvent: E) {
        viewModelScope.launch(ioDispatcher) {
            _event.emit(newEvent)
        }
    }
}