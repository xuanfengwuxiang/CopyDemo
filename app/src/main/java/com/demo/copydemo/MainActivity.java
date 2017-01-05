package com.demo.copydemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String path = Environment.getExternalStorageDirectory()
                .getPath();
        System.out.println(path);
        try {
            decompress(path + File.separator + "forgirl.apk", path + File.separator + "forgirl");
            findJpgAndCopy(path + File.separator + "forgirl", path + File.separator + "imagess");
            deleteFolder(path + File.separator + "forgirl");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //删除文件夹
    private void deleteFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(this, "NOFolder", Toast.LENGTH_SHORT).show();
            return;
        }
        if (file.isDirectory()) {

            File[] files = file.listFiles();
            for (File item : files) {
                if (item.isDirectory()) {
                    deleteFolder(item.getPath());
                } else {
                    item.delete();
                }
            }

            file.delete();

        } else {
            file.delete();
        }

    }

    //查找并复制.jpg
    private void findJpgAndCopy(String sourcePath, String targetPath) {
        File file = new File(sourcePath);
        if (!file.exists()) {
            Toast.makeText(this, "Fuckyou", Toast.LENGTH_SHORT).show();
            return;
        }
        File file1 = new File(targetPath);
        if (!file1.exists() || !file1.isDirectory()) {
            if (file1.mkdirs()) {
                System.out.println("image文件夹创建成功！");
            }

        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File item : files) {
                if (item.isDirectory()) {
                    findJpgAndCopy(item.getPath(), targetPath);
                } else {
                    if (item.getName().contains(".jpg")) {
                        copyJpg(item, file1);
                    }
                }


            }
        } else {
            if (file.getName().contains(".jpg")) {
                copyJpg(file, file1);
            }
        }


    }

    //复制
    private void copyJpg(File file, File file1) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        if (!file1.exists()) {
            file1.mkdirs();
        }

        try {
            fis = new FileInputStream(file);
            File file3 = new File(file1.getPath() + "/" + file.getName());
            fos = new FileOutputStream(file3);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    //解压
    public static void decompress(String zipPath, String targetPath) throws IOException,
            FileNotFoundException {
        File file = new File(zipPath);
        if (!file.isFile()) {
            throw new FileNotFoundException("file not exist!");
        }
        if (targetPath == null || "".equals(targetPath)) {
            targetPath = file.getParent();
        }
        ZipFile zipFile = new ZipFile(zipPath);
        Enumeration<? extends ZipEntry> files = zipFile.entries();
        ZipEntry entry = null;
        File outFile = null;
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        while (files.hasMoreElements()) {
            entry = files.nextElement();
            outFile = new File(targetPath + File.separator + entry.getName());
            // 如果条目为目录，则跳向下一个
            if (entry.isDirectory()) {
                outFile.mkdirs();
                continue;
            }
            // 创建目录
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            // 创建新文件
            outFile.createNewFile();
            // 如果不可写，则跳向下一个条目
            if (!outFile.canWrite()) {
                continue;
            }
            try {
                bin = new BufferedInputStream(zipFile.getInputStream(entry));
                bout = new BufferedOutputStream(new FileOutputStream(outFile));
                byte[] buffer = new byte[1024];
                int readCount = -1;
                while ((readCount = bin.read(buffer)) != -1) {
                    bout.write(buffer, 0, readCount);
                }
            } finally {
                try {
                    bin.close();
                    bout.flush();
                    bout.close();
                } catch (Exception e) {
                }
            }
        }
    }


}
