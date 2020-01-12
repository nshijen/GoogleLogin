package com.example.googlelogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {


    lateinit var mGoogleSignInClient: GoogleSignInClient


    override fun onClick(v: View) {
        when (v.id) {
            sign_in_button.id -> signIn()
            bt_signOut.id -> signOut()
        }
    }

    private fun signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(
            this,
            OnCompleteListener { task ->
                Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show()
            })
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createGoogleSignInOption()
    }

    private fun createGoogleSignInOption() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail().build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this)
        updateUi(lastSignedInAccount)
        sign_in_button.setOnClickListener(this)
        bt_signOut.setOnClickListener(this)
    }

    private fun updateUi(lastSignedInAccount: GoogleSignInAccount?) {
        Log.d(
            TAG,
            "name:" + lastSignedInAccount?.displayName + "email:" + lastSignedInAccount?.email
        )
        tvEmailId.setText(lastSignedInAccount?.displayName.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            val account = task?.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUi(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUi(null)
        }

    }

    companion object {
        var RC_SIGN_IN: Int = 243
        var TAG = this.javaClass.name
    }
}

