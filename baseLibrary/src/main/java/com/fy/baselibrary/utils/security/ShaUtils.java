package com.fy.baselibrary.utils.security;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * description android 获取签名信息工具类
 * Created by fangs on 2024/3/4 17:29.
 */
public class ShaUtils {

    private static final String MD5 = "MD5";
    private static final String SHA1 = "SHA1";
    private static final String SHA256 = "SHA256";

    /**
     * 获取签名信息
     */
    private static Signature[] getSignatures(Context context, String packageName) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                        packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES
                );
                return packageInfo.signingInfo.getSigningCertificateHistory();
            } else {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                        packageName,
                        PackageManager.GET_SIGNATURES
                );
                return packageInfo.signatures;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取相应类型的字符串（把签名的byte[]信息转换成 95:F4:D4:FG 这样的字符串形式）
     */
    private static String getSignatureByteString(Signature signature, String type) {
        try {
            byte[] hexBytes = signature.toByteArray();
            MessageDigest digest = MessageDigest.getInstance(type);
            byte[] digestBytes = digest.digest(hexBytes);
            StringBuilder sb = new StringBuilder();

            for (byte digestByte : digestBytes) {
                sb.append(
                        (Integer.toHexString((Byte.valueOf(digestByte).intValue() & 0xFF) | 0x100))
                        .substring(1, 3).toUpperCase(Locale.ROOT)
                ).append(":");
            }
            return sb.substring(0, sb.length() - 1);
        } catch (Exception e) {
            return "error";
        }
    }

    private static List<String> getFingerprint(Context context, String type) {
        String packageName = context.getPackageName();
        Signature[] signatures = getSignatures(context, packageName);

        List<String> list = new ArrayList<>();
        for (Signature signature : signatures) {
            String fingerprint = getSignatureByteString(signature, type);
            list.add(fingerprint);
        }

        return list;
    }


    /**
     * 获取签名的MD5值
     */
    public static String getMD5(Context context) {
        List<String> list = getFingerprint(context, MD5);
        if (list.isEmpty()) {
            return "";
        }
        return list.get(0);
    }

    /**
     * 获取签名 sha1 值
     */
    public static String getSHA1(Context context) {
        List<String> list = getFingerprint(context, SHA1);
        if (list.isEmpty()) {
            return "";
        }
        return list.get(0);
    }

    /**
     * 获取签名 sha256 值
     */
    public static String getSHA256(Context context) {
        List<String> list = getFingerprint(context, SHA256);
        if (list.isEmpty()) {
            return "";
        }
        return list.get(0);
    }

}
