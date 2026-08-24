package com.uncaan.mengaji

import androidx.compose.ui.window.ComposeUIViewController
import com.uncaan.mengaji.core.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}