package jp.co.drip.sample.githubstatusviewer

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private val isUpdateing: MutableLiveData<Boolean> = MutableLiveData()
    private val statusText: MutableLiveData<String> = MutableLiveData()
    private val statusColorCode: MutableLiveData<Int> = MutableLiveData()
    private val lastUpdated: MutableLiveData<String> = MutableLiveData()
    private val handler = Handler()
    private val dateFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    private val apiUrl = "https://kctbh9vrtdwd.statuspage.io/api/v2/status.json"

    fun getIsUpdating(): LiveData<Boolean> {
        return isUpdateing
    }

    fun getStatusText(): LiveData<String> {
        return statusText
    }

    fun getStatusColorCode(): LiveData<Int> {
        return statusColorCode
    }

    fun getLastUpdated(): LiveData<String> {
        return lastUpdated
    }

    fun init() {
        statusText.value = "initialized"
        statusColorCode.value = R.color.darkGray
    }

    fun update() {
        isUpdateing.value = true
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(apiUrl)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                handler.post(success(response))
            }
            override fun onFailure(call: Call, e: IOException) {
                handler.post(failure())
            }
        })
    }

    private fun success(response: Response) = object : Runnable {
        override fun run () {
            response.body?.also {
                val json = JSONObject(it.string())
                val status = json.getJSONObject("status")
                statusText.value = status.getString("description")
                val colorCode = getColorCodeByIndicator(status.getString("indicator"))
                statusColorCode.value = colorCode
            }?:run {
                setFailureValues()
            }
            finishUpdate()
        }
    }

    private fun failure() = object : Runnable {
        override fun run () {
            setFailureValues()
            finishUpdate()
        }
    }

    private fun setFailureValues() {
        statusText.value = "Unknown Error"
        statusColorCode.value = R.color.red
    }

    private fun finishUpdate() {
        lastUpdated.value = dateFormatter.format(Date())
        isUpdateing.value = false
    }

    private fun getColorCodeByIndicator(indicator: String): Int {
        return when (indicator) {
            "none" -> R.color.green
            "minor" -> R.color.orange
            "major" -> R.color.red
            "critical" -> R.color.red
            else -> R.color.darkGray
        }
    }
}