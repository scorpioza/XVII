package com.twoeightnine.root.xvii.features.appearance

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.CompoundButton
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.twoeightnine.root.xvii.R
import com.twoeightnine.root.xvii.base.BaseFragment
import com.twoeightnine.root.xvii.chats.attachments.gallery.GalleryFragment
import com.twoeightnine.root.xvii.extensions.load
import com.twoeightnine.root.xvii.managers.Prefs
import com.twoeightnine.root.xvii.uikit.Munch
import com.twoeightnine.root.xvii.uikit.paint
import com.twoeightnine.root.xvii.utils.*
import com.twoeightnine.root.xvii.views.LoadingDialog
import global.msnthrp.xvii.uikit.extensions.applyBottomInsetPadding
import global.msnthrp.xvii.uikit.extensions.hide
import global.msnthrp.xvii.uikit.extensions.lowerIf
import global.msnthrp.xvii.uikit.extensions.setVisible
import kotlinx.android.synthetic.main.chat_input_panel.*
import kotlinx.android.synthetic.main.fragment_appearance.*
import kotlinx.android.synthetic.main.view_appearance_sample.*
import kotlinx.android.synthetic.main.view_appearance_sample.view.*

class AppearanceFragment : BaseFragment() {

    private lateinit var bottomSheetHelper: BottomSheetHelper
    private lateinit var permissionHelper: PermissionHelper

    private var isLightBefore = false
    private var colorBefore = 0
    private var currentColor = 0

    var dialog: LoadingDialog? = null

    override fun getLayoutId() = R.layout.fragment_appearance

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        colorBefore = Prefs.color
        currentColor = colorBefore
        initViews()
        invalidateSample()
        switchLightTheme.stylize()
        rlHideBottom.stylizeColor()
        pbAttach.hide()
        rlAttachCount.hide()

        etInput.isClickable = false
        etInput.isFocusable = false

        bottomSheetHelper = BottomSheetHelper(
                rlBottom,
                rlHideBottom,
                tvBottomTitle,
                R.id.flBottom,
                childFragmentManager,
                resources.getDimensionPixelSize(R.dimen.bottomsheet_height)
        )
        permissionHelper = PermissionHelper(this)

