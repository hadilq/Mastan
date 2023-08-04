/**
 * Copyright 2023 Hadi Lashkari Ghouchani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.androiddev.social.shared

import android.net.Uri
import android.text.TextUtils
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern


private val LINK_HEADER_PATTERN: Pattern =
    Pattern.compile("(?:(?:,\\s*)?<([^>]+)>|;\\s*(\\w+)=['\"](\\w+)['\"])")

fun headerLinks(httpResponse: Response<*>): Pair<Uri?, Uri?> {
    val link: String = httpResponse.headers()["Link"]!!
    var nextPageUri: Uri? = null
    var prevPageUri: Uri? = null
    if (!TextUtils.isEmpty(link)) {
        val matcher: Matcher = LINK_HEADER_PATTERN.matcher(link)
        var url: String? = null
        while (matcher.find()) {
            if (url == null) {
                val _url = matcher.group(1) ?: continue
                url = _url
            } else {
                val paramName = matcher.group(2)
                val paramValue = matcher.group(3)
                if (paramName == null || paramValue == null) return Pair(null,null)
                if ("rel" == paramName) {
                    when (paramValue) {
                        "next" -> nextPageUri = Uri.parse(url)
                        "prev" -> prevPageUri = Uri.parse(url)
                    }
                    url = null
                }
            }
        }
    }
    return prevPageUri to nextPageUri
}

class HeaderPaginationList<T> : ArrayList<T> {
    var nextPageUri: Uri? = null
    var prevPageUri: Uri? = null

    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor() : super()
    constructor(c: Collection<T>) : super(c)
}