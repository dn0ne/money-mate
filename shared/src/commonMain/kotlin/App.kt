import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.app.data.DatabaseModule
import com.dn0ne.moneymate.app.domain.Theme
import com.dn0ne.moneymate.app.presentation.SpendingListEvent
import com.dn0ne.moneymate.app.presentation.SpendingListScreen
import com.dn0ne.moneymate.app.presentation.SpendingListViewModel
import com.dn0ne.moneymate.core.presentation.MoneyMateTheme
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun App(
    darkTheme: Boolean,
    dynamicColor: Boolean
) {
    val viewModel = getViewModel(
        key = "spending-list-screen",
        factory = viewModelFactory {
            SpendingListViewModel(DatabaseModule.dataSource)
        }
    )
    val state by viewModel.state.collectAsState()

    val useDarkTheme =
        when (state.settings.theme) {
            Theme.LIGHT -> false
            Theme.DARK -> true
            Theme.SYSTEM -> darkTheme
        }


    val useDynamicColor =
        if (!dynamicColor) {
            false
        } else {
            state.settings.dynamicColor ?: run {
                viewModel.onEvent(
                    SpendingListEvent.OnDynamicColorChanged(true)
                )
                true
            }
        }


    MoneyMateTheme(
        darkTheme = useDarkTheme,
        dynamicColor = useDynamicColor
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            SpendingListScreen(
                state = state,
                newSpending = viewModel.newSpending,
                newCategory = viewModel.newCategory,
                onEvent = viewModel::onEvent,
            )

            val screenCoverColor by remember {
                mutableStateOf(
                    if (darkTheme) Color.Black else Color.White
                )
            }
            val screenCoverSize by remember {
                mutableStateOf(3000f)
            }
            val strokeWidth by animateFloatAsState(
                targetValue = if (!state.isDataLoaded) {
                    screenCoverSize
                } else 0f,
                animationSpec = tween(durationMillis = 200)
            )
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(100.dp)
            ) {
                drawArc(
                    color = screenCoverColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(
                        x = center.x - screenCoverSize / 2,
                        y = center.y - screenCoverSize / 2
                    ),
                    size = Size(width = screenCoverSize, height = screenCoverSize),
                    style = Stroke(
                        width = strokeWidth
                    )
                )
            }
        }
    }


}