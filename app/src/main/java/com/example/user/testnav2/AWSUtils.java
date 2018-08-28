//package com.example.user.testnav2;
//import android.app.Activity;
//import android.os.Bundle;
//import android.util.Log;
//
//
//        <service android:name="com.amazonaws.mobileconnectors.s3.transferutility.TransferService" android:enabled="true" />
//
//import com.amazonaws.mobile.client.AWSMobileClient;
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
//import com.amazonaws.services.s3.AmazonS3Client;
//
//import java.io.File;
//
//public class AWSUtils extends Activity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        AWSMobileClient.getInstance().initialize(this).execute();
//        downloadWithTransferUtility();
//    }
//
//    private void downloadWithTransferUtility() {
//
//        TransferUtility transferUtility =
//                TransferUtility.builder()
//                        .context(getApplicationContext())
//                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
//                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
//                        .build();
//
//        TransferObserver downloadObserver =
//                transferUtility.download(
//                        "s3Folder/s3Key.txt",
//                        new File("/path/to/file/localFile.txt"));
//
//        // Attach a listener to the observer to get state update and progress notifications
//        downloadObserver.setTransferListener(new TransferListener() {
//
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                if (TransferState.COMPLETED == state) {
//                    // Handle a completed upload.
//                }
//            }
//
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
//                int percentDone = (int)percentDonef;
//
//                Log.d(LOG_TAG, "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
//            }
//
//            @Override
//            public void onError(int id, Exception ex) {
//                // Handle errors
//            }
//
//        });
//
//        // If you prefer to poll for the data, instead of attaching a
//        // listener, check for the state and progress in the observer.
//        if (TransferState.COMPLETED == downloadObserver.getState()) {
//            // Handle a completed upload.
//        }
//
//        Log.d(LOG_TAG, "Bytes Transferred: " + downloadObserver.getBytesTransferred());
//        Log.d(LOG_TAG, "Bytes Total: " + downloadObserver.getBytesTotal());
//    }
//}