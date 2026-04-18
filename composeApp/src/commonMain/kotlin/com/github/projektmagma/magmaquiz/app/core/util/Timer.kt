package com.github.projektmagma.magmaquiz.app.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class Timer(
    private val scope: CoroutineScope
) {
    private var job: Job? = null

    fun start(
        from: Int,
        onTick: (Int) -> Unit,
        onFinish: () -> Unit = {}
    ) {
        job?.cancel()
        job = scope.launch {
            var left = from.coerceAtLeast(0)
            onTick(left)

            while (isActive && left > 0) {
                delay(1.seconds)
                left -= 1
                onTick(left)
            }

            if (isActive) onFinish()
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
}