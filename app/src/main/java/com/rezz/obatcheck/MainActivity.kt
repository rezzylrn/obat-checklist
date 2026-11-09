package com.rezz.obatcheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rezz.obatcheck.data.CheckStore
import com.rezz.obatcheck.data.loadDrugs
import com.rezz.obatcheck.ui.ObatCheckTheme
import com.rezz.obatcheck.ui.ObatScreen
import com.rezz.obatcheck.ui.ObatViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categories = loadDrugs(applicationContext)
        val checkStore = CheckStore(applicationContext)
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ObatViewModel(checkStore, categories) as T
            }
        }
        setContent {
            ObatCheckTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val vm: ObatViewModel = viewModel(factory = factory)
                    ObatScreen(vm)
                }
            }
        }
    }
}