        svContent.applyBottomInsetPadding()
        rlBottom.applyBottomInsetPadding()
    }

    private fun invalidateSample() {
        applyColors()
        applyTexts()
        applyVisibility()
    }

    private fun applyColors() {
        val color = Munch.ColorScope.fromColor(currentColor)
        csThemeColor.color = currentColor

        arrayOf(ivMic, ivSend, ivBackSample, readStateDot).forEach { iv ->
            iv.drawable.paint(color.color)
        }

        if (switchLightTheme.isChecked) {

            rlToolbar.setBackgroundColor(Color.WHITE)
            rlSampleRoot.setBackgroundColor(Color.WHITE)
            rlInputBack.setBackgroundColor(Color.WHITE)

            arrayOf(tvTitle, tvBodyIn, tvBodyOut, etInput).forEach { it.setTextColor(0xff222222.toInt()) }
            arrayOf(tvDateIn, tvDateOut).forEach { it.setTextColor(0xff444444.toInt()) }
            tvSubtitle.setTextColor(0xff888888.toInt())

            arrayOf(ivKeyboard, ivAttach).forEach { it.paint(color.colorWhite50) }
            (llMessageIn.background as? GradientDrawable)?.setColor(color.colorWhite20)
            (llMessageOut.background as? GradientDrawable)?.setColor(color.colorWhite10)
        } else {

            rlToolbar.setBackgroundColor(0xff0e0c13.toInt())
            rlSampleRoot.setBackgroundColor(0xff0e0c13.toInt())
            rlInputBack.setBackgroundColor(0xff15121c.toInt())

            arrayOf(tvTitle, tvBodyIn, tvBodyOut, etInput).forEach { it.setTextColor(0xffdddddd.toInt()) }
            arrayOf(tvDateIn, tvDateOut).forEach { it.setTextColor(0xffaaaaaa.toInt()) }
            tvSubtitle.setTextColor(0xff555555.toInt())

            arrayOf(ivKeyboard, ivAttach).forEach { it.paint(color.colorBlack50) }
            (llMessageIn.background as? GradientDrawable)?.setColor(color.colorBlack20)
            (llMessageOut.background as? GradientDrawable)?.setColor(color.colorBlack10)
        }
    }

    private fun applyTexts() {
        val context = context ?: return

        val useAppleEmojis = switchAppleEmojis.isChecked
        val showSeconds = switchShowSeconds.isChecked
        val inLower = switchLowerTexts.isChecked

        val sampleIn = getString(R.string.appearance_sample_in)
        val sampleOut = getString(R.string.appearance_sample_out)
        val sampleDateIn = getTime(time() - 3647, withSeconds = showSeconds)
        val sampleDateOut = getTime(time() - 364, withSeconds = showSeconds)
        val sampleLastSeen = getLastSeenText(
                context.resources,
                isOnline = false,
                timeStamp = time() - 2147,
                deviceCode = 0,
                withSeconds = showSeconds
        )

        tvBodyIn.text = when {
            useAppleEmojis -> EmojiHelper.getEmojied(context, sampleIn, ignorePref = true)
            else -> sampleIn
        }
        tvBodyOut.text = when {
            useAppleEmojis -> EmojiHelper.getEmojied(context, sampleOut, ignorePref = true)
            else -> sampleOut
        }

        tvBodyIn.setTextSize(TypedValue.COMPLEX_UNIT_SP, stMessageSize.value.toFloat())
        tvBodyOut.setTextSize(TypedValue.COMPLEX_UNIT_SP, stMessageSize.value.toFloat())
        etInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, stMessageSize.value.toFloat() + 2)

        tvDateIn.text = sampleDateIn
        tvDateOut.text = sampleDateOut
        tvSubtitle.text = sampleLastSeen

        rlToolbar.tvTitle.text = getString(R.string.appearance_sample_name)
        etInput.setText(getString(R.string.appearance_sample_input))
        tvTitle.lowerIf(inLower)
        etInput.lowerIf(inLower)
    }

    private fun applyVisibility() {
        val showVoice = switchShowVoice.isChecked
        val showStickers = switchShowStickers.isChecked

        ivKeyboard.setVisible(showStickers)
        ivMic.setVisible(showVoice)
        ivSend.setVisible(!showVoice)
    }

    private fun initViews() {
        isLightBefore = Prefs.isLightTheme
        switchLightTheme.onCheckedListener = CompoundButton.OnCheckedChangeListener { _, b ->
            applyColors()
        }
        switchLightTheme.isChecked = isLightBefore
        if (Prefs.chatBack.isNotEmpty()) {
            updatePhoto(Prefs.chatBack)
        }
        btnGallery.setOnClickListener { openGallery() }
        csThemeColor.setOnClickListener {
            showColorPicker(currentColor) { color ->
                currentColor = color
                applyColors()
            }
        }

        switchChatBack.onCheckedListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            llCustomBack.setVisible(isChecked)
            if (!isChecked) {
                deletePhoto()
            }
        }
        switchChatBack.isChecked = Prefs.chatBack.isNotBlank()
        llCustomBack.setVisible(switchChatBack.isChecked)
        btnColor.setOnClickListener {
            showColorPicker(currentColor, ::convertColor)
        }

        switchShowSeconds.isChecked = Prefs.showSeconds
        switchLowerTexts.isChecked = Prefs.lowerTexts
        switchAppleEmojis.isChecked = Prefs.appleEmojis
        switchShowStickers.isChecked = Prefs.showStickers
        switchShowVoice.isChecked = Prefs.showVoice

        CompoundButton.OnCheckedChangeListener { _, _ ->
            applyTexts()
        }.apply {
            switchAppleEmojis.onCheckedListener = this
            switchLowerTexts.onCheckedListener = this
            switchShowSeconds.onCheckedListener = this
        }

        CompoundButton.OnCheckedChangeListener { _, _ ->
            applyVisibility()
        }.apply {
            switchShowStickers.onCheckedListener = this
            switchShowVoice.onCheckedListener = this
        }

        stMessageSize.value = Prefs.messageTextSize
        stMessageSize.onValueChangedListener = { applyTexts() }
    }

    private fun openGallery() {
        permissionHelper.doOrRequest(
                arrayOf(PermissionHelper.READ_STORAGE, PermissionHelper.WRITE_STORAGE),
                R.string.no_access_to_storage,
                R.string.need_access_to_storage
        ) {
            bottomSheetHelper.openBottomSheet(GalleryFragment.newInstance(onlyPhotos = true) {
                bottomSheetHelper.closeBottomSheet()
                if (it.isNotEmpty()) {
                    convertPhoto(it[0].path)
                } else {
                    showError(activity, R.string.error)
                }
            }, getString(R.string.gallery))
        }
    }

    private fun deletePhoto() {
        ivBackground.setImageBitmap(null)
        Prefs.chatBack = ""
    }

    private fun convertPhoto(path: String) {
        val activity = activity ?: return

        val newPath = getCroppedImagePath(activity, path)
        if (newPath != null) {
            Prefs.chatBack = newPath
            updatePhoto(newPath)
        } else {
            showAlert(context, getString(R.string.unable_to_crop))
        }
    }

    private fun convertColor(color: Int) {
        val activity = activity ?: return

        val newPath = createColoredBitmap(activity, color)
        if (newPath != null) {
            Prefs.chatBack = newPath
            updatePhoto(newPath)
        } else {
            showAlert(context, getString(R.string.unable_to_pick_color))
        }
    }

    private fun updatePhoto(path: String) {
        ivBackground.load("file://$path")
    }

    override fun onStop() {
        super.onStop()
        GalleryFragment.clear()

        Prefs.showSeconds = switchShowSeconds.isChecked
        Prefs.lowerTexts = switchLowerTexts.isChecked
        Prefs.appleEmojis = switchAppleEmojis.isChecked
        Prefs.showStickers = switchShowStickers.isChecked
        Prefs.showVoice = switchShowVoice.isChecked
        Prefs.messageTextSize = stMessageSize.value
    }

    /**
     * for parent activity
     */
    fun hasChanges() = isLightBefore != switchLightTheme.isChecked
            || currentColor != colorBefore

    /**
     * for parent activity
     */
    fun askForRestarting() {
        showConfirm(context, getString(R.string.wanna_change_theme)) { yes ->
            if (yes) {
                Prefs.color = currentColor
                Prefs.isLightTheme = switchLightTheme.isChecked
                restartApp(context, getString(R.string.theme_changed))
            } else {
                switchLightTheme.isChecked = isLightBefore
                currentColor = colorBefore
                activity?.onBackPressed()
            }
        }
    }

    private fun showColorPicker(initColor: Int, onPicked: (Int) -> Unit) {
        ColorPickerDialogBuilder.with(context)
                .initialColor(initColor)
                .lightnessSliderOnly()
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton(R.string.ok) { _, color, _ ->
                    onPicked(color)
                }
                .setNegativeButton(R.string.cancel, null)
                .build()
                .apply { stylize() }
                .show()
    }

    companion object {

        fun newInstance() = AppearanceFragment()
    }
}