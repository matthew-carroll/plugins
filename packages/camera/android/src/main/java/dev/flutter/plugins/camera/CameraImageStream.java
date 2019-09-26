// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package dev.flutter.plugins.camera;

import android.media.Image;

import androidx.annotation.NonNull;

public interface CameraImageStream {
  void sendImage(@NonNull Image image);
}
