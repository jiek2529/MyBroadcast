#广播权限

1. 权限申请回调是异步的模块，__不阻塞UI__。

2. 权限请求时，同组权限授权原理在26时修正

```
Prior to Android 8.0 (API level 26), if an app requested a permission at runtime and the permission was granted, the system also incorrectly granted the app the rest of the permissions that belonged to the same permission group, and that were registered in the manifest.
__在Android 8.0（API级别26）之前，如果应用程序在运行时请求了权限，并且授予了许可权限，系统也会错误地将应用程序授予属于同一权限组的其余权限，并且已在表现。__

For apps targeting Android 8.0, this behavior has been corrected. The app is granted only the permissions it has explicitly requested. However, once the user grants a permission to the app, all subsequent requests for permissions in that permission group are automatically granted.
__针对Android 8.0的应用，此行为已被更正。该应用只被授予它明确要求的权限。但是，一旦用户授予应用程序的权限，该权限组中所有后续的权限请求都将被自动授予。__

For example, suppose an app lists both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE in its manifest. The app requests READ_EXTERNAL_STORAGE and the user grants it. If the app targets API level 24 or lower, the system also grants WRITE_EXTERNAL_STORAGE at the same time, because it belongs to the same STORAGE permission group and is also registered in the manifest. If the app targets Android 8.0 (API level 26), the system grants only READ_EXTERNAL_STORAGE at that time; however, if the app later requests WRITE_EXTERNAL_STORAGE, the system immediately grants that privilege without prompting the user.
__例如，假设一个应用程序同时列出READ_EXTERNAL_STORAGE，并 WRITE_EXTERNAL_STORAGE在其清单。应用程序请求READ_EXTERNAL_STORAGE和用户授予它。如果应用程序的目标API为24级或更低版本，系统也将同时授予WRITE_EXTERNAL_STORAGE，因为它属于相同的STORAGE权限组，并且也在清单中注册。如果应用程式指定Android 8.0（API 26级），系统只会READ_EXTERNAL_STORAGE在当时授予; 然而，如果应用程序稍后请求WRITE_EXTERNAL_STORAGE，系统将立即授予该权限，而不提示用户。__
```

3. 不同包同权限时，后一个安装不上，androidO(8.0-26)
Failure [INSTALL_FAILED_DUPLICATE_PERMISSION: Package com.example.gaopj.mybroadcast attempting to redeclare permission com.myper.hello already owned by com.example.test]

4. 当发送权限广播时，如果不写请求自定义权限，将无法发送：
W/BroadcastQueue: Permission Denial: receiving Intent { act=BR_HELLO flg=0x10 (has extras) } to ProcessRecord{ce6ae00 5526:com.example.gaopj.mybroadcastA/u0a264} (pid=5526, uid=10264) requires com.jiek.mypermission due to sender com.example.gaopj.mybroadcast (uid 10264)


##自定义权限

<uses-permission android:name="com.jiek.mypermission"/>
<permission android:name="com.jiek.mypermission" android:protectionLevel="normal"/>

