package bwie.com.zbardemo1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;


public class MainActivity extends AppCompatActivity implements QRCodeView.Delegate {
    private static final int REQUEST_CODE = 666;

    private ZBarView mZXingView;

    private Button button;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mZXingView = findViewById(R.id.zxingview);
        mZXingView.setDelegate(this);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mZXingView.openFlashlight();//开启闪光灯
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //ActivityCompat检查和请求权限的类
        //Manifest.permission获取清单文件中所有支持的权限
        //PackageManager使用两个权限常量，一个PERMISSION_GRANTED：允许；一个PERMISSION_DENIED:不予许
        int havePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (havePermission == PackageManager.PERMISSION_GRANTED) {//允许使用相机
            mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
            mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.CAMERA)
                    &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
                mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
            }
        }
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();

        mZXingView.startSpot(); // 开始识别
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        if (isDark) {
            button.setVisibility(View.VISIBLE);
            mZXingView.getScanBoxView().setTipText("环境过暗，请打开闪光灯");
        } else {
            button.setVisibility(View.GONE);
            mZXingView.getScanBoxView().setTipText("");
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e("dt", "打开相机出错");
    }
}
