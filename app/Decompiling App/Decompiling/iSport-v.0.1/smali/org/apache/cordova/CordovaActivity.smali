.class public Lorg/apache/cordova/CordovaActivity;
.super Landroid/app/Activity;
.source "CordovaActivity.java"

# interfaces
.implements Lorg/apache/cordova/CordovaInterface;


# static fields
.field private static ACTIVITY_EXITING:I

.field private static ACTIVITY_RUNNING:I

.field private static ACTIVITY_STARTING:I

.field public static TAG:Ljava/lang/String;


# instance fields
.field private LOG_TAG:Ljava/lang/Object;

.field protected activityResultCallback:Lorg/apache/cordova/CordovaPlugin;

.field protected activityResultKeepRunning:Z

.field private activityState:I

.field protected appView:Lorg/apache/cordova/CordovaWebView;

.field private backgroundColor:I

.field protected cancelLoadUrl:Z

.field private initCallbackClass:Ljava/lang/String;

.field protected keepRunning:Z

.field private lastIntent:Landroid/content/Intent;

.field private lastRequestCode:I

.field private lastResponseCode:Ljava/lang/Object;

.field protected loadUrlTimeoutValue:I

.field private responseCode:Ljava/lang/Object;

.field protected root:Landroid/widget/LinearLayout;

.field protected spinnerDialog:Landroid/app/ProgressDialog;

.field protected splashDialog:Landroid/app/Dialog;

.field protected splashscreen:I

.field protected splashscreenTime:I

.field private final threadPool:Ljava/util/concurrent/ExecutorService;

.field protected webViewClient:Lorg/apache/cordova/CordovaWebViewClient;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .prologue
    .line 135
    const-string v0, "CordovaActivity"

    sput-object v0, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    .line 151
    const/4 v0, 0x0

    sput v0, Lorg/apache/cordova/CordovaActivity;->ACTIVITY_STARTING:I

    .line 152
    const/4 v0, 0x1

    sput v0, Lorg/apache/cordova/CordovaActivity;->ACTIVITY_RUNNING:I

    .line 153
    const/4 v0, 0x2

    sput v0, Lorg/apache/cordova/CordovaActivity;->ACTIVITY_EXITING:I

    return-void
.end method

.method public constructor <init>()V
    .locals 3

    .prologue
    const/4 v2, 0x0

    const/4 v1, 0x0

    .line 134
    invoke-direct {p0}, Landroid/app/Activity;-><init>()V

    .line 142
    iput-boolean v1, p0, Lorg/apache/cordova/CordovaActivity;->cancelLoadUrl:Z

    .line 143
    iput-object v2, p0, Lorg/apache/cordova/CordovaActivity;->spinnerDialog:Landroid/app/ProgressDialog;

    .line 144
    invoke-static {}, Ljava/util/concurrent/Executors;->newCachedThreadPool()Ljava/util/concurrent/ExecutorService;

    move-result-object v0

    iput-object v0, p0, Lorg/apache/cordova/CordovaActivity;->threadPool:Ljava/util/concurrent/ExecutorService;

    .line 154
    iput v1, p0, Lorg/apache/cordova/CordovaActivity;->activityState:I

    .line 157
    iput-object v2, p0, Lorg/apache/cordova/CordovaActivity;->activityResultCallback:Lorg/apache/cordova/CordovaPlugin;

    .line 162
    const/high16 v0, -0x100

    iput v0, p0, Lorg/apache/cordova/CordovaActivity;->backgroundColor:I

    .line 170
    iput v1, p0, Lorg/apache/cordova/CordovaActivity;->splashscreen:I

    .line 171
    const/16 v0, 0xbb8

    iput v0, p0, Lorg/apache/cordova/CordovaActivity;->splashscreenTime:I

    .line 174
    const/16 v0, 0x4e20

    iput v0, p0, Lorg/apache/cordova/CordovaActivity;->loadUrlTimeoutValue:I

    .line 179
    const/4 v0, 0x1

    iput-boolean v0, p0, Lorg/apache/cordova/CordovaActivity;->keepRunning:Z

    return-void
.end method


# virtual methods
.method public addService(Ljava/lang/String;Ljava/lang/String;)V
    .locals 1
    .parameter "serviceType"
    .parameter "className"
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    .prologue
    .line 827
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-object v0, v0, Lorg/apache/cordova/CordovaWebView;->pluginManager:Lorg/apache/cordova/PluginManager;

    if-eqz v0, :cond_0

    .line 828
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-object v0, v0, Lorg/apache/cordova/CordovaWebView;->pluginManager:Lorg/apache/cordova/PluginManager;

    invoke-virtual {v0, p1, p2}, Lorg/apache/cordova/PluginManager;->addService(Ljava/lang/String;Ljava/lang/String;)V

    .line 830
    :cond_0
    return-void
.end method

.method public backHistory()Z
    .locals 1

    .prologue
    .line 532
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_0

    .line 533
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0}, Lorg/apache/cordova/CordovaWebView;->backHistory()Z

    move-result v0

    .line 535
    :goto_0
    return v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method public cancelLoadUrl()V
    .locals 1
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    .prologue
    .line 506
    const/4 v0, 0x1

    iput-boolean v0, p0, Lorg/apache/cordova/CordovaActivity;->cancelLoadUrl:Z

    .line 507
    return-void
.end method

.method public clearAuthenticationTokens()V
    .locals 1

    .prologue
    .line 246
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-object v0, v0, Lorg/apache/cordova/CordovaWebView;->viewClient:Lorg/apache/cordova/CordovaWebViewClient;

    if-eqz v0, :cond_0

    .line 247
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-object v0, v0, Lorg/apache/cordova/CordovaWebView;->viewClient:Lorg/apache/cordova/CordovaWebViewClient;

    invoke-virtual {v0}, Lorg/apache/cordova/CordovaWebViewClient;->clearAuthenticationTokens()V

    .line 249
    :cond_0
    return-void
.end method

.method public clearCache()V
    .locals 2

    .prologue
    .line 513
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-nez v0, :cond_0

    .line 514
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->init()V

    .line 516
    :cond_0
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Lorg/apache/cordova/CordovaWebView;->clearCache(Z)V

    .line 517
    return-void
.end method

.method public clearHistory()V
    .locals 1

    .prologue
    .line 523
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0}, Lorg/apache/cordova/CordovaWebView;->clearHistory()V

    .line 524
    return-void
.end method

