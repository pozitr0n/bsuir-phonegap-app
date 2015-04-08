.class final Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl$HttpUrlConnectionDelegate;
.super Lcom/squareup/okhttp/internal/http/HttpURLConnectionImpl;
.source "HttpsURLConnectionImpl.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x12
    name = "HttpUrlConnectionDelegate"
.end annotation


# instance fields
.field final synthetic this$0:Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl;


# direct methods
.method private constructor <init>(Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl;Ljava/net/URL;Lcom/squareup/okhttp/OkHttpClient;Lcom/squareup/okhttp/internal/http/OkResponseCache;Ljava/util/Set;)V
    .locals 0
    .parameter
    .parameter "url"
    .parameter "client"
    .parameter "responseCache"
    .parameter
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/net/URL;",
            "Lcom/squareup/okhttp/OkHttpClient;",
            "Lcom/squareup/okhttp/internal/http/OkResponseCache;",
            "Ljava/util/Set",
            "<",
            "Lcom/squareup/okhttp/Route;",
            ">;)V"
        }
    .end annotation

    .prologue
    .line 406
    .local p5, failedRoutes:Ljava/util/Set;,"Ljava/util/Set<Lcom/squareup/okhttp/Route;>;"
    iput-object p1, p0, Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl$HttpUrlConnectionDelegate;->this$0:Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl;

    .line 407
    invoke-direct {p0, p2, p3, p4, p5}, Lcom/squareup/okhttp/internal/http/HttpURLConnectionImpl;-><init>(Ljava/net/URL;Lcom/squareup/okhttp/OkHttpClient;Lcom/squareup/okhttp/internal/http/OkResponseCache;Ljava/util/Set;)V

    .line 408
    return-void
.end method

.method synthetic constructor <init>(Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl;Ljava/net/URL;Lcom/squareup/okhttp/OkHttpClient;Lcom/squareup/okhttp/internal/http/OkResponseCache;Ljava/util/Set;Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl$1;)V
    .locals 0
    .parameter "x0"
    .parameter "x1"
    .parameter "x2"
    .parameter "x3"
    .parameter "x4"
    .parameter "x5"

    .prologue
    .line 404
    invoke-direct/range {p0 .. p5}, Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl$HttpUrlConnectionDelegate;-><init>(Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl;Ljava/net/URL;Lcom/squareup/okhttp/OkHttpClient;Lcom/squareup/okhttp/internal/http/OkResponseCache;Ljava/util/Set;)V

    return-void
.end method


# virtual methods
.method protected getHttpConnectionToCache()Ljava/net/HttpURLConnection;
    .locals 1

    .prologue
    .line 411
    iget-object v0, p0, Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl$HttpUrlConnectionDelegate;->this$0:Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl;

    return-object v0
.end method

.method public getSecureCacheResponse()Ljava/net/SecureCacheResponse;
    .locals 1

    .prologue
    .line 415
    iget-object v0, p0, Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl$HttpUrlConnectionDelegate;->httpEngine:Lcom/squareup/okhttp/internal/http/HttpEngine;

    instance-of v0, v0, Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl$HttpsEngine;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lcom/squareup/okhttp/internal/http/HttpsURLConnectionImpl$HttpUrlConnectionDelegate;->httpEngine:Lcom/squareup/okhttp/internal/http/HttpEngine;

    invoke-virtual {v0}, Lcom/squareup/okhttp/internal/http/HttpEngine;->getCacheResponse()Ljava/net/CacheResponse;

    move-result-object v0

    check-cast v0, Ljava/net/SecureCacheResponse;

    :goto_0
    return-object v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method
