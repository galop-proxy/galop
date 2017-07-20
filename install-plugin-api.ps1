$originalDir = $PSScriptRoot

cd $env:TEMP
$tempDir = [System.Guid]::NewGuid().ToString()
New-Item -Type Directory -Name $tempDir
Set-Location $tempDir

git clone https://github.com/galop-proxy/plugin-api.git
Set-Location plugin-api

mvn clean install

cd $originalDir
