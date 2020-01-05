package com.example.uipractice.ui.keybord;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.uipractice.R;
import com.example.uipractice.api.ApiRepository;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

public class KeybordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keybord);
        View viewById = findViewById(R.id.tv_clip_borad);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUtils("复制aaa",KeybordActivity.this);
//                Toast.makeText(v.getContext(),"复制成功",Toast.LENGTH_LONG).show();
            }
        });
        listener(this);
    }

    /**
     * 复制到剪贴板
     */
    public static void copyUtils(final String text, final Context context) {
        // 从API11开始android推荐使用android.content.ClipboardManager
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(text);

    }

    public static void copy(String content,Context context) {
        if (!TextUtils.isEmpty(content)) {
            // 得到剪贴板管理器
            ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(content.trim());
            // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
            ClipData clipData = ClipData.newPlainText(null, content);
            // 把数据集设置（复制）到剪贴板
            cmb.setPrimaryClip(clipData);
        }
    }

    /**
     * 获取系统剪贴板内容
     */
    public static String getClipContent(Context context) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
                CharSequence addedText = manager.getPrimaryClip().getItemAt(0).getText();
                String addedTextString = String.valueOf(addedText);
                if (!TextUtils.isEmpty(addedTextString)) {
                    return addedTextString;
                }
            }
        }
        return "";
    }

    /**
     * 清空剪贴板内容
     */
    public static void clearClipboard(Context context) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            try {
                manager.setPrimaryClip(manager.getPrimaryClip());
                manager.setText(null);
            } catch (Exception e) {

            }
        }
    }

    public static void listener(final Context context) {
        // 获取系统剪贴板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        // 添加剪贴板数据改变监听器
        clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                // 剪贴板中的数据被改变，此方法将被回调
                System.out.println("onPrimaryClipChanged()");
                Toast.makeText(context,"剪切板变化了。。。",Toast.LENGTH_LONG).show();
            }
        });

        // 移除指定的剪贴板数据改变监听器
        // clipboard.removePrimaryClipChangedListener(listener);
    }
}
