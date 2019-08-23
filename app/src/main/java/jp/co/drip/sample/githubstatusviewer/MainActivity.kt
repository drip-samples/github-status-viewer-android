package jp.co.drip.sample.githubstatusviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model = ViewModelProviders.of(this)[MainViewModel::class.java]

        model.getIsUpdating().observe(this, Observer {
            buttonUpdate.text = if (it) "Updateing..." else "Update"
            val color = ContextCompat.getColor(this, if (it) R.color.darkGray else R.color.blue)
            buttonUpdate.setBackgroundColor(color)
            buttonUpdate.isEnabled = !it
        })

        model.getStatusText().observe(this, Observer {
            statusText.text = it
        })

        model.getStatusColorCode().observe(this, Observer {
            val color = ContextCompat.getColor(this, it)
            statusText.setBackgroundColor(color)
        })

        model.getLastUpdated().observe(this, Observer {
            lastUpdated.text = it
        })

        buttonUpdate.setOnClickListener {
            model.update()
        }

        model.init()
        model.update()
    }
}
