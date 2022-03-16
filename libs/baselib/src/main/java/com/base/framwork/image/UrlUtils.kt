package com.base.framwork.image

class UrlUtils {

    companion object {
        /**
         * 删除url多余的斜杠
         */
        @JvmStatic
        fun removeExtraSlashOfUrl(url: String): String {
            if (url.isEmpty()) {
                return url
            }
            return url.replace("(?<!(http:|https:))/+".toRegex(), "/")
        }

        /**
         * 检查url是完整的url，是否域名和路径拼接的时候丢了斜杠
         * @param url 完整的路径，
         * @param host 需要校验的host
         */
        @JvmStatic
        fun checkUrl(url: String, host: String): String {
            // 先去掉host斜杠
            var host = host
            var url = url
            if (host.endsWith("/")) {
                host = host.substring(0, host.length - 1)
            }
            if (url.startsWith(host)) {
                val path = url.substringAfter(host)
                if (!host.endsWith("/") && !path.startsWith("/")) {
                    url = host.plus("/").plus(path)

                }
            }
            // 删除多余的斜杠
            return removeExtraSlashOfUrl(url)
        }

        /**
         * 拼接 host 和 path, 处理斜杠过多或者丢失问题问题
         * @param host
         * @param path
         * @return
         */
        @JvmStatic
        fun appendPath(host: String, path: String): String? {
            var url: String = host + path
            // 校验url 的合法性
            return checkUrl(url,host)
        }

    }
}


