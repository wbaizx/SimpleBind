# SimpleBind

 一个仿butterknife迷你版，只有绑定控件和点击监听功能

 用法跟butterknife一样

 ```java
 	allprojects {
 		repositories {
 			...
 			maven { url 'https://jitpack.io' }
 		}
 	}

 	implementation 'com.github.wbaizx.SimpleBind:simple-bind:1.0.1'
 	annotationProcessor 'com.github.wbaizx.SimpleBind:simple-compiler:1.0.1'
 ```

```java
    //绑定控件
    @BindView(R.id.test1)
    TextView test1;
    @BindView(R.id.test2)
    TextView aaa;

    //绑定点击事件
    @BindOnClick({R.id.test1, R.id.test2})
    public void onc(View view) {
        if (view.getId() == R.id.test2) {
            startActivity(new Intent(this, Main2Activity.class));
        } else {
            finish();
        }
    }

    //绑定
    SimpleBind.bind(this);
    //or
    SimpleBind.bind(this, view);
```

现在才发现butterknife也是真心难
