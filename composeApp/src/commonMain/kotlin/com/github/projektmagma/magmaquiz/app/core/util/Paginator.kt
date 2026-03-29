package com.github.projektmagma.magmaquiz.app.core.util

import com.github.projektmagma.magmaquiz.app.core.domain.NetworkError
import com.github.projektmagma.magmaquiz.shared.data.domain.abstraction.Resource

class Paginator<Key, Item>(
    private val initialKey: Key,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextKey: Key) -> Resource<Item, NetworkError>,
    private val getNextKey: suspend (currentKey: Key, result: Item) -> Key,
    private val onError: suspend (NetworkError) -> Unit,
    private val onSuccess: suspend (result: Item, newKey: Key) -> Unit,
    private val endReached: (currentKey: Key, result: Item) -> Boolean
) {

    private var currentKey = initialKey
    private var isMakingRequest = false
    private var isEndReached = false

    suspend fun loadNextItems() {
        if(isMakingRequest || isEndReached) {
            return
        }

        isMakingRequest = true
        onLoadUpdated(true)

        val result = onRequest(currentKey)
        isMakingRequest = false

        when (result){
            is Resource.Error -> { 
                onError(result.error)
                onLoadUpdated(false)
            }
            is Resource.Success -> {
                val item = result.data
                currentKey = getNextKey(currentKey, item)

                onSuccess(item, currentKey)

                onLoadUpdated(false)

                isEndReached = endReached(currentKey, item)
            }
        }
    }

    fun reset() {
        currentKey = initialKey
        isEndReached = false
    }
}
