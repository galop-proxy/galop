$originalDir = $PSScriptRoot

cd $env:TEMP
$tempDir = [System.Guid]::NewGuid().ToString()
New-Item -Type Directory -Name $tempDir
Set-Location $tempDir

git clone https://github.com/galop-proxy/plugin-api.git 2> $null
Set-Location plugin-api

mvn clean install

cd $originalDir
