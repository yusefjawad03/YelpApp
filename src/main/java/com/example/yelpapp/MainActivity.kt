package com.example.yelpapp
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button
    private lateinit var signupBtn: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        username=findViewById(R.id.usernameText)
        password=findViewById(R.id.editTextTextPassword)
        loginBtn=findViewById(R.id.loginButton)
        signupBtn=findViewById(R.id.signupButton)
        firebaseAuth=FirebaseAuth.getInstance()

        val sharedPrefs=getSharedPreferences("savedStuff", MODE_PRIVATE)

        createNotificationChannel()

        signupBtn.setOnClickListener {
            val inputtedUsername:String=username.text.toString().trim()
            val inputtedPassword:String=password.text.toString().trim()

            val notification= NotificationCompat.Builder(this, "default")
                .setContentTitle("Welcome to Yelp")
                .setContentText("This is a cool app")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat)

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@setOnClickListener
            }
            NotificationManagerCompat.from(this).notify(0,notification)

            return@setOnClickListener
            firebaseAuth.createUserWithEmailAndPassword(inputtedUsername, inputtedPassword)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        val user=firebaseAuth.currentUser
                        Toast.makeText(this,"created user: ${user!!.email}",Toast.LENGTH_LONG).show()
                    }else{
                        val exception=it.exception
                        AlertDialog.Builder(this)
                            .setTitle("oops")
                            .setMessage("$exception")
                            .show()
                    }
                }

        }

        loginBtn.setOnClickListener {
            val inputtedUsername:String=username.text.toString()
            val inputtedPassword:String=password.text.toString()


            firebaseAuth.signInWithEmailAndPassword(inputtedUsername, inputtedPassword)
                .addOnCompleteListener{
                    if (it.isSuccessful){
                        val user=firebaseAuth.currentUser
                        Toast.makeText(this, "Logged in as ${user!!.email}",
                            Toast.LENGTH_LONG).show()
                        Log.d("Firebase", "good login")
                        sharedPrefs
                            .edit()
                            .putString("USERNAME", inputtedUsername)
                            .apply()

                        val intent=Intent(this@MainActivity,MapsActivity::class.java)
//            val intent=Intent(this@MainActivity,YelpListings::class.java)
//                        firebaseAnalytics.logEvent("LoginFail", null)
                        startActivity(intent)
                    }else{
                        val exception=it.exception
                        Toast.makeText(this, "Failed Login $exception",
                            Toast.LENGTH_LONG).show()
                        Log.d("Firebase", "Failed login $exception")
                    }
                }



        }
        username.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

        val savedUserName=sharedPrefs.getString("USERNAME", "")
        username.setText(savedUserName)




    }
    private val textWatcher: TextWatcher=object:TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputtedUsername:String=username.text.toString()
            val inputtedPassword:String=password.text.toString()

            val enableButton: Boolean=inputtedUsername.isNotBlank()&& inputtedPassword.isNotBlank()
            loginBtn.setEnabled(enableButton)
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }
    private fun createNotificationChannel(){
        val title="Yusefs Default Notification Channel"
        val description="All notifications here"
        val id="default"
        val importance=NotificationManager.IMPORTANCE_HIGH
        val notificationChannel=NotificationChannel(id, title, importance)
        notificationChannel.description=description
        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE)
        as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}