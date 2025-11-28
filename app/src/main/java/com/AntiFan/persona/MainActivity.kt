package com.AntiFan.persona

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.AntiFan.persona.ui.AppNavigation // <-- 确保导入 AppNavigation
import com.AntiFan.persona.ui.theme.PersonaTheme
import dagger.hilt.android.AndroidEntryPoint
import com.AntiFan.persona.data.DataInitializer
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // ✅ 注入初始化器
    @Inject
    lateinit var dataInitializer: DataInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ 触发初始化检查
        dataInitializer.initData()

        setContent {
            PersonaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}