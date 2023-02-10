import android.text.TextPaint
import android.text.style.ClickableSpan
import androidx.annotation.ColorInt

abstract class ClickableForegroundColorSpan(@ColorInt val color: Int) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.color = color
        ds.isUnderlineText = false
    }

}