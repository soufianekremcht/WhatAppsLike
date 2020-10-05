package com.luisppinheiroj.whatappslike.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.helper.FirebaseConstants.USERS_PATH
import com.luisppinheiroj.whatappslike.helper.InputHelper
import com.luisppinheiroj.whatappslike.ui.base.BaseActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : BaseActivity(){


    public val TAG : String = "login_activity"

    @BindView(R.id.user_register_btn)
    lateinit var registerBtn : Button

    @BindView(R.id.register_login_btn)
    lateinit var loginBtn : Button

    @BindView(R.id.register_name_field)
    lateinit var nameField : EditText

    @BindView(R.id.register_email_field)
    lateinit var emailField : EditText

    @BindView(R.id.register_password_field)
    lateinit var passwordField : EditText

    @BindView(R.id.register_confirm_password_field)
    lateinit var confirmPasswordField : EditText



    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        ButterKnife.bind(this)
        // Initialize Firebase Auth
        auth = Firebase.auth

        user_register_btn.setOnClickListener {

            if (InputHelper.isEmpty(emailField) || InputHelper.isEmpty(nameField)) {
                Toasty.error(this, getString(R.string.empty_field_msg)).show()
            } else if (InputHelper.isEmpty(passwordField)) {
                Toasty.error(this, getString(R.string.password_not_valid_msg)).show()
            } else if (passwordField.text.toString() != confirmPasswordField.text.toString()){
                //Toasty.error(this, "The Two passwords are not The same").show()
                confirmPasswordField.error =getString(R.string.error_different_passwords_msg)
            }else{
                val email = emailField.text.toString()
                val password = passwordField.text.toString()
                val newUsername =  nameField.text.toString()

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val userUid = auth.currentUser!!.uid
                            val currentUserDb = FirebaseDatabase.getInstance()
                                .getReference(USERS_PATH)
                                .child(userUid)
                            currentUserDb.child("name").setValue(newUsername)
                            currentUserDb.child("email").setValue(email)

                            goToLoginActivity()
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            showMessage("Registration failed. "+ task.exception?.message)
                        }
                    }
            }

        }

        loginBtn.setOnClickListener {
            goToLoginActivity()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }


    private fun goToLoginActivity() {
        val intent : Intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private fun saveNewUserInfo(){
        val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val mUserDB = FirebaseDatabase.getInstance().reference.child("user").child(user.uid)

            mUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                  /*  if (!dataSnapshot.exists()) {
                        val userMap: MutableMap<String, Any> = HashMap()
                        userMap["email"] = user.email
                        userMap["name"] = user.displayName

                        mUserDB.updateChildren(userMap)
                    }*/
                    goToLoginActivity()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }
}