.method public displayError(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V
    .locals 7
    .parameter "title"
    .parameter "message"
    .parameter "button"
    .parameter "exit"

    .prologue
    .line 994
    move-object v2, p0

    .line 995
    .local v2, me:Lorg/apache/cordova/CordovaActivity;
    new-instance v0, Lorg/apache/cordova/CordovaActivity$4;

    move-object v1, p0

    move-object v3, p2

    move-object v4, p1

    move-object v5, p3

    move v6, p4

    invoke-direct/range {v0 .. v6}, Lorg/apache/cordova/CordovaActivity$4;-><init>(Lorg/apache/cordova/CordovaActivity;Lorg/apache/cordova/CordovaActivity;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V

    invoke-virtual {v2, v0}, Lorg/apache/cordova/CordovaActivity;->runOnUiThread(Ljava/lang/Runnable;)V

    .line 1018
    return-void
.end method

.method public endActivity()V
    .locals 1

    .prologue
    .line 878
    sget v0, Lorg/apache/cordova/CordovaActivity;->ACTIVITY_EXITING:I

    iput v0, p0, Lorg/apache/cordova/CordovaActivity;->activityState:I

    .line 879
    invoke-super {p0}, Landroid/app/Activity;->finish()V

    .line 880
    return-void
.end method

.method public getActivity()Landroid/app/Activity;
    .locals 0

    .prologue
    .line 304
    return-object p0
.end method

.method public getAuthenticationToken(Ljava/lang/String;Ljava/lang/String;)Lorg/apache/cordova/AuthenticationToken;
    .locals 1
    .parameter "host"
    .parameter "realm"

    .prologue
    .line 236
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-object v0, v0, Lorg/apache/cordova/CordovaWebView;->viewClient:Lorg/apache/cordova/CordovaWebViewClient;

    if-eqz v0, :cond_0

    .line 237
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-object v0, v0, Lorg/apache/cordova/CordovaWebView;->viewClient:Lorg/apache/cordova/CordovaWebViewClient;

    invoke-virtual {v0, p1, p2}, Lorg/apache/cordova/CordovaWebViewClient;->getAuthenticationToken(Ljava/lang/String;Ljava/lang/String;)Lorg/apache/cordova/AuthenticationToken;

    move-result-object v0

    .line 239
    :goto_0
    return-object v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method public getBooleanProperty(Ljava/lang/String;Z)Z
    .locals 5
    .parameter "name"
    .parameter "defaultValue"

    .prologue
    .line 557
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->getIntent()Landroid/content/Intent;

    move-result-object v4

    invoke-virtual {v4}, Landroid/content/Intent;->getExtras()Landroid/os/Bundle;

    move-result-object v0

    .line 558
    .local v0, bundle:Landroid/os/Bundle;
    if-nez v0, :cond_1

    .line 577
    .end local p2
    :cond_0
    :goto_0
    return p2

    .line 561
    .restart local p2
    :cond_1
    invoke-static {}, Ljava/util/Locale;->getDefault()Ljava/util/Locale;

    move-result-object v4

    invoke-virtual {p1, v4}, Ljava/lang/String;->toLowerCase(Ljava/util/Locale;)Ljava/lang/String;

    move-result-object p1

    .line 564
    :try_start_0
    invoke-virtual {v0, p1}, Landroid/os/Bundle;->get(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/lang/Boolean;
    :try_end_0
    .catch Ljava/lang/ClassCastException; {:try_start_0 .. :try_end_0} :catch_0

    .line 574
    .local v2, p:Ljava/lang/Boolean;
    :goto_1
    if-eqz v2, :cond_0

    .line 577
    invoke-virtual {v2}, Ljava/lang/Boolean;->booleanValue()Z

    move-result p2

    goto :goto_0

    .line 565
    .end local v2           #p:Ljava/lang/Boolean;
    :catch_0
    move-exception v1

    .line 566
    .local v1, e:Ljava/lang/ClassCastException;
    invoke-virtual {v0, p1}, Landroid/os/Bundle;->get(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v3

    .line 567
    .local v3, s:Ljava/lang/String;
    const-string v4, "true"

    invoke-virtual {v4, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_2

    .line 568
    const/4 v4, 0x1

    invoke-static {v4}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v2

    .restart local v2       #p:Ljava/lang/Boolean;
    goto :goto_1

    .line 571
    .end local v2           #p:Ljava/lang/Boolean;
    :cond_2
    const/4 v4, 0x0

    invoke-static {v4}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v2

    .restart local v2       #p:Ljava/lang/Boolean;
    goto :goto_1
.end method

.method public getContext()Landroid/content/Context;
    .locals 2
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    .prologue
    .line 1060
    sget-object v0, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    const-string v1, "This will be deprecated December 2012"

    invoke-static {v0, v1}, Lorg/apache/cordova/LOG;->d(Ljava/lang/String;Ljava/lang/String;)V

    .line 1061
    return-object p0
.end method

.method public getDoubleProperty(Ljava/lang/String;D)D
    .locals 5
    .parameter "name"
    .parameter "defaultValue"

    .prologue
    .line 633
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->getIntent()Landroid/content/Intent;

    move-result-object v3

    invoke-virtual {v3}, Landroid/content/Intent;->getExtras()Landroid/os/Bundle;

    move-result-object v0

    .line 634
    .local v0, bundle:Landroid/os/Bundle;
    if-nez v0, :cond_1

    .line 647
    .end local p2
    :cond_0
    :goto_0
    return-wide p2

    .line 637
    .restart local p2
    :cond_1
    invoke-static {}, Ljava/util/Locale;->getDefault()Ljava/util/Locale;

    move-result-object v3

    invoke-virtual {p1, v3}, Ljava/lang/String;->toLowerCase(Ljava/util/Locale;)Ljava/lang/String;

    move-result-object p1

    .line 640
    :try_start_0
    invoke-virtual {v0, p1}, Landroid/os/Bundle;->get(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/lang/Double;
    :try_end_0
    .catch Ljava/lang/ClassCastException; {:try_start_0 .. :try_end_0} :catch_0

    .line 644
    .local v2, p:Ljava/lang/Double;
    :goto_1
    if-eqz v2, :cond_0

    .line 647
    invoke-virtual {v2}, Ljava/lang/Double;->doubleValue()D

    move-result-wide p2

    goto :goto_0

    .line 641
    .end local v2           #p:Ljava/lang/Double;
    :catch_0
    move-exception v1

    .line 642
    .local v1, e:Ljava/lang/ClassCastException;
    invoke-virtual {v0, p1}, Landroid/os/Bundle;->get(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v3}, Ljava/lang/Double;->parseDouble(Ljava/lang/String;)D

    move-result-wide v3

    invoke-static {v3, v4}, Ljava/lang/Double;->valueOf(D)Ljava/lang/Double;

    move-result-object v2

    .restart local v2       #p:Ljava/lang/Double;
    goto :goto_1
.end method

.method public getIntegerProperty(Ljava/lang/String;I)I
    .locals 4
    .parameter "name"
    .parameter "defaultValue"

    .prologue
    .line 588
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->getIntent()Landroid/content/Intent;

    move-result-object v3

    invoke-virtual {v3}, Landroid/content/Intent;->getExtras()Landroid/os/Bundle;

    move-result-object v0

    .line 589
    .local v0, bundle:Landroid/os/Bundle;
    if-nez v0, :cond_1

    .line 602
    .end local p2
    :cond_0
    :goto_0
    return p2

    .line 592
    .restart local p2
    :cond_1
    invoke-static {}, Ljava/util/Locale;->getDefault()Ljava/util/Locale;

    move-result-object v3

    invoke-virtual {p1, v3}, Ljava/lang/String;->toLowerCase(Ljava/util/Locale;)Ljava/lang/String;

    move-result-object p1

    .line 595
    :try_start_0
    invoke-virtual {v0, p1}, Landroid/os/Bundle;->get(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/lang/Integer;
    :try_end_0
    .catch Ljava/lang/ClassCastException; {:try_start_0 .. :try_end_0} :catch_0

    .line 599
    .local v2, p:Ljava/lang/Integer;
    :goto_1
    if-eqz v2, :cond_0

    .line 602
    invoke-virtual {v2}, Ljava/lang/Integer;->intValue()I

    move-result p2

    goto :goto_0

    .line 596
    .end local v2           #p:Ljava/lang/Integer;
    :catch_0
    move-exception v1

    .line 597
    .local v1, e:Ljava/lang/ClassCastException;
    invoke-virtual {v0, p1}, Landroid/os/Bundle;->get(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v3}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    move-result v3

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v2

    .restart local v2       #p:Ljava/lang/Integer;
    goto :goto_1
.end method

.method public getStringProperty(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    .locals 3
    .parameter "name"
    .parameter "defaultValue"

    .prologue
    .line 613
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->getIntent()Landroid/content/Intent;

    move-result-object v2

    invoke-virtual {v2}, Landroid/content/Intent;->getExtras()Landroid/os/Bundle;

    move-result-object v0

    .line 614
    .local v0, bundle:Landroid/os/Bundle;
    if-nez v0, :cond_1

    .line 622
    .end local p2
    :cond_0
    :goto_0
    return-object p2

    .line 617
    .restart local p2
    :cond_1
    invoke-static {}, Ljava/util/Locale;->getDefault()Ljava/util/Locale;

    move-result-object v2

    invoke-virtual {p1, v2}, Ljava/lang/String;->toLowerCase(Ljava/util/Locale;)Ljava/lang/String;

    move-result-object p1

    .line 618
    invoke-virtual {v0, p1}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 619
    .local v1, p:Ljava/lang/String;
    if-eqz v1, :cond_0

    move-object p2, v1

    .line 622
    goto :goto_0
.end method

.method public getThreadPool()Ljava/util/concurrent/ExecutorService;
    .locals 1

    .prologue
    .line 1210
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->threadPool:Ljava/util/concurrent/ExecutorService;

    return-object v0
.end method

.method public init()V
    .locals 3

    .prologue
    .line 349
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->makeWebView()Lorg/apache/cordova/CordovaWebView;

    move-result-object v0

    .line 350
    .local v0, webView:Lorg/apache/cordova/CordovaWebView;
    invoke-virtual {p0, v0}, Lorg/apache/cordova/CordovaActivity;->makeWebViewClient(Lorg/apache/cordova/CordovaWebView;)Lorg/apache/cordova/CordovaWebViewClient;

    move-result-object v1

    invoke-virtual {p0, v0}, Lorg/apache/cordova/CordovaActivity;->makeChromeClient(Lorg/apache/cordova/CordovaWebView;)Lorg/apache/cordova/CordovaChromeClient;

    move-result-object v2

    invoke-virtual {p0, v0, v1, v2}, Lorg/apache/cordova/CordovaActivity;->init(Lorg/apache/cordova/CordovaWebView;Lorg/apache/cordova/CordovaWebViewClient;Lorg/apache/cordova/CordovaChromeClient;)V

    .line 351
    return-void
.end method

.method public init(Lorg/apache/cordova/CordovaWebView;Lorg/apache/cordova/CordovaWebViewClient;Lorg/apache/cordova/CordovaChromeClient;)V
    .locals 5
    .parameter "webView"
    .parameter "webViewClient"
    .parameter "webChromeClient"
    .annotation build Landroid/annotation/SuppressLint;
        value = {
            "NewApi"
        }
    .end annotation

    .prologue
    const/4 v4, 0x0

    const/4 v3, -0x1

    .line 362
    sget-object v0, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    const-string v1, "CordovaActivity.init()"

    invoke-static {v0, v1}, Lorg/apache/cordova/LOG;->d(Ljava/lang/String;Ljava/lang/String;)V

    .line 365
    iput-object p1, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    .line 366
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    const/16 v1, 0x64

    invoke-virtual {v0, v1}, Lorg/apache/cordova/CordovaWebView;->setId(I)V

    .line 368
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0, p2}, Lorg/apache/cordova/CordovaWebView;->setWebViewClient(Lorg/apache/cordova/CordovaWebViewClient;)V

    .line 369
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0, p3}, Lorg/apache/cordova/CordovaWebView;->setWebChromeClient(Lorg/apache/cordova/CordovaChromeClient;)V

    .line 370
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {p2, v0}, Lorg/apache/cordova/CordovaWebViewClient;->setWebView(Lorg/apache/cordova/CordovaWebView;)V

    .line 371
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {p3, v0}, Lorg/apache/cordova/CordovaChromeClient;->setWebView(Lorg/apache/cordova/CordovaWebView;)V

    .line 373
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    new-instance v1, Landroid/widget/LinearLayout$LayoutParams;

    const/high16 v2, 0x3f80

    invoke-direct {v1, v3, v3, v2}, Landroid/widget/LinearLayout$LayoutParams;-><init>(IIF)V

    invoke-virtual {v0, v1}, Lorg/apache/cordova/CordovaWebView;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 378
    const-string v0, "DisallowOverscroll"

    invoke-virtual {p0, v0, v4}, Lorg/apache/cordova/CordovaActivity;->getBooleanProperty(Ljava/lang/String;Z)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 379
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v1, 0x9

    if-lt v0, v1, :cond_0

    .line 380
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    const/4 v1, 0x2

    invoke-virtual {v0, v1}, Lorg/apache/cordova/CordovaWebView;->setOverScrollMode(I)V

    .line 385
    :cond_0
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    const/4 v1, 0x4

    invoke-virtual {v0, v1}, Lorg/apache/cordova/CordovaWebView;->setVisibility(I)V

    .line 386
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->root:Landroid/widget/LinearLayout;

    iget-object v1, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0, v1}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 387
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->root:Landroid/widget/LinearLayout;

    invoke-virtual {p0, v0}, Lorg/apache/cordova/CordovaActivity;->setContentView(Landroid/view/View;)V

    .line 390
    iput-boolean v4, p0, Lorg/apache/cordova/CordovaActivity;->cancelLoadUrl:Z

    .line 392
    return-void
.end method

.method public isUrlWhiteListed(Ljava/lang/String;)Z
    .locals 1
    .parameter "url"

    .prologue
    .line 1027
    invoke-static {p1}, Lorg/apache/cordova/Config;->isUrlWhiteListed(Ljava/lang/String;)Z

    move-result v0

    return v0
.end method

.method loadSpinner()V
    .locals 6

    .prologue
    const/4 v5, 0x0

    .line 472
    const/4 v1, 0x0

    .line 473
    .local v1, loading:Ljava/lang/String;
    iget-object v4, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v4, :cond_0

    iget-object v4, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v4}, Lorg/apache/cordova/CordovaWebView;->canGoBack()Z

    move-result v4

    if-nez v4, :cond_3

    .line 474
    :cond_0
    const-string v4, "LoadingDialog"

    invoke-virtual {p0, v4, v5}, Lorg/apache/cordova/CordovaActivity;->getStringProperty(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 479
    :goto_0
    if-eqz v1, :cond_2

    .line 481
    const-string v3, ""

    .line 482
    .local v3, title:Ljava/lang/String;
    const-string v2, "Loading Application..."

    .line 484
    .local v2, message:Ljava/lang/String;
    invoke-virtual {v1}, Ljava/lang/String;->length()I

    move-result v4

    if-lez v4, :cond_1

    .line 485
    const/16 v4, 0x2c

    invoke-virtual {v1, v4}, Ljava/lang/String;->indexOf(I)I

    move-result v0

    .line 486
    .local v0, comma:I
    if-lez v0, :cond_4

    .line 487
    const/4 v4, 0x0

    invoke-virtual {v1, v4, v0}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v3

    .line 488
    add-int/lit8 v4, v0, 0x1

    invoke-virtual {v1, v4}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v2

    .line 495
    .end local v0           #comma:I
    :cond_1
    :goto_1
    invoke-virtual {p0, v3, v2}, Lorg/apache/cordova/CordovaActivity;->spinnerStart(Ljava/lang/String;Ljava/lang/String;)V

    .line 497
    .end local v2           #message:Ljava/lang/String;
    .end local v3           #title:Ljava/lang/String;
    :cond_2
    return-void

    .line 477
    :cond_3
    const-string v4, "LoadingPageDialog"

    invoke-virtual {p0, v4, v5}, Lorg/apache/cordova/CordovaActivity;->getStringProperty(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    goto :goto_0

    .line 491
    .restart local v0       #comma:I
    .restart local v2       #message:Ljava/lang/String;
    .restart local v3       #title:Ljava/lang/String;
    :cond_4
    const-string v3, ""

    .line 492
    move-object v2, v1

    goto :goto_1
.end method

.method public loadUrl(Ljava/lang/String;)V
    .locals 2
    .parameter "url"

    .prologue
    .line 402
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-nez v0, :cond_0

    .line 403
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->init()V

    .line 406
    :cond_0
    const-string v0, "SplashScreenDelay"

    iget v1, p0, Lorg/apache/cordova/CordovaActivity;->splashscreenTime:I

    invoke-virtual {p0, v0, v1}, Lorg/apache/cordova/CordovaActivity;->getIntegerProperty(Ljava/lang/String;I)I

    move-result v0

    iput v0, p0, Lorg/apache/cordova/CordovaActivity;->splashscreenTime:I

    .line 407
    iget v0, p0, Lorg/apache/cordova/CordovaActivity;->splashscreenTime:I

    if-lez v0, :cond_1

    .line 409
    const-string v0, "SplashScreen"

    const/4 v1, 0x0

    invoke-virtual {p0, v0, v1}, Lorg/apache/cordova/CordovaActivity;->getIntegerProperty(Ljava/lang/String;I)I

    move-result v0

    iput v0, p0, Lorg/apache/cordova/CordovaActivity;->splashscreen:I

    .line 410
    iget v0, p0, Lorg/apache/cordova/CordovaActivity;->splashscreen:I

    if-eqz v0, :cond_1

    .line 412
    iget v0, p0, Lorg/apache/cordova/CordovaActivity;->splashscreenTime:I

    invoke-virtual {p0, v0}, Lorg/apache/cordova/CordovaActivity;->showSplashScreen(I)V

    .line 417
    :cond_1
    const-string v0, "BackgroundColor"

    const/high16 v1, -0x100

    invoke-virtual {p0, v0, v1}, Lorg/apache/cordova/CordovaActivity;->getIntegerProperty(Ljava/lang/String;I)I

    move-result v0

    iput v0, p0, Lorg/apache/cordova/CordovaActivity;->backgroundColor:I

    .line 418
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->root:Landroid/widget/LinearLayout;

    iget v1, p0, Lorg/apache/cordova/CordovaActivity;->backgroundColor:I

    invoke-virtual {v0, v1}, Landroid/widget/LinearLayout;->setBackgroundColor(I)V

    .line 421
    const-string v0, "KeepRunning"

    const/4 v1, 0x1

    invoke-virtual {p0, v0, v1}, Lorg/apache/cordova/CordovaActivity;->getBooleanProperty(Ljava/lang/String;Z)Z

    move-result v0

    iput-boolean v0, p0, Lorg/apache/cordova/CordovaActivity;->keepRunning:Z

    .line 424
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0}, Lorg/apache/cordova/CordovaWebView;->getParent()Landroid/view/ViewParent;

    move-result-object v0

    if-eqz v0, :cond_2

    .line 427
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->loadSpinner()V

    .line 431
    :cond_2
    iget v0, p0, Lorg/apache/cordova/CordovaActivity;->splashscreen:I

    if-eqz v0, :cond_3

    .line 433
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget v1, p0, Lorg/apache/cordova/CordovaActivity;->splashscreenTime:I

    invoke-virtual {v0, p1, v1}, Lorg/apache/cordova/CordovaWebView;->loadUrl(Ljava/lang/String;I)V

    .line 439
    :goto_0
    return-void

    .line 437
    :cond_3
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0, p1}, Lorg/apache/cordova/CordovaWebView;->loadUrl(Ljava/lang/String;)V

    goto :goto_0
.end method

.method public loadUrl(Ljava/lang/String;I)V
    .locals 0
    .parameter "url"
    .parameter "time"

    .prologue
    .line 450
    iput p2, p0, Lorg/apache/cordova/CordovaActivity;->splashscreenTime:I

    .line 451
    invoke-virtual {p0, p1}, Lorg/apache/cordova/CordovaActivity;->loadUrl(Ljava/lang/String;)V

    .line 464
    return-void
.end method

.method protected makeChromeClient(Lorg/apache/cordova/CordovaWebView;)Lorg/apache/cordova/CordovaChromeClient;
    .locals 1
    .parameter "webView"

    .prologue
    .line 342
    new-instance v0, Lorg/apache/cordova/CordovaChromeClient;

    invoke-direct {v0, p0, p1}, Lorg/apache/cordova/CordovaChromeClient;-><init>(Lorg/apache/cordova/CordovaInterface;Lorg/apache/cordova/CordovaWebView;)V

    return-object v0
.end method

.method protected makeWebView()Lorg/apache/cordova/CordovaWebView;
    .locals 1

    .prologue
    .line 314
    new-instance v0, Lorg/apache/cordova/CordovaWebView;

    invoke-direct {v0, p0}, Lorg/apache/cordova/CordovaWebView;-><init>(Landroid/content/Context;)V

    return-object v0
.end method

.method protected makeWebViewClient(Lorg/apache/cordova/CordovaWebView;)Lorg/apache/cordova/CordovaWebViewClient;
    .locals 2
    .parameter "webView"

    .prologue
    .line 326
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v1, 0xb

    if-ge v0, v1, :cond_0

    .line 327
    new-instance v0, Lorg/apache/cordova/CordovaWebViewClient;

    invoke-direct {v0, p0, p1}, Lorg/apache/cordova/CordovaWebViewClient;-><init>(Lorg/apache/cordova/CordovaInterface;Lorg/apache/cordova/CordovaWebView;)V

    .line 329
    :goto_0
    return-object v0

    :cond_0
    new-instance v0, Lorg/apache/cordova/IceCreamCordovaWebViewClient;

    invoke-direct {v0, p0, p1}, Lorg/apache/cordova/IceCreamCordovaWebViewClient;-><init>(Lorg/apache/cordova/CordovaInterface;Lorg/apache/cordova/CordovaWebView;)V

    goto :goto_0
.end method

.method protected onActivityResult(IILandroid/content/Intent;)V
    .locals 6
    .parameter "requestCode"
    .parameter "resultCode"
    .parameter "intent"

    .prologue
    .line 915
    sget-object v3, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    const-string v4, "Incoming Result"

    invoke-static {v3, v4}, Lorg/apache/cordova/LOG;->d(Ljava/lang/String;Ljava/lang/String;)V

    .line 916
    invoke-super {p0, p1, p2, p3}, Landroid/app/Activity;->onActivityResult(IILandroid/content/Intent;)V

    .line 917
    sget-object v3, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "Request code = "

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 918
    iget-object v3, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v3, :cond_3

    const/16 v3, 0x1435

    if-ne p1, v3, :cond_3

    .line 919
    iget-object v3, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v3}, Lorg/apache/cordova/CordovaWebView;->getWebChromeClient()Lorg/apache/cordova/CordovaChromeClient;

    move-result-object v3

    invoke-virtual {v3}, Lorg/apache/cordova/CordovaChromeClient;->getValueCallback()Landroid/webkit/ValueCallback;

    move-result-object v1

    .line 920
    .local v1, mUploadMessage:Landroid/webkit/ValueCallback;,"Landroid/webkit/ValueCallback<Landroid/net/Uri;>;"
    sget-object v3, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    const-string v4, "did we get here?"

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 921
    if-nez v1, :cond_1

    .line 941
    .end local v1           #mUploadMessage:Landroid/webkit/ValueCallback;,"Landroid/webkit/ValueCallback<Landroid/net/Uri;>;"
    :cond_0
    :goto_0
    return-void

    .line 923
    .restart local v1       #mUploadMessage:Landroid/webkit/ValueCallback;,"Landroid/webkit/ValueCallback<Landroid/net/Uri;>;"
    :cond_1
    if-eqz p3, :cond_2

    const/4 v3, -0x1

    if-eq p2, v3, :cond_5

    :cond_2
    const/4 v2, 0x0

    .line 924
    .local v2, result:Landroid/net/Uri;
    :goto_1
    sget-object v3, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "result = "

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 927
    invoke-interface {v1, v2}, Landroid/webkit/ValueCallback;->onReceiveValue(Ljava/lang/Object;)V

    .line 930
    .end local v1           #mUploadMessage:Landroid/webkit/ValueCallback;,"Landroid/webkit/ValueCallback<Landroid/net/Uri;>;"
    .end local v2           #result:Landroid/net/Uri;
    :cond_3
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->activityResultCallback:Lorg/apache/cordova/CordovaPlugin;

    .line 931
    .local v0, callback:Lorg/apache/cordova/CordovaPlugin;
    if-nez v0, :cond_4

    iget-object v3, p0, Lorg/apache/cordova/CordovaActivity;->initCallbackClass:Ljava/lang/String;

    if-eqz v3, :cond_4

    .line 934
    iget-object v3, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-object v3, v3, Lorg/apache/cordova/CordovaWebView;->pluginManager:Lorg/apache/cordova/PluginManager;

    iget-object v4, p0, Lorg/apache/cordova/CordovaActivity;->initCallbackClass:Ljava/lang/String;

    invoke-virtual {v3, v4}, Lorg/apache/cordova/PluginManager;->getPlugin(Ljava/lang/String;)Lorg/apache/cordova/CordovaPlugin;

    move-result-object v3

    iput-object v3, p0, Lorg/apache/cordova/CordovaActivity;->activityResultCallback:Lorg/apache/cordova/CordovaPlugin;

    .line 935
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->activityResultCallback:Lorg/apache/cordova/CordovaPlugin;

    .line 937
    :cond_4
    if-eqz v0, :cond_0

    .line 938
    sget-object v3, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    const-string v4, "We have a callback to send this result to"

    invoke-static {v3, v4}, Lorg/apache/cordova/LOG;->d(Ljava/lang/String;Ljava/lang/String;)V

    .line 939
    invoke-virtual {v0, p1, p2, p3}, Lorg/apache/cordova/CordovaPlugin;->onActivityResult(IILandroid/content/Intent;)V

    goto :goto_0

    .line 923
    .end local v0           #callback:Lorg/apache/cordova/CordovaPlugin;
    .restart local v1       #mUploadMessage:Landroid/webkit/ValueCallback;,"Landroid/webkit/ValueCallback<Landroid/net/Uri;>;"
    :cond_5
    invoke-virtual {p3}, Landroid/content/Intent;->getData()Landroid/net/Uri;

    move-result-object v2

    goto :goto_1
.end method

.method public onConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 0
    .parameter "newConfig"

    .prologue
    .line 546
    invoke-super {p0, p1}, Landroid/app/Activity;->onConfigurationChanged(Landroid/content/res/Configuration;)V

    .line 547
    return-void
.end method

.method public onCreate(Landroid/os/Bundle;)V
    .locals 10
    .parameter "savedInstanceState"

    .prologue
    const/16 v9, 0x800

    const/16 v8, 0x400

    const/4 v7, 0x1

    const/4 v5, 0x0

    const/4 v6, -0x1

    .line 259
    invoke-static {p0}, Lorg/apache/cordova/Config;->init(Landroid/app/Activity;)V

    .line 260
    sget-object v3, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    const-string v4, "CordovaActivity.onCreate()"

    invoke-static {v3, v4}, Lorg/apache/cordova/LOG;->d(Ljava/lang/String;Ljava/lang/String;)V

    .line 261
    invoke-super {p0, p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V

    .line 263
    if-eqz p1, :cond_0

    .line 265
    const-string v3, "callbackClass"

    invoke-virtual {p1, v3}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    iput-object v3, p0, Lorg/apache/cordova/CordovaActivity;->initCallbackClass:Ljava/lang/String;

    .line 268
    :cond_0
    const-string v3, "ShowTitle"

    invoke-virtual {p0, v3, v5}, Lorg/apache/cordova/CordovaActivity;->getBooleanProperty(Ljava/lang/String;Z)Z

    move-result v3

    if-nez v3, :cond_1

    .line 270
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->getWindow()Landroid/view/Window;

    move-result-object v3

    invoke-virtual {v3, v7}, Landroid/view/Window;->requestFeature(I)Z

    .line 273
    :cond_1
    const-string v3, "SetFullscreen"

    invoke-virtual {p0, v3, v5}, Lorg/apache/cordova/CordovaActivity;->getBooleanProperty(Ljava/lang/String;Z)Z

    move-result v3

    if-eqz v3, :cond_2

    .line 275
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->getWindow()Landroid/view/Window;

    move-result-object v3

    invoke-virtual {v3, v8, v8}, Landroid/view/Window;->setFlags(II)V

    .line 284
    :goto_0
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->getWindowManager()Landroid/view/WindowManager;

    move-result-object v3

    invoke-interface {v3}, Landroid/view/WindowManager;->getDefaultDisplay()Landroid/view/Display;

    move-result-object v0

    .line 285
    .local v0, display:Landroid/view/Display;
    invoke-virtual {v0}, Landroid/view/Display;->getWidth()I

    move-result v2

    .line 286
    .local v2, width:I
    invoke-virtual {v0}, Landroid/view/Display;->getHeight()I

    move-result v1

    .line 288
    .local v1, height:I
    new-instance v3, Lorg/apache/cordova/LinearLayoutSoftKeyboardDetect;

    invoke-direct {v3, p0, v2, v1}, Lorg/apache/cordova/LinearLayoutSoftKeyboardDetect;-><init>(Landroid/content/Context;II)V

    iput-object v3, p0, Lorg/apache/cordova/CordovaActivity;->root:Landroid/widget/LinearLayout;

    .line 289
    iget-object v3, p0, Lorg/apache/cordova/CordovaActivity;->root:Landroid/widget/LinearLayout;

    invoke-virtual {v3, v7}, Landroid/widget/LinearLayout;->setOrientation(I)V

    .line 290
    iget-object v3, p0, Lorg/apache/cordova/CordovaActivity;->root:Landroid/widget/LinearLayout;

    iget v4, p0, Lorg/apache/cordova/CordovaActivity;->backgroundColor:I

    invoke-virtual {v3, v4}, Landroid/widget/LinearLayout;->setBackgroundColor(I)V

    .line 291
    iget-object v3, p0, Lorg/apache/cordova/CordovaActivity;->root:Landroid/widget/LinearLayout;

    new-instance v4, Landroid/widget/LinearLayout$LayoutParams;

    const/4 v5, 0x0

    invoke-direct {v4, v6, v6, v5}, Landroid/widget/LinearLayout$LayoutParams;-><init>(IIF)V

    invoke-virtual {v3, v4}, Landroid/widget/LinearLayout;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 295
    const/4 v3, 0x3

    invoke-virtual {p0, v3}, Lorg/apache/cordova/CordovaActivity;->setVolumeControlStream(I)V

    .line 296
    return-void

    .line 280
    .end local v0           #display:Landroid/view/Display;
    .end local v1           #height:I
    .end local v2           #width:I
    :cond_2
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->getWindow()Landroid/view/Window;

    move-result-object v3

    invoke-virtual {v3, v9, v9}, Landroid/view/Window;->setFlags(II)V

    goto :goto_0
.end method

.method public onCreateOptionsMenu(Landroid/view/Menu;)Z
    .locals 1
    .parameter "menu"

    .prologue
    .line 1036
    const-string v0, "onCreateOptionsMenu"

    invoke-virtual {p0, v0, p1}, Lorg/apache/cordova/CordovaActivity;->postMessage(Ljava/lang/String;Ljava/lang/Object;)V

    .line 1037
    invoke-super {p0, p1}, Landroid/app/Activity;->onCreateOptionsMenu(Landroid/view/Menu;)Z

    move-result v0

    return v0
.end method

.method public onDestroy()V
    .locals 2

    .prologue
    .line 790
    sget-object v0, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    const-string v1, "CordovaActivity.onDestroy()"

    invoke-static {v0, v1}, Lorg/apache/cordova/LOG;->d(Ljava/lang/String;Ljava/lang/String;)V

    .line 791
    invoke-super {p0}, Landroid/app/Activity;->onDestroy()V

    .line 794
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->removeSplashScreen()V

    .line 796
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_0

    .line 797
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0}, Lorg/apache/cordova/CordovaWebView;->handleDestroy()V

    .line 802
    :goto_0
    return-void

    .line 800
    :cond_0
    sget v0, Lorg/apache/cordova/CordovaActivity;->ACTIVITY_EXITING:I

    iput v0, p0, Lorg/apache/cordova/CordovaActivity;->activityState:I

    goto :goto_0
.end method

.method public onKeyDown(ILandroid/view/KeyEvent;)Z
    .locals 1
    .parameter "keyCode"
    .parameter "event"

    .prologue
    .line 1160
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_1

    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0}, Lorg/apache/cordova/CordovaWebView;->getFocusedChild()Landroid/view/View;

    move-result-object v0

    if-eqz v0, :cond_1

    const/4 v0, 0x4

    if-eq p1, v0, :cond_0

    const/16 v0, 0x52

    if-ne p1, v0, :cond_1

    .line 1161
    :cond_0
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0, p1, p2}, Lorg/apache/cordova/CordovaWebView;->onKeyDown(ILandroid/view/KeyEvent;)Z

    move-result v0

    .line 1164
    :goto_0
    return v0

    :cond_1
    invoke-super {p0, p1, p2}, Landroid/app/Activity;->onKeyDown(ILandroid/view/KeyEvent;)Z

    move-result v0

    goto :goto_0
.end method

.method public onKeyUp(ILandroid/view/KeyEvent;)Z
    .locals 1
    .parameter "keyCode"
    .parameter "event"

    .prologue
    .line 1141
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_2

    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0}, Lorg/apache/cordova/CordovaWebView;->isCustomViewShowing()Z

    move-result v0

    if-nez v0, :cond_0

    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0}, Lorg/apache/cordova/CordovaWebView;->getFocusedChild()Landroid/view/View;

    move-result-object v0

    if-eqz v0, :cond_2

    :cond_0
    const/4 v0, 0x4

    if-eq p1, v0, :cond_1

    const/16 v0, 0x52

    if-ne p1, v0, :cond_2

    .line 1143
    :cond_1
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0, p1, p2}, Lorg/apache/cordova/CordovaWebView;->onKeyUp(ILandroid/view/KeyEvent;)Z

    move-result v0

    .line 1145
    :goto_0
    return v0

    :cond_2
    invoke-super {p0, p1, p2}, Landroid/app/Activity;->onKeyUp(ILandroid/view/KeyEvent;)Z

    move-result v0

    goto :goto_0
