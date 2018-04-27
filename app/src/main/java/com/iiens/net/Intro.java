package com.iiens.net;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class Intro extends AppIntro2 {

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
        slide.setImageDrawable( R.drawable.intro_iiens );
        slide.setBgColor( getResources().getColor( R.color.orange ) );

        addSlide( AppIntroFragment.newInstance( slide ) );

        slide.setTitle( getResources().getString( R.string.intro_titre_2 ) );
        slide.setDescription( getResources().getString( R.string.intro_description_2 ) );
        slide.setImageDrawable( R.drawable.intro_news );
        slide.setBgColor( getResources().getColor( R.color.intro_news ) );

        addSlide( AppIntroFragment.newInstance( slide ) );

        slide.setTitle( getResources().getString( R.string.intro_titre_3 ) );
        slide.setDescription( getResources().getString( R.string.intro_description_3 ) );
        slide.setImageDrawable( R.drawable.intro_twitter );
        slide.setBgColor( getResources().getColor( R.color.ltBlue ) );

        addSlide( AppIntroFragment.newInstance( slide ) );

        slide.setTitle( getResources().getString( R.string.intro_titre_4 ) );
        slide.setDescription( getResources().getString( R.string.intro_description_4 ) );
        slide.setImageDrawable( R.drawable.intro_schedule );
        slide.setBgColor( getResources().getColor( R.color.ltGreen ) );

        addSlide( AppIntroFragment.newInstance( slide ) );

        slide.setTitle( getResources().getString( R.string.intro_titre_5 ) );
        slide.setDescription( getResources().getString( R.string.intro_description_5 ) );
        slide.setImageDrawable( R.drawable.arise );
        slide.setBgColor( getResources().getColor( R.color.bordeaux ) );

        addSlide( AppIntroFragment.newInstance( slide ) );

        slide.setTitle( getResources().getString( R.string.intro_titre_6 ) );
        slide.setDescription( getResources().getString( R.string.intro_description_6 ) );
        slide.setImageDrawable( R.drawable.intro_github );
        slide.setBgColor( getResources().getColor( R.color.orange ) );

        addSlide( AppIntroFragment.newInstance( slide ) );

        // Hide Skip/Done button.
        showSkipButton( false );
        setProgressButtonEnabled( true );

        setFadeAnimation();
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

        SharedPreferences.Editor editor = getSharedPreferences(
                getResources().getString( R.string.app_settings ),
                Context.MODE_PRIVATE
        ).edit();

        editor.putBoolean( GlobalState.PrefsConst.FIRST_LAUNCH, false );
        editor.apply();

        startActivity( new Intent( Intro.this, Main.class ) );
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {

        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}