package com.mahendran_sakkarai.camera.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Mahendran Sakkarai on 1/4/2017.
 */

public class PermissionUtil {
    public static final int PERMISSION_REQUEST = 1;

    public static boolean checkPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }

        return true;
    }

    public static void requestPermissions(Activity activity, String... permissions) {
        List<String> permissionsToRequest = new ArrayList<>();
        if (!checkPermissions(activity, permissions)) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)
                    permissionsToRequest.add(permission);
            }

            if (!permissionsToRequest.isEmpty())
                ActivityCompat.requestPermissions(activity, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), PERMISSION_REQUEST);
        }
    }

    public static void validatePermissionResult(PermissionResult updater, String[] mPermissions, String[] permissions, int[] grantResults) {
        HashMap<String, Integer> permissionGroup = new HashMap<>();
        List<String> rejectedPermission = new ArrayList<>();
        for (String permission : mPermissions)
            permissionGroup.put(permission, PackageManager.PERMISSION_GRANTED);

        Iterator<String> results = permissionGroup.keySet().iterator();

        for (int i = 0; i < permissions.length; i++) {
            permissionGroup.put(permissions[i], grantResults[i]);
        }

        while (results.hasNext()) {
            String resultKey = results.next();
            if (permissionGroup.get(resultKey) == PackageManager.PERMISSION_DENIED)
                rejectedPermission.add(resultKey);
        }

        if (rejectedPermission.isEmpty()) {
            updater.permissionGranted();
        } else {
            updater.permissionNotGranted(rejectedPermission.toArray(new String[rejectedPermission.size()]));
        }
    }

    public interface PermissionResult {
        void permissionGranted();

        void permissionNotGranted(String... permissions);
    }
}
