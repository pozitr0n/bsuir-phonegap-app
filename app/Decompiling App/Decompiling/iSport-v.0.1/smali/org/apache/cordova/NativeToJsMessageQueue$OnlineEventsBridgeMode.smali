.class Lorg/apache/cordova/NativeToJsMessageQueue$OnlineEventsBridgeMode;
.super Lorg/apache/cordova/NativeToJsMessageQueue$BridgeMode;
.source "NativeToJsMessageQueue.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lorg/apache/cordova/NativeToJsMessageQueue;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "OnlineEventsBridgeMode"
.end annotation


# instance fields
.field online:Z

.field final runnable:Ljava/lang/Runnable;

.field final synthetic this$0:Lorg/apache/cordova/NativeToJsMessageQueue;


# direct methods
.method constructor <init>(Lorg/apache/cordova/NativeToJsMessageQueue;)V
    .locals 2
    .parameter

    .prologue
    .line 309
    iput-object p1, p0, Lorg/apache/cordova/NativeToJsMessageQueue$OnlineEventsBridgeMode;->this$0:Lorg/apache/cordova/NativeToJsMessageQueue;

    const/4 v0, 0x0

    invoke-direct {p0, p1, v0}, Lorg/apache/cordova/NativeToJsMessageQueue$BridgeMode;-><init>(Lorg/apache/cordova/NativeToJsMessageQueue;Lorg/apache/cordova/NativeToJsMessageQueue$1;)V

    .line 301
    const/4 v0, 0x0

    iput-boolean v0, p0, Lorg/apache/cordova/NativeToJsMessageQueue$OnlineEventsBridgeMode;->online:Z

    .line 302
    new-instance v0, Lorg/apache/cordova/NativeToJsMessageQueue$OnlineEventsBridgeMode$1;

    invoke-direct {v0, p0}, Lorg/apache/cordova/NativeToJsMessageQueue$OnlineEventsBridgeMode$1;-><init>(Lorg/apache/cordova/NativeToJsMessageQueue$OnlineEventsBridgeMode;)V

    iput-object v0, p0, Lorg/apache/cordova/NativeToJsMessageQueue$OnlineEventsBridgeMode;->runnable:Ljava/lang/Runnable;

    .line 310
    #getter for: Lorg/apache/cordova/NativeToJsMessageQueue;->webView:Lorg/apache/cordova/CordovaWebView;
    invoke-static {p1}, Lorg/apache/cordova/NativeToJsMessageQueue;->access$400(Lorg/apache/cordova/NativeToJsMessageQueue;)Lorg/apache/cordova/CordovaWebView;

    move-result-object v0

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Lorg/apache/cordova/CordovaWebView;->setNetworkAvailable(Z)V

    .line 311
    return-void
.end method


# virtual methods
.method notifyOfFlush(Z)V
    .locals 1
    .parameter "fromOnlineEvent"

    .prologue
    .line 317
    if-eqz p1, :cond_0

    .line 318
    iget-boolean v0, p0, Lorg/apache/cordova/NativeToJsMessageQueue$OnlineEventsBridgeMode;->online:Z

    if-nez v0, :cond_1

    const/4 v0, 0x1

    :goto_0
    iput-boolean v0, p0, Lorg/apache/cordova/NativeToJsMessageQueue$OnlineEventsBridgeMode;->online:Z

    .line 320
    :cond_0
    return-void

    .line 318
    :cond_1
    const/4 v0, 0x0

    goto :goto_0
.end method

.method onNativeToJsMessageAvailable()V
    .locals 2

    .prologue
    .line 313
    iget-object v0, p0, Lorg/apache/cordova/NativeToJsMessageQueue$OnlineEventsBridgeMode;->this$0:Lorg/apache/cordova/NativeToJsMessageQueue;

    #getter for: Lorg/apache/cordova/NativeToJsMessageQueue;->cordova:Lorg/apache/cordova/CordovaInterface;
    invoke-static {v0}, Lorg/apache/cordova/NativeToJsMessageQueue;->access$500(Lorg/apache/cordova/NativeToJsMessageQueue;)Lorg/apache/cordova/CordovaInterface;

    move-result-object v0

    invoke-interface {v0}, Lorg/apache/cordova/CordovaInterface;->getActivity()Landroid/app/Activity;

    move-result-object v0

    iget-object v1, p0, Lorg/apache/cordova/NativeToJsMessageQueue$OnlineEventsBridgeMode;->runnable:Ljava/lang/Runnable;

    invoke-virtual {v0, v1}, Landroid/app/Activity;->runOnUiThread(Ljava/lang/Runnable;)V

    .line 314
    return-void
.end method
