package com.luisppinheiroj.whatappslike.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.luisppinheiroj.whatappslike.R
import com.luisppinheiroj.whatappslike.ui.authentication.LoginActivity
import com.luisppinheiroj.whatappslike.ui.base.BaseActivity
import com.luisppinheiroj.whatappslike.ui.chats.ChatsFragment
import com.luisppinheiroj.whatappslike.ui.new_chat.CreateChatActivity


class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener{
    @BindView(R.id.main_toolbar)
    lateinit var mainToolbar : Toolbar


    @BindView(R.id.new_chat_fab)
    lateinit var createNewChatFab : FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        setSupportActionBar(mainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        createNewChatFab.setOnClickListener{
            showNewChatActivity()
        }
        if (savedInstanceState == null)
            showChatsFragment()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.bottom_nav_chats -> showChatsFragment()
            R.id.bottom_nav_search -> showChatsFragment()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_main_logout -> {
                userLogout()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun userLogout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()

    }

    private fun showNewChatActivity(){
        val intent : Intent = Intent(this,CreateChatActivity::class.java)
        startActivity(intent)
    }

    private fun showChatsFragment(){
        loadFragment(ChatsFragment.newInstance())
    }


    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        //transaction.addToBackStack(null)
        transaction.commit()
    }



    private fun getCurrentUserInfo(){
        val user = Firebase.auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid
        }
    }

    private fun updateUserInfo(){
        val user = Firebase.auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = "Jane Q. User"
            photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("MainActivity", "User profile updated.")
                }
            }
    }


}