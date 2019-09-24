package dev.flutter.plugins.camera;

import android.Manifest.permission;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.PluginRegistry;

public class CameraPermissions {
  private static final int CAMERA_REQUEST_ID = 9796;
  private boolean ongoing = false;

  public void requestPermissions(
      ActivityPluginBinding activityPluginBinding,
      boolean enableAudio,
      ResultCallback callback
  ) {
    if (ongoing) {
      callback.onResult("cameraPermission", "Camera permission request ongoing");
    }
    Activity activity = activityPluginBinding.getActivity();
    if (!hasCameraPermission(activity) || (enableAudio && !hasAudioPermission(activity))) {
      activityPluginBinding.addRequestPermissionsResultListener(
          new CameraRequestPermissionsListener(
              (String errorCode, String errorDescription) -> {
                ongoing = false;
                callback.onResult(errorCode, errorDescription);
              }));
      ongoing = true;
      ActivityCompat.requestPermissions(
          activity,
          enableAudio
              ? new String[] {permission.CAMERA, permission.RECORD_AUDIO}
              : new String[] {permission.CAMERA},
          CAMERA_REQUEST_ID);
    } else {
      // Permissions already exist. Call the callback with success.
      callback.onResult(null, null);
    }
  }

  private boolean hasCameraPermission(Activity activity) {
    return ContextCompat.checkSelfPermission(activity, permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED;
  }

  private boolean hasAudioPermission(Activity activity) {
    return ContextCompat.checkSelfPermission(activity, permission.RECORD_AUDIO)
        == PackageManager.PERMISSION_GRANTED;
  }

  private static class CameraRequestPermissionsListener
      implements PluginRegistry.RequestPermissionsResultListener {
    final ResultCallback callback;

    private CameraRequestPermissionsListener(ResultCallback callback) {
      this.callback = callback;
    }

    @Override
    public boolean onRequestPermissionsResult(int id, String[] permissions, int[] grantResults) {
      if (id == CAMERA_REQUEST_ID) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
          callback.onResult("cameraPermission", "MediaRecorderCamera permission not granted");
        } else if (grantResults.length > 1
            && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
          callback.onResult("cameraPermission", "MediaRecorderAudio permission not granted");
        } else {
          callback.onResult(null, null);
        }
        return true;
      }
      return false;
    }
  }

  interface ResultCallback {
    void onResult(String errorCode, String errorDescription);
  }
}
