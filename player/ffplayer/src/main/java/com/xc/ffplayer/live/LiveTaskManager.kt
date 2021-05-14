package com.xc.ffplayer.live

import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object LiveTaskManager {

    @JvmStatic
    private var CPU_COUNT = Runtime.getRuntime().availableProcessors()
    @JvmStatic
    private var CORE_POOL_SIZE = Math.max(2,Math.min(CPU_COUNT-1,4))
    @JvmStatic
    private var MAXIMUM_POOL_SIZE = CPU_COUNT * 2 - 1
    @JvmStatic
    private var KEEP_ALIVE_SECONS = 30L
    @JvmStatic
    private var workQueue = LinkedBlockingDeque<Runnable>(5)

    @JvmStatic
    private var poolExecutor = ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,KEEP_ALIVE_SECONS,TimeUnit.SECONDS, workQueue)

    init {
        poolExecutor.allowCoreThreadTimeOut(true)
    }

    fun execute(runnable: Runnable) {
        poolExecutor.execute(runnable)
    }

}