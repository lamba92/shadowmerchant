package com.github.lamba92.shadowmerchant

import NodeJS.get
import com.github.lamba92.shadowmerchant.api.ShadowMerchantBot
import process

suspend fun main() {
    val bot = ShadowMerchantBot()

    bot.loginStores(listOf(Stores.amazonItaly("basti.lamberto@gmail.com", process.env["AMAZON_PWD"]!!)))
    
}
