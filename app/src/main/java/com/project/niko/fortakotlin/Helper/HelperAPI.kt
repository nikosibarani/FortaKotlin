package com.project.niko.fortakotlin.Helper

import android.content.Context
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import org.apache.http.entity.StringEntity

class HelperAPI {
    companion object {
        private val BASE_URL = "https://developers.zomato.com/api/v2.1/"

        //private val USER_KEY = "7f9b7427028eeef040d8a466f7f10417"

        //private val USER_KEY = "86f40ebf4aadc1172d88dca9d32a9600"

        private val USER_KEY = "062f9db2881b1f36d4acb6b345844374"

        private val client = AsyncHttpClient()

        fun get(url: String, params: RequestParams?, responseHandler: AsyncHttpResponseHandler?) {
            client.setEnableRedirects(true, true, true)
            client.setTimeout(100000)
            client.addHeader("user-key", USER_KEY)
            client.get(getAbsoluteUrl(url), params, responseHandler)
        }

        fun post(context: Context, url: String, entity: StringEntity, responseHandler: AsyncHttpResponseHandler) {
            client.setTimeout(100000)
            client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler)
        }

        private fun getAbsoluteUrl(relativeUrl: String): String {
            return BASE_URL + relativeUrl
        }
    }
}
