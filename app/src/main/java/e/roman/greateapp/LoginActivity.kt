package e.roman.greateapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity(), FireBaseListener{

    private lateinit var btnReg : Button
    private lateinit var btnLogin : Button
    private lateinit var login : EditText
    private lateinit var password : EditText
    private lateinit var sharedPrefs : SharedPreferences
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        this.btnReg = findViewById(R.id.btn_reg)
        this.btnLogin = findViewById(R.id.btn_login)
        this.login = findViewById(R.id.login)
        this.password = findViewById(R.id.password)
        this.sharedPrefs = getSharedPreferences(getString(R.string.shared_prefs_name), Context.MODE_PRIVATE)
        this.db = FirebaseFirestore.getInstance()
    }

    override fun onResume() {
        super.onResume()

        btnReg.setOnClickListener {
            val intent = Intent(this, RegNameActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener{ checkLogin() }
    }

    private fun checkLogin(){
        val login = this.login.text.toString()
        val password = this.password.text.toString().toMD5()
        if (login.isNotEmpty() && password.isNotEmpty()) {
            db.collection(getString(R.string.field_users)).document(login).get().addOnSuccessListener { document ->
                if (document != null) {
                    if (document.data?.get(getString(R.string.field_password))?.toString()  == password) {
                        this.onSuccess(document)
                    } else {
                        this.onFailure("???????????? ???? ??????????????????")
                    }
                }

            }.addOnFailureListener { this.onFailure("????????????") }
        }
        Log.d("check22", "func finished")
    }

    override fun finish() {
        //?????????? ???????????? ???????? ?????????? ???? ???????????????? ?????????? ???????????????????? ???????????? "??????????"
    }

    override fun onSuccess(document: DocumentSnapshot?) {
        Log.d("check22", "onSuccess start")
        //val intent = Intent(this, MainScreenActivity::class.java)
        db.collection(getString(R.string.coll_path_universities)).document(document!![getString(R.string.field_university)].toString()).get().addOnSuccessListener { doc ->
            if (doc != null) {
                sharedPrefs.edit().putString(
                    getString(R.string.field_university),
                    doc!![getString(R.string.field_name)].toString()
                ).apply()
                Log.d("check22",  doc!![getString(R.string.field_name)].toString())
                sharedPrefs.edit().putBoolean(getString(R.string.field_signed), true).apply()
                sharedPrefs.edit()
                    .putString(getString(R.string.field_login), this.login.text.toString()).apply()
                sharedPrefs.edit().putString(
                    getString(R.string.first_name),
                    document!![getString(R.string.field_first_name)].toString()
                ).apply()
                sharedPrefs.edit().putString(
                    getString(R.string.field_second_name),
                    document[getString(R.string.field_second_name)].toString()
                ).apply()
                sharedPrefs.edit().putString(
                    getString(R.string.field_third_name),
                    document[getString(R.string.field_third_name)].toString()
                ).apply()
                sharedPrefs.edit().putString(
                    getString(R.string.field_birth_date),
                    document[getString(R.string.field_birth_date)].toString()
                ).apply()
                intent = Intent(this, MainScreenActivity::class.java)
                var names = ""
                val login = this.login.text.toString()
                db.collection(getString(R.string.coll_path_events)).get().addOnSuccessListener {
                    for (doc in it)
                        if (doc[getString(R.string.field_owner)].toString() != login)
                            names += "/" + doc[getString(R.string.field_name)]!!.toString()
                    val bundle = Bundle()
                    bundle.putString("names", names)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
                //startActivity(Intent(this, MainScreenActivity::class.java))
                //Log.d("check22", "start activity")
            }
        }
        /*sharedPrefs.edit().putBoolean(getString(R.string.field_signed), true).apply()
        sharedPrefs.edit().putString(getString(R.string.field_login), this.login.text.toString()).apply()
        sharedPrefs.edit().putString(getString(R.string.first_name), document!![getString(R.string.field_first_name)].toString()).apply()
        sharedPrefs.edit().putString(getString(R.string.field_second_name), document[getString(R.string.field_second_name)].toString()).apply()
        sharedPrefs.edit().putString(getString(R.string.field_third_name), document[getString(R.string.field_third_name)].toString()).apply()
        sharedPrefs.edit().putString(getString(R.string.field_birth_date), document[getString(R.string.field_birth_date)].toString()).apply()
        startActivity(intent)*/
    }

    override fun onFailure(msg : String?) {
        Log.d("check22", "onFailure start")
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        sharedPrefs.edit().putBoolean(getString(R.string.field_signed), false).apply()
    }

}