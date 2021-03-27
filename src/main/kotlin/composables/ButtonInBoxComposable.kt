package composables

import LeftViewType
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonConstants
import androidx.compose.material.Colors
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp

@Composable
fun buttonInBox(
    text: String,
    colors: Colors,
    onChange: (type: LeftViewType) -> Unit
) {
    val active = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .background(color = Color.Transparent)
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = { onChange(LeftViewType.FUNCTION) },
            shape = RoundedCornerShape(50),
            border = BorderStroke(2.dp, colors.secondaryVariant),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .pointerMoveFilter(
                    onEnter = {
                        active.value = true
                        false
                    },
                    onExit = {
                        active.value = false
                        false
                    }),
            colors = ButtonConstants.defaultButtonColors(
                backgroundColor = if (!active.value) colors.secondary else colors.primaryVariant
            )
        ) {
            Text(
                text = text,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                maxLines = 1
            )
        }
    }
}