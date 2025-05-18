package com.example.tributaria.features.calendar.util

import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> ListenableFuture<T>.await(): T =
    suspendCancellableCoroutine { cont ->
        val executor: Executor = Executors.newSingleThreadExecutor()
        this.addListener({
            try {
                cont.resume(this.get())
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }, executor)
    }
