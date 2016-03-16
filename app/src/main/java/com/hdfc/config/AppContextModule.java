package com.hdfc.config;

import android.content.Context;
import android.location.LocationManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Suhail on 3/12/2016.
 */
@Module //a module could also include other modules
public class AppContextModule {
    private final CareTaker application;

    public AppContextModule(CareTaker application) {
        this.application = application;
    }

    @Provides
    public CareTaker application() {
        return this.application;
    }

    @Provides
    public Context applicationContext() {
        return this.application;
    }

    @Provides
    public LocationManager locationService(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
}