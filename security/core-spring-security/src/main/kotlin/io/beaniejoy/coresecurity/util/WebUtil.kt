package io.beaniejoy.coresecurity.util

import javax.servlet.http.HttpServletRequest

class WebUtil {
    companion object {
        const val XML_HTTP_REQUEST = "XMLHttpRequest"
        const val X_REQUESTED_WITH = "X-Requested-With"

        const val CONTENT_TYPE = "Content-type"
        const val CONTENT_TYPE_JSON = "application/json"

        fun isAjax(request: HttpServletRequest): Boolean {
            return XML_HTTP_REQUEST == request.getHeader(X_REQUESTED_WITH)
        }

        fun isContentTypeJson(request: HttpServletRequest): Boolean {
            return request.getHeader(CONTENT_TYPE).contains(CONTENT_TYPE_JSON)
        }
    }
}