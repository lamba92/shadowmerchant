package com.github.lamba92.shadowmerchant.api

import com.github.lamba92.shadowmerchant.Logger
import com.github.lamba92.shadowmerchant.awaitWithoutLock
import com.github.lamba92.shadowmerchant.core.Store
import com.github.lamba92.shadowmerchant.core.specializedProcessors
import com.github.lamba92.shadowmerchant.data.BuyableItem
import com.github.lamba92.shadowmerchant.launchLoop
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.sync.Mutex
import kotlin.random.Random
import kotlin.time.DurationUnit.MILLISECONDS
import kotlin.time.toDuration

object ShadowMerchantBot {

    private val logger = Logger("ShadowMerchantBot")

    data class BuyingTask(
        val store: Store,
        val item: BuyableItem,
    )

    fun CoroutineScope.buyItemsFrom(stores: Collection<Store>, browser: Browser, pagesCount: Int = 10) =
        launch {
            val pageQueue = Channel<Page>(capacity = pagesCount)
            val refreshTaskQueue = Channel<BuyingTask>(capacity = pagesCount * 2)
            val purchaseTaskQueue = Channel<Pair<BuyingTask, Page>>(capacity = RENDEZVOUS)
            val queuesMutex = Mutex()

            // open pages and add them to the pageQueue
            repeat(pagesCount) {
                pageQueue.send(browser.newPage())
            }

            launch {
                // populate refreshTasksQueue
                stores.flatMap { store -> store.buyableItems.map { BuyingTask(store, it) } }
                    .forEach { refreshTaskQueue.send(it) }
            }

            // throttle refreshes to n per second
            val refreshClock = produce {
                while (isActive) {
                    send(Unit)
                    delay(10.toDuration(MILLISECONDS))
                }
            }

            // login to stores
            coroutineScope {
                for (store in stores) {
                    launch {
                        require(specializedProcessors[store::class]!=null
                        ) { logger.error("Store processor implementation for ${store.name} not found!")}
                        val dedicatedProcessor= specializedProcessors[store::class]!!
                        val page = pageQueue.receive()
                        dedicatedProcessor.loginStore(store, page)
                        pageQueue.send(page)
                    }
                }
            }

            // refreshes of pages
            launchLoop {
                queuesMutex.awaitWithoutLock() // check if an purchase attempt is en course and wait for it to finish
                val task = refreshTaskQueue.receive() // get a task
                val page = pageQueue.receive() // get a page to use

                // start the refresh job
                launch {
                    refreshClock.receive() // throttle refreshes by waiting on a common event producer

                    require(specializedProcessors[task.store::class]!=null
                    ) { logger.error("Store processor implementation for ${task.store.name} not found!")}
                    val dedicatedProcessor= specializedProcessors[task.store::class]!!

                    if (dedicatedProcessor.checkAvailability(task.store,task.item, page))
                        purchaseTaskQueue.send(task to page) // send to other actor the task and the page loaded
                    else {
                        pageQueue.send(page) // release the page back in queue
                        refreshTaskQueue.send(task) // reschedule the task
                    }
                }
            }

            // actually tries to buy
            launchLoop {
                for ((task, page) in purchaseTaskQueue) {
                    launch { queuesMutex.lock() } // lock refresh queue as soon as possible but non blockingly

                    require(specializedProcessors[task.store::class]!=null
                    ) { logger.error("Store processor implementation for ${task.store.name} not found!")}
                    val dedicatedProcessor= specializedProcessors[task.store::class]!!


                    dedicatedProcessor.buyItem(task.store,page,)


                    val isPurchaseSuccessful = Random.nextBoolean()
                    if (isPurchaseSuccessful)
                        TODO("Notify and do some programmatic stuff with $task")
                    else {
                        queuesMutex.unlock()
                        pageQueue.send(page)
                        refreshTaskQueue.send(task)
                    }
                }
            }
        }

}