.end method

.method public onMessage(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;
    .locals 6
    .parameter "id"
    .parameter "data"

    .prologue
    const/4 v5, 0x0

    .line 1176
    sget-object v2, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "onMessage("

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, ","

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, ")"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Lorg/apache/cordova/LOG;->d(Ljava/lang/String;Ljava/lang/String;)V

    .line 1177
    const-string v2, "splashscreen"

    invoke-virtual {v2, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_3

    .line 1178
    const-string v2, "hide"

    invoke-virtual {p2}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_1

    .line 1179
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->removeSplashScreen()V

    .line 1206
    :cond_0
    :goto_0
    const/4 v2, 0x0

    return-object v2

    .line 1183
    :cond_1
    iget-object v2, p0, Lorg/apache/cordova/CordovaActivity;->splashDialog:Landroid/app/Dialog;

    if-eqz v2, :cond_2

    iget-object v2, p0, Lorg/apache/cordova/CordovaActivity;->splashDialog:Landroid/app/Dialog;

    invoke-virtual {v2}, Landroid/app/Dialog;->isShowing()Z

    move-result v2

    if-nez v2, :cond_0

    .line 1184
    :cond_2
    const-string v2, "SplashScreen"

    invoke-virtual {p0, v2, v5}, Lorg/apache/cordova/CordovaActivity;->getIntegerProperty(Ljava/lang/String;I)I

    move-result v2

    iput v2, p0, Lorg/apache/cordova/CordovaActivity;->splashscreen:I

    .line 1185
    iget v2, p0, Lorg/apache/cordova/CordovaActivity;->splashscreenTime:I

    invoke-virtual {p0, v2}, Lorg/apache/cordova/CordovaActivity;->showSplashScreen(I)V

    goto :goto_0

    .line 1189
    :cond_3
    const-string v2, "spinner"

    invoke-virtual {v2, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_4

    .line 1190
    const-string v2, "stop"

    invoke-virtual {p2}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_0

    .line 1191
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->spinnerStop()V

    .line 1192
    iget-object v2, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v2, v5}, Lorg/apache/cordova/CordovaWebView;->setVisibility(I)V

    goto :goto_0

    .line 1195
    :cond_4
    const-string v2, "onReceivedError"

    invoke-virtual {v2, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_5

    move-object v0, p2

    .line 1196
    check-cast v0, Lorg/json/JSONObject;

    .line 1198
    .local v0, d:Lorg/json/JSONObject;
    :try_start_0
    const-string v2, "errorCode"

    invoke-virtual {v0, v2}, Lorg/json/JSONObject;->getInt(Ljava/lang/String;)I

    move-result v2

    const-string v3, "description"

    invoke-virtual {v0, v3}, Lorg/json/JSONObject;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    const-string v4, "url"

    invoke-virtual {v0, v4}, Lorg/json/JSONObject;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    invoke-virtual {p0, v2, v3, v4}, Lorg/apache/cordova/CordovaActivity;->onReceivedError(ILjava/lang/String;Ljava/lang/String;)V
    :try_end_0
    .catch Lorg/json/JSONException; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    .line 1199
    :catch_0
    move-exception v1

    .line 1200
    .local v1, e:Lorg/json/JSONException;
    invoke-virtual {v1}, Lorg/json/JSONException;->printStackTrace()V

    goto :goto_0

    .line 1203
    .end local v0           #d:Lorg/json/JSONObject;
    .end local v1           #e:Lorg/json/JSONException;
    :cond_5
    const-string v2, "exit"

    invoke-virtual {v2, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_0

    .line 1204
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->endActivity()V

    goto :goto_0
.end method

.method protected onNewIntent(Landroid/content/Intent;)V
    .locals 1
    .parameter "intent"

    .prologue
    .line 741
    invoke-super {p0, p1}, Landroid/app/Activity;->onNewIntent(Landroid/content/Intent;)V

    .line 743
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_0

    .line 744
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0, p1}, Lorg/apache/cordova/CordovaWebView;->onNewIntent(Landroid/content/Intent;)V

    .line 745
    :cond_0
    return-void
.end method

.method public onOptionsItemSelected(Landroid/view/MenuItem;)Z
    .locals 1
    .parameter "item"

    .prologue
    .line 1048
    const-string v0, "onOptionsItemSelected"

    invoke-virtual {p0, v0, p1}, Lorg/apache/cordova/CordovaActivity;->postMessage(Ljava/lang/String;Ljava/lang/Object;)V

    .line 1049
    const/4 v0, 0x1

    return v0
.end method

.method protected onPause()V
    .locals 2

    .prologue
    .line 715
    invoke-super {p0}, Landroid/app/Activity;->onPause()V

    .line 717
    sget-object v0, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    const-string v1, "Paused the application!"

    invoke-static {v0, v1}, Lorg/apache/cordova/LOG;->d(Ljava/lang/String;Ljava/lang/String;)V

    .line 720
    iget v0, p0, Lorg/apache/cordova/CordovaActivity;->activityState:I

    sget v1, Lorg/apache/cordova/CordovaActivity;->ACTIVITY_EXITING:I

    if-ne v0, v1, :cond_1

    .line 734
    :cond_0
    :goto_0
    return-void

    .line 724
    :cond_1
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_0

    .line 729
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-boolean v1, p0, Lorg/apache/cordova/CordovaActivity;->keepRunning:Z

    invoke-virtual {v0, v1}, Lorg/apache/cordova/CordovaWebView;->handlePause(Z)V

    .line 733
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->removeSplashScreen()V

    goto :goto_0
.end method

.method public onPrepareOptionsMenu(Landroid/view/Menu;)Z
    .locals 1
    .parameter "menu"

    .prologue
    .line 1042
    const-string v0, "onPrepareOptionsMenu"

    invoke-virtual {p0, v0, p1}, Lorg/apache/cordova/CordovaActivity;->postMessage(Ljava/lang/String;Ljava/lang/Object;)V

    .line 1043
    const/4 v0, 0x1

    return v0
.end method

.method public onReceivedError(ILjava/lang/String;Ljava/lang/String;)V
    .locals 7
    .parameter "errorCode"
    .parameter "description"
    .parameter "failingUrl"

    .prologue
    .line 956
    move-object v3, p0

    .line 959
    .local v3, me:Lorg/apache/cordova/CordovaActivity;
    const-string v0, "errorUrl"

    const/4 v1, 0x0

    invoke-virtual {v3, v0, v1}, Lorg/apache/cordova/CordovaActivity;->getStringProperty(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v6

    .line 960
    .local v6, errorUrl:Ljava/lang/String;
    if-eqz v6, :cond_1

    const-string v0, "file://"

    invoke-virtual {v6, v0}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v0

    if-nez v0, :cond_0

    invoke-static {v6}, Lorg/apache/cordova/Config;->isUrlWhiteListed(Ljava/lang/String;)Z

    move-result v0

    if-eqz v0, :cond_1

    :cond_0
    invoke-virtual {p3, v6}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_1

    .line 963
    new-instance v0, Lorg/apache/cordova/CordovaActivity$2;

    invoke-direct {v0, p0, v3, v6}, Lorg/apache/cordova/CordovaActivity$2;-><init>(Lorg/apache/cordova/CordovaActivity;Lorg/apache/cordova/CordovaActivity;Ljava/lang/String;)V

    invoke-virtual {v3, v0}, Lorg/apache/cordova/CordovaActivity;->runOnUiThread(Ljava/lang/Runnable;)V

    .line 983
    :goto_0
    return-void

    .line 973
    :cond_1
    const/4 v0, -0x2

    if-eq p1, v0, :cond_2

    const/4 v2, 0x1

    .line 974
    .local v2, exit:Z
    :goto_1
    new-instance v0, Lorg/apache/cordova/CordovaActivity$3;

    move-object v1, p0

    move-object v4, p2

    move-object v5, p3

    invoke-direct/range {v0 .. v5}, Lorg/apache/cordova/CordovaActivity$3;-><init>(Lorg/apache/cordova/CordovaActivity;ZLorg/apache/cordova/CordovaActivity;Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v3, v0}, Lorg/apache/cordova/CordovaActivity;->runOnUiThread(Ljava/lang/Runnable;)V

    goto :goto_0

    .line 973
    .end local v2           #exit:Z
    :cond_2
    const/4 v2, 0x0

    goto :goto_1
.end method

.method protected onResume()V
    .locals 4

    .prologue
    .line 752
    invoke-super {p0}, Landroid/app/Activity;->onResume()V

    .line 754
    invoke-static {p0}, Lorg/apache/cordova/Config;->init(Landroid/app/Activity;)V

    .line 756
    sget-object v1, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    const-string v2, "Resuming the App"

    invoke-static {v1, v2}, Lorg/apache/cordova/LOG;->d(Ljava/lang/String;Ljava/lang/String;)V

    .line 760
    const-string v1, "ErrorUrl"

    const/4 v2, 0x0

    invoke-virtual {p0, v1, v2}, Lorg/apache/cordova/CordovaActivity;->getStringProperty(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 761
    .local v0, errorUrl:Ljava/lang/String;
    sget-object v1, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "CB-3064: The errorUrl is "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v1, v2}, Lorg/apache/cordova/LOG;->d(Ljava/lang/String;Ljava/lang/String;)V

    .line 763
    iget v1, p0, Lorg/apache/cordova/CordovaActivity;->activityState:I

    sget v2, Lorg/apache/cordova/CordovaActivity;->ACTIVITY_STARTING:I

    if-ne v1, v2, :cond_1

    .line 764
    sget v1, Lorg/apache/cordova/CordovaActivity;->ACTIVITY_RUNNING:I

    iput v1, p0, Lorg/apache/cordova/CordovaActivity;->activityState:I

    .line 783
    :cond_0
    :goto_0
    return-void

    .line 768
    :cond_1
    iget-object v1, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v1, :cond_0

    .line 772
    iget-object v1, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-boolean v2, p0, Lorg/apache/cordova/CordovaActivity;->keepRunning:Z

    iget-boolean v3, p0, Lorg/apache/cordova/CordovaActivity;->activityResultKeepRunning:Z

    invoke-virtual {v1, v2, v3}, Lorg/apache/cordova/CordovaWebView;->handleResume(ZZ)V

    .line 775
    iget-boolean v1, p0, Lorg/apache/cordova/CordovaActivity;->keepRunning:Z

    if-eqz v1, :cond_2

    iget-boolean v1, p0, Lorg/apache/cordova/CordovaActivity;->activityResultKeepRunning:Z

    if-eqz v1, :cond_0

    .line 778
    :cond_2
    iget-boolean v1, p0, Lorg/apache/cordova/CordovaActivity;->activityResultKeepRunning:Z

    if-eqz v1, :cond_0

    .line 779
    iget-boolean v1, p0, Lorg/apache/cordova/CordovaActivity;->activityResultKeepRunning:Z

    iput-boolean v1, p0, Lorg/apache/cordova/CordovaActivity;->keepRunning:Z

    .line 780
    const/4 v1, 0x0

    iput-boolean v1, p0, Lorg/apache/cordova/CordovaActivity;->activityResultKeepRunning:Z

    goto :goto_0
.end method

.method protected onSaveInstanceState(Landroid/os/Bundle;)V
    .locals 2
    .parameter "outState"

    .prologue
    .line 1215
    invoke-super {p0, p1}, Landroid/app/Activity;->onSaveInstanceState(Landroid/os/Bundle;)V

    .line 1216
    iget-object v1, p0, Lorg/apache/cordova/CordovaActivity;->activityResultCallback:Lorg/apache/cordova/CordovaPlugin;

    if-eqz v1, :cond_0

    .line 1218
    iget-object v1, p0, Lorg/apache/cordova/CordovaActivity;->activityResultCallback:Lorg/apache/cordova/CordovaPlugin;

    invoke-virtual {v1}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/Class;->getName()Ljava/lang/String;

    move-result-object v0

    .line 1219
    .local v0, cClass:Ljava/lang/String;
    const-string v1, "callbackClass"

    invoke-virtual {p1, v1, v0}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    .line 1221
    .end local v0           #cClass:Ljava/lang/String;
    :cond_0
    return-void
.end method

.method public postMessage(Ljava/lang/String;Ljava/lang/Object;)V
    .locals 1
    .parameter "id"
    .parameter "data"

    .prologue
    .line 811
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_0

    .line 812
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0, p1, p2}, Lorg/apache/cordova/CordovaWebView;->postMessage(Ljava/lang/String;Ljava/lang/Object;)V

    .line 814
    :cond_0
    return-void
.end method

.method public removeAuthenticationToken(Ljava/lang/String;Ljava/lang/String;)Lorg/apache/cordova/AuthenticationToken;
    .locals 1
    .parameter "host"
    .parameter "realm"

    .prologue
    .line 215
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-object v0, v0, Lorg/apache/cordova/CordovaWebView;->viewClient:Lorg/apache/cordova/CordovaWebViewClient;

    if-eqz v0, :cond_0

    .line 216
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-object v0, v0, Lorg/apache/cordova/CordovaWebView;->viewClient:Lorg/apache/cordova/CordovaWebViewClient;

    invoke-virtual {v0, p1, p2}, Lorg/apache/cordova/CordovaWebViewClient;->removeAuthenticationToken(Ljava/lang/String;Ljava/lang/String;)Lorg/apache/cordova/AuthenticationToken;

    move-result-object v0

    .line 218
    :goto_0
    return-object v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method public removeSplashScreen()V
    .locals 1

    .prologue
    .line 1086
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->splashDialog:Landroid/app/Dialog;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->splashDialog:Landroid/app/Dialog;

    invoke-virtual {v0}, Landroid/app/Dialog;->isShowing()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 1087
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->splashDialog:Landroid/app/Dialog;

    invoke-virtual {v0}, Landroid/app/Dialog;->dismiss()V

    .line 1088
    const/4 v0, 0x0

    iput-object v0, p0, Lorg/apache/cordova/CordovaActivity;->splashDialog:Landroid/app/Dialog;

    .line 1090
    :cond_0
    return-void
.end method

.method public sendJavascript(Ljava/lang/String;)V
    .locals 1
    .parameter "statement"

    .prologue
    .line 839
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_0

    .line 840
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-object v0, v0, Lorg/apache/cordova/CordovaWebView;->jsMessageQueue:Lorg/apache/cordova/NativeToJsMessageQueue;

    invoke-virtual {v0, p1}, Lorg/apache/cordova/NativeToJsMessageQueue;->addJavaScript(Ljava/lang/String;)V

    .line 842
    :cond_0
    return-void
.end method

.method public setActivityResultCallback(Lorg/apache/cordova/CordovaPlugin;)V
    .locals 0
    .parameter "plugin"

    .prologue
    .line 944
    iput-object p1, p0, Lorg/apache/cordova/CordovaActivity;->activityResultCallback:Lorg/apache/cordova/CordovaPlugin;

    .line 945
    return-void
.end method

.method public setAuthenticationToken(Lorg/apache/cordova/AuthenticationToken;Ljava/lang/String;Ljava/lang/String;)V
    .locals 1
    .parameter "authenticationToken"
    .parameter "host"
    .parameter "realm"

    .prologue
    .line 201
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-object v0, v0, Lorg/apache/cordova/CordovaWebView;->viewClient:Lorg/apache/cordova/CordovaWebViewClient;

    if-eqz v0, :cond_0

    .line 202
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    iget-object v0, v0, Lorg/apache/cordova/CordovaWebView;->viewClient:Lorg/apache/cordova/CordovaWebViewClient;

    invoke-virtual {v0, p1, p2, p3}, Lorg/apache/cordova/CordovaWebViewClient;->setAuthenticationToken(Lorg/apache/cordova/AuthenticationToken;Ljava/lang/String;Ljava/lang/String;)V

    .line 204
    :cond_0
    return-void
.end method

.method public setBooleanProperty(Ljava/lang/String;Z)V
    .locals 2
    .parameter "name"
    .parameter "value"
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    .prologue
    .line 661
    sget-object v0, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    const-string v1, "Setting boolean properties in CordovaActivity will be deprecated in 3.0 on July 2013, please use config.xml"

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 662
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->getIntent()Landroid/content/Intent;

    move-result-object v0

    invoke-virtual {p1}, Ljava/lang/String;->toLowerCase()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1, p2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Z)Landroid/content/Intent;

    .line 663
    return-void
.end method

.method public setDoubleProperty(Ljava/lang/String;D)V
    .locals 2
    .parameter "name"
    .parameter "value"
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    .prologue
    .line 706
    sget-object v0, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    const-string v1, "Setting double properties in CordovaActivity will be deprecated in 3.0 on July 2013, please use config.xml"

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 707
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->getIntent()Landroid/content/Intent;

    move-result-object v0

    invoke-virtual {p1}, Ljava/lang/String;->toLowerCase()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1, p2, p3}, Landroid/content/Intent;->putExtra(Ljava/lang/String;D)Landroid/content/Intent;

    .line 708
    return-void
.end method

.method public setIntegerProperty(Ljava/lang/String;I)V
    .locals 2
    .parameter "name"
    .parameter "value"
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    .prologue
    .line 676
    sget-object v0, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    const-string v1, "Setting integer properties in CordovaActivity will be deprecated in 3.0 on July 2013, please use config.xml"

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 677
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->getIntent()Landroid/content/Intent;

    move-result-object v0

    invoke-virtual {p1}, Ljava/lang/String;->toLowerCase()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1, p2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    .line 678
    return-void
.end method

.method public setStringProperty(Ljava/lang/String;Ljava/lang/String;)V
    .locals 2
    .parameter "name"
    .parameter "value"
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    .prologue
    .line 691
    sget-object v0, Lorg/apache/cordova/CordovaActivity;->TAG:Ljava/lang/String;

    const-string v1, "Setting string properties in CordovaActivity will be deprecated in 3.0 on July 2013, please use config.xml"

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 692
    invoke-virtual {p0}, Lorg/apache/cordova/CordovaActivity;->getIntent()Landroid/content/Intent;

    move-result-object v0

    invoke-virtual {p1}, Ljava/lang/String;->toLowerCase()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1, p2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 693
    return-void
.end method

.method protected showSplashScreen(I)V
    .locals 2
    .parameter "time"

    .prologue
    .line 1097
    move-object v1, p0

    .line 1099
    .local v1, that:Lorg/apache/cordova/CordovaActivity;
    new-instance v0, Lorg/apache/cordova/CordovaActivity$5;

    invoke-direct {v0, p0, v1, p1}, Lorg/apache/cordova/CordovaActivity$5;-><init>(Lorg/apache/cordova/CordovaActivity;Lorg/apache/cordova/CordovaActivity;I)V

    .line 1135
    .local v0, runnable:Ljava/lang/Runnable;
    invoke-virtual {p0, v0}, Lorg/apache/cordova/CordovaActivity;->runOnUiThread(Ljava/lang/Runnable;)V

    .line 1136
    return-void
.end method

.method public showWebPage(Ljava/lang/String;ZZLjava/util/HashMap;)V
    .locals 1
    .parameter "url"
    .parameter "openExternal"
    .parameter "clearHistory"
    .parameter
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/String;",
            "ZZ",
            "Ljava/util/HashMap",
            "<",
            "Ljava/lang/String;",
            "Ljava/lang/Object;",
            ">;)V"
        }
    .end annotation

    .prologue
    .line 1075
    .local p4, params:Ljava/util/HashMap;,"Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/Object;>;"
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    if-eqz v0, :cond_0

    .line 1076
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->appView:Lorg/apache/cordova/CordovaWebView;

    invoke-virtual {v0, p1, p2, p3, p4}, Lorg/apache/cordova/CordovaWebView;->showWebPage(Ljava/lang/String;ZZLjava/util/HashMap;)V

    .line 1078
    :cond_0
    return-void
