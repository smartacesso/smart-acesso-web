# Baixa dependencias runtime do firebase-admin 9.3.0 para WEB-INF/lib
$libDir = Join-Path $PSScriptRoot "..\WebContent\WEB-INF\lib"
$base = "https://repo1.maven.org/maven2"

$artifacts = @(
    "com/google/guava/guava/32.1.3-jre/guava-32.1.3-jre.jar",
    "com/google/guava/failureaccess/1.0.2/failureaccess-1.0.2.jar",
    "com/google/guava/listenablefuture/9999.0-empty-to-avoid-conflict-with-guava/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar",
    "com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar",
    "com/google/errorprone/error_prone_annotations/2.26.1/error_prone_annotations-2.26.1.jar",
    "com/google/j2objc/j2objc-annotations/3.0.0/j2objc-annotations-3.0.0.jar",
    "com/google/code/gson/gson/2.10.1/gson-2.10.1.jar",
    "com/google/auth/google-auth-library-credentials/1.28.0/google-auth-library-credentials-1.28.0.jar",
    "com/google/http-client/google-http-client/1.44.1/google-http-client-1.44.1.jar",
    "com/google/http-client/google-http-client-gson/1.44.1/google-http-client-gson-1.44.1.jar",
    "com/google/http-client/google-http-client-jackson2/1.44.1/google-http-client-jackson2-1.44.1.jar",
    "com/google/http-client/google-http-client-apache-v2/1.44.1/google-http-client-apache-v2-1.44.1.jar",
    "com/fasterxml/jackson/core/jackson-core/2.17.0/jackson-core-2.17.0.jar",
    "com/fasterxml/jackson/core/jackson-annotations/2.17.0/jackson-annotations-2.17.0.jar",
    "com/google/oauth-client/google-oauth-client/1.35.0/google-oauth-client-1.35.0.jar",
    "com/google/api-client/google-api-client/2.5.1/google-api-client-2.5.1.jar",
    "com/google/api-client/google-api-client-gson/2.5.1/google-api-client-gson-2.5.1.jar",
    "com/google/api-client/google-api-client-jackson2/2.5.1/google-api-client-jackson2-2.5.1.jar",
    "com/google/api/api-common/2.31.0/api-common-2.31.0.jar",
    "com/google/auto/value/auto-value-annotations/1.10.4/auto-value-annotations-1.10.4.jar",
    "com/google/protobuf/protobuf-java/3.25.3/protobuf-java-3.25.3.jar",
    "com/google/protobuf/protobuf-java-util/3.25.3/protobuf-java-util-3.25.3.jar",
    "io/grpc/grpc-api/1.62.2/grpc-api-1.62.2.jar",
    "io/grpc/grpc-context/1.62.2/grpc-context-1.62.2.jar",
    "io/grpc/grpc-core/1.62.2/grpc-core-1.62.2.jar",
    "io/grpc/grpc-stub/1.62.2/grpc-stub-1.62.2.jar",
    "io/grpc/grpc-protobuf/1.62.2/grpc-protobuf-1.62.2.jar",
    "io/grpc/grpc-protobuf-lite/1.62.2/grpc-protobuf-lite-1.62.2.jar",
    "io/grpc/grpc-netty-shaded/1.62.2/grpc-netty-shaded-1.62.2.jar",
    "io/grpc/grpc-services/1.62.2/grpc-services-1.62.2.jar",
    "com/google/api/grpc/proto-google-common-protos/2.39.0/proto-google-common-protos-2.39.0.jar",
    "com/google/cloud/google-cloud-core/2.39.0/google-cloud-core-2.39.0.jar",
    "com/google/cloud/google-cloud-core-grpc/2.39.0/google-cloud-core-grpc-2.39.0.jar",
    "com/google/cloud/google-cloud-storage/2.39.0/google-cloud-storage-2.39.0.jar",
    "com/google/cloud/google-cloud-firestore/3.21.1/google-cloud-firestore-3.21.1.jar",
    "com/google/firebase/firebase-admin/9.3.0/firebase-admin-9.3.0.jar",
    "com/google/auth/google-auth-library-oauth2-http/1.28.0/google-auth-library-oauth2-http-1.28.0.jar",
    "org/slf4j/slf4j-api/2.0.13/slf4j-api-2.0.13.jar",
    "io/opencensus/opencensus-api/0.31.1/opencensus-api-0.31.1.jar",
    "io/opencensus/opencensus-contrib-http-util/0.31.1/opencensus-contrib-http-util-0.31.1.jar",
    "org/apache/httpcomponents/httpclient/4.5.14/httpclient-4.5.14.jar",
    "org/apache/httpcomponents/httpcore/4.4.16/httpcore-4.4.16.jar",
    "commons-codec/commons-codec/1.16.1/commons-codec-1.16.1.jar",
    "commons-logging/commons-logging/1.2/commons-logging-1.2.jar",
    "io/netty/netty-codec-http/4.1.109.Final/netty-codec-http-4.1.109.Final.jar",
    "io/netty/netty-handler/4.1.109.Final/netty-handler-4.1.109.Final.jar",
    "io/netty/netty-transport/4.1.109.Final/netty-transport-4.1.109.Final.jar",
    "io/netty/netty-common/4.1.109.Final/netty-common-4.1.109.Final.jar",
    "io/netty/netty-buffer/4.1.109.Final/netty-buffer-4.1.109.Final.jar",
    "io/netty/netty-codec/4.1.109.Final/netty-codec-4.1.109.Final.jar",
    "io/netty/netty-resolver/4.1.109.Final/netty-resolver-4.1.109.Final.jar",
    "com/google/api/gax/2.48.0/gax-2.48.0.jar",
    "com/google/api/gax-grpc/2.48.0/gax-grpc-2.48.0.jar",
    "com/google/api/grpc/proto-google-cloud-firestore-v1/3.21.1/proto-google-cloud-firestore-v1-3.21.1.jar",
    "com/google/api/grpc/proto-google-iam-v1/1.34.0/proto-google-iam-v1-1.34.0.jar",
    "org/threeten/threetenbp/1.6.9/threetenbp-1.6.9.jar",
    "javax/annotation/javax.annotation-api/1.3.2/javax.annotation-api-1.3.2.jar",
    "org/checkerframework/checker-qual/3.42.0/checker-qual-3.42.0.jar"
)

New-Item -ItemType Directory -Force -Path $libDir | Out-Null

foreach ($path in $artifacts) {
    $fileName = Split-Path $path -Leaf
    $dest = Join-Path $libDir $fileName
    if (Test-Path $dest) {
        Write-Host "OK (exists): $fileName"
        continue
    }
    $url = "$base/$path"
    Write-Host "Downloading $fileName ..."
    try {
        Invoke-WebRequest -Uri $url -OutFile $dest -UseBasicParsing
    } catch {
        Write-Warning "Failed: $url - $_"
    }
}

Write-Host "Done. JARs in $libDir"
Write-Host ""
& (Join-Path $PSScriptRoot "verify-firebase-libs.ps1")
exit $LASTEXITCODE
