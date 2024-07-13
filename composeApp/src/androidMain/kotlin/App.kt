import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import housekmp.composeapp.generated.resources.Res
import housekmp.composeapp.generated.resources.compose_multiplatform
import org.example.house.HouseInfoScreenState
import org.example.house.Orientation

@Composable
@Preview
fun App(state: HouseInfoScreenState, orientation: Orientation) {
    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = orientation.toString())
            LazyColumn {
                state
                    .houses
                    .forEach {
                        item {
                            Text(text = it.toString())
                        }
                    }
            }
        }
    }
}