.end method

.method public spinnerStart(Ljava/lang/String;Ljava/lang/String;)V
    .locals 7
    .parameter "title"
    .parameter "message"

    .prologue
    const/4 v3, 0x1

    .line 851
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->spinnerDialog:Landroid/app/ProgressDialog;

    if-eqz v0, :cond_0

    .line 852
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->spinnerDialog:Landroid/app/ProgressDialog;

    invoke-virtual {v0}, Landroid/app/ProgressDialog;->dismiss()V

    .line 853
    const/4 v0, 0x0

    iput-object v0, p0, Lorg/apache/cordova/CordovaActivity;->spinnerDialog:Landroid/app/ProgressDialog;

    .line 855
    :cond_0
    move-object v6, p0

    .line 856
    .local v6, me:Lorg/apache/cordova/CordovaActivity;
    new-instance v5, Lorg/apache/cordova/CordovaActivity$1;

    invoke-direct {v5, p0, v6}, Lorg/apache/cordova/CordovaActivity$1;-><init>(Lorg/apache/cordova/CordovaActivity;Lorg/apache/cordova/CordovaActivity;)V

    move-object v0, p0

    move-object v1, p1

    move-object v2, p2

    move v4, v3

    invoke-static/range {v0 .. v5}, Landroid/app/ProgressDialog;->show(Landroid/content/Context;Ljava/lang/CharSequence;Ljava/lang/CharSequence;ZZLandroid/content/DialogInterface$OnCancelListener;)Landroid/app/ProgressDialog;

    move-result-object v0

    iput-object v0, p0, Lorg/apache/cordova/CordovaActivity;->spinnerDialog:Landroid/app/ProgressDialog;

    .line 862
    return-void
