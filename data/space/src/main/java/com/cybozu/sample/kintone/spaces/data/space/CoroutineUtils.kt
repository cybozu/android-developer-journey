package com.cybozu.sample.kintone.spaces.data.space

import android.util.Log
import kotlinx.coroutines.CancellationException

internal suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (cancellationException: CancellationException) {
        // キャンセル例外は必ず再スローしてコルーチンの協調的キャンセルを維持
        throw cancellationException
    } catch (exception: Exception) {
        Log.w("CoroutineUtils", "suspendRunCatching でエラーが発生しました", exception)
        Result.failure(exception)
    }
