package com.example.uipractice.opengl

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.uipractice.R
import com.example.uipractice.opengl.views.CameraGLSurfaceView
import com.example.uipractice.opengl.views.RecordButton
import kotlinx.android.synthetic.main.activity_opengl_cameractivity.*

class OpenglCameractivity : AppCompatActivity(), RecordButton.OnRecordListener, RadioGroup.OnCheckedChangeListener {

    private val cameraView: GLSurfaceView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_cameractivity)

        val btn_record: RecordButton = findViewById(R.id.btn_record)
        btn_record.setOnRecordListener(this)

        //速度

        //速度
        val rgSpeed = findViewById<RadioGroup>(R.id.rg_speed)
        rgSpeed.setOnCheckedChangeListener(this)
    }

    override fun onRecordStart() {
        cameraSurfaceView.startRecord()
    }

    override fun onRecordStop() {
        cameraSurfaceView.stopRecord()
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.btn_extra_slow -> cameraSurfaceView.setSpeed(CameraGLSurfaceView.Speed.MODE_EXTRA_SLOW)
            R.id.btn_slow -> cameraSurfaceView.setSpeed(CameraGLSurfaceView.Speed.MODE_SLOW)
            R.id.btn_normal -> cameraSurfaceView.setSpeed(CameraGLSurfaceView.Speed.MODE_NORMAL)
            R.id.btn_fast -> cameraSurfaceView.setSpeed(CameraGLSurfaceView.Speed.MODE_FAST)
            R.id.btn_extra_fast -> cameraSurfaceView.setSpeed(CameraGLSurfaceView.Speed.MODE_EXTRA_FAST)
        }
    }
}