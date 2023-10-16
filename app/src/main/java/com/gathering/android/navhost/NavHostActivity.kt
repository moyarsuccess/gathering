package com.gathering.android.navhost

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.gathering.android.R
import com.gathering.android.common.isComposeEnabled
import com.gathering.android.databinding.ActNavHostBinding
import com.gathering.android.ui.theme.GatheringTheme
import com.gathering.android.ui.theme.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavHostActivity : AppCompatActivity() {

    private lateinit var binding: ActNavHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isComposeEnabled) {
            ComposeView(this).apply {
                setContent {
                    GatheringTheme {
                        Surface(
                            color = MaterialTheme.colorScheme.background
                        ) {
                            MyApp()
                        }
                    }
                }
            }
        } else {
            binding = ActNavHostBinding.inflate(layoutInflater)
            setContentView(binding.root)
            binding.navView.setupWithNavController(findNavController())
        }
    }

    private fun findNavController(): NavController {
        return findNavController(R.id.nav_host_fragment_activity_main)
    }

    @Composable
    fun MyApp() {
        val navController = rememberNavController()
        Scaffold(bottomBar = {
            MyBottomNavigation()
        }) { innerPadding ->
            NavHost(
                navController,
                startDestination = Screen.Home.route,
                Modifier.padding(innerPadding)
            ) {
                composable(Screen.MyEvent.route) {

                }
                composable(Screen.Profile.route) {

                }
            }
        }
    }

    @Composable
    fun MyBottomNavigation() {
        val screens = listOf("Home", "MyEvent", "Profile")

        val selectedIndex = rememberSaveable {
            mutableIntStateOf(0)
        }

        BottomNavigation {
            screens.forEachIndexed { index, screen ->
                BottomNavigationItem(
                    label = {
                        Text(text = screen)
                    },
                    icon = {
                        Icon(
                            imageVector = getIconForScreen(screen),
                            contentDescription = screen
                        )
                    },
                    selected = index == selectedIndex.intValue,
                    onClick = {
                        selectedIndex.intValue = index
                    },
                )
            }
        }
    }

}

@Composable
fun getIconForScreen(screen: String): ImageVector {
    return when (screen) {
        "Home" -> Icons.Filled.Home
        "MyEvent" -> Icons.Filled.Event
        "Post" -> Icons.Filled.Person
        else -> Icons.Filled.Home
    }
}

