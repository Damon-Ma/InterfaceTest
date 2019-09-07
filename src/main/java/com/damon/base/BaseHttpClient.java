package com.damon.base;

import com.damon.config.HttpLogger;
import com.damon.config.TestConfig;
import com.damon.utils.ReadProperties;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.net.ssl.*;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName MyHttpClient
 * @Description  封装OKhttpclient
 * @Author Damon
 * @Date 2018/11/27
 * @Version 1.0
 **/
public class BaseHttpClient {

    public OkHttpClient mOkHttpClient() {

        //初始化日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //初始化cookiejar
        final Map<String, List<Cookie>> cookieMap = new HashMap<String, List<Cookie>>();

        CookieJar cookieJar = new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                cookieMap.put(httpUrl.host(),list);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                if (cookieMap.get(httpUrl.host())==null){
                    return new ArrayList<Cookie>();
                }else {
                    return cookieMap.get(httpUrl.host());
                }
            }
        };

        //初始化HostnameVerifier
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };

        //获取HttpClient对象
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addNetworkInterceptor(loggingInterceptor)
                .cookieJar(cookieJar)
                .connectTimeout(10,TimeUnit.SECONDS);

        //从配置文件中读取是否使用ssl验证
        if (ReadProperties.useSsl()){
            TestConfig.cerPath = ReadProperties.getCerPath();
            builder = builder.hostnameVerifier(hostnameVerifier)
                    .sslSocketFactory(getSSLSocketFactory(),new MyX509TrustManager());
        }

        return builder.build();
}

    //证书
    private SSLSocketFactory getSSLSocketFactory() {
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        TrustManager[] trustManagers = {new MyX509TrustManager()};
        try {
            context.init(null, trustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return context.getSocketFactory();
    }

    //X509TrustManager
    private class MyX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            if (chain == null) {
                throw new CertificateException("checkServerTrusted: X509Certificate array is null");
            }
            if (chain.length < 1) {
                throw new CertificateException("checkServerTrusted: X509Certificate is empty");
            }
            if (!(null != authType && authType.equals("ECDHE_RSA"))) {
                throw new CertificateException("checkServerTrusted: AuthType is not ECDHE_RSA");
            }

            //检查所有证书
            try {
                TrustManagerFactory factory = TrustManagerFactory.getInstance("X509");
                factory.init((KeyStore) null);
                for (TrustManager trustManager : factory.getTrustManagers()) {
                    ((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

            //获取本地证书中的信息
            String clientEncoded = "";
            String clientSubject = "";
            String clientIssUser = "";
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                InputStream inputStream = new FileInputStream(new File(TestConfig.cerPath));
                X509Certificate clientCertificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
                clientEncoded = new BigInteger(1, clientCertificate.getPublicKey().getEncoded()).toString(16);
                clientSubject = clientCertificate.getSubjectDN().getName();
                clientIssUser = clientCertificate.getIssuerDN().getName();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //获取网络中的证书信息
            X509Certificate certificate = chain[0];
            PublicKey publicKey = certificate.getPublicKey();
            String serverEncoded = new BigInteger(1, publicKey.getEncoded()).toString(16);
            if (!clientEncoded.equals(serverEncoded)) {
                throw new CertificateException("server's PublicKey is not equals to client's PublicKey");
            }
            String subject = certificate.getSubjectDN().getName();
            if (!clientSubject.equals(subject)) {
                throw new CertificateException("server's subject is not equals to client's subject");
            }
            String issuser = certificate.getIssuerDN().getName();
            if (!clientIssUser.equals(issuser)) {
                throw new CertificateException("server's issuser is not equals to client's issuser");
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
