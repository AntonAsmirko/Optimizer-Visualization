package composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun textCentred(
    modifier: Modifier,
    message: String
) {
    Column(modifier) {
        Text(
            modifier = Modifier
                .padding(30.dp),
            text = message
        )
    }
}