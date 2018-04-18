package com.iiens.net;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        int colorBlack = getResources().getColor( R.color.black );

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        SliderPage slide = new SliderPage();
        slide.setTitleColor( colorBlack );
        slide.setDescColor( colorBlack );

        slide.setTitle( getResources().getString( R.string.intro_titre_1 ) );
        slide.setDescription( getResources().getString( R.string.intro_description_1 ) );
        slide.setImageDrawable( R.drawable.ic_launcher );
        slide.setBgColor( getResources().getColor( R.color.orange ) );

        addSlide( AppIntroFragment.newInstance( slide ) );

        slide.setTitle( getResources().getString( R.string.intro_titre_2 ) );
        slide.setDescription( getResources().getString( R.string.intro_description_2 ) );

        addSlide( AppIntroFragment.newInstance( slide ) );

        slide.setTitle( getResources().getString( R.string.intro_titre_3 ) );
        slide.setDescription( getResources().getString( R.string.intro_description_3 ) );

        addSlide( AppIntroFragment.newInstance( slide ) );

        slide.setTitle( getResources().getString( R.string.intro_titre_4 ) );
        slide.setDescription( getResources().getString( R.string.intro_description_4 ) );

        addSlide( AppIntroFragment.newInstance( slide ) );

        slide.setTitle( getResources().getString( R.string.intro_titre_5 ) );
        slide.setDescription( getResources().getString( R.string.intro_description_5 ) );

        addSlide( AppIntroFragment.newInstance( slide ) );

        slide.setTitle( getResources().getString( R.string.intro_titre_6 ) );
        slide.setDescription( getResources().getString( R.string.intro_description_6 ) );
        slide.setImageDrawable( R.drawable.logo_github );

        addSlide( AppIntroFragment.newInstance( slide ) );

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor( getResources().getColor( R.color.darkorange ) );
        setSeparatorColor( colorBlack );

        // Hide Skip/Done button.
        showSkipButton( false );
        setProgressButtonEnabled( true );
        setDoneText( getResources().getString( R.string.intro_ok ) );
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.

        startActivity( new Intent( IntroActivity.this, Main.class ) );
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}