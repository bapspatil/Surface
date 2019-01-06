package com.bapspatil.surface.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bapspatil.surface.R
import com.bapspatil.surface.SurfaceApp
import kotlinx.android.synthetic.main.activity_main.*

/*
** Created by Bapusaheb Patil {@link https://bapspatil.com}
*/

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnDeauth.setOnClickListener {
            SurfaceApp.layerClient?.deauthenticate()
        }
    }
}
