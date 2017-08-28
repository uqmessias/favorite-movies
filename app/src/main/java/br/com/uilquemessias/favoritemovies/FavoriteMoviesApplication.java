package br.com.uilquemessias.favoritemovies;

import android.app.Application;

import com.facebook.stetho.Stetho;


public class FavoriteMoviesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
