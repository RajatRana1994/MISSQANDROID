package com.msq.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.msq.R
import org.json.JSONObject
import java.util.*

enum class AuthType { FACEBOOK, GOOGLE }

class SocialAuth(
    private val context: Context,
    private val authListener: SocialAuthListener,
    private val loginButton: LoginButton,
) :
    GoogleApiClient.OnConnectionFailedListener, FacebookCallback<LoginResult> {

    interface SocialAuthListener {
        fun onFacebook(`object`: JSONObject, token: String)
        fun onGoogle(acct: GoogleSignInAccount?)
    }

    private val socialMap = hashMapOf<String, String>()
    private val RC_GOOGLE_SIGN_IN = 901
    private var googleApiClient: GoogleApiClient? = null
    private val FACEBOOK_EMAIL_SCOPE = "email"
    private val callbackManager = CallbackManager.Factory.create()

    // ...
// Initialize Firebase Auth
    companion object {
        private var googleClientId:Int=0
        private val INSTANCE: SocialAuth? = null
        public fun getInstance(
            cxt: Context,
            authListener: SocialAuthListener,
            loginButton: LoginButton,
        ): SocialAuth {
            googleClientId+=1
            return INSTANCE ?: SocialAuth(cxt, authListener, loginButton)
        }

    }

    init {
        if (INSTANCE == null) initGoogle()

        loginButton.setReadPermissions(Arrays.asList<String>(FACEBOOK_EMAIL_SCOPE))
        loginButton.registerCallback(callbackManager, this)
    }

    fun setupAuth(auth: AuthType) {
        when (auth) {
            AuthType.FACEBOOK -> {
                LoginManager.getInstance().logOut()
                loginButton.performClick()
            }
            AuthType.GOOGLE -> {
                googleAuth()
            }
        }
    }

    private fun initGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.your_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        googleApiClient = GoogleApiClient.Builder(context)
            .enableAutoManage(context as AppCompatActivity,googleClientId,  this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()


    }

    private fun googleAuth() {
        Auth.GoogleSignInApi.signOut(googleApiClient!!)
        val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient!!)
        (context as AppCompatActivity).startActivityForResult(intent, RC_GOOGLE_SIGN_IN)
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            if (result!!.isSuccess) {
                // Google Sign-In was successful, authenticate with Firebase
                val account = result!!.signInAccount
                if (account != null) {
                    authListener.onGoogle(account)
                    if (googleApiClient!!.isConnected) {
                        googleApiClient!!.clearDefaultAccountAndReconnect()
                    }
                    return
                }
            } else {
//                showErrorToast("Google sign in cancelled")
            }
//            isLoginUnderProcess = false
        }

    }


    override fun onConnectionFailed(p0: ConnectionResult) {

    }


    override fun onSuccess(result: LoginResult) {

        val obj:GraphRequest.GraphJSONObjectCallback
        val token = result!!.accessToken!!.token!!
        val request = GraphRequest.newMeRequest(result?.accessToken) { `object`, response ->
            try {
                authListener.onFacebook(`object`!!, token)
                Log.e("fbMap", socialMap.toString())
                //Check Facebook Google Id Existence
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,link,picture")
        request.parameters = parameters
        request.executeAsync();
    }

    override fun onCancel() {

    }


    override fun onError(error: FacebookException) {

    }
}