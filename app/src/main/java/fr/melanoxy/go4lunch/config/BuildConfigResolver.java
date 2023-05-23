package fr.melanoxy.go4lunch.config;

import fr.melanoxy.go4lunch.BuildConfig;

public class BuildConfigResolver {

    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

}