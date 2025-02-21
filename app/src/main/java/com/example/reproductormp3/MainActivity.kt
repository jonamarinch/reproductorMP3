package com.example.reproductormp3

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

// Declaración de la clase MainActivity que extiende AppCompatActivity
class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var handler: Handler
    private var isPlaying = true
    private var currentPosition = 0
    private val SELECT_FILE_REQUEST_CODE = 100

    /*
    Al crear la actividad
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Sobreescribe método
        super.onCreate(savedInstanceState)
        // Se establece el layout
        setContentView(R.layout.activity_main)

        // Se crea el reproductor
        mediaPlayer = MediaPlayer.create(this, R.raw.musica)
        // Se pone el volumen del reproductor al máximo
        mediaPlayer.setVolume(1.0f, 1.0f)
        // Se añade la barra de reproducción
        seekBar = findViewById(R.id.seekBar)
        // Se añade el botón para reproducir/pausar
        val playButton: Button = findViewById(R.id.button_play)
        // Y un botón para seleccionar archivo de audio
        val selectFileButton: Button = findViewById(R.id.button_select_file)

        // Esto es necesario para ir actualizando la barra de reproducción en segundo plano
        handler = Handler(Looper.getMainLooper())

        // El máximo de la barra es la duración de la canción
        seekBar.max = mediaPlayer.duration

        // Este método controla el efecto de pulsar el botón:
        // 1. Se pausa o reproduce el contenido
        // 2. Se cambia el texto del botón
        // 3. Se cambia el booleano 'isPlaying'
        playButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                playButton.text = "Reproducir"
            } else {
                mediaPlayer.start()
                playButton.text = "Pausar"
                updateSeekBar()
            }
            isPlaying = !isPlaying
        }

        // Sirve para detectar cambios en la barra y reproducir a partir de donde el usuario haya indicado
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Método para seleccionar el fichero de audio
        selectFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "audio/*"
            startActivityForResult(intent, SELECT_FILE_REQUEST_CODE)
        }

        // Sirve para restaurar el estado al girar la pantalla
        savedInstanceState?.let {
            currentPosition = it.getInt("currentPosition", 0)
            mediaPlayer.seekTo(currentPosition)
            if (it.getBoolean("isPlaying", false)) {
                mediaPlayer.start()
                updateSeekBar()
                playButton.text = "Pausar"
                isPlaying = true
            }
        }
    }

    // Cuando se ha recibido un nuevo fichero, se añade la música
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                playAudio(uri)
            }
        }
    }

    // Cuando se ha recibido un nuevo fichero, se añade la música (data source), empieza a sonar
    private fun playAudio(uri: Uri) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(this, uri)
        mediaPlayer.prepare()
        mediaPlayer.start()
        seekBar.max = mediaPlayer.duration
        updateSeekBar()
        // Se debe cambiar el texto del botón
        val playButton: Button = findViewById(R.id.button_play)
        playButton.text = "Pausar"
        // Se debe cambiar el boolean
        isPlaying = true
    }

    /*
    Método para actualizar la barra de progreso según el mediaPlayer esté reproduciendo la música
     */
    private fun updateSeekBar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    seekBar.progress = mediaPlayer.currentPosition
                    handler.postDelayed(this, 500)
                }
            }
        }, 500)
    }

    /*
    Se guarda la posición en cualquier cambio de configuración (como una rotación de pantalla)
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentPosition", mediaPlayer.currentPosition)
        outState.putBoolean("isPlaying", isPlaying)
    }

    /*
    Método onResume: se llama cuando la actividad está en primer plano e interactiva
     */
    override fun onResume() {
        super.onResume()
        Log.d("CicloDeVida", "onResume ejecutado") // Registro en el log indicando que onResume fue llamado

        mediaPlayer.start() // Inicia la música cuando la actividad está en primer plano
    }

    /*
    Método onPause: se llama cuando la actividad pierde el foco pero sigue parcialmente visible
     */
    override fun onPause() {
        super.onPause()
        Log.d("CicloDeVida", "onPause ejecutado") // Registro en el log indicando que onPause fue llamado

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause() // Pausa la música si la actividad pierde el foco
            isPlaying = false
        }
    }

    /*
    Método onStop: se llama cuando la actividad deja de ser visible para el usuario
     */
    override fun onStop() {
        super.onStop()
        Log.d("CicloDeVida", "onStop ejecutado") // Registro en el log indicando que onStop fue llamado
    }

    /*
    Se cierra la app finalmente y se liberan los recursos
     */
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
