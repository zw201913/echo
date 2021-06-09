package com.github.echo.server;

/**
 * @author zouwei
 * @className EchoVersion
 * @date: 2021/6/7 下午4:59
 * @description:
 */
public final class EchoVersion {

    public static final String ECHO_VERSION_KEY = "echo.version";

    public static final String CURRENTLY_VERSION = Version.VERSION_1_0_0.getVersion();

    private enum Version {
        VERSION_1_0_0("1.0.0");

        Version(String v) {
            this.v = v;
        }

        private String v;

        public String getVersion() {
            return this.v;
        }
    }
}
