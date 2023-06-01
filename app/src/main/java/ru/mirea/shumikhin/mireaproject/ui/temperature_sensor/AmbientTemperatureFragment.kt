package ru.mirea.shumikhin.mireaproject.ui.temperature_sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.mirea.shumikhin.mireaproject.databinding.FragmentAmbientTemperatureBinding


class AmbientTemperatureFragment : Fragment(){
    private lateinit var binding: FragmentAmbientTemperatureBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var temperature: Sensor
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAmbientTemperatureBinding.inflate(inflater, container, false)

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(temperatureSensorEventListener, temperature, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(temperatureSensorEventListener)
    }
    private var temperatureSensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                var res = event.values[0]
                binding.tvTemperatureResult.text = "$res\u2103"
            }
        }
    }


}