package com.example.reproductormp3

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

// Declaración de la clase MainActivity que extiende AppCompatActivity
class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer;
    private var isPlaying = false // Variable para controlar el estado de la música

    // Método onCreate: se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Asocia el archivo de diseño XML a la actividad
        Log.d("CicloDeVida", "onCreate ejecutado") // Registro en el log indicando que onCreate fue llamado

        // Inicializar el reproductor de música
        mediaPlayer = MediaPlayer.create(this, R.raw.musica) // Archivo mp3 en res/raw

        // Referencia al botón
        val playButton: Button = findViewById(R.id.button_play)

        // Listener para el botón
        playButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause() // Pausa la música si está reproduciendo
                playButton.text = "Play" // Cambia el texto del botón
            } else {
                mediaPlayer.start() // Reproduce la música si está pausada
                playButton.text = "Pause" // Cambia el texto del botón
            }
            isPlaying = !isPlaying // Alterna el estado
        }
    }

    // Método onStart: se llama cuando la actividad pasa a ser visible para el usuario
    override fun onStart() {
        super.onStart()
        Log.d("CicloDeVida", "onStart ejecutado") // Registro en el log indicando que onStart fue llamado
    }

    // Método onResume: se llama cuando la actividad está en primer plano e interactiva
    override fun onResume() {
        super.onResume()
        Log.d("CicloDeVida", "onResume ejecutado") // Registro en el log indicando que onResume fue llamado

        mediaPlayer.start() // Inicia la música cuando la actividad está en primer plano
    }

    // Método onPause: se llama cuando la actividad pierde el foco pero sigue parcialmente visible
    override fun onPause() {
        super.onPause()
        Log.d("CicloDeVida", "onPause ejecutado") // Registro en el log indicando que onPause fue llamado

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause() // Pausa la música si la actividad pierde el foco
            isPlaying = false
        }
    }

    // Método onStop: se llama cuando la actividad deja de ser visible para el usuario
    override fun onStop() {
        super.onStop()
        Log.d("CicloDeVida", "onStop ejecutado") // Registro en el log indicando que onStop fue llamado
    }

    // Método onDestroy: se llama antes de que la actividad sea destruida
    override fun onDestroy() {
        super.onDestroy()
        Log.d("CicloDeVida", "onDestroy ejecutado") // Registro en el log indicando que onDestroy fue llamado

        mediaPlayer.release() // Libera recursos al destruir la actividad
    }
}
