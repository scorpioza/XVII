package com.twoeightnine.root.xvii.scheduled.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twoeightnine.root.xvii.App
import com.twoeightnine.root.xvii.db.AppDb
import com.twoeightnine.root.xvii.scheduled.core.ScheduledMessage
import com.twoeightnine.root.xvii.scheduled.core.SendMessageWorker
import com.twoeightnine.root.xvii.utils.applySingleSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ScheduledMessagesViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val scheduledMessagesLiveData = MutableLiveData<List<ScheduledMessage>>()
    private val peersMapLiveData = MutableLiveData<Map<Int, String>>()

    val scheduledMessages: LiveData<List<ScheduledMessage>>
        get() = scheduledMessagesLiveData

    val peersMap: LiveData<Map<Int, String>>
        get() = peersMapLiveData

    @Inject
    lateinit var appDb: AppDb

    init {
        App.appComponent?.inject(this)
    }

    fun loadScheduledMessages() {
        appDb.scheduledMessagesDao()
                .getActualScheduledMessages()
                .compose(applySingleSchedulers())
                .subscribe({ scheduledMessages ->
                    val peerIds = scheduledMessages.map { it.peerId }.distinct()
                    loadPeers(peerIds) { peersMap ->
                        peersMapLiveData.value = peersMap
                        scheduledMessagesLiveData.value = scheduledMessages
                    }
                }, { throwable ->
                    throwable.printStackTrace()
                })
                .addToDisposables()
    }

    fun cancelScheduledMessage(context: Context, scheduledMessage: ScheduledMessage) {
        SendMessageWorker.cancelWorker(context, scheduledMessage.id)
        appDb.scheduledMessagesDao()
                .deleteScheduledMessage(scheduledMessage)
                .compose(applySingleSchedulers())
                .subscribe({
                    loadScheduledMessages()
                }, { throwable ->
                    throwable.printStackTrace()
                })
                .addToDisposables()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    private fun loadPeers(peerIds: List<Int>, onSuccess: (Map<Int, String>) -> Unit) {
        appDb.dialogsDao()
                .getDialogsByPeerIds(peerIds)
                .map { dialogs -> dialogs.map { dialog -> dialog.peerId to (dialog.alias ?: dialog.title) } }
                .map { pairList -> pairList.toMap() }
                .compose(applySingleSchedulers())
                .subscribe(onSuccess, { throwable ->
                    throwable.printStackTrace()
                })
                .addToDisposables()
    }

    private fun Disposable.addToDisposables() {
        compositeDisposable.add(this)
    }
}