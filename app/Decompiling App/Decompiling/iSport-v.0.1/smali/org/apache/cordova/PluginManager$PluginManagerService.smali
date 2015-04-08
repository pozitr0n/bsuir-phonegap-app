.class Lorg/apache/cordova/PluginManager$PluginManagerService;
.super Lorg/apache/cordova/CordovaPlugin;
.source "PluginManager.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lorg/apache/cordova/PluginManager;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "PluginManagerService"
.end annotation


# instance fields
.field final synthetic this$0:Lorg/apache/cordova/PluginManager;


# direct methods
.method private constructor <init>(Lorg/apache/cordova/PluginManager;)V
    .locals 0
    .parameter

    .prologue
    .line 437
    iput-object p1, p0, Lorg/apache/cordova/PluginManager$PluginManagerService;->this$0:Lorg/apache/cordova/PluginManager;

    invoke-direct {p0}, Lorg/apache/cordova/CordovaPlugin;-><init>()V

    return-void
.end method

.method synthetic constructor <init>(Lorg/apache/cordova/PluginManager;Lorg/apache/cordova/PluginManager$1;)V
    .locals 0
    .parameter "x0"
    .parameter "x1"

    .prologue
    .line 437
    invoke-direct {p0, p1}, Lorg/apache/cordova/PluginManager$PluginManagerService;-><init>(Lorg/apache/cordova/PluginManager;)V

    return-void
.end method


# virtual methods
.method public execute(Ljava/lang/String;Lorg/apache/cordova/CordovaArgs;Lorg/apache/cordova/CallbackContext;)Z
    .locals 2
    .parameter "action"
    .parameter "args"
    .parameter "callbackContext"
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lorg/json/JSONException;
        }
    .end annotation

    .prologue
    .line 440
    const-string v0, "startup"

    invoke-virtual {v0, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 447
    iget-object v0, p0, Lorg/apache/cordova/PluginManager$PluginManagerService;->this$0:Lorg/apache/cordova/PluginManager;

    #getter for: Lorg/apache/cordova/PluginManager;->numPendingUiExecs:Ljava/util/concurrent/atomic/AtomicInteger;
    invoke-static {v0}, Lorg/apache/cordova/PluginManager;->access$200(Lorg/apache/cordova/PluginManager;)Ljava/util/concurrent/atomic/AtomicInteger;

    move-result-object v0

    invoke-virtual {v0}, Ljava/util/concurrent/atomic/AtomicInteger;->getAndIncrement()I

    .line 448
    iget-object v0, p0, Lorg/apache/cordova/PluginManager$PluginManagerService;->this$0:Lorg/apache/cordova/PluginManager;

    #getter for: Lorg/apache/cordova/PluginManager;->ctx:Lorg/apache/cordova/CordovaInterface;
    invoke-static {v0}, Lorg/apache/cordova/PluginManager;->access$300(Lorg/apache/cordova/PluginManager;)Lorg/apache/cordova/CordovaInterface;

    move-result-object v0

    invoke-interface {v0}, Lorg/apache/cordova/CordovaInterface;->getActivity()Landroid/app/Activity;

    move-result-object v0

    new-instance v1, Lorg/apache/cordova/PluginManager$PluginManagerService$1;

    invoke-direct {v1, p0}, Lorg/apache/cordova/PluginManager$PluginManagerService$1;-><init>(Lorg/apache/cordova/PluginManager$PluginManagerService;)V

    invoke-virtual {v0, v1}, Landroid/app/Activity;->runOnUiThread(Ljava/lang/Runnable;)V

    .line 453
    const/4 v0, 0x1

    .line 455
    :goto_0
    return v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method
