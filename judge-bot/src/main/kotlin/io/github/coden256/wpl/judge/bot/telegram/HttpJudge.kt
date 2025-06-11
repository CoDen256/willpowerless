package io.github.coden256.wpl.judge.bot.telegram

import io.github.coden256.utils.notNullOrFailure
import io.github.coden256.utils.success
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import kotlin.time.Duration


class HttpJudge(
    val client: OkHttpClient,
    val endpoint: String
) {
    fun request(): Result<String> {

        val request: Request = Request.Builder()
            .url("$endpoint/budget/")
            .post(RequestBody.create(null, ""))
            .build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return Result.failure(IllegalStateException("Requesting to create budget failed: ${response.code}, ${response.body?.string()}"))
        return response.body?.string().notNullOrFailure()
    }

    fun remaining(): Result<Duration>{
        val request: Request = Request.Builder()
            .url("$endpoint/budget/")
            .get()
            .build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            if (response.code == 400) {
                val dur = response.body?.string()?.let { Duration.parseIsoString(it) } ?: Duration.ZERO
                return Result.failure(BudgetExceededException(dur))
            }
            return Result.failure(IllegalStateException("code: ${response.code}\nbody:\n${response.body?.string()}"))
        }
        return response.body?.string()?.let { Duration.parseIsoString(it) }.notNullOrFailure()
    }

    class BudgetExceededException (val budget: Duration): Exception("Budget exceeded: $budget")
}