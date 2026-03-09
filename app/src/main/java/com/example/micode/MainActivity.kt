package com.example.micode

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    data class CodeItem(val code: String, val desc: String)

    private val allCodes = listOf(
        CodeItem("3223", "开启隐藏dc调光"),
        CodeItem("1217", "清除当前用户数据"),
        CodeItem("94341", "开启WLAN详细日志相关设置"),
        CodeItem("74663", "启用电话调试功能"),
        CodeItem("86583", "VoLTE运营商检查"),
        CodeItem("869434", "VoWiFi运营商检查"),
        CodeItem("8667", "显示VoNR开关"),
        CodeItem("547693784", "可见5G省电开关"),
        CodeItem("54638", "显示5G网络菜单"),
        CodeItem("5476937", "启用5G电源优化"),
        CodeItem("932428", "启用微信和通话共存功能"),
        CodeItem("7276937", "启用SA功率优化"),
        CodeItem("34834", "显示5G开关"),
        CodeItem("726633", "显示5G网络模式选择"),
        CodeItem("7282583", "启用SA模式"),
        CodeItem("842372", "显示副SA开关"),
        CodeItem("544636", "显示NR信号信息"),
        CodeItem("46361", "启用UPLMN设置显示"),
        CodeItem("639276", "设置 radio.newapn.secretcode"),
        CodeItem("467", "设置 radio.editims.secretcode"),
        CodeItem("5487587682", "显示号码变更对话框"),
        CodeItem("67234", "opcfg"),
        CodeItem("672341", "启用cota操作配置"),
        CodeItem("543732", "启用DSDA"),
        CodeItem("737678", "reportTelephonyStatistic"),
        CodeItem("26822277437", "显示运营商频道: persist.sys.cota.carrier"),
        CodeItem("6865625", "显示MTN解锁"),
        CodeItem("968353", "MiuiLog access$200"),
        CodeItem("794824", "显示网络功能辅助菜单"),
        CodeItem("2976633", "启用频段: BWP"),
        CodeItem("616633", "启用频段: N1模式"),
        CodeItem("6286633", "启用频段: N28模式"),
        CodeItem("65686633", "启用频段: N5和N8模式"),
        CodeItem("717717", "open diag"),
        CodeItem("997997", "收集charger日志到/sdcard/diag_logs"),
        CodeItem("6335463", "进入offline log状态"),
        CodeItem("63231", "NFC apdu"),
        CodeItem("63232", "NFC rfl1"),
        CodeItem("63233", "NFC rfl2"),
        CodeItem("63239", "NFC all"),
        CodeItem("284", "抓取日志"),
        CodeItem("995995", "收集modem日志到/sdcard/diag_logs"),
        CodeItem("9959952", "收集modem_custom日志到/sdcard/diag_logs"),
        CodeItem("996996", "收集audio日志到/sdcard/diag_logs"),
        CodeItem("998998", "打开audio动态log"),
        CodeItem("27284", "触发modem panic，/sdcard/ramdump"),
        CodeItem("477477", "收集gps日志到/sdcard/diag_logs"),
        CodeItem("334334", "收集sensor日志到/sdcard/diag_logs"),
        CodeItem("9434", "收集WLAN日志"),
        CodeItem("5959", "开启蓝牙日志功能"),
        CodeItem("289434", "catchBTCLog"),
        CodeItem("25327337", "CustomizedCheck"),
        CodeItem("5228378", "set labtest_flag"),
        CodeItem("6485", "电池信息"),
        CodeItem("286", "ATM信息"),
        CodeItem("4679", "catchMediaLog"),
        CodeItem("11811", "StabilityMainMenuActivity"),
        CodeItem("6666", "退出演示模式")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recycler = findViewById<RecyclerView>(R.id.recycler)
        val search = findViewById<TextInputEditText>(R.id.search)
        val switchFallback = findViewById<MaterialSwitch>(R.id.switchFallback)

        val adapter = CodeAdapter { item ->
            val ok = sendSecretCode(item.code)
            if (ok) {
                Toast.makeText(this, getString(R.string.send_success), Toast.LENGTH_SHORT).show()
            } else {
                if (switchFallback.isChecked) {
                    openDialer(item.code)
                    Toast.makeText(this, getString(R.string.dial_fallback), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.send_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        adapter.submit(allCodes)

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s?.toString()?.trim()?.lowercase() ?: ""
                if (q.isEmpty()) {
                    adapter.submit(allCodes)
                } else {
                    adapter.submit(allCodes.filter {
                        it.code.contains(q) || it.desc.lowercase().contains(q)
                    })
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun sendSecretCode(code: String): Boolean {
        return try {
            val intent = Intent("android.provider.Telephony.SECRET_CODE")
            intent.data = Uri.parse("android_secret_code://$code")
            sendBroadcast(intent)
            true
        } catch (_: Throwable) {
            false
        }
    }

    private fun openDialer(code: String) {
        val formatted = "*#*#$code#*#*"
        val uri = Uri.parse("tel:${Uri.encode(formatted)}")
        val intent = Intent(Intent.ACTION_DIAL, uri)
        startActivity(intent)
    }

    class CodeAdapter(private val onClick: (CodeItem) -> Unit) :
        RecyclerView.Adapter<CodeViewHolder>() {
        private val data = mutableListOf<CodeItem>()
        fun submit(items: List<CodeItem>) {
            data.clear()
            data.addAll(items)
            notifyDataSetChanged()
        }
        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): CodeViewHolder {
            val v = android.view.LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            return CodeViewHolder(v)
        }
        override fun onBindViewHolder(holder: CodeViewHolder, position: Int) {
            val item = data[position]
            holder.bind(item, onClick)
        }
        override fun getItemCount(): Int = data.size
    }

    class CodeViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val t1 = itemView.findViewById<android.widget.TextView>(android.R.id.text1)
        private val t2 = itemView.findViewById<android.widget.TextView>(android.R.id.text2)
        fun bind(item: CodeItem, onClick: (CodeItem) -> Unit) {
            t1.text = item.code
            t2.text = item.desc
            itemView.setOnClickListener { onClick(item) }
        }
    }
}
