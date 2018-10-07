package com.example.renlvda.MyEye.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.renlvda.MyEye.R;
import com.example.renlvda.MyEye.entity.MyUser;
import com.example.renlvda.MyEye.entity.Picture;
import com.example.renlvda.MyEye.utils.L;
import com.example.renlvda.MyEye.utils.UtilTools;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

//import com.example.renlvda.MyEye.fragment.UserFragment.CAMERA_REQUEST_CODE;

/**
 * 项目名:  MyEye
 * 包名:    com.example.renlvda.MyEye.fragment
 * 文件名:  PictureFragment
 * 创建者:  任律达
 * 修改者:  赵宁
 * 创建时间:  2017/11/2 19:50
 * 描述:    图片协助
 */
public class PictureFragment extends Fragment implements View.OnClickListener {

    //拍照按钮
    private Button mPhotoButton;
    //相册按钮
    private Button mAlbumButton;
    //协助描述
    private EditText mEditText;
    //发送按钮
    private Button mSendButton;

    private ImageView mImageView;

    private File imagefile = null;
    private String description;
    private String usernaem;
    private Picture mPicture;

    private static final String TAG = "PictureFragment";
    private final int CAMERA_REQUEST_CODE = 5;
    private final int IMAGE_REQUEST_CODE = 6;
    private final int RESULT_REQUEST_CODE = 7;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 127;
    public static final int CAMERA = 128;
    public static final String PHOTO_IMAGE_ASSO_FILE_NAME = "imgPicAsso.jpg";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, null);

        initView(view);

        return view;
    }

    private void initView(View view) {
        mPhotoButton = (Button) view.findViewById(R.id.btn_photo);
        mAlbumButton = (Button) view.findViewById(R.id.btn_album);
        mEditText = (EditText) view.findViewById(R.id.edit_description);
        mSendButton = (Button) view.findViewById(R.id.btn_send_photo);
        mImageView = (ImageView) view.findViewById(R.id.pic_image);

        mPhotoButton.setOnClickListener(this);
        mSendButton.setOnClickListener(this);

        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        usernaem = userInfo.getUsername();

        mPicture = new Picture();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_photo:
                takePhoto();
                break;
//            case R.id.btn_album:
//                open_album();
//                break;
            case R.id.btn_send_photo:
                send_photo();
                break;
        }
    }

    private void takePhoto() {
        if (!checkPermission()) {   //如果可用
            Toast.makeText(this.getContext(), "拍照权限不足(｡･ω･｡)", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            //判断内存卡是否可用，可用的话就进行储存
            // 指定输出路径
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            }
            File imagePath = new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_ASSO_FILE_NAME);
//            File newFile = new File(imagePath, PHOTO_IMAGE_ASSO_FILE_NAME);
            Uri contentUri = getUriForFile(getContext(), imagePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);

        }
    }

    private void open_album() {

    }

    private void send_photo() {
        Log.e(TAG, "send_photo（）");
        description = mEditText.getText().toString();
//        File imagePath = new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_ASSO_FILE_NAME);
//        Uri contentUri = getUriForFile(getContext(), imagePath);
//        Log.e(TAG, "send_photo: " + imagePath + " | " + contentUri + " | " + contentUri.getEncodedPath() + " | " + contentUri.getPath());
//        File imagefile = new File(contentUri.getPath());

        mPicture.setDescription(description).setUsername(usernaem);
        File file = new File(Environment.getDataDirectory().getParentFile().getAbsolutePath(),PHOTO_IMAGE_ASSO_FILE_NAME);
//        if (file.exists()) {
            BmobFile bmobFile = new BmobFile(file);
            mPicture.setImage(bmobFile);
            Log.e(TAG, "imagefile != null " + bmobFile.getFilename());
//        }

        mPicture.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Log.e(TAG, "创建数据成功：" + objectId);
                } else {
                    Log.e(TAG, "bmob 创建数据失败：" + e.getMessage() + "," + e.getErrorCode());
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

    }

    /**
     * 检查拍照权限
     */
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            return false;
        }
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请CAMERA权限
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    CAMERA);
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != getActivity().RESULT_CANCELED) {
            switch (requestCode) {
                //相册数据
//                case IMAGE_REQUEST_CODE:
//                    break;
                //相机数据
                case CAMERA_REQUEST_CODE:
                    Log.e(TAG, "onActivityResult: CAMERA_REQUEST_CODE" + Environment.getExternalStorageDirectory());
                    File imagePath = new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_ASSO_FILE_NAME);
//                    tempFile = new File(imagePath, PHOTO_IMAGE_ASSO_FILE_NAME);
                    Uri contentUri = getUriForFile(getContext(), imagePath);
                    mImageView.setImageURI(contentUri);
                    break;
            }
        }
    }

    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {//简单地拦截一下
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context, "com.myeye.fileprovider", file);//comxf.activity.provider.download
        } else {
            Log.e(TAG, "getUriForFile: Uri.fromFile(file)");
            uri = Uri.fromFile(file);
        }
        Log.e(TAG, "getUriForFile: Uri.fromFile(file) ========== " + uri);
        return uri;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //保存
        UtilTools.putImageToShare(getActivity(), mImageView);
    }

}
