package com.example.sqllite

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.sqllite.databinding.EditActivityBinding
import com.example.sqllite.db.MyDbManager
import com.example.sqllite.db.MyIntentConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {
    lateinit var binding: EditActivityBinding
    var id = 0
    var isEditState = false
    val imageRequestCode = 10
    var tempImageUri = "empty"
    val myDbManager = MyDbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getMyIntents()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == imageRequestCode) {
            binding.imMyImage.setImageURI(data?.data)
            tempImageUri = data?.data.toString()
            contentResolver.takePersistableUriPermission(
                data?.data!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    fun onClickAddImage(view: View) {
        binding.mainImageLayout.visibility = View.VISIBLE
        binding.fbAddImage.visibility = View.GONE
    }

    fun onClickDeleteImage(view: View) {
        binding.mainImageLayout.visibility = View.GONE
        binding.fbAddImage.visibility = View.VISIBLE
        tempImageUri = "empty"
    }

    fun onClickChooseImage(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, imageRequestCode)
    }

    fun onClickSave(view: View) {
        val myTitle = binding.edTitle.text.toString()
        val myDisc = binding.edDisc.text.toString()
        CoroutineScope(Dispatchers.Main).launch {
            if (myTitle != "" && myDisc != "") {
                if (isEditState) {
                    myDbManager.updateItem(myTitle, myDisc, tempImageUri, id, getCurrentTime())
                } else {
                    myDbManager.insertToDb(myTitle, myDisc, tempImageUri,getCurrentTime())
                }
            }
            finish()
        }
    }

    fun getMyIntents() {
        binding.fbEdit.visibility = View.GONE
        val i = intent
        if (i != null) {
            if (i.getStringExtra(MyIntentConstants.I_TITLE_KEY) != null) {
                binding.fbAddImage.visibility = View.GONE
                binding.edTitle.setText(i.getStringExtra(MyIntentConstants.I_TITLE_KEY))
                isEditState = true
                binding.edTitle.isEnabled = false
                binding.edDisc.isEnabled = false
                binding.fbEdit.visibility = View.VISIBLE
                binding.edDisc.setText(i.getStringExtra(MyIntentConstants.I_DESC_KEY))
                id = i.getIntExtra(MyIntentConstants.I_ID_KEY, 0)
                if (i.getStringExtra(MyIntentConstants.I_URI_KEY) != "empty") {
                    binding.mainImageLayout.visibility = View.VISIBLE
                    tempImageUri = i.getStringExtra(MyIntentConstants.I_URI_KEY)!!
                    binding.imMyImage.setImageURI(Uri.parse(tempImageUri))
                    binding.imButtonDeleteImage.visibility = View.GONE
                    binding.imButtonEditImage.visibility = View.GONE
                }
            }
        }
    }

    fun onEditEnable(view: View) {
        binding.edTitle.isEnabled = true
        binding.edDisc.isEnabled = true
        binding.fbEdit.visibility = View.GONE
        binding.fbAddImage.visibility = View.VISIBLE
        if(tempImageUri == "empty") return
        binding.imButtonEditImage.visibility = View.VISIBLE
        binding.imButtonDeleteImage.visibility = View.VISIBLE

    }

    private fun getCurrentTime(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        return formatter.format(time)
    }

}

