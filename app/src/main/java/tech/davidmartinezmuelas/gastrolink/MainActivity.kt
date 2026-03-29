package tech.davidmartinezmuelas.gastrolink

import android.os.Bundle
import androidx.activity.viewModels
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import tech.davidmartinezmuelas.gastrolink.ui.AppViewModel
import tech.davidmartinezmuelas.gastrolink.ui.navigation.AppNavGraph
import tech.davidmartinezmuelas.gastrolink.ui.theme.GastroLinkTheme

class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            GastroLinkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(
                        navController = navController,
                        viewModel = appViewModel
                    )
                }
            }
        }
    }
}