###protectionLevel说明
Value | Meaning
---|---
"normal" | The default value. A lower-risk permission that gives requesting applications access to isolated application-level features, with minimal risk to other applications, the system, or the user. The system automatically grants this type of permission to a requesting application at installation, without asking for the user's explicit approval (though the user always has the option to review these permissions before installing).
__“正常”__ | __默认值。低风险权限，允许请求应用程序访问隔离的应用程序级功能，对其他应用程序、系统或用户的风险最小。系统在安装时自动向请求的应用程序授予这种类型的权限，而不要求用户明确的批准（尽管用户总是有选择在安装之前查看这些权限）。这是默认值。__
"dangerous" | A higher-risk permission that would give a requesting application access to private user data or control over the device that can negatively impact the user. Because this type of permission introduces potential risk, the system may not automatically grant it to the requesting application. For example, any dangerous permissions requested by an application may be displayed to the user and require confirmation before proceeding, or some other approach may be taken to avoid the user automatically allowing the use of such facilities.
__“危险”| __更高风险的许可将使请求应用程序访问私有用户数据或控制可能对用户产生负面影响的设备。因为这种类型的权限引入潜在的风险，所以系统可能不会自动将其授予请求的应用程序。例如，应用程序请求的任何危险许可可以显示给用户，并且需要在进行之前确认，或者可以采取一些其他方法来避免用户自动允许使用这些设施。__
"signature" | A permission that the system grants only if the requesting application is signed with the same certificate as the application that declared the permission. If the certificates match, the system automatically grants the permission without notifying the user or asking for the user's explicit approval.
__“签名”| __仅当请求的应用程序使用与声明权限的应用程序相同的证书签署系统时才允许。如果证书匹配，则系统自动授予权限，而不通知用户或要求用户明确批准。__
"signatureOrSystem" | A permission that the system grants only to applications that are in the Android system image or that are signed with the same certificate as the application that declared the permission. Please avoid using this option, as the signature protection level should be sufficient for most needs and works regardless of exactly where applications are installed. The "signatureOrSystem" permission is used for certain special situations where multiple vendors have applications built into a system image and need to share specific features explicitly because they are being built together.
__“signatureOrSystem”__ | __系统仅授予Android系统映像中的应用程序或使用与声明权限的应用程序相同的证书签名的权限。请避免使用此选项，因为签名保护级别对于大多数需求而言应该是足够的，而不管确切安装应用程序的位置。“signatureOrSystem”权限用于某些特殊情况，其中多个供应商将应用程序内置到系统映像中，并需要明确共享特定功能，因为它们正在构建在一起。__


##发送

1. sendBroadcast(Intent intent)
    __全局广播__
    全手机应用都能收到广播, 也不能被abortReceiver拦截传递。

2. sendBroadcast(Intent intent, String receiverPermission)
    __带权限广播__
    只有manifest声明了权限的应用的广播接收器才能接收到。也不能被abortReceiver拦截传递。
    当未声明权限时，发送广播时，是发不出去的。报warning __W/BroadcastQueue: Permission Denial: receiving Intent { act=BR_HELLO flg=0x10 (has extras) } to ProcessRecord{80b87ac 19888:com.example.test/u0a255} (pid=19888, uid=10255) requires com.jiek.mypermission due to sender com.example.test (uid 10255)__

```
    Intent intent = new Intent(BR_MYSELF);
    intent.putExtra("hello", this.getClass().getName());
    intent.putExtra("time", System.currentTimeMillis());
    sendOrderedBroadcast(intent, "com.jiek.mypermission");
```

3. sendOrderedBroadcast(Intent intent, String receiverPermission)
    __带权限有序广播__
    只有manifest声明了权限的应用的广播接收器才能接收到。
    使用abortReceiver进行拦截传递时，可被中断，按priority进行高至低分发中断。
    但同级别时，优先注册的先收到，也能中断同级广播接收器。

4. sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras)

5. sendBroadcastAsUser(Intent intent, UserHandler user)

6. sendBroadcastAsUser(Intent intent, UserHandler user, String receiverPermission)

@Deprecated public void sendStickyBroadcast(Intent intent)

@Deprecated public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras)

@Deprecated public void sendStickyBroadcastAsUser(Intent intent, UserHandle user)

@Deprecated public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras)

