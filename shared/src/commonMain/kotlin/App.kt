
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.dn0ne.moneymate.app.data.DatabaseModule
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
    MoneyMateTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor
    ) {
        val viewModel = getViewModel(
            key = "spending-list-screen",
            factory = viewModelFactory {
                SpendingListViewModel(DatabaseModule.dataSource)
            }
        )
        val state by viewModel.state.collectAsState()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        )
        {
            SpendingListScreen(
                state = state,
                newSpending = viewModel.newSpending,
                onEvent = viewModel::onEvent
            )
        }
    }
}