.end method

.method public spinnerStop()V
    .locals 1

    .prologue
    .line 868
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->spinnerDialog:Landroid/app/ProgressDialog;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->spinnerDialog:Landroid/app/ProgressDialog;

    invoke-virtual {v0}, Landroid/app/ProgressDialog;->isShowing()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 869
    iget-object v0, p0, Lorg/apache/cordova/CordovaActivity;->spinnerDialog:Landroid/app/ProgressDialog;

    invoke-virtual {v0}, Landroid/app/ProgressDialog;->dismiss()V

    .line 870
    const/4 v0, 0x0

    iput-object v0, p0, Lorg/apache/cordova/CordovaActivity;->spinnerDialog:Landroid/app/ProgressDialog;

    .line 872
    :cond_0
    return-void
.end method

.method public startActivityForResult(Lorg/apache/cordova/CordovaPlugin;Landroid/content/Intent;I)V
    .locals 1
    .parameter "command"
    .parameter "intent"
    .parameter "requestCode"

    .prologue
    .line 892
    iput-object p1, p0, Lorg/apache/cordova/CordovaActivity;->activityResultCallback:Lorg/apache/cordova/CordovaPlugin;

    .line 893
    iget-boolean v0, p0, Lorg/apache/cordova/CordovaActivity;->keepRunning:Z

    iput-boolean v0, p0, Lorg/apache/cordova/CordovaActivity;->activityResultKeepRunning:Z

    .line 896
    if-eqz p1, :cond_0

    .line 897
    const/4 v0, 0x0

    iput-boolean v0, p0, Lorg/apache/cordova/CordovaActivity;->keepRunning:Z

    .line 901
    :cond_0
    invoke-super {p0, p2, p3}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent;I)V

    .line 902
    return-void
.end method
