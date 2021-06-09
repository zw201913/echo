package com.github.echo.server;

/**
 * @author zouwei
 * @className VERSION
 * @date: 2021/6/7 下午4:45
 * @description:
 */
public enum Version {
    VERSION_1_0_0("1.0.0");

    Version(String v) {
        this.v = v;
    }

    private String v;

    public String getVersion(){
        return this.v;
    }
}
