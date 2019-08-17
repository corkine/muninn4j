package com.mazhangjing.muninn.v2.check;

import java.util.List;
import java.util.Map;

public class Configure {

    private Map<String, String> transConfig;
    private List<String> volumeOrder;
    private Map<String, Map<String, Object>> volumeInfo;

    @Override
    public String toString() {
        return "Configure{" +
                "transConfig=" + transConfig +
                ", volumeOrder=" + volumeOrder +
                ", volumeInfo=" + volumeInfo +
                '}';
    }

    public Map<String, String> getTransConfig() {
        return transConfig;
    }

    public void setTransConfig(Map<String, String> transConfig) {
        this.transConfig = transConfig;
    }

    public List<String> getVolumeOrder() {
        return volumeOrder;
    }

    public void setVolumeOrder(List<String> volumeOrder) {
        this.volumeOrder = volumeOrder;
    }

    public Map<String, Map<String, Object>> getVolumeInfo() {
        return volumeInfo;
    }

    public void setVolumeInfo(Map<String, Map<String, Object>> volumeInfo) {
        this.volumeInfo = volumeInfo;
    }

    public Configure() {
    }
}
