# ============================================================
# 一键构建并部署 E2 到本机 Tomcat 10.1.50（端口 8080）
# 用法（PowerShell）：
#   cd "d:\Study\Java框架project\E2"
#   powershell -ExecutionPolicy Bypass -File .\scripts\deploy-tomcat.ps1
# ============================================================
$ErrorActionPreference = 'Stop'

$projectDir = Split-Path -Parent $PSScriptRoot
$mvn        = 'D:\Study\Java\apache-maven-3.9.12\bin\mvn.cmd'
$tomcatHome = 'D:\Study\Java\apache-tomcat-10.1.50'
$war        = Join-Path $projectDir 'target\E2.war'
$targetWar  = Join-Path $tomcatHome 'webapps\E2.war'

# 指定 JDK 17，避免使用到更高版本 JDK 导致模块访问告警
$env:JAVA_HOME = 'D:\Study\Java\jdk-17'
$env:JRE_HOME  = 'D:\Study\Java\jdk-17'

Write-Host '[1/3] 构建 war ...'
& $mvn -f (Join-Path $projectDir 'pom.xml') clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    throw 'Maven 构建失败，请先检查编译错误'
}

Write-Host '[2/3] 部署到 Tomcat ...'
$listening = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if ($listening) {
    try {
        & (Join-Path $tomcatHome 'bin\shutdown.bat') 2>&1 | Out-Null
        Start-Sleep -Seconds 3
    } catch {
        # 没有运行中的实例时 shutdown.bat 会报错，忽略即可
    }
}
if (Test-Path (Join-Path $tomcatHome 'webapps\E2')) {
    Remove-Item -Recurse -Force (Join-Path $tomcatHome 'webapps\E2')
}
Copy-Item $war $targetWar -Force

Write-Host '[3/3] 启动 Tomcat ...'
& (Join-Path $tomcatHome 'bin\startup.bat')

Write-Host ''
Write-Host '启动完成，稍等几秒后访问：'
Write-Host '  http://localhost:8080/E2/'
Write-Host '  账号：root / 123456'
Write-Host ''
Write-Host '停止 Tomcat：'
Write-Host "  & '$tomcatHome\bin\shutdown.bat'"
