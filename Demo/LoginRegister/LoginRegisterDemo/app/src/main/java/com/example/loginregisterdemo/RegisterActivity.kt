package com.example.loginregisterdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import android.provider.OpenableColumns

private const val SERVER_URL = "http://192.168.1.x:3000/register"
private const val REGISTER_URL = "http://192.168.1.x:3000/register"

class RegisterActivity : AppCompatActivity() {

    private val REQUEST_IMAGE = 200
    private val REQUEST_DOC = 101

    private var selectedImageUri: Uri? = null
    private var selectedPdfUri: Uri? = null

    private lateinit var profileImageView: ImageView
    private lateinit var name: EditText
    private lateinit var surname: EditText
    private lateinit var trid: EditText
    private lateinit var phone: EditText
    private lateinit var address: EditText
    private lateinit var mail: EditText
    private lateinit var passwd: EditText
    private lateinit var passwd2: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        profileImageView = findViewById(R.id.profileImageView)
        name = findViewById(R.id.name)
        surname = findViewById(R.id.surname)
        trid = findViewById(R.id.trid)
        phone = findViewById(R.id.phone)
        address = findViewById(R.id.address)
        mail = findViewById(R.id.mail)
        passwd = findViewById(R.id.passwd)
        passwd2 = findViewById(R.id.passwd2)
        registerButton = findViewById(R.id.regButton)

        profileImageView.setOnClickListener { selectImage() }
        findViewById<Button>(R.id.docButton).setOnClickListener { selectPdf() }
        findViewById<Button>(R.id.vehicleRegButton).setOnClickListener { selectPdf() }
        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }

        registerButton.setOnClickListener {
            if (passwd.text.toString() == passwd2.text.toString()) {
                uploadFiles()
            } else {
                Toast.makeText(this, "Şifreler uyuşmuyor!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectPdf() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "PDF Seç"), REQUEST_DOC)
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE -> {
                    selectedImageUri = data?.data
                    profileImageView.setImageURI(selectedImageUri)
                }
                REQUEST_DOC -> {
                    selectedPdfUri = data?.data
                }
            }
        }
    }

    private fun uploadFiles() {
        val imageFile = selectedImageUri?.let { getFileFromUri(it) }
        val pdfFile1 = selectedPdfUri?.let { getFileFromUri(it) }
        val pdfFile2 = selectedPdfUri?.let { getFileFromUri(it) }
        val mimeType = contentResolver.getType(selectedImageUri!!)

        // Kullanıcı verilerini JSON olarak hazırlıyoruz.
        val userJson = JSONObject().apply {
            put("name", name.text.toString())
            put("surname", surname.text.toString())
            put("trid", trid.text.toString())
            put("phone", phone.text.toString())
            put("address", address.text.toString())
            put("mail", mail.text.toString())
            put("passwd", passwd.text.toString())
        }

        // MultipartBody içinde hem dosyaları hem de JSON verisini göndereceğiz.
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("user", userJson.toString())  // Kullanıcı bilgilerini ekle
            .apply {
                imageFile?.let {
                    addFormDataPart("profile_image", it.name, it.asRequestBody(mimeType?.toMediaTypeOrNull()))
                }
                pdfFile1?.let {
                    addFormDataPart("document", it.name, it.asRequestBody("application/pdf".toMediaTypeOrNull()))
                }
                pdfFile2?.let {
                    addFormDataPart("vehicleReg", it.name, it.asRequestBody("application/pdf".toMediaTypeOrNull()))
                }
            }
            .build()

        // POST isteği gönderiliyor
        val request = Request.Builder().url(SERVER_URL).post(requestBody).build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Dosya yüklenemedi.", Toast.LENGTH_SHORT).show()
                    Log.e("Upload", "Dosya yükleme hatası: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("Upload", "Dosya ve kullanıcı bilgileri başarıyla yüklendi: ${response.body?.string()}")
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }
                } else {
                    Log.e("Upload", "Dosya ve kullanıcı yükleme hatası: ${response.code}")
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Yükleme hatası.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    private fun getFileFromUri(uri: Uri): File? {
        val fileName = getFileName(uri) ?: return null
        val file = File(cacheDir, fileName)
        contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }
        }
        return file
    }

    private fun getFileName(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) it.getString(index) else null
            } else null
        }
    }

    private fun registerUser() {
        val requestBody = JSONObject().apply {
            put("name", name.text.toString())
            put("surname", surname.text.toString())
            put("trid", trid.text.toString())
            put("phone", phone.text.toString())
            put("address", address.text.toString())
            put("mail", mail.text.toString())
            put("passwd", passwd.text.toString())
        }.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder().url(REGISTER_URL).post(requestBody).build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Register", "Hata: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("Register", "Kayıt başarılı: ${response.body?.string()}")
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Kayıt başarılı!", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }
                }
                else {
                        Log.e("Register", "Kayıt hatası: ${response.code}")
                    }
                }
            })
    }
}
