package com.example.pg.baseview;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUploadTask extends AsyncTask<Void, Void, String> {
    //容器名称
//    private static final String CONTAINER_NAME = "my-file";
    private static final String CONTAINER_NAME = "markenverify";
    //连接字符
    private static final String storageConnectionString =
            "DefaultEndpointsProtocol=https;" +
                    "AccountName=b2bqacne2dttsa01;" +
                    "AccountKey=Gld3MeGTGfIpbHpWwRvMLxDI7BD+otywaUxVOFwheJvVg0ZXYAYssQ4oI7cYgTITUjmGkQ2UmvUFQc7e25SF6Q==;" +
                    "EndpointSuffix=core.chinacloudapi.cn";

    //    private static final String storageConnectionString =
//            "DefaultEndpointsProtocol=http;" +
//                    "AccountName=my01test01;" +
//                    "AccountKey=tTP+zfmrUGb6FhiPBW/fRNCrjnfwX1QkJIvLpMp0BpWvFhgGjhlq6Syn5DFGgMjTsvPwhC8GmB6db/jklDpzPw==";
    private ProgressDialog mDialog;
    private File mUploadFile;
    private Context mContext;
    private UploadCallback uploadCallback;

    public FileUploadTask(Context context, File uploadFile, UploadCallback uploadCallback) {
        mContext = context;
        mUploadFile = uploadFile;
        this.uploadCallback = uploadCallback;

    }

    @Override
    protected void onPreExecute() {
        mDialog = new ProgressDialog(mContext);
        mDialog.setTitle("上传图片");
        mDialog.setMessage("上传中...");
        mDialog.setCancelable(false);
        mDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        String status;
        try {
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(CONTAINER_NAME);
            container.createIfNotExists();

//            String blobName = System.currentTimeMillis() + ".txt";
            long time = System.currentTimeMillis();
            String timeName = getTime(time);
            String blobName = timeName + "/" + System.currentTimeMillis() + ".jpg";
            CloudBlockBlob blockBlob = container.getBlockBlobReference(blobName);
            blockBlob.upload(new FileInputStream(mUploadFile), mUploadFile.length());
            status = blockBlob.getUri().toString();
        } catch (Exception e) {
            status = "";
        }
        return status;
    }

    @Override
    protected void onPostExecute(String status) {
//        if (status == true) {
//            Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
//        }
        if (TextUtils.isEmpty(status)) {
            Toast.makeText(mContext, "上传图片失败", Toast.LENGTH_SHORT).show();
            uploadCallback.failed(status);
        } else {
            uploadCallback.success(status);
        }

        mDialog.cancel();
        super.onPostExecute(status);
    }


    public interface UploadCallback {
        void success(String result);

        void failed(String... args);
    }

    private String getTime(long time) {
        String rel = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        return rel;
    }
}
