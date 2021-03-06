package beauty.beautydemo.screens;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import beauty.beautydemo.R;
import beauty.beautydemo.base.BeautyBaseActivity;
import beauty.beautydemo.screens.materialmenu.SimpleHeaderDrawerActivity;
import beauty.beautydemo.tools.BaseTools;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;


/**
 * Created by Miroslaw Stanek on 21.02.15.
 */
public class PublishActivity extends BeautyBaseActivity implements View.OnClickListener {
    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";

    @InjectView(R.id.rl_tag)
    RelativeLayout mRlTag;// 选择标签

    @InjectView(R.id.tv_tags_show)
    TextView mTagShow; // 显示所选择的tag

    ToggleButton tbFollowers;

    ToggleButton tbDirect;

    ImageView ivPhoto;

    public Toolbar toolbar;

    private boolean propagatingToggleState = false;
    private Uri photoUri;
    private int photoSize;

    private ArrayList<String> mSelectPath;
    private String mPhotoPath = "";

    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri) {
        Intent intent = new Intent(openingActivity, PublishActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.inject(this);

        initView();

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);

        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);

        if (savedInstanceState == null) {
//            mSelectPath = getIntent().getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT); // 照片list
            mPhotoPath = getIntent().getStringExtra(PUBLISH_IMAGE_PATH);
            final File file = new File(mPhotoPath);
            photoUri = Uri.fromFile(file);
//            photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
        } else {
            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
        }
        updateStatusBarColor();

        ivPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                loadThumbnailPhoto();
                return true;
            }
        });
    }

    public void initView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);

        tbFollowers = (ToggleButton) findViewById(R.id.tbFollowers);
        tbFollowers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onFollowersCheckedChange(isChecked);
            }
        });
        tbDirect = (ToggleButton) findViewById(R.id.tbDirect);
        tbDirect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onDirectCheckedChange(isChecked);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (BaseTools.isAndroid5()) {
            getWindow().setStatusBarColor(0xff888888);
        }
    }

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);
        Picasso.with(this)
                .load(photoUri)
                .centerCrop()
                .resize(photoSize, photoSize)
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivPhoto.animate()
                                .scaleX(1.f).scaleY(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .setStartDelay(200)
                                .start();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            if(mTagShow.getText().toString().equals("")){
                Toast.makeText(PublishActivity.this, "至少选择一个tag", Toast.LENGTH_SHORT).show();
                return true;
            }
            bringMainActivityToTop();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void bringMainActivityToTop() {
        Intent intent = new Intent(this, SimpleHeaderDrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(SimpleHeaderDrawerActivity.ACTION_SHOW_LOADING_ITEM);
        intent.putExtra(SimpleHeaderDrawerActivity.MENUID, 6);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }


    public void onFollowersCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbDirect.setChecked(!checked);
            propagatingToggleState = false;
        }
    }


    public void onDirectCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbFollowers.setChecked(!checked);
            propagatingToggleState = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }

    @OnClick(R.id.rl_tag)
    void selectTags(View v) {
        Intent intent = new Intent(PublishActivity.this, SelectTagsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_TAGS);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SELECT_TAGS: //选择
                ArrayList<String> selectTag;
                if (data != null && data.getStringArrayListExtra(FLAG_SELECT_TAGS) != null) {
                    selectTag = data.getStringArrayListExtra(FLAG_SELECT_TAGS);
                    if (selectTag != null && selectTag.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (String s : selectTag) {
                            sb.append(s).append("、");
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        mTagShow.setText(sb.toString());
                    }
                }
        }
    }
}
