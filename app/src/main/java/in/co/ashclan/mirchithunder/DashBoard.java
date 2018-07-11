package in.co.ashclan.mirchithunder;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mzule.fantasyslide.SideBar;
import com.github.mzule.fantasyslide.Transformer;

public class DashBoard extends AppCompatActivity  {

    private ViewPager viewpagerTop, viewPagerBackground;
    private ImageView image_menu;
    public static final int ADAPTER_TYPE_TOP = 1;
    public static final int ADAPTER_TYPE_BOTTOM = 2;
    public static final String EXTRA_IMAGE = "image";
    public static final String EXTRA_TRANSITION_IMAGE = "image";
    public String title;
    private DrawerLayout drawerLayout;
    Toolbar toolbar;

    CardView cardView_Gallery,cardView_Selfie,cardView_Achivements,cardView_logout;

    private int[] listItems = {R.mipmap.img1, R.mipmap.img2, R.mipmap.img3, R.mipmap.img4,
            R.mipmap.img5, R.mipmap.img6, R.mipmap.img7, R.mipmap.img8, R.mipmap.img9, R.mipmap.img10};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final DrawerArrowDrawable indicator = new DrawerArrowDrawable(this);
        indicator.setColor(Color.WHITE);
        this.getSupportActionBar().setHomeAsUpIndicator(indicator);

        init();
        setupViewPager();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (((ViewGroup) drawerView).getChildAt(1).getId() == R.id.leftSideBar) {
                    indicator.setProgress(slideOffset);
                }
            }
        });


        cardView_Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashBoard.this,MyGallery.class));
            }
        });
        cardView_Selfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashBoard.this,MirchiSelfie.class));
            }
        });
        cardView_Achivements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashBoard.this,Achivements.class));
            }
        });
        cardView_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashBoard.this, "Logout", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setTransformer() {
        final float spacing = getResources().getDimensionPixelSize(R.dimen.spacing);
        SideBar rightSideBar = (SideBar) findViewById(R.id.leftSideBar);
        rightSideBar.setTransformer(new Transformer() {
            private View lastHoverView;

            @Override
            public void apply(ViewGroup sideBar, View itemView, float touchY, float slideOffset, boolean isLeft) {
                boolean hovered = itemView.isPressed();
                if (hovered && lastHoverView != itemView) {
                    animateIn(itemView);
                    animateOut(lastHoverView);
                    lastHoverView = itemView;
                }
            }

            private void animateOut(View view) {
                if (view == null) {
                    return;
                }
                ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", -spacing, 0);
                translationX.setDuration(200);
                translationX.start();
            }

            private void animateIn(View view) {
                ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", 0, -spacing);
                translationX.setDuration(200);
                translationX.start();
            }
        });
    }


    public void onClick(View view) {

            if (view instanceof TextView) {
                title = ((TextView) view).getText().toString();
                    if (title.startsWith(getResources().getString(R.string.Profile))) {
                        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this,UserProfile.class));
                    }
                    else if (title.startsWith(getResources().getString(R.string.Gallery))) {
                        //  startActivity(UniversalActivity.newIntent(this, title));
                        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this,MyGallery.class));
                    }
                    else if (title.startsWith(getResources().getString(R.string.Achievements))) {
                        //  startActivity(UniversalActivity.newIntent(this, title));
                        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this,Achivements.class));
                    }
                    else if (title.startsWith(getResources().getString(R.string.Selfie))) {
                        //  startActivity(UniversalActivity.newIntent(this, title));
                        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this,MirchiSelfie.class));
                    }
                    else if (title.startsWith(getResources().getString(R.string.Logout))) {
                        //  startActivity(UniversalActivity.newIntent(this, title));
                        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
                    }
        } else if (view.getId() == R.id.userInfo) {
            //startActivity(UniversalActivity.newIntent(this, "个人中心"));
            Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
        }
        switch (view.getId())
        {
        }
    }

    private void init() {

       // image_menu = (ImageView)findViewById(R.id.toolbar_imgMenu) ;
        viewpagerTop = (ViewPager) findViewById(R.id.viewpagerTop);
        viewPagerBackground = (ViewPager) findViewById(R.id.viewPagerbackground);

        viewpagerTop.setClipChildren(false);
        viewpagerTop.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.pager_margin));
        viewpagerTop.setOffscreenPageLimit(3);
        viewpagerTop.setPageTransformer(false, new CarouselEffectTransformer(this)); // Set transformer

        //Cardviews
        cardView_Gallery = (CardView)findViewById(R.id.card_Gallery);
        cardView_Selfie = (CardView)findViewById(R.id.card_selfie);
        cardView_Achivements = (CardView)findViewById(R.id.card_achivements);
        cardView_logout = (CardView)findViewById(R.id.card_logout);
    }
    /**
     * Setup viewpager and it's events
     */
    private void setupViewPager() {
        // Set Top ViewPager Adapter
        MyPagerAdapter adapter = new MyPagerAdapter(this, listItems, ADAPTER_TYPE_TOP);
        viewpagerTop.setAdapter(adapter);

        // Set Background ViewPager Adapter
        MyPagerAdapter adapterBackground = new MyPagerAdapter(this, listItems, ADAPTER_TYPE_BOTTOM);
        viewPagerBackground.setAdapter(adapterBackground);


        viewpagerTop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int index = 0;

            @Override
            public void onPageSelected(int position) {
                index = position;

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int width = viewPagerBackground.getWidth();
                viewPagerBackground.scrollTo((int) (width * position + width * positionOffset), 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    viewPagerBackground.setCurrentItem(index);
                }

            }
        });
    }
    /**
     * Handle all click event of activity
     */
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.linMain:
                if (view.getTag() != null) {
                    int poisition = Integer.parseInt(view.getTag().toString());
                    //Toast.makeText(getApplicationContext(), "Poistion: " + poisition, Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(this,FullScreenActivity.class);
                    intent.putExtra(EXTRA_IMAGE,listItems[poisition]);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view.findViewById(R.id.imageCover), EXTRA_TRANSITION_IMAGE);
                    ActivityCompat.startActivity(this, intent, options.toBundle());
                }
                break;
        }
    }
}
