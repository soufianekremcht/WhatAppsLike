package com.luisppinheiroj.whatappslike.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.helper.InputHelper
import com.luisppinheiroj.whatappslike.ui.main.MainActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {

    public val TAG : String = "login_activity"

    @BindView(R.id.user_login_btn)
    lateinit var loginBtn : Button

    @BindView(R.id.register_btn)
    lateinit var registerBtn :Button

    @BindView(R.id.login_forget_password_btn)
    lateinit var userForgetPasswordBtn : Button

    @BindView(R.id.login_email_field)
    lateinit var emailField : EditText

    @BindView(R.id.login_password_field)
    lateinit var passwordField : EditText




    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)
        // Initialize Firebase Auth
        auth = Firebase.auth

        loginBtn.setOnClickListener{
            if (InputHelper.isEmpty(emailField)|| InputHelper.isEmpty(passwordField)){
                Toasty.error(this,"You Should Type Your Information").show()
            }else{
                auth.signInWithEmailAndPassword(
                    emailField.text.toString(),
                    passwordField.text.toString()).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            val user = auth.currentUser
                            checkIfLogged(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toasty.normal(baseContext,
                                "Authentication failed. "+ task.exception?.message).show()
                            checkIfLogged(null)
                        }
                    }

            }

        }

        login_forget_password_btn.setOnClickListener{

        }
        registerBtn.setOnClickListener{
            goToRegisterActivity()
        }


    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        checkIfLogged(currentUser)
    }

    private fun checkIfLogged(currentUser: FirebaseUser?) {
        if (currentUser != null){
            goToMainActivity()
        }
    }

    private fun goToMainActivity() {
        val intent : Intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToRegisterActivity() {
        val intent : Intent = Intent(this,RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}