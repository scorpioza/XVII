package com.twoeightnine.root.xvii.chats.attachments.attachments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.base.BaseFragment
import com.twoeightnine.root.xvii.chats.attachments.audios.AudioAttachmentsFragment
import com.twoeightnine.root.xvii.chats.attachments.docs.DocAttachmentsFragment
import com.twoeightnine.root.xvii.chats.attachments.links.LinkAttachmentsFragment
import com.twoeightnine.root.xvii.chats.attachments.photos.PhotoAttachmentsFragment
import com.twoeightnine.root.xvii.chats.attachments.videos.VideoAttachmentsFragment
import global.msnthrp.xvii.uikit.base.adapters.BasePagerAdapter
import kotlinx.android.synthetic.main.fragment_attachments_history.*

class AttachmentsFragment : BaseFragment() {

    private val adapter by lazy {
        BasePagerAdapter(childFragmentManager)
    }

    private val peerId by lazy { arguments?.getInt(ARG_PEER_ID) ?: 0 }

    override fun getLayoutId() = R.layout.fragment_attachments_history

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
        with(adapter) {
            add(PhotoAttachmentsFragment.newInstance(peerId), getString(R.string.photos))
            add(AudioAttachmentsFragment.newInstance(peerId), getString(R.string.audios))
            add(VideoAttachmentsFragment.newInstance(peerId), getString(R.string.videos))
            add(LinkAttachmentsFragment.newInstance(peerId), getString(R.string.links))
            add(DocAttachmentsFragment.newInstance(peerId), getString(R.string.docs))
        }
        viewPager.adapter = adapter
        xviiToolbar.setupWith(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    companion object {
        private const val ARG_PEER_ID = "peerId"

        fun createArgs(peerId: Int) = Bundle().apply {
            putInt(ARG_PEER_ID, peerId)
        }
    }

}