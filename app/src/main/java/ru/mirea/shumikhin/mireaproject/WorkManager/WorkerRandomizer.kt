package ru.mirea.shumikhin.mireaproject.WorkManager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import net.datafaker.Faker
import java.util.concurrent.TimeUnit

class WorkerRandomizer(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    private val faker = Faker()
    private var quote: String = ""
    override fun doWork(): Result {
        Log.d(TAG, "doWork: start")
        try {
            TimeUnit.SECONDS.sleep(10)
            quote = faker.movie().quote()
            saveQuote(applicationContext, quote)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        Log.d(TAG, "doWork: end")
        return Result.success()
    }

    private fun saveQuote(context: Context, quote: String) {
        val sharedPreferences =
            context.getSharedPreferences("my_randomizer_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(WORKER_MSG, quote)
        editor.apply()
    }

    companion object {
        const val TAG = "UploadWorker"
        const val WORKER_MSG = "WorkerMessage"
    }
}