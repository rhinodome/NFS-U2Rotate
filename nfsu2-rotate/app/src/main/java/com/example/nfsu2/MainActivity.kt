package com.example.nfsu2

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.io.File

/**
 * Launcher with a built-in file browser. Pick your NFSU2 ISO once; after that
 * "Play" boots Dolphin straight into it.
 */
class MainActivity : Activity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var info: TextView
    private lateinit var play: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("nfsu2", MODE_PRIVATE)

        val pad = (24 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(pad, pad, pad, pad)
            setBackgroundColor(Color.BLACK)
        }
        val title = TextView(this).apply {
            text = "Need for Speed Underground 2"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        info = TextView(this).apply {
            textSize = 14f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, pad / 2, 0, pad / 2)
        }
        play = Button(this).apply {
            text = "Play"
            setOnClickListener { launchGame() }
        }
        val browse = Button(this).apply {
            text = "Browse for ISO…"
            setOnClickListener { pickIso() }
        }
        listOf(title, info, play, browse).forEach { root.addView(it) }
        setContentView(root)

        if (savedUri() == null) pickIso() else refresh()
        play.requestFocus()
    }

    private fun savedUri(): Uri? = prefs.getString("uri", null)?.let { Uri.parse(it) }

    private fun pickIso() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            )
        }
        startActivityForResult(i, REQ_PICK)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQ_PICK || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: Exception) { /* some providers don't allow persisting */ }
        prefs.edit().putString("uri", uri.toString()).apply()
        refresh()
    }

    private fun refresh() {
        val uri = savedUri()
        if (uri == null) {
            info.text = "No ISO selected"
            play.isEnabled = false
            return
        }
        val name = resolvePath(uri) ?: uri.lastPathSegment ?: uri.toString()
        val id = readGameId(uri)
        info.text = name + (id?.let { "\nGame ID: $it" } ?: "")
        play.isEnabled = true
    }

    /** Turns a picked document into a real filesystem path when it lives on internal/SD storage. */
    private fun resolvePath(uri: Uri): String? {
        if (!DocumentsContract.isDocumentUri(this, uri)) return null
        if (uri.authority != "com.android.externalstorage.documents") return null
        val parts = DocumentsContract.getDocumentId(uri).split(":", limit = 2)
        if (parts.size != 2) return null
        return if (parts[0].equals("primary", true)) "/storage/emulated/0/${parts[1]}"
        else "/storage/${parts[0]}/${parts[1]}"
    }

    /** GameCube images store the 6-character game ID in their first 6 bytes. */
    private fun readGameId(uri: Uri): String? = try {
        contentResolver.openInputStream(uri)?.use { s ->
            val b = ByteArray(6)
            if (s.read(b) == 6) String(b, Charsets.US_ASCII).takeIf { it.matches(Regex("[A-Z0-9]{6}")) }
            else null
        }
    } catch (_: Exception) { null }

    private fun launchGame() {
        val uri = savedUri() ?: return pickIso()
        val path = resolvePath(uri)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            component = ComponentName(
                BuildConfig.DOLPHIN_PKG,
                "org.dolphinemu.dolphinemu.ui.main.MainActivity"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (path != null) {
                data = Uri.fromFile(File(path))
                putExtra("AutoStartFile", path)
            } else {
                data = uri
                putExtra("AutoStartFile", uri.toString())
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    grantUriPermission(BuildConfig.DOLPHIN_PKG, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (_: Exception) {}
            }
        }
        try {
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Dolphin not installed or launch failed", Toast.LENGTH_LONG).show()
        }
    }

    companion object { private const val REQ_PICK = 1 }
}
