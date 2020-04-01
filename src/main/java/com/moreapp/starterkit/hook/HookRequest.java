package com.moreapp.starterkit.hook;

import java.util.Map;

public class HookRequest {
    private Map view;
    private Map configuration;
    private Map registration;
    private String seal;

    public String getSeal() {
        return seal;
    }

    public void setSeal(String seal) {
        this.seal = seal;
    }

    public Map getView() {
        return view;
    }

    public void setView(Map view) {
        this.view = view;
    }

    public Map getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map configuration) {
        this.configuration = configuration;
    }

    public Map getRegistration() {
        return registration;
    }

    public void setRegistration(Map registration) {
        this.registration = registration;
    }
}
