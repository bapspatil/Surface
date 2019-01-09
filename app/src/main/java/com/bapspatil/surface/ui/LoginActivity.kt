package com.bapspatil.surface.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.EditorInfo
import com.bapspatil.surface.R
import com.bapspatil.surface.SurfaceApp
import com.bapspatil.surface.model.IWSResponse
import com.bapspatil.surface.util.IdentityTokenService
import com.layer.sdk.LayerClient
import com.layer.sdk.exceptions.LayerException
import com.layer.sdk.listeners.LayerAuthenticationListener
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), LayerAuthenticationListener {

    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SurfaceApp.layerClient?.registerAuthenticationListener(this)
        if(SurfaceApp.layerClient?.isAuthenticated!!)
            startActivity<MainActivity>()
        else {
            setContentView(R.layout.activity_login)

            btnLogin.setOnClickListener {
                if (isValidCredentials(emailEditText.text.toString(), passwordEditText.text.toString())) {
                    mProgressDialog = ProgressDialog(this)
                    if(!mProgressDialog.isShowing) mProgressDialog.show()
                    SurfaceApp.layerClient?.authenticate()
                }
            }

            passwordEditText.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnLogin.performClick()
                }
                return@setOnEditorActionListener true
            }
        }
    }

    override fun onAuthenticated(layerClient: LayerClient?, userId: String?) {
        Log.d("LAYER_AUTH", "User authenticated: $userId")
        longToast("Welcome to Surface!")

        if(mProgressDialog.isShowing) mProgressDialog.hide()
        startActivity<MainActivity>()
    }

    override fun onDeauthenticated(layerClient: LayerClient?) {
        Log.d("LAYER_DEAUTH", "User deauthorized.")
        longToast("Successfully logged out!")
    }

    override fun onAuthenticationError(layerClient: LayerClient?, e: LayerException?) {
        Log.e("LAYER_AUTH_ERROR", e.toString())
        longToast("Invalid email or password.")
    }

    override fun onAuthenticationChallenge(layerClient: LayerClient?, nonce: String?) {
        lateinit var identityToken: String

        val iwsService = IdentityTokenService.retrofit.create(IdentityTokenService::class.java)
        val iwsCall = iwsService.getIdentityToken(emailEditText.text.toString(), passwordEditText.text.toString(), nonce)
        iwsCall.enqueue(object : Callback<IWSResponse> {
            override fun onFailure(call: Call<IWSResponse>, t: Throwable) {
                // Do nothing here
            }

            override fun onResponse(call: Call<IWSResponse>, response: Response<IWSResponse>) {
                if(response.body() != null) {
                    Log.d("LAYER_IDENTITY_TOKEN", response.body()?.identityToken)
                    identityToken = response.body()?.identityToken!!
                    SurfaceApp.layerClient?.answerAuthenticationChallenge(identityToken)
                }
            }
        })

        Log.d("LAYER_NONCE", nonce)
    }

    private fun isValidCredentials(email: String, password: String) = !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)

}
