package com.app.imagefilterdemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

import static com.app.imagefilterdemo.GPUImageFilterTools.*;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, GPUImage.OnPictureSavedListener {

    private static final int REQUEST_PICK_IMAGE = 1;
    private GPUImageFilter mFilter;
    private FilterAdjuster mFilterAdjuster;
    private GPUImageView mGPUImageView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((SeekBar) findViewById(R.id.seekBar)).setOnSeekBarChangeListener(this);
        findViewById(R.id.button_choose_filter).setOnClickListener(this);
        findViewById(R.id.button_save).setOnClickListener(this);
        findViewById(R.id.button_pik_image).setOnClickListener(this);

        mGPUImageView = findViewById(R.id.gpuImage);

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    handleImage(data.getData());
                } else {
                    finish();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.button_choose_filter:
                GPUImageFilterTools.showDialog(this, new OnGpuImageFilterChosenListener() {
                    @Override
                    public void onGpuImageFilterChosenListener(GPUImageFilter filter) {
                        switchFilterTo(filter);
                        mGPUImageView.requestRender();
                    }
                });
                break;
            case R.id.button_save:
                saveImage();
                break;
            case R.id.button_pik_image:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE);
                break;
            default:
                break;
        }

    }

    @Override
    public void onPictureSaved(final Uri uri) {
        Toast.makeText(this, "Saved: " + uri.toString(), Toast.LENGTH_SHORT).show();
    }

    private void saveImage() {
        String fileName = System.currentTimeMillis() + ".jpg";
        mGPUImageView.saveToPictures("GPUImage", fileName, this);
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImageView.setFilter(mFilter);
            mFilterAdjuster = new FilterAdjuster(mFilter);
        }
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(progress);
        }
        mGPUImageView.requestRender();
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
    }

    private void handleImage(final Uri selectedImage) {
        mGPUImageView.setImage(selectedImage);
    }
}