// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.packageinfo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.legacy.PluginRegistry.Registrar;
import java.util.HashMap;
import java.util.Map;

/** PackageInfoPlugin */
public class PackageInfoPlugin implements MethodCallHandler {
  private final Registrar mRegistrar;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel =
        new MethodChannel(registrar.messenger(), "plugins.flutter.io/package_info");
    channel.setMethodCallHandler(new PackageInfoPlugin(registrar));
  }

  public static void registerWith(io.flutter.plugin.common.PluginRegistry.Registrar registrar) {
    final MethodChannel channel =
        new MethodChannel(registrar.messenger(), "plugins.flutter.io/package_info");
    channel.setMethodCallHandler(new PackageInfoPlugin(registrar));
  }

  private PackageInfoPlugin(Registrar registrar) {
    this.mRegistrar = registrar;
  }

  private PackageInfoPlugin(io.flutter.plugin.common.PluginRegistry.Registrar registrar) {
    this.mRegistrar = null;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    try {
      Context context = mRegistrar.context();
      if (call.method.equals("getAll")) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);

        Map<String, String> map = new HashMap<String, String>();
        map.put("appName", info.applicationInfo.loadLabel(pm).toString());
        map.put("packageName", context.getPackageName());
        map.put("version", info.versionName);
        map.put("buildNumber", String.valueOf(info.versionCode));

        result.success(map);
      } else {
        result.notImplemented();
      }
    } catch (PackageManager.NameNotFoundException ex) {
      result.error("Name not found", ex.getMessage(), null);
    }
  }
}
