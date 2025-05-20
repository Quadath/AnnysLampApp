import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun BrightnessSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..255f
) {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp
    val targetHeight = screenHeightDp * 0.8f

    Box(
        modifier = modifier
            .height(targetHeight)
            .width(30.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val newValue = (1f - offset.y / size.height) * (valueRange.endInclusive - valueRange.start)
                    onValueChange(newValue.coerceIn(valueRange.start, valueRange.endInclusive))
                }
            }
    ) {
        val percent = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(percent)
                .align(Alignment.BottomCenter)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
        )
    }
}