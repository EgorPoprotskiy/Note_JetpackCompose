package com.egorpoprotskiy.note

import android.app.Application
import com.egorpoprotskiy.note.data.AppContainer
import com.egorpoprotskiy.note.data.AppDataContainer

//3.6
class NoteApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}