package com.mei.tangramdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.libra.expr.compiler.api.ViewCompilerApi;
import com.libra.virtualview.compiler.config.ConfigManager;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.view.image.NativeImageImp;
import com.tmall.wireless.vaf.virtualview.view.text.NativeTextImp;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author wenshi
 * @github
 * @Description
 * @since 2019/8/14
 */
public class DemoActivity extends AppCompatActivity {

    private static final String TYPE = "PLAY_VV";
    private static final String PLAY = "component_demo/virtualview.xml";
    private static final String CONFIG = "config.properties";
    private static final String PLAY_DATA = "component_demo/virtualview.json";

    LinearLayout mLinearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);

        mLinearLayout = findViewById(R.id.container);

        findViewById(R.id.tv0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFile();
                mLinearLayout.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("-------------","***************bbbbbb");
                    }
                });
            }
        });

        findViewById(R.id.tv1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("-------------","***************"+((ViewGroup)mLinearLayout.getChildAt(2)).getChildCount());

                ((ViewGroup)mLinearLayout.getChildAt(2)).getChildAt(3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("-------------","***************11111");
                    }
                });

               //  if (mLinearLayout.getChildCount() > 2) {
               //      mLinearLayout.removeViews(2, mLinearLayout.getChildCount() - 2);
               //  }
            }
        });

        // "android.resource://com.mei.tangramdemo/ +R.mipmap.luo"
    }

    private void loadFile() {
        byte[] binResult = compile(TYPE, PLAY, 1);
        if (binResult != null) {
            // mLinearLayout.removeAllViews();
            VirtualViewUtils.getInstance().initVaf(this).getsViewManager().loadBinBufferSync(binResult);
            View container = VirtualViewUtils.getInstance().initVaf(this).getsVafContext().getContainerService().getContainer(TYPE, true);
            IContainer iContainer = (IContainer) container;

            JSONObject json = Utils.getJSONDataFromAsset(this, PLAY_DATA);
            if (json != null) {
                iContainer.getVirtualView().setVData(json);
            }
            Layout.Params p = iContainer.getVirtualView().getComLayoutParams();
            LinearLayout.LayoutParams marginLayoutParams = new LinearLayout.LayoutParams(p.mLayoutWidth, p.mLayoutHeight);
            marginLayoutParams.leftMargin = p.mLayoutMarginLeft;
            marginLayoutParams.topMargin = p.mLayoutMarginTop;
            marginLayoutParams.rightMargin = p.mLayoutMarginRight;
            marginLayoutParams.bottomMargin = p.mLayoutMarginBottom;
            mLinearLayout.addView(container, marginLayoutParams);
        }
    }

    private byte[] compile(String type, String name, int version) {
        ViewCompilerApi viewCompiler = new ViewCompilerApi();
        viewCompiler.setConfigLoader(new AssetConfigLoader());
        InputStream fis = null;
        try {
            fis = getAssets().open(name);
            byte[] result = viewCompiler.compile(fis, type, version);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class AssetConfigLoader implements ConfigManager.ConfigLoader {

        @Override
        public InputStream getConfigResource() {
            try {
                InputStream inputStream = getAssets().open(CONFIG);
                return inputStream;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
