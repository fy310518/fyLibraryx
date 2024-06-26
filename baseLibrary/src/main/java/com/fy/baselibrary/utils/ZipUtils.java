package com.fy.baselibrary.utils;

import com.fy.baselibrary.utils.notify.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * describe: 文件 压缩 解压缩 工具类
 * Created by fangs on 2020/3/27 13:22.
 */
public class ZipUtils {
    public static final String TAG = "ZIP";

    private ZipUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断 输入流 是否为一个压缩文件
     * @param source
     * @return
     */
    public static boolean isArchiveFile(File source) {
        try {
            return isArchiveFile(new FileInputStream(source));
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public static boolean isArchiveFile(InputStream input) {
        byte[] ZIP_HEADER_1 = new byte[]{80, 75, 3, 4};
        byte[] ZIP_HEADER_2 = new byte[]{80, 75, 5, 6};

        boolean isArchive = false;
//        InputStream input = null;
        try {
//            input = new InputStream(source);
            byte[] buffer = new byte[4];
            int length = input.read(buffer, 0, 4);
            if (length == 4) {
                isArchive = (Arrays.equals(ZIP_HEADER_1, buffer)) || (Arrays.equals(ZIP_HEADER_2, buffer));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }

        return isArchive;
    }

    /**
     * 获取压缩文件大小
     * @param filePath
     * @return
     */
    public static long getZipSize(String filePath){
        long size = 0;
        try {
            ZipFile f = new ZipFile(filePath);
            Enumeration<? extends ZipEntry> en = f.entries();
            while (en.hasMoreElements()) {
                size += en.nextElement().getSize();
            }
        } catch (IOException e) {
            size = 0;
        }
        return size;
    }

    /**
     * 解压zip到指定的路径
     * @param zipFileString  要解压的 文件（可以是 手机sd卡 中的文件，也可以是 assets 目录下的文件）
     * @param outPathString
     */
    public static void unZipFolder(String zipFileString, String outPathString, OnZipProgress zipProgress) {
        long zipLength = getZipSize(zipFileString);

        InputStream is = null;
        try {
            if (FileUtils.fileIsExist(zipFileString)) {
                is = new FileInputStream(zipFileString);
            } else {
                is = ResUtils.getAssetsInputStream(zipFileString);
            }

            unZipFolder(is, outPathString, zipLength, zipProgress);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压zip到指定的路径
     * @param is            要解压的 文件 输入流
     * @param outPathString 要解压缩路径
     */
    public static void unZipFolder(InputStream is, String outPathString, long zipLength, OnZipProgress zipProgress) throws Exception {
        ZipInputStream inZip = new ZipInputStream(is);
        ZipEntry zipEntry;
        String szName = "";
        long count = 0;

        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
//                String canonicalPath = folder.getCanonicalPath();
//                if (!canonicalPath.startsWith(outPathString)) {
//                    // SecurityException
//                } else {
//                }
                FileUtils.folderIsExists(folder.getParent());
            } else {
                L.e(TAG, outPathString + File.separator + szName);
                File file = new File(outPathString + File.separator + szName);
//                String canonicalPath = file.getCanonicalPath();
//                if (!canonicalPath.startsWith(outPathString)) {
//                    // SecurityException
//                    continue;
//                }
                if (!file.exists()) {
                    L.e(TAG, "Create the file:" + outPathString + File.separator + szName);
                    FileUtils.folderIsExists(file.getParent());
                    FileUtils.fileIsExists(file.getPath());
                }

                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    if (zipLength > 0 && null != zipProgress) {
                        count += len;
                        int progress = (int) ((count * 100) / zipLength);
                        zipProgress.onProgress(progress);
                    }

                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }



    /**
     * 压缩文件和文件夹
     * @param srcFileString 要压缩的文件或文件夹
     * @param zipFileString 解压完成的Zip路径
     */
    public static void zipFolder(String srcFileString, String zipFileString) throws Exception {
        //创建ZIP
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
        //创建文件
        File file = new File(srcFileString);
        // 压缩
        L.e("---->" + file.getParent() + "===" + file.getAbsolutePath());

        zipFiles(file.getParent() + File.separator, file.getName(), outZip);
        //完成和关闭
        outZip.finish();
        outZip.close();
    }

    /**
     * 压缩文件
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     */
    private static void zipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        L.e("folderString:" + folderString + "\n" + "fileString:" + fileString + "\n==========================");
        if (zipOutputSteam == null) return;

        File file = new File(folderString + fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            //文件夹
            String fileList[] = file.list();
            //没有子文件和压缩
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }

            //子文件和递归
            for (int i = 0; i < fileList.length; i++) {
                zipFiles(folderString + fileString + "/", fileList[i], zipOutputSteam);
            }
        }
    }


    /**
     * 返回ZIP中的文件列表（文件和文件夹）
     * @param zipFileString  ZIP的名称
     * @param bContainFolder 是否包含文件夹
     * @param bContainFile   是否包含文件
     */
    public static List<File> getFileList(String zipFileString, boolean bContainFolder, boolean bContainFile) throws Exception {
        List<File> fileList = new ArrayList<>();
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // 获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(szName);
                if (bContainFolder) {
                    fileList.add(folder);
                }
            } else {
                File file = new File(szName);
                if (bContainFile) {
                    fileList.add(file);
                }
            }
        }
        inZip.close();
        return fileList;
    }

    /** Zip解压进度回调 接口*/
    public interface OnZipProgress{
        void onProgress(int progress);
    }
}
