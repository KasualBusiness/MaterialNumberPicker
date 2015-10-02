# MaterialNumberPicker
MaterialNumberPicker intends to give you more flexibility than the native NumberPicker. As many Android developers complained about this component since it was released, this library allows you to access some most wanted private attributes through reflection so that you could easily customize your NumberPicker. Finally, we built on top of it a pattern builder for flexibility reasons.

![alt tag](images/picker_presentation.png)

## Download

The easiest way to add this library to your Android project is to add this line to your `build.gradle` file

``
compile 'biz.kasual:materialnumberpicker:1.1.0'
``

MaterialNumberPicker requires at minimum Android 3.0, same as the native NumberPicker.

## Usage

You can build you number picker by using the following example. It gathers the property currently available with its default values:

````
MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(context)
                .minValue(1)
                .maxValue(10)
                .defaultValue(1)
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.BLUE)
                .textColor(Color.BLACK)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();
```

By default there is no formatter when you build your number picker but you can easily add one using the `formatter` proprety.

## License

```
Copyright 2015 Kasual Business.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
