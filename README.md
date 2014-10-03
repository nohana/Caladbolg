# Caladbolg
[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/nohana/Caladbolg?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![Build Status](https://travis-ci.org/nohana/Caladbolg.svg?branch=master)](https://travis-ci.org/nohana/Caladbolg)

Circular color picker dialog for andorid produced by nohana.

## Screen Shot

![Color Selection](https://raw.githubusercontent.com/nohana/Caladbolg/master/documents/ss1.png)

## How to use

If you would like to show a color picker dialog from your activity,

```java
public class MyActivity extends FragmentActivity implements OnPickedColorListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);

        findViewById(R.id.button_pick).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // create instance with a default selected color and
                    // show the dialog
                    Caladbolg.getInstance(Color.BLACK).show(getSupportFragmentManager(), "caladbolg");
                }
        });
    }

    @Override
    public void onPickedColor(int rgb, int alpha) {
        // if the user select a color
    }
}
```

## License

This library is licensed under Apache License v2.

```
Copyright (C) 2014 nohana, Inc. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
```