```
sendStickyBroadcast

Sticky的意思是即使没有接收者，发送的广播Intent也会一直驻留在系统中，一旦有receiver注册，就会立即收到之前发送的广播。
__发送这个广播的应用需要权限<uses-permissionandroid:name="android.permission.BROADCAST_STICKY" />__

如果sendStickyBroadcast发了多个广播，但暂时没有接收者，系统会保留最后一条广播。当有receiver接收到广播并处理后，系统中驻留的广播Intent仍存在，只有在接收者调用removeStickyBroadcast后系统才会移除该Intent。

本来广播就是一种松耦合的机制，而stickyBroadcast对发送方和接收方的耦合要求就更加宽松了，发送广播时接收方可以不存在，但只要接收方一出现就会保证它一定能收到之前发送的广播。比如USB或hdmi的插拔等状态信息，一开机时就已经用sticky的广播把这些信息发送出去了，而接收者所在的应用可能在以后的某个时机才会运行起来，只要一有接收者被注册，它就能立即收到广播来进行异步处理，所以对双方来说都是个完全异步的过程。
```



##接收

接收的当需要权限才能使用时，须在manifest中添加 uses-permission.

当其它应用用权限的广播发来时，如未添加其权限，将接收不到此广播。

1. 签名不同加权限否能收广播？
2. 同签名不加权限能否收广播？

###定义广播接收器
```
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
    		if (intent.getAction().equals(BR_MYSELF)) {
            log(intent);
            log("priority: 999 ");
		        Log.e("Hello", this.getClass().getName());
		        Log.e("Hello", "hello="+intent.getStringExtra("hello"));
		        Log.e("Hello", "time="+intent.getLongExtra("time", 0));
		        abortBroadcast();//流产中断广播传递
        }
    }
}
```

```
IntentFilter intentFilter = new IntentFilter("TEST_BROADCAST_ACTION");
intentFilter.setPriority(999);// 设置优先级
registerReceiver(receiver, intentFilter);
```

###abortReceiver tips

1. __当使用sendBroadcast发广播，接受器中使用 abortBroadcast()中断时，是不能中断传播的，而会报以下运行时异常，但不会崩溃，也不用捕获__

```
	BroadcastReceiver: BroadcastReceiver trying to return result during a non-ordered broadcast
  java.lang.RuntimeException: BroadcastReceiver trying to return result during a non-ordered broadcast
       at android.content.BroadcastReceiver.checkSynchronousHint(BroadcastReceiver.java:783)
       at android.content.BroadcastReceiver.abortBroadcast(BroadcastReceiver.java:689)
       ....onReceive(...MyReceiver.java:53)
```

2. __当一个app中使用了多个同priority的广播接收器时，最先注册的会优先收到广播； 当使用了abortReceiver中断时，他也能中断同级别的向后传递。__

3. 友好使用abortReceiver
```
if(isOrderedBroadcast()) {//当为有序广播时，可进行流产处理。
    abortBroadcast();
}
```


##总结自定义权限广播

##android 8.0(nexus 5x)上进行测试

1. 自定义权限的apk签名差异安装
    1. __签名相同__的应用，能正常安装。不管它的protectedLevel是否一致。
    2. __签名不同__的应用，只有第一个能安装上。
2. 自定义权限的广播发送
    1. 如果未先requestPermission.将会被permission denied.  > warning __W/BroadcastQueue: Permission Denial: receiving Intent { act=action_mypermission flg=0x10 (has extras) } to ProcessRecord{80b87ac 19888:com.example.test/u0a255} (pid=19888, uid=10255) requires com.jiek.mypermission due to sender com.example.test (uid 10255)__
3. 自定义权限的广播接收器带priority时，同级别的abortReceiver也能被优先注册的执行时流产。

##android 6.0(360n5) 上进行测试

1. 自定义权限的apk签名差异安装, 可以直接安装
2. 自定义权限的广播发送
    1.　未请求权限时，也能正常收到广播。
    2. dangerous 的权限不用请求也能发送。（但motorola　6.0不申请广播__发不出__。）
3. 自定义权限的广播接收器带priority时，同级别的abortReceiver也能被优先注册的执行时流产。

------

##references

* https://developer.android.com/guide/topics/manifest/permission-element.html
* https://developer.android.com/reference/android/content/BroadcastReceiver.html
* [BroadcastReceiver的原理和使用](http://blog.csdn.net/yueqian_scut/article/details/51298996)