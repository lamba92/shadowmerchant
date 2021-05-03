package com.github.lamba92.shadowmerchant.api

import com.github.lamba92.shadowmerchant.Logger
import com.github.lamba92.shadowmerchant.awaitWithoutLock
import com.github.lamba92.shadowmerchant.data.BuyableItem
import com.github.lamba92.shadowmerchant.data.LoginData
import com.github.lamba92.shadowmerchant.data.Store
import com.github.lamba92.shadowmerchant.launchLoop
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.sync.Mutex
import kotlin.random.Random
import kotlin.time.DurationUnit.MILLISECONDS
import kotlin.time.toDuration

class ShadowMerchantBot(
    private val browser: Browser,
) {

    private val logger = Logger("ShadowMerchantBot")

    sealed class ItemCheckResult {
        object NotAvailable : ItemCheckResult()
        data class ItemFound(val activePage: Page) : ItemCheckResult()
    }

    companion object {
        suspend fun create(
            headless: Boolean = false,
            viewPort: ViewPortSize? = null,
        ) = ShadowMerchantBot(Browser.launch(headless, viewPort))

        suspend operator fun invoke(
            headless: Boolean = false,
            viewPort: ViewPortSize? = null,
        ) = create(headless, viewPort)
    }

    private data class BuyingTask(
        val store: Store,
        val item: BuyableItem,
    )

    enum class PurchaseAttempt {
        SUCCESSFUL, FAILED
    }

    fun CoroutineScope.buyItemsFrom(stores: Collection<Store>, pagesCount: Int) = launch {
        val pageQueue = Channel<Page>(pagesCount)
        val refreshTaskQueue = Channel<BuyingTask>(UNLIMITED)
        val purchaseTaskQueue = Channel<Pair<BuyingTask, Page>>(RENDEZVOUS)
        val queuesMutex = Mutex()

        // open pages and add them to the pageQueue
        repeat(pagesCount) {
            pageQueue.send(browser.newPage())
        }

        // populate refreshTasksQueue
        stores.flatMap { store -> store.buyableItems.map { BuyingTask(store, it) } }
            .forEach { refreshTaskQueue.send(it) }

        // throttle refreshes to n per second
        val refreshClock = produce {
            while (isActive) {
                send(Unit)
                delay(100.toDuration(MILLISECONDS))
            }
        }

        // login to stores
        coroutineScope {
            for (store in stores) {
                launch {
                    val page = pageQueue.receive()
                    page.loginStore(store)
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
                if (page.checkAvailability(task))
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
                // TODO actually try to buy item
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

    private suspend fun Page.loginStore(store: Store) {
        logger.info("Initiating login to ${store.name}")
        navigateTo(store.loginData.loginLink)
        type(store.loginData.usernameInputSelector, store.loginData.username)
        val loginData = store.loginData
        if (loginData is LoginData.DoubleStepLogin)
            click(loginData.firstStepButtonSelector, true)
        type(loginData.passwordInputSelector, store.loginData.password)
        loginData.stayConnectedCheckboxSelector?.let { click(it) }
        click(loginData.loginButtonSelector, true)
        logger.info("Logged into ${store.name}")
    }

    private suspend fun Page.checkAvailability(task: BuyingTask): Boolean {
        val selectors = buildList {
            task.item.customAddToCartFlow?.let { add(it.selectors.first().selector) }
            task.item.customBuyoutFlow?.let { add(it.selectors.first().selector) }
            add(task.store.addToCartFlow.selectors.first().selector)
            add(task.store.buyoutFlow.selectors.first().selector)
        }
        return selectors.any { isSelectorVisible(it) }
    }

    private suspend fun Page.addItemToCart(task: BuyingTask): Boolean {
        val flow = task.item.customAddToCartFlow ?: task.store.addToCartFlow
        for ((index, selectorWithWaitTime) in flow.selectors.withIndex()) {
            val (selector, waitTime) = selectorWithWaitTime
            waitForSelector(selector)
            click(selector, flow.selectors.lastIndex == index)
            if (flow.selectors.lastIndex != index)
                delay(waitTime)
        }
        TODO("Check if added to cart")
    }
}
