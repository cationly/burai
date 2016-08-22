/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.proxy;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import burai.com.env.Environments;

public final class ProxyServer {

    protected static final String PROP_KEY_HOST = "proxy_host";
    protected static final String PROP_KEY_PORT = "proxy_port";
    protected static final String PROP_KEY_USER = "proxy_user";
    protected static final String PROP_KEY_PASSWORD = "proxy_password";
    protected static final String PROP_KEY_SAVEPASSWORD = "proxy_save_password";

    private ProxyServer() {
        // NOP
    }

    public static void initProxyServer() {
        initProxyServer(null);
    }

    public static void initProxyServer(String password) {
        // set variables
        String hostStr = Environments.getProperty(PROP_KEY_HOST);
        String portStr = Environments.getProperty(PROP_KEY_PORT);
        String userStr = Environments.getProperty(PROP_KEY_USER);
        String passStr = null;

        if (password == null || password.trim().isEmpty()) {
            passStr = Environments.getProperty(PROP_KEY_PASSWORD);
        } else {
            passStr = password;
        }

        if (hostStr != null) {
            hostStr = hostStr.trim();
        }

        if (portStr != null) {
            portStr = portStr.trim();
        }

        if (userStr != null) {
            userStr = userStr.trim();
        }

        if (passStr != null) {
            passStr = passStr.trim();
        }

        // set proxy server
        if (hostStr == null || hostStr.isEmpty() || portStr == null || portStr.isEmpty()) {
            try {
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
                System.clearProperty("https.proxyHost");
                System.clearProperty("https.proxyPort");
                System.clearProperty("ftp.proxyHost");
                System.clearProperty("ftp.proxyPort");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            System.setProperty("http.proxyHost", hostStr);
            System.setProperty("http.proxyPort", portStr);
            System.setProperty("https.proxyHost", hostStr);
            System.setProperty("https.proxyPort", portStr);
            System.setProperty("ftp.proxyHost", hostStr);
            System.setProperty("ftp.proxyPort", portStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set account
        if (userStr == null || userStr.isEmpty() || passStr == null || passStr.isEmpty()) {
            try {
                Authenticator.setDefault(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            Authenticator.setDefault(new MyAuthenticator(userStr, passStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MyAuthenticator extends Authenticator {
        private String user;
        private String pass;

        public MyAuthenticator(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.user, this.pass.toCharArray());
        }
    }

}
